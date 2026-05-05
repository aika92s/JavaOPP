package logger;

import factory.Auto;
import factory.Dealer;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
    private FileWriter writer;
    private boolean enabled;

    public Logger(boolean enabled) throws IOException {
        writer = new FileWriter("logger");
        this.enabled = enabled;
    }
    public synchronized void log(Dealer dealer, Auto auto) throws IOException {
        if (!enabled) return;

        int dealerId = dealer.getId();
        int autoId = auto.getId();

        int motorId = auto.getMotor().getId();
        int bodyId = auto.getBody().getId();
        int accessoryId = auto.getAccessory().getId();

        String message = String.format("%s: Dealer %d: Auto %d (Body: %d, Motor: %d, Accessory: %d)%n",
                new java.util.Date(), dealerId, autoId, bodyId, motorId, accessoryId);

        writer.write(message);
        writer.flush();
    }

    public void close() throws IOException {
        writer.close();
    }
}
