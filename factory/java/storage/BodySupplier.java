package storage;
import parts.Body;

import java.util.concurrent.atomic.AtomicInteger;

public class BodySupplier extends Supplier<Body> {
    private static AtomicInteger nextId = new AtomicInteger(0);

    public BodySupplier(Storage<Body> storage, int latency) {
        super(storage, latency);
    }

    public Body produce() {
        return new Body(nextId.getAndIncrement());
    }
}
