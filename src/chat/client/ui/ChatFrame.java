package client.ui;

import client.ChatClient;
import client.MessageCallback;
import common.Message;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChatFrame extends JFrame implements MessageCallback {
    private final ChatClient client;
    private final String username;
    private final String sessionId;
    private JTabbedPane tabbedPane;
    private final Map<String, String> groupNames = new HashMap<>();

    public ChatFrame(ChatClient client, String username, String sessionId) {
        this.client = client;
        this.username = username;
        this.sessionId = sessionId;
        client.setCallback(this);

        setTitle("Chat — " + username);
        setSize(800, 600);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                client.disconnect(sessionId);
                dispose();
                System.exit(0);
            }
        });
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        tabbedPane = new JTabbedPane();

        JButton newChatBtn = new JButton("+ Private Chat");
        newChatBtn.addActionListener(e -> {
            String target = JOptionPane.showInputDialog(this, "Enter username:");
            if (target != null && !target.isBlank()) openChatTab(target, null, false);
        });

        JButton newGroupBtn = new JButton("+ Group");
        newGroupBtn.addActionListener(e -> {
            String gName = JOptionPane.showInputDialog(this, "Enter group name:");
            if (gName != null && !gName.isBlank()) {
                try {
                    client.createGroup(sessionId, gName, gid -> {
                        groupNames.put(gid, gName);
                        SwingUtilities.invokeLater(() -> openChatTab(gName, gid, true));
                    });
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        JPanel toolbar = new JPanel();
        toolbar.add(newChatBtn);
        toolbar.add(newGroupBtn);

        add(toolbar, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private ChatPanel openChatTab(String displayName, String groupId, boolean isGroup) {
        String key = isGroup ? groupId : displayName;
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            ChatPanel p = (ChatPanel) tabbedPane.getComponentAt(i);
            if (p.getChatTarget().equals(key)) {
                tabbedPane.setSelectedIndex(i);
                return p;
            }
        }
        ChatPanel panel = new ChatPanel(client, username, sessionId, key, isGroup);
        tabbedPane.addTab(displayName, panel);
        tabbedPane.setSelectedComponent(panel);
        return panel;
    }

    @Override
    public void onMessage(Message message, String fromUser) {
        if (fromUser == null || fromUser.isEmpty()) {
            System.err.println("onMessage: fromUser is null/empty, message ignored");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            ChatPanel panel;
            if (message.getToGroup() != null) {
                String gid = message.getToGroup();
                panel = openChatTab(groupNames.getOrDefault(gid, "Group"), gid, true);
            } else {
                panel = openChatTab(fromUser, null, false);
            }
            panel.displayMessage(message, fromUser);
        });
    }

    @Override
    public void onUserAdded(String userName, String groupId, String groupName) {
        SwingUtilities.invokeLater(() -> {
            groupNames.put(groupId, groupName);
            ChatPanel p = openChatTab(groupName, groupId, true);
            p.displayMessage(null, "System: " + userName + " joined.");
        });
    }


}