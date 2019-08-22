package com.chudakov.geometry.perf.overmars;

import com.chudakov.geometry.alg.convexhull.overmars.ParallelConvexHull2D;
import com.chudakov.geometry.alg.convexhull.overmars.SequentialConvexHull2D;
import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.datastructure.ConvexHull;
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
    public ConvexHull benchmarkSequential(ConvexHull2DState state) {
        return new SequentialConvexHull2D().solve(state.points);
    }

    @Benchmark
    public ConvexHull benchmarkParallel(ConvexHull2DState state) {
        return new ParallelConvexHull2D(state.inputSizeThreshold).solve(state.points);
    }

    @State(Scope.Benchmark)
    public static class ConvexHull2DState {

        @Param({"100000", "1000000", "10000000"})
        public int size;
        @Param({"20", "30", "40", "50"})
        public int tasksPerThread;

        public int inputSizeThreshold;
        public List<Point2D> points;

        @Setup(Level.Trial)
        public void generateGraph() {
            inputSizeThreshold = Math.max(
                    size / Runtime.getRuntime().availableProcessors() / tasksPerThread,
                    10000
            );
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