package factory;

import parts.Accessory;
import parts.Body;
import parts.Motor;
import storage.Storage;
import java.util.concurrent.atomic.AtomicInteger;

public class Worker implements Runnable {

    private Storage<Body> bodyStorage;
    private Storage<Motor> motorStorage;
    private Storage<Accessory> accessoryStorage;
    private Storage<Auto> autoStorage;

    private static AtomicInteger produced = new AtomicInteger(0);
    private static AtomicInteger nextId = new AtomicInteger(0);

    public Worker(Storage<Body> bodyStorage, Storage<Motor> motorStorage,
                  Storage<Accessory> accessoryStorage, Storage<Auto> autoStorage) {

        this.bodyStorage = bodyStorage;
        this.motorStorage = motorStorage;
        this.accessoryStorage = accessoryStorage;
        this.autoStorage = autoStorage;
    }

    private Body getBody() throws InterruptedException {
        return bodyStorage.take();
    }

    private Motor getMotor() throws InterruptedException {
        return motorStorage.take();
    }

    private Accessory getAccessory() throws InterruptedException {
        return accessoryStorage.take();
    }

    private Auto produce() throws InterruptedException {

        Accessory accessory = getAccessory();
        Motor motor = getMotor();
        Body body = getBody();

        produced.incrementAndGet();
        return new Auto(motor, body, accessory, nextId.getAndIncrement());
    }

    public static int getProduced() {
        return produced.get();
    }

    public void run() {
        try {
            Auto auto = produce();
            autoStorage.add(auto);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
