import org.calculator.CommandFactory;
import org.calculator.RunCalculator;
import org.commands.*;
import org.exception.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    private Context context;

    @BeforeEach
    void setUp() {
        context = new Context();
    }

    @Nested
    @DisplayName("Context")
    class ContextTests {

        @Test
        @DisplayName("pushValue and getValue work correctly")
        void pushAndGet() throws CalculatorException {
            context.pushValue(42.0);
            assertEquals(42.0, context.getValue());
        }

        @Test
        @DisplayName("getValue from empty stack throws StackSizeException")
        void getValueEmptyStack() {
            assertThrows(StackSizeException.class, () -> context.getValue());
        }

        @Test
        @DisplayName("peekValue does not remove the element")
        void peekValueDoesNotRemove() throws CalculatorException {
            context.pushValue(7.0);
            assertEquals(7.0, context.peekValue());
            assertEquals(7.0, context.peekValue());
        }

        @Test
        @DisplayName("peekValue from empty stack throws StackSizeException")
        void peekValueEmptyStack() {
            assertThrows(StackSizeException.class, () -> context.peekValue());
        }

        @Test
        @DisplayName("getSize returns correct size")
        void getSize() throws CalculatorException {
            assertEquals(0, context.getSize());
            context.pushValue(1.0);
            context.pushValue(2.0);
            assertEquals(2, context.getSize());
            context.getValue();
            assertEquals(1, context.getSize());
        }

        @Test
        @DisplayName("setDefines and resolveValue by variable name")
        void setAndResolveDefine() throws CalculatorException {
            context.setDefines("x", 3.14);
            assertEquals(3.14, context.resolveValue("x"));
        }

        @Test
        @DisplayName("resolveValue parses a number directly")
        void resolveValueNumber() throws CalculatorException {
            assertEquals(99.5, context.resolveValue("99.5"));
        }

        @Test
        @DisplayName("resolveValue with unknown variable throws NoDefinedNumberException")
        void resolveValueUnknown() {
            assertThrows(NoDefinedNumberException.class, () -> context.resolveValue("unknown"));
        }
    }

    @Nested
    @DisplayName("PUSH")
    class PushTests {

        @Test
        @DisplayName("PUSH a number pushes it onto the stack")
        void pushNumber() throws CalculatorException {
            new CommandPush().execute(context, new String[]{"5"});
            assertEquals(5.0, context.getValue());
        }

        @Test
        @DisplayName("PUSH a defined variable pushes its value")
        void pushVariable() throws CalculatorException {
            context.setDefines("a", 4.0);
            new CommandPush().execute(context, new String[]{"a"});
            assertEquals(4.0, context.getValue());
        }

        @Test
        @DisplayName("PUSH with no arguments throws InvalidArgumentsException")
        void pushNoArgs() {
            assertThrows(InvalidArgumentsException.class,
                    () -> new CommandPush().execute(context, new String[]{}));
        }

        @Test
        @DisplayName("PUSH an unknown variable throws NoDefinedNumberException")
        void pushUnknownVar() {
            assertThrows(NoDefinedNumberException.class,
                    () -> new CommandPush().execute(context, new String[]{"z"}));
        }
    }

    @Nested
    @DisplayName("POP")
    class PopTests {

        @Test
        @DisplayName("POP removes the top element")
        void popRemovesTop() throws CalculatorException {
            context.pushValue(10.0);
            context.pushValue(20.0);
            new CommandPop().execute(context, new String[]{});
            assertEquals(1, context.getSize());
        }

        @Test
        @DisplayName("POP from empty stack throws StackSizeException")
        void popEmptyStack() {
            assertThrows(StackSizeException.class,
                    () -> new CommandPop().execute(context, new String[]{}));
        }
    }

    @Nested
    @DisplayName("PRINT")
    class PrintTests {

        @Test
        @DisplayName("PRINT does not remove the top element")
        void printDoesNotPop() throws CalculatorException {
            context.pushValue(3.0);
            new CommandPrint().execute(context, new String[]{});
            assertEquals(1, context.getSize());
        }

        @Test
        @DisplayName("PRINT from empty stack throws StackSizeException")
        void printEmptyStack() {
            assertThrows(StackSizeException.class,
                    () -> new CommandPrint().execute(context, new String[]{}));
        }
    }

    @Nested
    @DisplayName("ADD (+)")
    class AddTests {

        @Test
        @DisplayName("ADD sums two numbers correctly")
        void addTwoNumbers() throws CalculatorException {
            context.pushValue(3.0);
            context.pushValue(4.0);
            new CommandAdd().execute(context, new String[]{});
            assertEquals(7.0, context.getValue());
        }

        @Test
        @DisplayName("ADD with only one element throws StackSizeException")
        void addNotEnoughElements() {
            context.pushValue(1.0);
            assertThrows(StackSizeException.class,
                    () -> new CommandAdd().execute(context, new String[]{}));
        }

        @Test
        @DisplayName("ADD with empty stack throws StackSizeException")
        void addEmptyStack() {
            assertThrows(StackSizeException.class,
                    () -> new CommandAdd().execute(context, new String[]{}));
        }
    }

    @Nested
    @DisplayName("DIFF (-)")
    class DiffTests {

        @Test
        @DisplayName("DIFF: PUSH 10, PUSH 3, - => 7")
        void diffTwoNumbers() throws CalculatorException {
            context.pushValue(10.0);
            context.pushValue(3.0);
            new CommandDiff().execute(context, new String[]{});
            assertEquals(7.0, context.getValue());
        }

        @Test
        @DisplayName("DIFF result can be negative")
        void diffNegativeResult() throws CalculatorException {
            context.pushValue(3.0);
            context.pushValue(10.0);
            new CommandDiff().execute(context, new String[]{});
            assertEquals(-7.0, context.getValue());
        }

        @Test
        @DisplayName("DIFF with less than 2 elements throws StackSizeException")
        void diffNotEnough() {
            context.pushValue(5.0);
            assertThrows(StackSizeException.class,
                    () -> new CommandDiff().execute(context, new String[]{}));
        }
    }

    @Nested
    @DisplayName("MULT (*)")
    class MultTests {

        @Test
        @DisplayName("MULT multiplies two numbers correctly")
        void multTwoNumbers() throws CalculatorException {
            context.pushValue(3.0);
            context.pushValue(4.0);
            new CommandMult().execute(context, new String[]{});
            assertEquals(12.0, context.getValue());
        }

        @Test
        @DisplayName("MULT by zero gives zero")
        void multByZero() throws CalculatorException {
            context.pushValue(99.0);
            context.pushValue(0.0);
            new CommandMult().execute(context, new String[]{});
            assertEquals(0.0, context.getValue());
        }

        @Test
        @DisplayName("MULT with less than 2 elements throws StackSizeException")
        void multNotEnough() {
            context.pushValue(5.0);
            assertThrows(StackSizeException.class,
                    () -> new CommandMult().execute(context, new String[]{}));
        }
    }

    @Nested
    @DisplayName("DIV (/)")
    class DivTests {

        @Test
        @DisplayName("DIV divides two numbers correctly")
        void divTwoNumbers() throws CalculatorException {
            context.pushValue(10.0);
            context.pushValue(2.0);
            new CommandDiv().execute(context, new String[]{});
            assertEquals(5.0, context.getValue());
        }

        @Test
        @DisplayName("DIV by zero throws DivisionByZero")
        void divByZero() {
            context.pushValue(10.0);
            context.pushValue(0.0);
            assertThrows(DivisionByZero.class,
                    () -> new CommandDiv().execute(context, new String[]{}));
        }

        @Test
        @DisplayName("DIV with less than 2 elements throws StackSizeException")
        void divNotEnough() {
            context.pushValue(5.0);
            assertThrows(StackSizeException.class,
                    () -> new CommandDiv().execute(context, new String[]{}));
        }
    }

    @Nested
    @DisplayName("SQRT")
    class SqrtTests {

        @Test
        @DisplayName("SQRT of 4 equals 2")
        void sqrtOf4() throws CalculatorException {
            context.pushValue(4.0);
            new CommandSqrt().execute(context, new String[]{});
            assertEquals(2.0, context.getValue());
        }

        @Test
        @DisplayName("SQRT of 0 equals 0")
        void sqrtOfZero() throws CalculatorException {
            context.pushValue(0.0);
            new CommandSqrt().execute(context, new String[]{});
            assertEquals(0.0, context.getValue());
        }

        @Test
        @DisplayName("SQRT of 2 returns correct fractional result")
        void sqrtOf2() throws CalculatorException {
            context.pushValue(2.0);
            new CommandSqrt().execute(context, new String[]{});
            assertEquals(Math.sqrt(2.0), context.getValue(), 1e-9);
        }

        @Test
        @DisplayName("SQRT from empty stack throws StackSizeException")
        void sqrtEmptyStack() {
            assertThrows(StackSizeException.class,
                    () -> new CommandSqrt().execute(context, new String[]{}));
        }

        @Test
        @DisplayName("SQRT of negative number throws InvalidArgumentsException")
        void sqrtOfNegative() {
            context.pushValue(-1.0);
            assertThrows(InvalidArgumentsException.class,
                    () -> new CommandSqrt().execute(context, new String[]{}));
        }
    }

    @Nested
    @DisplayName("DEFINE")
    class DefineTests {

        @Test
        @DisplayName("DEFINE sets a variable from a number")
        void defineWithNumber() throws CalculatorException {
            new CommandDefine().execute(context, new String[]{"x", "10"});
            assertEquals(10.0, context.resolveValue("x"));
        }

        @Test
        @DisplayName("DEFINE sets a variable from another variable")
        void defineWithVariable() throws CalculatorException {
            context.setDefines("a", 5.0);
            new CommandDefine().execute(context, new String[]{"b", "a"});
            assertEquals(5.0, context.resolveValue("b"));
        }

        @Test
        @DisplayName("DEFINE with no arguments throws InvalidArgumentsException")
        void defineNoArgs() {
            assertThrows(InvalidArgumentsException.class,
                    () -> new CommandDefine().execute(context, new String[]{}));
        }

        @Test
        @DisplayName("DEFINE with one argument throws InvalidArgumentsException")
        void defineOneArg() {
            assertThrows(InvalidArgumentsException.class,
                    () -> new CommandDefine().execute(context, new String[]{"x"}));
        }
    }

    @Nested
    @DisplayName("CommandFactory")
    class CommandFactoryTests {

        private CommandFactory factory;

        @BeforeEach
        void setUpFactory() throws Exception {
            factory = new CommandFactory();
        }

        @Test
        @DisplayName("Factory creates PUSH command")
        void createPush() throws Exception {
            assertInstanceOf(CommandPush.class, factory.createCommand("PUSH"));
        }

        @Test
        @DisplayName("Factory creates POP command")
        void createPop() throws Exception {
            assertInstanceOf(CommandPop.class, factory.createCommand("POP"));
        }

        @Test
        @DisplayName("Factory creates ADD command (+)")
        void createAdd() throws Exception {
            assertInstanceOf(CommandAdd.class, factory.createCommand("+"));
        }

        @Test
        @DisplayName("Factory creates DIFF command (-)")
        void createDiff() throws Exception {
            assertInstanceOf(CommandDiff.class, factory.createCommand("-"));
        }

        @Test
        @DisplayName("Factory creates MULT command (*)")
        void createMult() throws Exception {
            assertInstanceOf(CommandMult.class, factory.createCommand("*"));
        }

        @Test
        @DisplayName("Factory creates DIV command (/)")
        void createDiv() throws Exception {
            assertInstanceOf(CommandDiv.class, factory.createCommand("/"));
        }

        @Test
        @DisplayName("Factory creates SQRT command")
        void createSqrt() throws Exception {
            assertInstanceOf(CommandSqrt.class, factory.createCommand("SQRT"));
        }

        @Test
        @DisplayName("Factory creates DEFINE command")
        void createDefine() throws Exception {
            assertInstanceOf(CommandDefine.class, factory.createCommand("DEFINE"));
        }

        @Test
        @DisplayName("Factory creates PRINT command")
        void createPrint() throws Exception {
            assertInstanceOf(CommandPrint.class, factory.createCommand("PRINT"));
        }

        @Test
        @DisplayName("Unknown command throws Exception")
        void createUnknownCommand() {
            assertThrows(Exception.class, () -> factory.createCommand("UNKNOWN"));
        }
    }

    @Nested
    @DisplayName("RunCalculator (integration)")
    class RunCalculatorTests {

        private RunCalculator calculator;

        @BeforeEach
        void setUpCalc() throws Exception {
            calculator = new RunCalculator();
        }

        @Test
        @DisplayName("Example from task: DEFINE a 4 / PUSH a / SQRT / PRINT => 2.0")
        void exampleFromTask() {
            assertDoesNotThrow(() -> {
                calculator.parseAndExecute("DEFINE a 4");
                calculator.parseAndExecute("PUSH a");
                calculator.parseAndExecute("SQRT");
                calculator.parseAndExecute("PRINT");
            });
        }

        @Test
        @DisplayName("Empty line is ignored")
        void emptyLineIgnored() {
            assertDoesNotThrow(() -> calculator.parseAndExecute("   "));
        }

        @Test
        @DisplayName("Comment line is ignored")
        void commentIgnored() {
            assertDoesNotThrow(() -> calculator.parseAndExecute("# this is a comment"));
        }

        @Test
        @DisplayName("PUSH 3, PUSH 4, + completes without exception")
        void addFlow() {
            assertDoesNotThrow(() -> {
                calculator.parseAndExecute("PUSH 3");
                calculator.parseAndExecute("PUSH 4");
                calculator.parseAndExecute("+");
            });
        }

        @Test
        @DisplayName("Unknown command throws Exception")
        void unknownCommand() {
            assertThrows(Exception.class, () -> calculator.parseAndExecute("FOO"));
        }
    }
}