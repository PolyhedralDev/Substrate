package com.dfsek.substrate.tests;

import com.dfsek.substrate.parser.Parser;
import org.apache.commons.io.IOUtils;

import java.nio.charset.Charset;

public class DevScript {
    public static void main(String... args) throws Exception {
        System.setProperty("substrate.Dump", Boolean.toString(true));
        String s = IOUtils.resourceToString("/dev.sbsc", Charset.defaultCharset());
        new Parser<>(In.class, Out.class).parse(s);
    }

    public record In() {

    }

    public record Out(boolean b) {

    }
}
