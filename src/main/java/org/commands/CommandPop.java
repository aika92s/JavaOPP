package org.commands;

import org.exception.CalculatorException;

public class CommandPop implements Command {

    @Override
    public void execute(Context context, String[] args) throws CalculatorException {
        context.getValue();
    }
}
