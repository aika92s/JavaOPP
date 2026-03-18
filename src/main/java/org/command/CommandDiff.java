package org.commands;

import org.exception.CalculatorException;
import org.exception.StackSizeException;

public class CommandDiff implements Command {

    @Override
    public void execute(Context context, String[] args) throws CalculatorException {

        if (context.getSize() < 2) {
            throw new StackSizeException("Not enough elements in stack for subtraction");
        }

        double a = context.getValue();
        double b = context.getValue();

        double diff = b - a;
        context.pushValue(diff);
    }
}
