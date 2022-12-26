package com.dfsek.substrate.tests;

import com.dfsek.substrate.Script;
import com.dfsek.substrate.environment.Environment;
import com.dfsek.substrate.parser.Parser;
import org.apache.commons.io.IOUtils;

import java.nio.charset.Charset;

public class DevScript {
    public static void main(String... args) throws Exception {
        System.setProperty("substrate.Dump", Boolean.toString(true));
        String s = IOUtils.resourceToString("/dev.sbsc", Charset.defaultCharset());
        Script<In, Out> script = new Parser<>(In.class, Out.class).parse(s);

        System.out.println(script.execute(new In(false), new Environment() {
        }));
    }

    public record In(boolean b) {

    }

    public record Out(boolean b) {

    }
}
