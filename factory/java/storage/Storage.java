package storage;

import java.util.LinkedList;
import java.util.Queue;

public class Storage<T> {

    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;

    public Storage(int capacity) {
        this.capacity = capacity;
    }

    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty())  {
            wait();
        }

        T part = queue.poll();
        notifyAll();
        return part;
    }

    public synchronized void add(T part) throws InterruptedException {
        while (queue.size() >= capacity) {  //вдруг случайно проснется
            wait();
        }

        queue.add(part);
        notifyAll();
    }

    public int getCapacity() {
        return capacity;
    }

    public synchronized int getSize() {
        return queue.size();
    }

    public synchronized void waitForChange() throws InterruptedException {
        wait();
    }
}
