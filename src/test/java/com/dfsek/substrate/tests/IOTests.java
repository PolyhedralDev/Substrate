package com.dfsek.substrate.tests;

import com.dfsek.substrate.environment.Environment;
import com.dfsek.substrate.environment.IO;
import com.dfsek.substrate.lang.std.function.StaticFunction;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.exception.ParseException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test input/output from scripts.
 */
public class IOTests {
    private static final String returnInputScript = getScript("returnInput");
    private static final String compareInputsEquals = getScript("compareInputsEquals");
    private static final String returnTupleIntDoubleString = getScript("returnTupleIntDoubleString");
    private static final String inputClosureInt = getScript("inputClosureInt");

    private static final String monadicInvalidBind = getScript("monadicInvalidBind");
    private static final String monadicInput = getScript("monadicInput");
    private static final String monadicInputTransformation = getScript("monadicInputTransformation");

    static {
        System.setProperty("substrate.Dump", Boolean.toString(Utils.DUMP_TO_JARS));
    }

    @Test
    public void singleBooleanRecordInput() throws NoSuchMethodException {
        Assertions.assertTrue(Utils.createParser(Records.BooleanInput.class, Records.BooleanInput.class, true).parse(returnInputScript).execute(new Records.BooleanInput(true), null).input());
    }

    @Test
    public void singleIntRecordInput() throws NoSuchMethodException {
        Assertions.assertEquals(5, Utils.createParser(Records.IntInput.class, Records.IntInput.class, true).parse(returnInputScript).execute(new Records.IntInput(5), null).input());
    }

    @Test
    public void singleDoubleRecordInput() throws NoSuchMethodException {
        Assertions.assertEquals(5.5, Utils.createParser(Records.DoubleInput.class, Records.DoubleInput.class, true).parse(returnInputScript).execute(new Records.DoubleInput(5.5), null).input());
    }

    @Test
    public void singleStringRecordInput() throws NoSuchMethodException {
        Assertions.assertEquals("bazinga", Utils.createParser(Records.StringInput.class, Records.StringInput.class, true).parse(returnInputScript).execute(new Records.StringInput("bazinga"), null).input());
    }

    @Test
    public void intEquals() throws NoSuchMethodException {
        Assertions.assertTrue(Utils.createParser(Records.TwoInts.class, Records.BooleanInput.class, true).parse(compareInputsEquals).execute(new Records.TwoInts(5, 5), null).input());
    }

    @Test
    public void doubleEquals() throws NoSuchMethodException {
        Assertions.assertTrue(Utils.createParser(Records.TwoDoubles.class, Records.BooleanInput.class, true).parse(compareInputsEquals).execute(new Records.TwoDoubles(5.5, 5.5), null).input());
    }

    @Test
    public void stringEquals() throws NoSuchMethodException {
        Assertions.assertTrue(Utils.createParser(Records.TwoStrings.class, Records.BooleanInput.class, true).parse(compareInputsEquals).execute(new Records.TwoStrings("bazinga", "bazinga"), null).input());
    }

    @Test
    public void returnTupleIntDoubleString() throws NoSuchMethodException {
        Assertions.assertEquals(new Records.IntDoubleString(5, 4.3, "bazinga"), Utils.createParser(Records.Void.class, Records.IntDoubleString.class, true).parse(returnTupleIntDoubleString).execute(new Records.Void(), null));
    }

    @Test
    public void basicMonadic() throws NoSuchMethodException {
        Records.IOOut.BasicEnvironment environment = new Records.IOOut.BasicEnvironment();
        Parser<Records.Void, Records.IOOut> parser = Utils.createParser(Records.Void.class, Records.IOOut.class, true);

        //parser.registerFunction("putLine", new StaticFunction(IOTests.class.getMethod("putLine", String.class)));

        try {
            parser.parse(monadicInvalidBind).execute(new Records.Void(), environment).io().apply(environment);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        fail();
    }

    @Test
    public void monadicInput() throws NoSuchMethodException {
        Records.IOOut.BasicEnvironment environment = new Records.IOOut.BasicEnvironment();
        Parser<Records.Void, Records.IOOut> parser = Utils.createParser(Records.Void.class, Records.IOOut.class, true);

        //parser.registerFunction("putLine", new StaticFunction(IOTests.class.getMethod("putLine", String.class)));
        //parser.registerFunction("getInt", new StaticFunction(IOTests.class.getMethod("getInt")));

        IO<Void, Records.IOOut.BasicEnvironment> io = parser.parse(monadicInput).execute(new Records.Void(), environment).io();
        System.out.println("Evaluated.");
        io.apply(environment);
    }

    @Test
    public void monadicInputTransformation() throws NoSuchMethodException {
        Records.IOOut.BasicEnvironment environment = new Records.IOOut.BasicEnvironment();
        Parser<Records.Void, Records.IOOut> parser = Utils.createParser(Records.Void.class, Records.IOOut.class, true);

        //parser.registerFunction("putLine", new StaticFunction(IOTests.class.getMethod("putLine", String.class)));
        //parser.registerFunction("getInt", new StaticFunction(IOTests.class.getMethod("getInt")));
        //parser.registerFunction("appendHash", new StaticFunction(IOTests.class.getMethod("appendHash", int.class)));

        System.out.println(new StaticFunction(IOTests.class.getMethod("appendHash", int.class)).reference());
        System.out.println(new StaticFunction(IOTests.class.getMethod("putLine", String.class)).reference());
        System.out.println(new StaticFunction(IOTests.class.getMethod("getInt")).reference());

        IO<Void, Records.IOOut.BasicEnvironment> io = parser.parse(monadicInputTransformation).execute(new Records.Void(), environment).io();
        System.out.println("Evaluated.");
        io.apply(environment);
    }

    public static IO<Void, Records.IOOut.BasicEnvironment> putLine(String in) {
        return env -> {
            env.getOut().println(in);
            return null;
        };
    }

    public static IO<Integer, Records.IOOut.BasicEnvironment> getInt() {
        return env -> ThreadLocalRandom.current().nextInt();
    }

    public static IO<String, Records.IOOut.BasicEnvironment> appendHash(int i) {
        return env -> i + "::" + ThreadLocalRandom.current().nextInt();
    }


    @Test
    public void inputClosureInt() throws NoSuchMethodException {
        System.setProperty(Utils.DISABLE_OPTIMISATION_PROPERTY, "true");
        Assertions.assertEquals(5, Utils.createParser(Records.IntInput.class, Records.IntInput.class, true).parse(inputClosureInt).execute(new Records.IntInput(5), null).input());
        System.clearProperty(Utils.DISABLE_OPTIMISATION_PROPERTY);
    }


    private static String getScript(String script) {
        try {
            return IOUtils.resourceToString("/scripts/io/" + script + ".sbsc", Charset.defaultCharset());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }



    public static class Records {
        public record BooleanInput(boolean input) {
        }

        public record DoubleInput(double input) {
        }

        public record StringInput(String input) {
        }

        public record IntInput(int input) {
        }


        public record TwoDoubles(double input1, double input2) {
        }

        public record TwoStrings(String input1, String input2) {
        }

        public record TwoInts(int input1, int input2) {
        }

        public record IntDoubleString(int input1, double input2, String input3) {
        }

        public record Void() {
        }

        public record IOOut(IO<java.lang.Void, BasicEnvironment> io) {
            public static final class BasicEnvironment implements Environment {
                public PrintStream getOut() {
                    return System.out;
                }
            }
        }
    }
}
