import config.Config;
import factory.Auto;
import factory.Dealer;
import gui.FactoryGUI;
import parts.Accessory;
import parts.Body;
import parts.Motor;
import storage.AccessorySupplier;
import storage.BodySupplier;
import storage.MotorSupplier;
import storage.Storage;
import threadpool.Controller;
import threadpool.ThreadPool;
import logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        try {
            Config config = new Config();

            Logger logger = new Logger(config.isLogSale());

            Storage<Body> bodyStorage = new Storage<Body>(config.getStorageBodySize());
            Storage<Motor> motorStorage = new Storage<Motor>(config.getStorageMotorSize());
            Storage<Accessory> accessoryStorage = new Storage<Accessory>(config.getStorageAccessorySize());

            Storage<Auto> autoStorage = new Storage<Auto>(config.getStorageAutoSize());

            BodySupplier bodySupplier = new BodySupplier(bodyStorage, config.getBodySupplierLatency());
            Thread bodyThread = new Thread(bodySupplier);
            bodyThread.start();

            MotorSupplier motorSupplier = new MotorSupplier(motorStorage, config.getMotorSupplierLatency());
            Thread motorThread = new Thread(motorSupplier);
            motorThread.start();

            List<Thread> accessorySuppliersThreads = new ArrayList<>();
            List<AccessorySupplier> accessorySuppliers = new ArrayList<>();

            int numberOfAccessorySuppliers = config.getAccessorySuppliers();

            for (int i = 0; i < numberOfAccessorySuppliers; i++) {
                AccessorySupplier accessorySupplier = new AccessorySupplier(accessoryStorage, config.getAccessorySupplierLatency());
                accessorySuppliers.add(accessorySupplier);

                Thread accessoryThread = new Thread(accessorySupplier);
                accessoryThread.start();

                accessorySuppliersThreads.add(accessoryThread);
            }

            int minLimit = 5;

            ThreadPool threadPool = new ThreadPool(config.getWorkers());
            Controller controller = new Controller(bodyStorage, motorStorage, accessoryStorage, autoStorage,
                    threadPool, minLimit);

            Thread controllerThread = new Thread(controller);
            controllerThread.start();

            List<Thread> dealerThreads = new ArrayList<>();
            List<Dealer> dealers = new ArrayList<>();

            int numberOfDealers = config.getDealers();

            for (int i = 0; i < numberOfDealers; i++) {
                Dealer dealer = new Dealer(autoStorage, config.getDealerLatency(), logger, i);
                dealers.add(dealer);

                Thread dealerThread = new Thread(dealer);
                dealerThread.start();

                dealerThreads.add(dealerThread);
            }

            List<Thread> allThreads = new ArrayList<>();
            allThreads.add(bodyThread);
            allThreads.add(motorThread);
            allThreads.addAll(accessorySuppliersThreads);
            allThreads.addAll(dealerThreads);
            allThreads.add(controllerThread);

            FactoryGUI gui = new FactoryGUI(bodyStorage, motorStorage, accessoryStorage, autoStorage, bodySupplier, motorSupplier,
                    accessorySuppliers, dealers, threadPool, logger, allThreads);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
