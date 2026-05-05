package factory;

import storage.Storage;
import logger.Logger;

import java.io.IOException;

public class Dealer implements Runnable {
    private Storage<Auto> autoStorage;
    private int latency;
    private int id;
    private int soldAutos = 0;
    private Logger logger;

    public Dealer(Storage<Auto> autoStorage, int latency, Logger logger, int id) {
        this.autoStorage = autoStorage;
        this.latency = latency;
        this.logger = logger;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getLatency() {
        return latency;
    }

    public void setLatency(int latency) {
        this.latency = latency;
    }

    public int getSoldAutos() {
        return soldAutos;
    }

    private Auto takeAuto() throws InterruptedException{
        return autoStorage.take();
    }

    public void run() {
        while (true) {
            try {
                Auto auto = takeAuto();
                try {
                    logger.log(this, auto);
                } catch (IOException ignored) {}
                soldAutos++;

                Thread.sleep(latency);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

}
