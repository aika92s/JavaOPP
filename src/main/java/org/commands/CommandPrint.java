package org.commands;

import org.exception.CalculatorException;
//todo system.out.println()
public class CommandPrint implements Command {

    @Override
    public void execute(Context context, String[] args) throws CalculatorException {
        double a = context.peekValue();
        System.out.println(a);
    }
}