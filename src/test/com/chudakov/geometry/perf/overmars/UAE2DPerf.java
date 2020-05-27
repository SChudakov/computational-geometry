package com.chudakov.geometry.perf.overmars;

import com.chudakov.geometry.uae.ParallelUAE2D;
import com.chudakov.geometry.uae.SequentialUAE2D;
import com.chudakov.geometry.uae.UAEResult;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 3, time = 10)
@Measurement(iterations = 3, time = 10)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class UAE2DPerf {
    @Benchmark
    public UAEResult benchmarkSequential(PerformanceState state) {
        return new SequentialUAE2D().solve(new ArrayList<>(state.vertices));
    }

    @Benchmark
    public UAEResult benchmarkParallel(PerformanceState state) {
        return new ParallelUAE2D(state.inputSizeThreshold).solve(new ArrayList<>(state.vertices));
    }
}
