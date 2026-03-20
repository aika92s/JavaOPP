package org.commands;
import org.exception.CalculatorException;
import org.exception.InvalidArgumentsException;

public class CommandPush implements Command {

    @Override
    public void execute(Context context, String[] args) throws CalculatorException  {

        if (args.length < 1) {
            throw new InvalidArgumentsException("Not Enough arguments");
        }

       double value = context.resolveValue(args[0]);
       context.pushValue(value);

    }
}
