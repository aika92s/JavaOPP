package common;

import java.io.Serializable;

import java.util.Set;

//todo

public class ServerResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;
    private String errorMessage = null;

    private String sessionId = null;

    private String groupName = null;

    private Set<String> userList = null;

    private String groupId;

    /*
    * eventType == null - reply on Client Request (check success and error message)
    * eventType == "userAdded" - someone has joined the chat (for group)
    * eventType = "message" - new message, display in chat
    */

    private String eventType = null;

    //если кто-то написал или вошел в чат чтобы его видно было
    private String username = null;

    private Message message = null;


    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Set<String> getUserList() {
        return userList;
    }

    public String getEventType() {
        return eventType;
    }

    public String getUsername() {
        return username;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserList(Set<String> userList) {
        this.userList = userList;
    }

    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getGroupId() { return groupId; }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
