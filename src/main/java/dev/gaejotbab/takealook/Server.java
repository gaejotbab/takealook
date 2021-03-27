package dev.gaejotbab.takealook;

import dev.gaejotbab.gaevlet.http.HttpMethod;
import dev.gaejotbab.gaevlet.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

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

            logger.info("HTTP 메서드: {}", method);
            logger.info("HTTP 요청 대상: {}", requestTarget);
            logger.info("HTTP 버전: 1.1");

            socket.close();
        } catch (IOException e) {
            throw new TakealookException("IO 문제가 발생했습니다.", e);
        }
    }
}
