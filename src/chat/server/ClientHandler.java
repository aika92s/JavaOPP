package server;

import common.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler implements Runnable {

    private final Socket socket;

    private final ObjectInputStream in; //что приходит от клиента
    private final ObjectOutputStream out; //что уходит клиенту

    private String sessionId;
    private String userName;
    private RateLimiter limiter;

    private ClientRegistry registry;
    private ConcurrentHashMap<String, ChatHistory> chatsHistory;
    private final GroupManager groupManager;
    private final Logger logger;
    private final int historyLimit;

    public ClientHandler(Socket socket, ClientRegistry registry,
                          GroupManager groupManager, int limit, Logger logger, int historyLimit) throws IOException {

        int milliseconds = 100000;

        this.socket = socket;
        this.socket.setSoTimeout(milliseconds);

        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        this.registry = registry;
        this.chatsHistory = new ConcurrentHashMap<>();
        this.groupManager = groupManager;
        this.logger = logger;

        limiter = new RateLimiter(limit);
        this.historyLimit = historyLimit;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    ClientRequest clientRequest = (ClientRequest) in.readObject();

                    ServerResponse serverResponse = HandleRequest(clientRequest);
                    sendResponse(serverResponse);

                    if ("logout".equals(clientRequest.getRequest())) break;

                } catch (ClassNotFoundException e) {
                    System.err.println("Can't handle a request\n");
                } catch (SocketTimeoutException e) {
                    System.err.println("Socket timeout\n");
                } catch (IOException e) {
                    System.err.println("Connection closed.");
                    e.printStackTrace();
                } catch (Exception e) {
                    System.err.println("Unhandled error in ClientHandler for " + userName);
                    break;
                }
            }
        } finally {
            closeResources();
        }
    }

    private ServerResponse HandleRequest(ClientRequest clientRequest) {

        String request = clientRequest.getRequest();
        ServerResponse serverResponse;

        switch (request) {
            case "login" -> serverResponse = handleLogin(clientRequest);
            case "logout" -> serverResponse = handleLogout(clientRequest);

            case "message" -> serverResponse = handleMessage(clientRequest);
            case "list" -> serverResponse = handleList(clientRequest);

            case "createGroup" -> serverResponse = handleCreate(clientRequest);
            case "addToGroup" -> serverResponse = handleAdd(clientRequest);

            default -> {
                serverResponse = new ServerResponse();
                serverResponse.setSuccess(false);
                serverResponse.setErrorMessage("Unknown command.");
            }
        }

        return serverResponse;
    }

    private ServerResponse handleLogin(ClientRequest clientRequest) {

        String userName = clientRequest.getUser().getUserName();
        ServerResponse serverResponse = new ServerResponse();

        if (registry.isUserContains(userName)) {
            serverResponse.setSuccess(false);
            String reason = "A user with that username already exists.\n";

            serverResponse.setErrorMessage(reason);
            logger.logLoginFailed(userName, reason);

            return serverResponse;
        }

        this.userName = userName;

        String sessionId = UUID.randomUUID().toString();
        serverResponse.setSessionId(sessionId);

        this.sessionId = sessionId;

        this.userName = userName;
        serverResponse.setSuccess(true);
        registry.addUser(userName, this);

        logger.logLogin(userName);

        return serverResponse;
    }

    private ServerResponse handleLogout(ClientRequest clientRequest) {

        ServerResponse serverResponse = new ServerResponse();

        if (!clientRequest.getSessionId().equals(this.sessionId)) {
            serverResponse.setSuccess(false);
            serverResponse.setErrorMessage("Invalid session ID.");

            return serverResponse;
        }

        if (!registry.isUserContains(userName)) {
            serverResponse.setSuccess(false);
            serverResponse.setErrorMessage("A user with that username doesn't exist.\n");

            return serverResponse;
        }

        serverResponse.setSuccess(true);
        registry.removeUser(userName);
        groupManager.removeUserFromAllGroups(userName);

        logger.logLogout(userName);
        return serverResponse;
    }

    private ServerResponse handleMessage(ClientRequest clientRequest) {

        ServerResponse serverResponse = new ServerResponse();

        if (!limiter.tryConsume()) {
            serverResponse.setSuccess(false);
            serverResponse.setErrorMessage("Too many requests.");

            logger.logRateLimitExceeded(userName);
            return serverResponse;
        }

        if (!clientRequest.getSessionId().equals(this.sessionId)) {
            serverResponse.setSuccess(false);
            serverResponse.setErrorMessage("Invalid session ID.");

            return serverResponse;
        }

        if (!registry.isUserContains(userName)) {
            serverResponse.setSuccess(false);
            serverResponse.setErrorMessage("A user with that username doesn't exist.\n");

            return serverResponse;
        }

        if (!clientRequest.isMessage()) {
            serverResponse.setSuccess(false);
            serverResponse.setErrorMessage("No message to send.\n");

            return serverResponse;
        }

        if (!clientRequest.isGroupMessage() && !clientRequest.isPrivateMessage()) {
            serverResponse.setSuccess(false);
            serverResponse.setErrorMessage("Message recipient not specified.\n");

            return serverResponse;
        }

        if (clientRequest.isGroupMessage()) {

            String toGroup = clientRequest.getMessage().getToGroup();

            if (!groupManager.isGroupExists(toGroup)) {
                serverResponse.setSuccess(false);
                serverResponse.setErrorMessage("A group with that username doesn't exist.\n");

                return serverResponse;
            }

            Group group = groupManager.getGroup(toGroup);

            if (!group.isUserInGroup(userName)) {
                serverResponse.setSuccess(false);
                serverResponse.setErrorMessage("Access denied: you are not a member of this group\n");

                return serverResponse;
            }

            ChatHistory groupChatHistory = group.getHistory();
            Message message = clientRequest.getMessage();

            groupChatHistory.addMessage(message);
            registry.broadcastMessage(message, group, userName);
            serverResponse.setSuccess(true);

            logger.logGroupMessage(userName, group.getGroupId());
            return serverResponse;
        }

        String toUser = clientRequest.getMessage().getToUser();

        if (!registry.isUserContains(toUser)) {
            serverResponse.setSuccess(false);
            serverResponse.setErrorMessage("A user with that username doesn't exist.\n");

            return serverResponse;
        }

        Message message = clientRequest.getMessage();
        serverResponse.setSuccess(true);
        updateHistory(toUser, message);

        registry.sendToUser(message, toUser, userName);

        logger.logPrivateMessage(userName, toUser);

        return serverResponse;
    }

    private ServerResponse handleList(ClientRequest clientRequest) {

        ServerResponse serverResponse = new ServerResponse();

        if (!clientRequest.getSessionId().equals(this.sessionId)) {
            serverResponse.setSuccess(false);
            serverResponse.setErrorMessage("Invalid session ID.");

            return serverResponse;
        }

        String groupId = clientRequest.getGroupId();

        if (!groupManager.isGroupExists(groupId)) {
            serverResponse.setSuccess(false);
            serverResponse.setErrorMessage("A group with that username doesn't exist.\n");

            return serverResponse;
        }

        Group group = groupManager.getGroup(groupId);

        if (!group.isUserInGroup(userName)) {
            serverResponse.setSuccess(false);
            serverResponse.setErrorMessage("Access denied: you are not a member of this group.\n");

            return serverResponse;
        }

        serverResponse.setSuccess(true);
        Set<String> groupList = group.getUserList();
        serverResponse.setUserList(groupList);

        return serverResponse;
    }

    private ServerResponse handleCreate(ClientRequest clientRequest) {
        ServerResponse serverResponse = new ServerResponse();

        if (!clientRequest.getSessionId().equals(this.sessionId)) {
            serverResponse.setSuccess(false);
            serverResponse.setErrorMessage("Invalid session ID.");

            return serverResponse;
        }

        String groupId = UUID.randomUUID().toString();
        String groupName = clientRequest.getGroupName();

        if (groupName == null) {
            serverResponse.setSuccess(false);
            serverResponse.setErrorMessage("Group name is not specified.");
            return serverResponse;
        }

        Group group = new Group(groupName);

        group.setGroupId(groupId);
        group.addUser(userName);
        groupManager.addGroup(group);

        serverResponse.setSuccess(true);
        serverResponse.setGroupId(groupId);
        serverResponse.setGroupName(groupName);

        logger.logGroupCreated(groupName, groupId, userName);

        return serverResponse;
    }

    private ServerResponse handleAdd(ClientRequest clientRequest) {
        ServerResponse serverResponse = new ServerResponse();

        if (!clientRequest.getSessionId().equals(this.sessionId)) {
            serverResponse.setSuccess(false);
            serverResponse.setErrorMessage("Invalid session ID.");

            return serverResponse;
        }

        String groupId = clientRequest.getGroupId();

        if (groupId == null || groupId.isEmpty()) {
            serverResponse.setSuccess(false);
            serverResponse.setErrorMessage("Group ID is null or empty.");
            return serverResponse;
        }

        if (!groupManager.isGroupExists(groupId)) {
            serverResponse.setSuccess(false);
            serverResponse.setErrorMessage("A group with that username doesn't exist.\n");

            return serverResponse;
        }

        Group group = groupManager.getGroup(groupId);

        if (!group.isUserInGroup(userName)) {
            serverResponse.setSuccess(false);
            serverResponse.setErrorMessage("Access denied: you are not a member of this group.\n");

            return serverResponse;
        }

        String targetUser = clientRequest.getTargetUser();

        if (!registry.isUserContains(targetUser)) {
            serverResponse.setSuccess(false);
            serverResponse.setErrorMessage("A user with that username doesn't exist.\n");

            return serverResponse;
        }

        if (group.isUserInGroup(targetUser)) {
            serverResponse.setSuccess(false);
            serverResponse.setErrorMessage("User is already a member of this group.\n");

            return serverResponse;
        }

        serverResponse.setSuccess(true);
        serverResponse.setGroupId(groupId);
        groupManager.getGroup(groupId).addUser(targetUser);

        registry.broadcast(group, targetUser);
        logger.logUserAddedToGroup(targetUser, groupId, userName);

        sendHistory(targetUser, group);

        return serverResponse;
    }

    private void updateHistory(String toUser, Message message) {
        chatsHistory.computeIfAbsent(toUser, k -> new ChatHistory()).addMessage(message);
        registry.addMessage(userName, toUser, message);
    }

    public void addToHistory(String withUser, Message message) {
        chatsHistory.computeIfAbsent(withUser, k -> new ChatHistory()).addMessage(message);
    }

    public synchronized void sendResponse(ServerResponse serverResponse) throws IOException {
        out.reset();
        out.writeObject(serverResponse);
        out.flush();
    }

    private void sendHistory(String targetUser, Group group) {
        ClientHandler targetHandler = registry.getHandler(targetUser);

        if (targetHandler == null) return;

        List<Message> history = group.getHistory().getHistory();

        int fromIndex = Math.max(0, history.size() - historyLimit);
        for (Message message : history.subList(fromIndex, history.size())) {
            ServerResponse response = new ServerResponse();

            response.setSuccess(true);
            response.setEventType("message");
            response.setMessage(message);

            response.setUsername(message.getFromUser().getUserName());

            try {
                targetHandler.sendResponse(response);
            } catch (IOException e) {
                System.err.println("Failed to send history to " + targetUser + ": " + e.getMessage());
            }
        }
    }

    private void closeResources() {
        try {
            if (userName != null) {
                registry.removeUser(userName);
                groupManager.removeUserFromAllGroups(userName);
            }
            if (in != null) in.close();
            if (out != null) out.close();

            if (socket != null && !socket.isClosed()) {
                logger.logClientDisconnected(userName, socket.getRemoteSocketAddress().toString());
                socket.close();
            }

        } catch (IOException ignored) {}
    }
}


