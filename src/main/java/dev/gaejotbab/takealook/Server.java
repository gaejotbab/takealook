package dev.gaejotbab.takealook;

import dev.gaejotbab.gaevlet.Filter;
import dev.gaejotbab.gaevlet.FilterChain;
import dev.gaejotbab.gaevlet.Gaevlet;
import dev.gaejotbab.gaevlet.HttpMethod;
import dev.gaejotbab.gaevlet.HttpRequest;
import dev.gaejotbab.gaevlet.HttpResponse;
import dev.gaejotbab.gaevlet.HttpVersion;
import dev.gaejotbab.takealook.TargetGaevletClassMapping.MatchingRule;
import dev.gaejotbab.webapp.AboutGaevlet;
import dev.gaejotbab.webapp.HomeGaevlet;
import dev.gaejotbab.webapp.NotFoundGaevlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
    private final Logger logger = LoggerFactory.getLogger(Server.class);

    private final int port;

    private final Map<Class<? extends Gaevlet>, Gaevlet> gaevletInstances = new HashMap<>();

    private final List<TargetGaevletClassMapping> targetGaevletClassMappings = List.of(
            TargetGaevletClassMapping.of("/favicon.ico", MatchingRule.EXACT, NotFoundGaevlet.class),
            TargetGaevletClassMapping.of("/about", MatchingRule.PREFIX, AboutGaevlet.class),
            TargetGaevletClassMapping.of("/", MatchingRule.EXACT, HomeGaevlet.class));

    private final List<Filter> filters = List.of(
            new RequestTargetCountingFilter(),
            new AccessLogFilter());

    private ExecutorService connectionHandlingThreadPool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());

    private Lock targetGaevletMappingLock = new ReentrantLock();

    private RequestTargetCountingFilter requestTargetCountingFilter = new RequestTargetCountingFilter();

    public Server(int port) {
        this.port = port;
    }

    public void run() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            logger.error("?????? ????????? ?????? ??? ??????????????????.", e);
            return;
        }

        while (true) {
            try {
                Socket socket = serverSocket.accept();

                connectionHandlingThreadPool.submit(() -> {
                    try {
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
                        InputStreamReader inputStreamReader = new InputStreamReader(bufferedInputStream, StandardCharsets.UTF_8);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                        String startLine = bufferedReader.readLine();
                        String[] tokens = startLine.split(" ", 3);
                        if (tokens.length < 3) {
                            throw new HttpRequestParsingFailedException("????????? start line??? ??????????????????.");
                        }

                        String methodString = tokens[0];
                        String requestTarget = tokens[1];
                        String versionString = tokens[2];

                        HttpMethod method;
                        try {
                            method = HttpMethod.valueOf(methodString);
                        } catch (IllegalArgumentException e) {
                            throw new HttpRequestParsingFailedException("????????? method??? ??????????????????.", e);
                        }

                        if (!versionString.equals("HTTP/1.1")) {
                            throw new HttpRequestParsingFailedException("????????? 1.1??? ????????????.");
                        }

                        HttpVersion version = HttpVersion.VERSION_1_1;

                        HashMap<String, String> requestHeaders = new HashMap<>();

                        String line;
                        while ((line = bufferedReader.readLine()).length() > 0) {
                            String headerTokens[] = line.split(": *", 2);
                            String name = headerTokens[0];
                            String value = headerTokens[1];
                            requestHeaders.put(name, value);
                        }

                        HttpRequest request = new HttpRequest.Builder()
                                .setMethod(method)
                                .setTarget(requestTarget)
                                .setVersion(version)
                                .setHeaders(requestHeaders)
                                .setRemoteAddr(socket.getInetAddress().toString())
                                .setRemotePort(socket.getPort())
                                .build();

                        targetGaevletMappingLock.lock();

                        Class<? extends Gaevlet> gaevletClass = null;

                        label: for (TargetGaevletClassMapping mapping : targetGaevletClassMappings) {
                            switch (mapping.matchingRule()) {
                                case EXACT -> {
                                    if (requestTarget.equals(mapping.target())) {
                                        gaevletClass = mapping.gaevletClass();
                                        break label;
                                    }
                                }
                                case PREFIX -> {
                                    if (requestTarget.startsWith(mapping.target())) {
                                        gaevletClass = mapping.gaevletClass();
                                        break label;
                                    }
                                }
                            }
                        }

                        if (gaevletClass == null) {
                            gaevletClass = NotFoundGaevlet.class;
                        }

                        Gaevlet gaevlet;

                        try {
                            gaevlet = gaevletInstances.get(gaevletClass);

                            if (gaevlet == null) {
                                gaevlet = gaevletClass.getConstructor().newInstance();
                                gaevletInstances.put(gaevletClass, gaevlet);
                            }
                        } catch (InstantiationException e) {
                            throw new TakealookException("?????? ????????? ?????????????????????.", e);
                        } catch (IllegalAccessException e) {
                            throw new TakealookException("????????? ???????????????.", e);
                        } catch (InvocationTargetException e) {
                            throw new TakealookException("?????? ????????? ?????????????????????.", e);
                        } catch (NoSuchMethodException e) {
                            throw new TakealookException("?????? ?????????????????? ????????????.", e);
                        } finally {
                            targetGaevletMappingLock.unlock();
                        }

                        HttpResponse response = new HttpResponse();

                        List<FilterChain> filterChains = new ArrayList<>(filters.size() + 1);

                        FilterChain lastFilterChain = new GaevletProcessingFilterChain(
                                gaevlet);
                        filterChains.add(lastFilterChain);

                        for (int i = filters.size() - 1; i >= 0; --i) {
                            Filter filter = filters.get(i);
                            FilterChain filterChain = new DefaultFilterChain(filter, lastFilterChain);
                            filterChains.add(filterChain);

                            lastFilterChain = filterChain;
                        }

                        FilterChain firstFilterChain = lastFilterChain;
                        firstFilterChain.doFilter(request, response);

                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
                        PrintWriter printWriter = new PrintWriter(outputStreamWriter);

                        printWriter.printf("%s %d %s\r\n",
                                response.getVersion().getVersionString(),
                                response.getStatusCode(),
                                response.getStatusText());

                        for (Map.Entry<String, String> responseHeaderEntry : response.getHeaders().entrySet()) {
                            printWriter.printf("%s: %s\r\n", responseHeaderEntry.getKey(), responseHeaderEntry.getValue());
                        }

                        printWriter.println("\r");
                        printWriter.flush();

                        if (response.getBody() != null) {
                            socket.getOutputStream().write(response.getBody());
                        }

                        socket.getOutputStream().close();

                        socket.close();
                    } catch (IOException e) {
                        logger.error("????????? ???????????? ??? ????????? ??????????????????.", e);
                    }
                });
            } catch (IOException e) {
                throw new TakealookException("????????? ??????????????? ??? IO ????????? ??????????????????.", e);
            }
        }
    }
}
