package com.chudakov.uae.perf.overmars;

import com.chudakov.simple.ch.Graham;
import com.chudakov.simple.ch.Jarvis;
import com.chudakov.simple.ch.QuickHull;
import com.chudakov.simple.ch.Point;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;

import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 3, time = 10)
@Measurement(iterations = 3, time = 10)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ConvexHull2DPerf {

    @Benchmark
    public List<Point> testJarvis(SequentialState state) {
        return new Jarvis().computeConvexHull(state.points);
    }

    @Benchmark
    public List<Point> testGraham(SequentialState state) {
        return new Graham().computeConvexHull(state.points);
    }

    @Benchmark
    public List<Point> testQuickHull(SequentialState state) {
        return new QuickHull().computeConvexHull(state.points);
    }
}
