package com.chudakov.geometry.alg.perf.overmars;

import com.chudakov.geometry.alg.convexhull.overmars.ConvexHull;
import com.chudakov.geometry.alg.convexhull.overmars.ParallelConvexHull2D;
import com.chudakov.geometry.alg.convexhull.overmars.SequentialConvexHull2D;
import com.chudakov.geometry.alg.perf.BaseConvexHull2DPerf;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 3, time = 10)
@Measurement(iterations = 8, time = 10)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ConvexHull2DPerf extends BaseConvexHull2DPerf {
    private static final int TASKS_TO_NUM_OF_THREADS_RATIO = 20;

    @Benchmark
    public ConvexHull benchmarkSequential(ConvexHull2DState state) {
        return new SequentialConvexHull2D().solve(state.points);
    }

    @Benchmark
    public ConvexHull benchmarkParallel(ConvexHull2DState state) {
        return new ParallelConvexHull2D(state.inputSizeThreshold).solve(state.points);
    }
}
