package server;

import common.*;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ClientRegistry {

    private final ConcurrentHashMap<String, ClientHandler> allUserList;

    public ClientRegistry() {
        allUserList = new ConcurrentHashMap<>();
    }

    public void addUser(String userName, ClientHandler handler) {
        allUserList.putIfAbsent(userName, handler);
        System.out.println("User registered: " + userName);
    }

    public void removeUser(String userName) {
        ClientHandler removed = allUserList.remove(userName);
        if (removed != null) {
            System.out.println("User disconnected: " + userName);
        }
    }

    public boolean isUserContains(String userName) {
        return allUserList.containsKey(userName);
    }

    public void broadcastMessage(Message message, Group group, String fromUser) {
        ServerResponse serverResponse = responseForMessage(message, fromUser);

        for (String userName : group.getUserList()) {
            ClientHandler clientHandler = allUserList.get(userName);
            if (clientHandler != null) {
                try {
                    clientHandler.sendResponse(serverResponse);
                } catch (IOException e) {
                    System.err.println("Failed to send to " + userName + ": " + e.getMessage());
                }
            } else {
                System.out.println("User '" + userName + "' is offline (group broadcast)");
            }
        }
    }

    public void sendToUser(Message message, String toUser, String fromUser) {
        ServerResponse serverResponse = responseForMessage(message, fromUser);
        ClientHandler clientHandler = allUserList.get(toUser);

        if (clientHandler != null) {
            try {
                clientHandler.sendResponse(serverResponse);
                System.out.println("Message sent: " + fromUser + " -> " + toUser);
            } catch (IOException e) {
                System.err.println("Failed to send message to " + toUser + ": " + e.getMessage());
            }
        } else {
            System.err.println("Recipient '" + toUser + "' is OFFLINE — message LOST");
        }
    }

    private ServerResponse responseForMessage(Message message, String fromUser) {
        ServerResponse serverResponse = new ServerResponse();
        serverResponse.setSuccess(true);
        serverResponse.setEventType("message");
        serverResponse.setMessage(message);
        serverResponse.setUsername(fromUser);
        return serverResponse;
    }

    public void broadcast(Group group, String targetUser) {
        ServerResponse serverResponse = new ServerResponse();
        serverResponse.setSuccess(true);
        serverResponse.setEventType("userAdded");
        serverResponse.setUsername(targetUser);
        serverResponse.setGroupName(group.getGroupName());
        serverResponse.setGroupId(group.getGroupId());

        for (String userName : group.getUserList()) {
            ClientHandler clientHandler = allUserList.get(userName);
            if (clientHandler != null) {
                try {
                    clientHandler.sendResponse(serverResponse);
                } catch (IOException e) {
                    System.err.println("Failed to broadcast to " + userName + ": " + e.getMessage());
                }
            }
        }
    }

    public void addMessage(String fromUser, String toUser, Message message) {
        ClientHandler clientHandler = allUserList.get(toUser);
        if (clientHandler != null) {
            clientHandler.addToHistory(fromUser, message);
        }
    }

    public ClientHandler getHandler(String userName) {
        return allUserList.get(userName);
    }
}
