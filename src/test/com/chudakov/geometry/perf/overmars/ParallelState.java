package com.chudakov.geometry.perf.overmars;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ParallelState extends BaseState{
    @Param({"20"/*, "30", "40", "50"*/})
    public int tasksPerThread;

    public int inputSizeThreshold;

    @Setup(Level.Trial)
    public void init() {
        inputSizeThreshold = size / Runtime.getRuntime().availableProcessors() / tasksPerThread;
        generatePoints();
    }
}
