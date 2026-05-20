package client;

import common.ServerResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

import common.Message;

public class MessageHandler {

    private volatile MessageCallback callback;
    private CompletableFuture<String> loginFuture;
    private CompletableFuture<String> groupCreatedFuture;

    private final ConcurrentLinkedQueue<PendingMessage> pendingMessages = new ConcurrentLinkedQueue<>();

    private static class PendingMessage {
        final Message message;
        final String fromUser;

        PendingMessage(Message message, String fromUser) {
            this.message = message;
            this.fromUser = fromUser;
        }
    }

    public void setCallback(MessageCallback callback) {
        this.callback = callback;
        flushPendingMessages();
    }

    private void flushPendingMessages() {
        if (callback == null) return;

        PendingMessage pm;
        while ((pm = pendingMessages.poll()) != null) {
            try {
                callback.onMessage(pm.message, pm.fromUser);
            } catch (Exception e) {
                System.err.println("Error processing pending message: " + e.getMessage());
            }
        }
    }

    public void setLoginFuture(CompletableFuture<String> future) {
        this.loginFuture = future;
    }

    public void setGroupCreatedFuture(CompletableFuture<String> future) {
        this.groupCreatedFuture = future;
    }

    public void handleEvent(ServerResponse response) {
        String eventType = response.getEventType();

        if (eventType == null) {
            handleResponse(response);
            return;
        }

        if (!response.isSuccess()) {
            System.err.println("Server response failed: " + response.getErrorMessage());
            return;
        }

        switch (eventType) {
            case "message" -> {
                Message msg = response.getMessage();
                String from = response.getUsername();

                if (callback != null) {
                    callback.onMessage(msg, from);
                } else {
                    pendingMessages.offer(new PendingMessage(msg, from));
                    System.out.println("Callback not ready, message queued");
                }
            }
            case "userAdded" -> {
                if (callback != null) {
                    callback.onUserAdded(
                            response.getUsername(),
                            response.getGroupId(),
                            response.getGroupName()
                    );
                }
            }
            case "groupCreated" -> {
                if (groupCreatedFuture != null && !groupCreatedFuture.isDone()) {
                    groupCreatedFuture.complete(response.getGroupId());
                    groupCreatedFuture = null;
                }
            }
            default -> System.err.println("Unknown event type: " + eventType);
        }
    }

    private void handleResponse(ServerResponse response) {
        if (loginFuture != null && !loginFuture.isDone()) {
            if (response.isSuccess()) {
                loginFuture.complete(response.getSessionId());
            } else {
                loginFuture.completeExceptionally(
                        new Exception(response.getErrorMessage())
                );
            }
            loginFuture = null;
        }

        if (groupCreatedFuture != null && !groupCreatedFuture.isDone()) {
            if (response.isSuccess()) {
                groupCreatedFuture.complete(response.getGroupId());
            } else {
                groupCreatedFuture.completeExceptionally(
                        new Exception(response.getErrorMessage())
                );
            }
            groupCreatedFuture = null;
        }
    }
}