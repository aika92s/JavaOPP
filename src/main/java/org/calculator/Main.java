package org.calculator;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {
    private static final Logger log = Logger.getLogger(Main.class.getName());

    static {
        try {
            Logger rootLog = Logger.getLogger("org.calculator");
            rootLog.setUseParentHandlers(false);

            FileHandler fh = new FileHandler("calculator.log", false);
            fh.setFormatter(new SimpleFormatter());
            rootLog.addHandler(fh);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws FileNotFoundException {

        InputStream input = System.in;
        if (args.length > 0) {
            input = new FileInputStream(args[0]);
        }

        log.info("Input stream selected");

        RunCalculator calculator = null;

        try {
            calculator = new RunCalculator();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
            System.err.println(e.getMessage());
            return;
        }

        log.info("The application has started working.");

        Scanner scan = new Scanner(input);

        while (scan.hasNextLine()) {
            try {
                calculator.parseAndExecute(scan.nextLine());

            } catch (Exception e) {

                System.err.println(e.getMessage());
                log.log(Level.SEVERE, e.getMessage());
            }
        }

        log.info("The application has completed successfully.");
    }
}