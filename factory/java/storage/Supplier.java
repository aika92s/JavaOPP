package storage;

public abstract class Supplier<T> implements Runnable {
    private int latency;
    private final Storage<T> storage;
    private int producedCount = 0;

    public Supplier(Storage<T> storage, int latency) {
        this.storage = storage;
        this.latency = latency;
    }

    public abstract T produce();

    public void addToStorage(T part) throws InterruptedException {
        storage.add(part);
    }

    public int getLatency() {
        return latency;
    }

    public void setLatency(int latency) {
        this.latency = latency;
    }

    public int getProducedCount() {
        return producedCount;
    }

    public void run() {

        while (true) {
            try {
                T part = produce();

                addToStorage(part);
                producedCount++;

                Thread.sleep(latency);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
