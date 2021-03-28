package dev.gaejotbab.takealook;

import dev.gaejotbab.gaevlet.Gaevlet;
import dev.gaejotbab.gaevlet.HttpMethod;
import dev.gaejotbab.gaevlet.HttpRequest;
import dev.gaejotbab.gaevlet.HttpResponse;
import dev.gaejotbab.gaevlet.HttpVersion;
import dev.gaejotbab.webapp.AboutHandler;
import dev.gaejotbab.webapp.HomeHandler;
import dev.gaejotbab.webapp.NotFoundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private final Logger logger = LoggerFactory.getLogger(Server.class);

    private final int port;

    private final HomeHandler homeHandler = new HomeHandler();
    private final NotFoundHandler notFoundHandler = new NotFoundHandler();
    private final AboutHandler aboutHandler = new AboutHandler();

    public Server(int port) {
        this.port = port;
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            Socket socket = serverSocket.accept();

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

            Map<String, Gaevlet> targetGaevletMappings = Map.of(
                    "/", homeHandler,
                    "/favicon.ico", notFoundHandler,
                    "/about", aboutHandler
            );

            Gaevlet gaevlet = targetGaevletMappings.getOrDefault(requestTarget, notFoundHandler);
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

            socket.getOutputStream().write(response.getBody());
            socket.getOutputStream().close();

            socket.close();
        } catch (IOException e) {
            throw new TakealookException("IO 문제가 발생했습니다.", e);
        }
    }
}
