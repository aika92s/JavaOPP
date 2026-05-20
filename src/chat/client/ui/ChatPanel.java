package client.ui;

import client.ChatClient;
import common.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ChatPanel extends JPanel {
    private final ChatClient client;
    private final String username;
    private final String sessionId;
    private final String chatTarget;
    private final boolean isGroup;
    private JTextArea chatArea;
    private JTextField inputField;
    private JTextField addUserField;
    private FileAttachment pendingAttachment;
    private JButton addUserBtn;

    public ChatPanel(ChatClient client, String username, String sessionId, String chatTarget, boolean isGroup) {
        this.client = client;
        this.username = username;
        this.sessionId = sessionId;
        this.chatTarget = chatTarget;
        this.isGroup = isGroup;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        inputField = new JTextField();
        JButton sendBtn = new JButton("Send");

        sendBtn.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(sendBtn, BorderLayout.EAST);

        // Панель для добавления пользователя (только для групп)
        if (isGroup) {
            JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            userPanel.setBorder(BorderFactory.createTitledBorder("Add User to Group"));

            addUserField = new JTextField(15);
            addUserField.setToolTipText("Enter username to add");

            addUserBtn = new JButton("Add User");
            addUserBtn.addActionListener(e -> addUserToGroup());

            userPanel.add(new JLabel("Username:"));
            userPanel.add(addUserField);
            userPanel.add(addUserBtn);

            add(userPanel, BorderLayout.NORTH);
        }

        add(bottom, BorderLayout.SOUTH);
    }

    private void addUserToGroup() {
        String targetUser = addUserField.getText().trim();
        if (targetUser.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username to add");
            return;
        }

        try {
            ClientRequest request = ClientRequest.forAdd(sessionId, chatTarget, targetUser);
            client.sendRequest(request);
            addUserField.setText("");
            JOptionPane.showMessageDialog(this, "Request to add " + targetUser + " sent");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error adding user: " + ex.getMessage());
        }
    }

    private void selectFile() {
        JFileChooser jfc = new JFileChooser();
        if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = jfc.getSelectedFile();
            try {
                pendingAttachment = new FileAttachment(f.getName(), "file", Files.readAllBytes(f.toPath()));
                inputField.setText("[File: " + f.getName() + "] " + inputField.getText());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "File error: " + ex.getMessage());
            }
        }
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty() && pendingAttachment == null) return;

        try {
            Message msg = isGroup ?
                    Message.groupMessage(new User(username, "swing"), chatTarget, text, pendingAttachment) :
                    Message.privateMessage(new User(username, "swing"), chatTarget, text, pendingAttachment);

            client.sendRequest(new ClientRequest("message", msg, sessionId));
            displayMessage(msg, "Me");
            inputField.setText("");
            pendingAttachment = null;
        } catch (IOException ex) {
            chatArea.append("Error sending: " + ex.getMessage() + "\n");
        }
    }

    public void displayMessage(Message m, String from) {
        if (m == null) {
            chatArea.append(from + "\n");
            return;
        }
        String content = (m.getText() != null ? m.getText() : "") +
                (m.getFile() != null ? " [File: " + m.getFile().getFileName() + "]" : "");
        chatArea.append(from + ": " + content + "\n");
    }

    public String getChatTarget() {
        return chatTarget;
    }
}