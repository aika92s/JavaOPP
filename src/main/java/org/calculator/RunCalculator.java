package org.calculator;
import org.commands.Command;
import org.commands.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Logger;

public class RunCalculator {
    private static final Logger log = Logger.getLogger(RunCalculator.class.getName());
    private final Context context = new Context();
    private final CommandFactory factory;

    public RunCalculator() throws Exception {
        factory = new CommandFactory();
    }

    public void parseAndExecute(String string) throws Exception {

        string = string.trim();

        if (string.isEmpty() || string.startsWith("#")) {
            return;
        }
        String[] strings = string.split("\\s+");

        String commandName = strings[0];
        String[] args = Arrays.copyOfRange(strings, 1, strings.length);

        log.info("Executing command: " + commandName + " with arguments: " + Arrays.toString(strings));

        Command command = factory.createCommand(commandName);
        command.execute(context, args);

    }
}