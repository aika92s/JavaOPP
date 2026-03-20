package org.commands;
import org.exception.DivisionByZero;
import org.exception.CalculatorException;
import org.exception.StackSizeException;

public class CommandDiv implements Command {

    @Override
    public void execute(Context context, String[] args) throws CalculatorException {

        if (context.getSize() < 2) {
            throw new StackSizeException("Not enough elements in stack for division");
        }

        double a = context.getValue();
        double b = context.getValue();

        if (a == 0) {
            throw new DivisionByZero("Division by zero");
        }

        double div = b / a;
        context.pushValue(div);
    }
}
