package server;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private final int port;
    private final int rateLimit;
    private final boolean logging;
    private final String host;
    private final int backlog;
    private final int historyLimit;

    public Config() throws IOException {
        FileReader reader = new FileReader("server.property");
        Properties properties = new Properties();

        properties.load(reader);

        logging = Boolean.parseBoolean(properties.getProperty("logging"));
        rateLimit = Integer.parseInt(properties.getProperty("rateLimit"));

        port = Integer.parseInt(properties.getProperty("port"));
        host = properties.getProperty("host");
        backlog = Integer.parseInt(properties.getProperty("backlog"));
        historyLimit = Integer.parseInt(properties.getProperty("historyLimit"));
    }

    public int getPort() {
        return port;
    }

    public int getRateLimit() {
        return rateLimit;
    }

    public boolean isLogging() {
        return logging;
    }

    public String getHost() {
        return host;
    }

    public int getBacklog() {
        return backlog;
    }

    public int getHistoryLimit() {
        return historyLimit;
    }
}
