package main;

import client.ChatClient;
import common.ClientRequest;
import common.Group;
import server.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        try {
            Server server = new Server();
            server.processRequests();

        } catch (IOException e) {
            System.out.println("Can't create server");
        }
    }
}
