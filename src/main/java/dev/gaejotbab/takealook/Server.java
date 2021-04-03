package dev.gaejotbab.takealook;

import dev.gaejotbab.gaevlet.Gaevlet;
import dev.gaejotbab.gaevlet.HttpMethod;
import dev.gaejotbab.gaevlet.HttpRequest;
import dev.gaejotbab.gaevlet.HttpResponse;
import dev.gaejotbab.gaevlet.HttpVersion;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final Logger logger = LoggerFactory.getLogger(Server.class);

    private final int port;

    private final Map<Class<? extends Gaevlet>, Gaevlet> gaevletInstances = new HashMap<>();

    private final Map<String, Class<? extends Gaevlet>> targetGaevletMappings = Map.of(
            "/", HomeGaevlet.class,
            "/favicon.ico", NotFoundGaevlet.class,
            "/about", AboutGaevlet.class
    );

    private ExecutorService connectionHandlingThreadPool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());

    public Server(int port) {
        this.port = port;
    }

    public void run() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            logger.error("서버 소켓을 여는 데 실패했습니다.", e);
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
                            throw new HttpRequestParsingFailedException("잘못된 start line이 들어왔습니다.");
                        }

                        String methodString = tokens[0];
                        String requestTarget = tokens[1];
                        String versionString = tokens[2];

                        HttpMethod method;
                        try {
                            method = HttpMethod.valueOf(methodString);
                        } catch (IllegalArgumentException e) {
                            throw new HttpRequestParsingFailedException("잘못된 method가 들어왔습니다.", e);
                        }

                        if (!versionString.equals("HTTP/1.1")) {
                            throw new HttpRequestParsingFailedException("버전이 1.1이 아닙니다.");
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
                                .build();

                        Class<? extends Gaevlet> gaevletClass = targetGaevletMappings.getOrDefault(requestTarget, NotFoundGaevlet.class);

                        Gaevlet gaevlet = gaevletInstances.get(gaevletClass);
                        if (gaevlet == null) {
                            try {
                                gaevlet = gaevletClass.getConstructor().newInstance();
                            } catch (InstantiationException e) {
                                throw new TakealookException("객체 생성에 실패하였습니다.", e);
                            } catch (IllegalAccessException e) {
                                throw new TakealookException("잘못된 접근입니다.", e);
                            } catch (InvocationTargetException e) {
                                throw new TakealookException("호출 대상이 잘못되었습니다.", e);
                            } catch (NoSuchMethodException e) {
                                throw new TakealookException("기본 컨스트럭터가 없습니다.", e);
                            }

                            gaevletInstances.put(gaevletClass, gaevlet);
                        }

                        HttpResponse response = new HttpResponse();
                        gaevlet.service(request, response);

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
                        logger.error("접속을 처리하던 중 오류가 발생했습니다.", e);
                    }
                });
            } catch (IOException e) {
                throw new TakealookException("접속을 받아들이던 중 IO 문제가 발생했습니다.", e);
            }
        }
    }
}
