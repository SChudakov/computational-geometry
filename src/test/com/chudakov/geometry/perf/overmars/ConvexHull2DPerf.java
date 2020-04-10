package com.chudakov.geometry.perf.overmars;

import com.chudakov.geometry.uae.ParallelUAE2D;
import com.chudakov.geometry.uae.SequentialUAE2D;
import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.uae.UAEResult;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 3, time = 10)
@Measurement(iterations = 3, time = 10)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ConvexHull2DPerf {
    @Benchmark
    public UAEResult benchmarkSequential(ConvexHull2DState state) {
        return new SequentialUAE2D().solve(new ArrayList<>(state.points));
    }

    @Benchmark
    public UAEResult benchmarkParallel(ConvexHull2DState state) {
        return new ParallelUAE2D(state.inputSizeThreshold).solve(new ArrayList<>(state.points));
    }

    @State(Scope.Benchmark)
    public static class ConvexHull2DState {

        @Param({"100000", "500000", "1000000", "5000000", "10000000", /*"25000000", "50000000", "70000000"*/})
        public int size;
        @Param({"20", "30", "40", "50"})
        public int tasksPerThread;

        public int inputSizeThreshold;
        public List<Point2D> points;

        @Setup(Level.Trial)
        public void generatePoints() {
            inputSizeThreshold = size / Runtime.getRuntime().availableProcessors() / tasksPerThread;
            int upperLimit = 1_000_000;
            this.points = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                points.add(new Point2D(
                        (int) (Math.random() * upperLimit),
                        (int) (Math.random() * upperLimit)
                ));
            }
        }
    }
}
