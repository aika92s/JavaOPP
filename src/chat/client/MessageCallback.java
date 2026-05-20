package client;

import common.Message;

public interface MessageCallback {
    void onMessage(Message message, String fromUser);
    void onUserAdded(String userName, String groupId, String groupName);
}