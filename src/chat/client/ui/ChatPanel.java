package client.ui;

import client.ChatClient;
import common.*;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;

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
    private JPanel messagesPanel;
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

        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(messagesPanel);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> e.getAdjustable().setValue(e.getAdjustable().getMaximum()));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        inputField = new JTextField();
        JButton sendBtn = new JButton("Send");
        JButton fileBtn = new JButton("Attach");
        fileBtn.addActionListener(e -> selectFile());

        sendBtn.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(sendBtn, BorderLayout.EAST);
        bottom.add(fileBtn, BorderLayout.WEST);

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
            inputField.setText("");
            pendingAttachment = null;
        } catch (IOException ex) {
            addTextMessage("Error sending: " + ex.getMessage());
        }
    }

    public void displayMessage(Message m, String from) {
        if (m == null) {
            addTextMessage(from);
            return;
        }

        if (m.getText() != null && !m.getText().isEmpty()) {
            addTextMessage(from + ": " + m.getText());
        }

        if (m.getFile() != null) {
            FileAttachment fa = m.getFile();
            JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
            filePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            filePanel.add(new JLabel(from + ": (Attachment) " + fa.getFileName()));

            JButton downloadBtn = new JButton("Download");
            downloadBtn.addActionListener(e -> saveFile(fa));
            filePanel.add(downloadBtn);

            String fileName = fa.getFileName().toLowerCase();
            if (fileName.endsWith(".wav") || fileName.endsWith(".mp3")) {
                Clip[] clipHolder = new Clip[1];
                JButton playBtn = new JButton("▶ Play");
                playBtn.addActionListener(e -> {
                    if (clipHolder[0] != null && clipHolder[0].isRunning()) {
                        clipHolder[0].stop();
                        playBtn.setText("▶ Play");
                    } else {
                        new Thread(() -> {
                            try {
                                ByteArrayInputStream bais = new ByteArrayInputStream(fa.getData());
                                AudioInputStream mp3Stream = AudioSystem.getAudioInputStream(bais);
                                AudioFormat baseFormat = mp3Stream.getFormat();
                                AudioFormat decodedFormat = new AudioFormat(
                                        AudioFormat.Encoding.PCM_SIGNED,
                                        baseFormat.getSampleRate(), 16,
                                        baseFormat.getChannels(),
                                        baseFormat.getChannels() * 2,
                                        baseFormat.getSampleRate(), false);
                                AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, mp3Stream);
                                clipHolder[0] = AudioSystem.getClip();
                                clipHolder[0].open(decodedStream);
                                clipHolder[0].start();
                                SwingUtilities.invokeLater(() -> playBtn.setText("⏹ Stop"));
                                clipHolder[0].addLineListener(event -> {
                                    if (event.getType() == LineEvent.Type.STOP) {
                                        SwingUtilities.invokeLater(() -> playBtn.setText("▶ Play"));
                                    }
                                });
                            } catch (Exception ex) {
                                SwingUtilities.invokeLater(() ->
                                        JOptionPane.showMessageDialog(this, "Playback error: " + ex.getMessage()));
                            }
                        }).start();
                    }
                });
                filePanel.add(playBtn);
            }

            filePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            messagesPanel.add(filePanel);
            messagesPanel.revalidate();
        }
    }

    private void addTextMessage(String text) {
        JLabel label = new JLabel(text);
        label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        messagesPanel.add(label);
        messagesPanel.revalidate();
    }

    private void saveFile(FileAttachment fa) {
        JFileChooser jfc = new JFileChooser();
        jfc.setSelectedFile(new File(fa.getFileName()));
        if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Files.write(jfc.getSelectedFile().toPath(), fa.getData());
                JOptionPane.showMessageDialog(this, "Saved!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
            }
        }
    }

    public String getChatTarget() {
        return chatTarget;
    }
}