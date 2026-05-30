package common;

import java.io.Serializable;

public class ClientRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String request;
    private Message message = null;

    private String sessionId;

    private User user = null; //login
    private String groupId = null;; //grouplist / addToGroup / createGroup
    private String groupName = null;
    private String targetUser = null; // addToGroup

    public ClientRequest(String request, Message message, String sessionId) {

        this.request = request;
        this.message = message;
        this.sessionId = sessionId;
    }

    //login
    public ClientRequest(String request, User user) {
        this.request = request;
        this.user = user;

    }

    public ClientRequest(String request, String sessionId) {
        this.request = request;
        this.sessionId = sessionId;
    }

    public Message getMessage() {
        return message;
    }

    public String getSessionId() {
        return sessionId;
    }

    public User getUser() {
        return user;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getRequest() {
        return request;
    }

    public boolean isPrivateMessage() {
        return message.getToUser() != null;
    }

    public boolean isGroupMessage() {
        return message.getToGroup() != null;
    }

    public boolean isMessage() {
        return message != null;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getGroupName() {
        return groupName;
    }

    public static ClientRequest forList(String sessionId, String groupId) {
        ClientRequest r = new ClientRequest("list", sessionId);
        r.groupId = groupId;
        return r;
    }

    public static ClientRequest forCreate(String sessionId, String groupName) {
        ClientRequest r = new ClientRequest("createGroup", sessionId);
        r.groupName = groupName;
        return r;
    }

    public static ClientRequest forAdd(String sessionId, String groupId, String targetUser) {
        ClientRequest r = new ClientRequest("addToGroup", sessionId);
        r.groupId = groupId;
        r.targetUser = targetUser;
        return r;
    }
}
