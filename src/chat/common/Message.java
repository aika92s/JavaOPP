package common;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.UUID;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private String messageId;
    private User fromUser;

    private String toUser;
    private String toGroup;

    private LocalTime timeStamp;

    private String text;
    private FileAttachment file;

    private static Message create(User fromUser, String toUser, String toGroup, String text, FileAttachment file) {
        Message message = new Message();
        message.fromUser = fromUser;
        message.toUser = toUser;
        message.toGroup = toGroup;
        message.text = text;
        message.file = file;
        message.messageId = UUID.randomUUID().toString();
        message.timeStamp = LocalTime.now();
        return message;
    }

    public static Message privateMessage(User fromUser, String toUser, String text) {
        return create(fromUser, toUser, null, text, null);
    }

    public static Message privateMessage(User fromUser, String toUser, FileAttachment file) {
        return create(fromUser, toUser, null, null, file);
    }

    public static Message privateMessage(User fromUser, String toUser, String text, FileAttachment file) {
        return create(fromUser, toUser, null, text, file);
    }

    public static Message groupMessage(User fromUser, String toGroup, String text) {
        return create(fromUser, null, toGroup, text, null);
    }

    public static Message groupMessage(User fromUser, String toGroup, FileAttachment file) {
        return create(fromUser, null, toGroup, null, file);
    }

    public static Message groupMessage(User fromUser, String toGroup, String text, FileAttachment file) {
        return create(fromUser, null, toGroup, text, file);
    }

    public String getMessageId() {
        return messageId;
    }

    public User getFromUser() {
        return fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public String getToGroup() {
        return toGroup;
    }

    public LocalTime getTimeStamp() {
        return timeStamp;
    }

    public String getText() {
        return text;
    }

    public FileAttachment getFile() {
        return file;
    }
}
