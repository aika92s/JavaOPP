package server;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String LOG_FILE = "server.log";

    private final boolean enabled;
    private PrintWriter fileWriter;

    public Logger(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            try {
                // append=true — не затираем лог при перезапуске
                fileWriter = new PrintWriter(new FileWriter(LOG_FILE, true), true);
                log("Server started");
            } catch (IOException e) {
                System.err.println("[Logger] Cannot open log file: " + e.getMessage());
            }
        }
    }

    public void logClientConnected(String address) {
        log("CONNECT: " + address);
    }

    public void logClientDisconnected(String userName, String address) {
        log("DISCONNECT: user=" + userName + " addr=" + address);
    }

    public void logLogin(String userName) {
        log("LOGIN: user=" + userName);
    }

    public void logLoginFailed(String userName, String reason) {
        log("LOGIN FAIL: user=" + userName + " reason=" + reason);
    }

    public void logLogout(String userName) {
        log("LOGOUT: user=" + userName);
    }

    public void logPrivateMessage(String fromUser, String toUser) {
        log("MSG PRIV: from=" + fromUser + " to=" + toUser);
    }

    public void logGroupMessage(String fromUser, String groupId) {
        log("MSG GROUP: from=" + fromUser + " group=" + groupId);
    }

    public void logGroupCreated(String groupName, String groupId, String byUser) {
        log("GROUP NEW: name=" + groupName + " id=" + groupId + " by=" + byUser);
    }

    public void logUserAddedToGroup(String targetUser, String groupId, String byUser) {
        log("GROUP ADD  | user=" + targetUser + " group=" + groupId + " by=" + byUser);
    }

    public void logRateLimitExceeded(String userName) {
        log("RATE LIMIT: user=" + userName);
    }

    public void logError(String context, String message) {
        log("ERROR: [" + context + "] " + message);
    }

    private void log(String message) {
        if (!enabled) return;

        String line = "[" + LocalDateTime.now().format(FORMATTER) + "] " + message;
        System.out.println(line);

        if (fileWriter != null) {
            fileWriter.println(line);
        }
    }

    public void close() {
        if (fileWriter != null) {
            log("Server stopped");
            fileWriter.close();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
}