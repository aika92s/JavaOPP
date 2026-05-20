package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatHistory implements Serializable  {
    private final List<Message> history;

    public ChatHistory() {
        history = new ArrayList<>();
    }

    public ChatHistory(List<Message> history) {
        this.history = history;
    }

    public synchronized List<Message> getHistory() {
        return history;
    }

    public synchronized int getSize() {
        return history.size();
    }

    public synchronized void addMessage(Message message) {
        history.add(message);
    }

}