package org.commands;

import org.exception.CalculatorException;
import org.exception.InvalidArgumentsException;

import static java.lang.Math.sqrt;

public class CommandSqrt implements Command {

    @Override
    public void execute(Context context, String[] args) throws CalculatorException {

        double a = context.getValue();

        if (a < 0) {
            throw new InvalidArgumentsException("Cannot take sqrt of a negative number");
        }

        double sqrt = sqrt(a);
        context.pushValue(sqrt);
    }
}
