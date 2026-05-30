package client;

import common.ServerResponse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.util.concurrent.CompletableFuture;

public class MessageListener implements Runnable {

    private final ObjectInputStream in;
    private final MessageHandler handler;
    private volatile boolean running = true;

    public MessageListener(ObjectInputStream in) {
        this.in = in;
        this.handler = new MessageHandler();
    }

    public void setLoginFuture(CompletableFuture<String> future) {
        handler.setLoginFuture(future);
    }

    public MessageHandler getHandler() {
        return handler;
    }

    public void stop() {
        running = false;
        try { in.close(); } catch (IOException ignored) {}
    }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Object obj = in.readObject();
                if (!(obj instanceof ServerResponse)) {
                    System.err.println("Unexpected object type: " + obj.getClass());
                    continue;
                }

                ServerResponse response = (ServerResponse) obj;
                handler.handleEvent(response);

            } catch (ClassNotFoundException e) {
                System.err.println("Deserialization error — class not found: " + e.getMessage());
                System.err.println("Убедитесь, что классы common.* есть в classpath получателя");
                break;
            } catch (SocketException e) {
                System.err.println("Connection lost: " + e.getMessage());
                break;
            } catch (IOException e) {
                System.err.println("I/O error: " + e.getMessage());
                break;
            } catch (Exception e) {
                System.err.println("Unexpected error in MessageListener: " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("MessageListener stopped");
    }
}

