package dev.gaejotbab.takealook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
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
            logger.info("소켓 주소: {}", socket.getInetAddress());
            logger.info("소켓 포트: {}", socket.getPort());
            InputStream inputStream = socket.getInputStream();
            inputStream.transferTo(System.out);

            socket.close();
        } catch (IOException e) {
            logger.error("IO 문제가 발생했습니다.", e);
        }
    }
}
