package dev.gaejotbab.takealook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static final int EXIT_FAILURE = 1;
    private final Logger logger = LoggerFactory.getLogger(Application.class);

    private final String[] args;

    public Application(String[] args) {
        this.args = args;
    }

    public void run() {
        if (args.length < 1) {
            logger.error("인자 갯수가 부족합니다.");
            System.exit(EXIT_FAILURE);
        }

        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            logger.error("포트가 잘못되었습니다.");
            System.exit(EXIT_FAILURE);
        }
    }

    public static void main(String[] args) {
        Application application = new Application(args);
        application.run();
    }
}
