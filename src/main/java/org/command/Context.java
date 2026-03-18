package org.commands;
import org.exception.NoDefinedNumberException;
import org.exception.StackSizeException;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;

public class Context {
    final private Stack<Double> stack = new Stack<>();
    final private Map<String, Double> defines = new HashMap<>();

    public void pushValue(double variable) {
        stack.push(variable);
    }

    public double getValue() throws StackSizeException {

        if (stack.empty()) {
            throw new StackSizeException("No value in stack");
        }

        return stack.pop();
    }

    //for PRINT
    public double peekValue() throws StackSizeException {

        if (stack.empty()) {
            throw new StackSizeException("No value in stack");
        }

        return stack.peek();
    }

    public void setDefines(String variable, double value) {
        defines.put(variable, value);
    }

    public double resolveValue(String value) throws NoDefinedNumberException {

        if (defines.containsKey(value)) return defines.get(value);

        try {
            return Double.parseDouble(value);

        } catch (NumberFormatException e) {
            throw new NoDefinedNumberException("Cant find a variable or number: " + value);
        }

    }

    public int getSize() {
        return stack.size();
    }

}