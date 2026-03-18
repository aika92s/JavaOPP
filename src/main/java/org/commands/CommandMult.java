package org.commands;

import org.exception.CalculatorException;
import org.exception.StackSizeException;

public class CommandMult implements Command {

    @Override
    public void execute(Context context, String[] args) throws CalculatorException {

        if (context.getSize() < 2) {
            throw new StackSizeException("Not enough elements in stack for multiplication");
        }

        double a = context.getValue();
        double b = context.getValue();

        double mult = a * b;
        context.pushValue(mult);
    }
}

