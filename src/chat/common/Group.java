package common;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Group implements Serializable {

    private static final long serialVersionUID = 1L;

    private String groupId = null;
    private final String groupName;

    private final Set<String> userList;
    private final ChatHistory history;

    public Group(String groupName) {
        this.groupName = groupName;

        userList = new HashSet<>();
        history = new ChatHistory();
    }
    public synchronized void addMessage(Message message) {
        history.addMessage(message);
    }

    public synchronized void addUser(String userName) {
        userList.add(userName);
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public synchronized ChatHistory getHistory() {
        return history;
    }

    public synchronized Set<String> getUserList() {
        return userList;
    }

    public synchronized void removeUser(String userName) {
        userList.remove(userName);
    }

    public synchronized boolean isUserInGroup(String userName) {
        return userList.contains(userName);
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public synchronized int getSize() {
        return userList.size();
    }
}
