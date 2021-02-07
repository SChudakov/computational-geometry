package com.chudakov.uae.perf.overmars;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class SequentialState extends BaseState {
    @Setup(Level.Trial)
    public void init() {
        generatePoints();
    }
}
