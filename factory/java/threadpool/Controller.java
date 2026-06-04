package threadpool;

import factory.Auto;
import factory.Worker;
import parts.Accessory;
import parts.Body;
import parts.Motor;
import storage.Storage;

public class Controller implements Runnable {
    private Storage<Body> bodyStorage;
    private Storage<Motor> motorStorage;
    private Storage<Accessory> accessoryStorage;
    private Storage<Auto> autoStorage;

    private ThreadPool threadPool;
    private int minLimit;

    public Controller(Storage<Body> bodyStorage, Storage<Motor> motorStorage,
                  Storage<Accessory> accessoryStorage, Storage<Auto> autoStorage, ThreadPool threadPool, int minLimit) {

        this.bodyStorage = bodyStorage;
        this.motorStorage = motorStorage;
        this.accessoryStorage = accessoryStorage;
        this.autoStorage = autoStorage;

        this.threadPool = threadPool;
        this.minLimit = minLimit;
    }

    public void run() {
        while (true) {
            try {
                while (threadPool.getQueueSize() < minLimit) {
                    threadPool.submitTask(new Worker(bodyStorage, motorStorage, accessoryStorage, autoStorage));
                }
                autoStorage.waitForChange();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
