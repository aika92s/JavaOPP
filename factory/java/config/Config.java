package config;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private int storageBodySize;
    private int storageMotorSize;
    private int storageAccessorySize;
    private int storageAutoSize;

    private int accessorySuppliers;

    private int bodySupplierLatency;
    private int motorSupplierLatency;
    private int accessorySupplierLatency;
    private int dealerLatency;

    private int workers;
    private int dealers;

    private boolean logSale;

    public Config() throws IOException {
        FileReader reader = new FileReader("config");
        Properties properties = new Properties();
        properties.load(reader);

        storageBodySize = Integer.parseInt(properties.getProperty("StorageBodySize"));
        storageMotorSize = Integer.parseInt(properties.getProperty("StorageMotorSize"));
        storageAccessorySize = Integer.parseInt(properties.getProperty("StorageAccessorySize"));
        storageAutoSize = Integer.parseInt(properties.getProperty("StorageAutoSize"));

        accessorySuppliers = Integer.parseInt(properties.getProperty("AccessorySuppliers"));

        bodySupplierLatency = Integer.parseInt(properties.getProperty("BodySupplierLatency"));
        motorSupplierLatency = Integer.parseInt(properties.getProperty("MotorSupplierLatency"));
        accessorySupplierLatency = Integer.parseInt(properties.getProperty("AccessorySupplierLatency"));
        dealerLatency = Integer.parseInt(properties.getProperty("DealerLatency"));

        workers = Integer.parseInt(properties.getProperty("Workers"));
        dealers = Integer.parseInt(properties.getProperty("Dealers"));

        logSale = Boolean.parseBoolean(properties.getProperty("LogSale"));
    }

    public int getStorageBodySize() {
        return storageBodySize;
    }

    public int getStorageMotorSize() {
        return storageMotorSize;
    }

    public int getStorageAccessorySize() {
        return storageAccessorySize;
    }

    public int getStorageAutoSize() {
        return storageAutoSize;
    }

    public int getAccessorySuppliers() {
        return accessorySuppliers;
    }

    public int getBodySupplierLatency() {
        return bodySupplierLatency;
    }

    public int getMotorSupplierLatency() {
        return motorSupplierLatency;
    }

    public int getAccessorySupplierLatency() {
        return accessorySupplierLatency;
    }

    public int getDealerLatency() {
        return dealerLatency;
    }

    public int getWorkers() {
        return workers;
    }

    public int getDealers() {
        return dealers;
    }

    public boolean isLogSale() {
        return logSale;
    }
}
