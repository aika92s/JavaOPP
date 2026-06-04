package storage;

import parts.Accessory;

import java.util.concurrent.atomic.AtomicInteger;

public class AccessorySupplier extends Supplier<Accessory> {
    private static AtomicInteger nextId = new AtomicInteger(0);

    public AccessorySupplier(Storage<Accessory> storage, int latency) {
        super(storage, latency);
    }

    public Accessory produce() {
        return new Accessory(nextId.getAndIncrement());
    }
}
