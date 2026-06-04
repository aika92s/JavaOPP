package storage;

import parts.Motor;

import java.util.concurrent.atomic.AtomicInteger;

public class MotorSupplier extends Supplier<Motor> {
    private static AtomicInteger nextId = new AtomicInteger(0);

    public MotorSupplier(Storage<Motor> storage, int latency) {
        super(storage, latency);
    }

    public Motor produce() {
        return new Motor(nextId.getAndIncrement());
    }
}
