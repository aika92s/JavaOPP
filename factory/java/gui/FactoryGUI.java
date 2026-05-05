package gui;

import factory.Auto;
import factory.Dealer;
import factory.Worker;
import logger.Logger;
import parts.Accessory;
import parts.Body;
import parts.Motor;
import storage.AccessorySupplier;
import storage.Storage;
import storage.Supplier;
import threadpool.ThreadPool;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class FactoryGUI extends JFrame {

    private Storage<Body> bodyStorage;
    private Storage<Motor> motorStorage;
    private Storage<Accessory> accessoryStorage;
    private Storage<Auto> autoStorage;

    private Supplier<Body> bodySupplier;
    private Supplier<Motor> motorSupplier;
    private List<AccessorySupplier> accessorySuppliers;

    private List<Dealer> dealers;

    private Logger logger;
    private ThreadPool threadPool;
    private List<Thread> allThreads;


    private JLabel bodyStorageLabel;
    private JLabel motorStorageLabel;
    private JLabel accessoryStorageLabel;
    private JLabel autoStorageLabel;

    private JLabel bodyProducedLabel;
    private JLabel motorProducedLabel;
    private JLabel accessoryProducedLabel;
    private JLabel autoProducedLabel;
    private JLabel taskQueueLabel;

    private JSlider bodySupplierSlider;
    private JSlider motorSupplierSlider;
    private JSlider accessorySupplierSlider;
    private JSlider dealerSlider;

    public FactoryGUI(Storage<Body> bodyStorage, Storage<Motor> motorStorage, Storage<Accessory> accessoryStorage,
               Storage<Auto> autoStorage, Supplier<Body> bodySupplier, Supplier<Motor> motorSupplier,
               List<AccessorySupplier> accessorySuppliers, List<Dealer> dealers, ThreadPool threadPool, Logger logger,
               List<Thread> allThreads) {

        super("Factory");

        this.bodyStorage = bodyStorage;
        this.motorStorage = motorStorage;
        this.accessoryStorage = accessoryStorage;
        this.autoStorage = autoStorage;
        this.bodySupplier = bodySupplier;
        this.motorSupplier = motorSupplier;
        this.accessorySuppliers = accessorySuppliers;
        this.dealers = dealers;
        this.threadPool = threadPool;
        this.logger = logger;
        this.allThreads = allThreads;

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                for (Thread t : allThreads) {
                    t.interrupt();
                }
                threadPool.shutdown();
                try { logger.close(); } catch (Exception ex) {}
                System.exit(0);
            }
        });

        initComponents();
        setupLayout();

        startTimer();

        setVisible(true);
    }

    private void initComponents() {

        bodyStorageLabel = new JLabel("Body storage: 0");
        motorStorageLabel = new JLabel("Motor storage: 0");
        accessoryStorageLabel = new JLabel("Accessory storage: 0");
        autoStorageLabel = new JLabel("Auto storage: 0");

        bodyProducedLabel = new JLabel("Body produced: 0");
        motorProducedLabel = new JLabel("Motor produced: 0");
        accessoryProducedLabel = new JLabel("Accessory produced: 0");
        autoProducedLabel = new JLabel("Autos produced: 0");
        taskQueueLabel = new JLabel("Tasks in queue: 0");

        bodySupplierSlider = new JSlider(0, 5000, bodySupplier.getLatency());
        motorSupplierSlider = new JSlider(0, 5000, motorSupplier.getLatency());
        accessorySupplierSlider = new JSlider(0, 5000, accessorySuppliers.getFirst().getLatency());
        dealerSlider = new JSlider(0, 5000, dealers.getFirst().getLatency());

        bodySupplierSlider.addChangeListener(new BodySliderListener());
        motorSupplierSlider.addChangeListener(new MotorSliderListener());
        accessorySupplierSlider.addChangeListener(new AccessorySliderListener());
        dealerSlider.addChangeListener(new DealerSliderListener());
    }


    private void setupLayout() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(bodyStorageLabel);
        panel.add(motorStorageLabel);
        panel.add(accessoryStorageLabel);
        panel.add(autoStorageLabel);
        panel.add(bodyProducedLabel);
        panel.add(motorProducedLabel);
        panel.add(accessoryProducedLabel);
        panel.add(autoProducedLabel);
        panel.add(taskQueueLabel);

        panel.add(new JLabel("Body supplier latency:"));
        panel.add(bodySupplierSlider);

        panel.add(new JLabel("Motor supplier latency:"));
        panel.add(motorSupplierSlider);

        panel.add(new JLabel("Accessory supplier latency:"));
        panel.add(accessorySupplierSlider);

        panel.add(new JLabel("Dealer latency:"));
        panel.add(dealerSlider);

        add(panel);
    }

    private void updateLabels() {
        bodyStorageLabel.setText("Body storage: " + bodyStorage.getSize());
        motorStorageLabel.setText("Motor storage: " + motorStorage.getSize());
        accessoryStorageLabel.setText("Accessory storage: " + accessoryStorage.getSize());
        autoStorageLabel.setText("Auto storage: " + autoStorage.getSize());

        bodyProducedLabel.setText("Body produced: " + bodySupplier.getProducedCount());
        motorProducedLabel.setText("Motor produced: " + motorSupplier.getProducedCount());
        accessoryProducedLabel.setText("Accessory produced: " + getTotalAccessoryProduced());
        autoProducedLabel.setText("Autos produced: " + Worker.getProduced());
        taskQueueLabel.setText("Tasks in queue: " + threadPool.getQueueSize());
    }

    private int getTotalAccessoryProduced() {
        int total = 0;
        for (Supplier<Accessory> s : accessorySuppliers) {
            total += s.getProducedCount();
        }
        return total;
    }

    private class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            updateLabels();
        }
    }

    private void startTimer() {
        Timer timer = new Timer(500, new TimerListener());
        timer.start();
    }

    private class BodySliderListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            bodySupplier.setLatency(bodySupplierSlider.getValue());
        }
    }

    private class MotorSliderListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            motorSupplier.setLatency(motorSupplierSlider.getValue());
        }
    }

    private class AccessorySliderListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            for (Supplier<Accessory> s : accessorySuppliers) {
                s.setLatency(accessorySupplierSlider.getValue());
            }
        }
    }

    private class DealerSliderListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            for (Dealer d : dealers) {
                d.setLatency(dealerSlider.getValue());
            }
        }
    }
}
