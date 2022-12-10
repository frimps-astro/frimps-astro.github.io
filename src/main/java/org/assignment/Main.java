package org.assignment;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
//        Options opt = new OptionsBuilder()
//                .include(Main.class.getSimpleName())
//                .forks(2)
//                .warmupIterations(2)
//                .measurementIterations(2)
//                .build();
//        new Runner(opt).run();
//        System.out.println("Hello world!");
    }
}