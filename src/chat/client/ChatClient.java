package client;

import common.ClientRequest;
import common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ChatClient {
    private final Socket socket; //сокет с адресом и портом сервера
    private ObjectInputStream in; //получает с сервера
    private ObjectOutputStream out; //отправляет на сервер
    private MessageListener listener; // в отдельном потоке слушает события;

    public ChatClient(int port, String host) throws IOException {

        socket = new Socket(host, port);

        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        listener = new MessageListener(in);

        new Thread(listener).start();
    }

    public void sendRequest(ClientRequest request) throws IOException{

        out.reset();
        out.writeObject(request);
        out.flush();
    }

    public void setCallback(MessageCallback callback) {
        listener.getHandler().setCallback(callback);
    }
//хуета какая-то
    public String login(String username) throws Exception {
        CompletableFuture<String> future = new CompletableFuture<>();
        listener.setLoginFuture(future);

        ClientRequest req = new ClientRequest("login", new User(username, "swing"));
        sendRequest(req);

        return future.get(5, TimeUnit.SECONDS); // бросит TimeoutException если нет ответа
    }

    public void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();

        } catch (IOException ignored) {}
    }

    public void createGroup(String sessionId, String groupName,
                            java.util.function.Consumer<String> onCreated) throws IOException {
        CompletableFuture<String> future = new CompletableFuture<>();
        listener.getHandler().setGroupCreatedFuture(future);
        future.thenAccept(onCreated);
        sendRequest(ClientRequest.forCreate(sessionId, groupName));
    }

    public void disconnect(String sessionId) {
        try {
            sendRequest(new ClientRequest("logout", sessionId));
            socket.close();
        } catch (IOException ignored) {}
    }
}

