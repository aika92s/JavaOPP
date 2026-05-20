package server;

import common.GroupManager;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

    /*
    ServerSocket принимается только подключения, обычный сокет: (serverSocket.accept() -> Socket)
     через него общается ClientHandler
    */

public class Server {
    private Config config;
    private ServerSocket serverSocket;

    private final ExecutorService service;
    private ClientRegistry registry;
    private GroupManager groupManager;

    public Server() throws IOException {
        service = Executors.newCachedThreadPool();
        registry = new ClientRegistry();
        groupManager = new GroupManager();

        config = new Config();
        serverSocket = new ServerSocket(config.getPort(), config.getBacklog(), InetAddress.getByName(config.getHost()));
    }

    public void processRequests() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                service.submit(new ClientHandler(socket, registry, groupManager, config.getRateLimit()));
            }
        } catch (IOException e) {
            System.err.println("Server stopped");
        } finally {
            service.shutdown();
            try {
                if (service.awaitTermination(100, TimeUnit.SECONDS)) {
                    service.shutdownNow();
                }
            } catch (InterruptedException ignored) {}
        }
    }

}
