package threadpool;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool {
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private Thread[] threads;

    public ThreadPool(int number) {
        threads = new Thread[number];

        for (int i = 0; i < number; i++) {
            threads[i] = new Thread(new PoolWorker());
            threads[i].start();
        }
    }

    public void submitTask(Runnable task) {
        taskQueue.add(task);
    }

    public int getQueueSize() {
        return taskQueue.size();
    }

    public class PoolWorker implements Runnable {
        public void run() {

            while (true) {
                try {
                    Runnable task = taskQueue.take();
                    task.run();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    public void shutdown() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }
}
