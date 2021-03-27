package dev.gaejotbab.takealook;

public class Application {
    private final String[] args;

    public Application(String[] args) {
        this.args = args;
    }

    public void run() {
        
    }

    public static void main(String[] args) {
        Application application = new Application(args);
        application.run();
    }
}
