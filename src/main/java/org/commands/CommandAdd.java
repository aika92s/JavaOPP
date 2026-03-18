package org.commands;
import org.exception.CalculatorException;
import org.exception.StackSizeException;

public class CommandAdd implements Command {

    @Override
    public void execute(Context context, String[] args) throws CalculatorException {

        if (context.getSize() < 2) {
            throw new StackSizeException("Not enough elements in stack for addition");
        }

        double a = context.getValue();
        double b = context.getValue();

        double sum = a + b;
        context.pushValue(sum);
    }
}
