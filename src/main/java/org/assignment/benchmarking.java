package org.assignment;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class benchmarking {
    @State(Scope.Benchmark)
    public static class StateVariables {
        public TreeSet<Integer> treeSet = new TreeSet<Integer>();
        public LinkedHashSet<Integer> linkedHashSet = new LinkedHashSet<Integer>();
        public HashSet<Integer> hashSet = new HashSet<Integer>();
        public Random rand = new Random();
    }


    //INSERT INTO SETS
    @Benchmark
    @OutputTimeUnit(TimeUnit.SECONDS)
    @BenchmarkMode(Mode.All)
    @Warmup(iterations = 2)
    @Measurement(iterations = 10)
    @Fork(value = 1, warmups = 1)
    public void insertIntoTreeset(StateVariables state) {
        for(int i=0; i<1000000; i++){
            state.treeSet.add(benchmarking.random_generator(state));
        }
    }
    @Benchmark
    @OutputTimeUnit(TimeUnit.SECONDS)
    @BenchmarkMode(Mode.All)
    @Warmup(iterations = 2)
    @Measurement(iterations = 10)
    @Fork(value = 1, warmups = 1)
    public void insertIntoLinkedHashset(StateVariables state) {
        for(int i=0; i<1000000; i++){
            state.linkedHashSet.add(benchmarking.random_generator(state));
        }
    }
    @Benchmark
    @OutputTimeUnit(TimeUnit.SECONDS)
    @BenchmarkMode(Mode.All)
    @Warmup(iterations = 2)
    @Measurement(iterations = 10)
    @Fork(value = 1, warmups = 1)
    public void insertIntoHashset(StateVariables state) {
        for(int i=0; i<1000000; i++){
            state.hashSet.add(benchmarking.random_generator(state));
        }
    }

    //SEARCH IN SETS
    @Benchmark
    @OutputTimeUnit(TimeUnit.SECONDS)
    @BenchmarkMode(Mode.All)
    @Warmup(iterations = 2)
    @Measurement(iterations = 10)
    @Fork(value = 1, warmups = 1)
    public void TreesetContains(StateVariables state, Blackhole blackhole) {
        blackhole.consume(state.treeSet.contains(benchmarking.random_generator(state)));
    }
    @Benchmark
    @OutputTimeUnit(TimeUnit.SECONDS)
    @BenchmarkMode(Mode.All)
    @Warmup(iterations = 2)
    @Measurement(iterations = 10)
    @Fork(value = 1, warmups = 1)
    public void LinkedHashsetContains(StateVariables state, Blackhole blackhole) {
        blackhole.consume(state.linkedHashSet.contains(benchmarking.random_generator(state)));
    }
    @Benchmark
    @OutputTimeUnit(TimeUnit.SECONDS)
    @BenchmarkMode(Mode.All)
    @Warmup(iterations = 2)
    @Measurement(iterations = 10)
    @Fork(value = 1, warmups = 1)
    public void HashsetContains(StateVariables state, Blackhole blackhole) {
        blackhole.consume(state.hashSet.contains(benchmarking.random_generator(state)));
    }

    //SORT SETS
    @Benchmark
    @OutputTimeUnit(TimeUnit.SECONDS)
    @BenchmarkMode(Mode.All)
    @Warmup(iterations = 2)
    @Measurement(iterations = 10)
    @Fork(value = 1, warmups = 1)
    public void TreesetSort(StateVariables state, Blackhole blackhole) {
        blackhole.consume(state.treeSet.stream().sorted());
    }
    @Benchmark
    @OutputTimeUnit(TimeUnit.SECONDS)
    @BenchmarkMode(Mode.All)
    @Warmup(iterations = 2)
    @Measurement(iterations = 10)
    @Fork(value = 1, warmups = 1)
    public void LinkedHashsetSort(StateVariables state, Blackhole blackhole) {
        blackhole.consume(state.linkedHashSet.stream().sorted());
    }
    @Benchmark
    @OutputTimeUnit(TimeUnit.SECONDS)
    @BenchmarkMode(Mode.All)
    @Warmup(iterations = 2)
    @Measurement(iterations = 10)
    @Fork(value = 1, warmups = 1)
    public void HashsetSort(StateVariables state, Blackhole blackhole) {
        blackhole.consume(state.hashSet.stream().sorted());
    }

    private static int random_generator(StateVariables state){
        int num = state.rand.nextInt(10000000);

        return num;
    }
}
