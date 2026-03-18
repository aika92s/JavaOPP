package org.commands;

import org.exception.CalculatorException;
import org.exception.InvalidArgumentsException;

public class CommandDefine implements Command {

    @Override
    public void execute(Context context, String[] args) throws CalculatorException {

        if (args.length < 2) {
            throw new InvalidArgumentsException("Not Enough arguments");
        }

        String variable = args[0];
        double value = context.resolveValue(args[1]);

        context.setDefines(variable, value);
    }
}
