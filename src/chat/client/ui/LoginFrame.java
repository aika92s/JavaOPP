package client.ui;

import client.ChatClient;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField hostField;
    private JTextField portField;
    private JTextField usernameField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Chat — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(320, 220);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        panel.add(new JLabel("Host:"), c);
        c.gridx = 1; c.weightx = 1;
        hostField = new JTextField("localhost");
        panel.add(hostField, c);

        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        panel.add(new JLabel("Port:"), c);
        c.gridx = 1; c.weightx = 1;
        portField = new JTextField("8080");
        panel.add(portField, c);

        c.gridx = 0; c.gridy = 2; c.weightx = 0;
        panel.add(new JLabel("Username:"), c);
        c.gridx = 1; c.weightx = 1;
        usernameField = new JTextField();
        panel.add(usernameField, c);

        c.gridx = 0; c.gridy = 3; c.gridwidth = 2;
        loginButton = new JButton("Connect");
        panel.add(loginButton, c);

        loginButton.addActionListener(e -> doLogin());
        usernameField.addActionListener(e -> doLogin());

        add(panel);
    }

    private void doLogin() {
        String host = hostField.getText().trim();
        String username = usernameField.getText().trim();
        int port;

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username.");
            return;
        }
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid port.");
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Connecting...");

        String finalHost = host;
        int finalPort = port;

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            ChatClient client;

            @Override
            protected String doInBackground() throws Exception {
                client = new ChatClient(finalPort, finalHost);
                return client.login(username); // возвращает sessionId
            }

            @Override
            protected void done() {
                try {
                    String sessionId = get();
                    new ChatFrame(client, username, sessionId).setVisible(true);
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Login failed: " + ex.getCause().getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    loginButton.setEnabled(true);
                    loginButton.setText("Connect");
                }
            }
        };
        worker.execute();
    }
}