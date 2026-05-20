package common;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String userName;
    private String sessionId = null;
    private final String clientType;

    public User(String userName, String clientType) {
        this.userName = userName;
        this.clientType = clientType;

    }

    public String getUserName() {
        return userName;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getClientType() {
        return clientType;
    }
}
