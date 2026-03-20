package org.calculator;

import org.commands.Command;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CommandFactory {
    private static final Logger log = Logger.getLogger(CommandFactory.class.getName());
    Properties config = new Properties(); //public class Properties extends Hashtable<Object,Object>

    public CommandFactory() throws Exception {
        try (InputStream is = CommandFactory.class.getResourceAsStream("/commands.properties"))
        {
            if (is == null) throw new Exception("Config file not found!");
            config.load(is);
        }
    }

    public Command createCommand(String commandName) throws Exception {

        String className = config.getProperty(commandName);
        if (className == null) {

            log.log(Level.WARNING, "Attempt to use an unknown command:" + commandName);
            throw new Exception("Unknown command: " + commandName);
        }

        log.fine("Creating a command instance: " + className);

        Class<?> command = Class.forName(className);

        return (Command) command.getDeclaredConstructor().newInstance();
    }
}
