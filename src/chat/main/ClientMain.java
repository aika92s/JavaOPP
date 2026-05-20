
package main;

import client.ChatClient;
import client.ui.LoginFrame;
import common.ClientRequest;
import common.Group;

import javax.swing.SwingUtilities;

public class ClientMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}