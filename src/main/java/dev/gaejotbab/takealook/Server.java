package dev.gaejotbab.takealook;

import dev.gaejotbab.gaevlet.http.HttpMethod;
import dev.gaejotbab.gaevlet.http.HttpRequest;
import dev.gaejotbab.gaevlet.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Server {
    private final Logger logger = LoggerFactory.getLogger(Server.class);

    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            Socket socket = serverSocket.accept();

            BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
            InputStreamReader inputStreamReader = new InputStreamReader(bufferedInputStream);
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

            HashMap<String, String> headers = new HashMap<>();

            String line;
            while ((line = bufferedReader.readLine()).length() > 0) {
                String headerTokens[] = line.split(": *", 2);
                String name = headerTokens[0];
                String value = headerTokens[1];
                headers.put(name, value);
            }

            HttpRequest request = new HttpRequest.Builder()
                    .setMethod(method)
                    .setTarget(requestTarget)
                    .setVersion(version)
                    .setHeaders(headers)
                    .build();

            OutputStream outputStream = socket.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            PrintWriter printWriter = new PrintWriter(outputStreamWriter);

            printWriter.println("HTTP/1.1 200 OK\r");
            printWriter.println("Content-Type: text/html; charset=UTF-8\r");
            printWriter.println("\r");
            printWriter.println("""
                    <!DOCTYPE html>
                    <html lang="ko">
                    <head>
                        <meta charset="UTF-8">
                        <meta http-equiv="X-UA-Compatible" content="IE=edge">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>떼껄룩 테스트 페이지</title>
                    </head>
                    <body>
                        <h1>떼껄룩 테스트 페이지</h1>
                        <p>별 거 없습니다.</p>
                    </body>
                    </html>
                    """);
            printWriter.close();

            socket.close();
        } catch (IOException e) {
            throw new TakealookException("IO 문제가 발생했습니다.", e);
        }
    }
}
