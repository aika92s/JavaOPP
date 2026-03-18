package org.commands;
import org.exception.CalculatorException;

public interface Command {
    public void execute(Context context, String[] args) throws CalculatorException;
}
