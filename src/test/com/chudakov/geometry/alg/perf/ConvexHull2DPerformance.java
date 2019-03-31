package com.chudakov.geometry.alg.perf;

import com.chudakov.geometry.alg.convexhull.simple.ParallelConvexHull2D;
import com.chudakov.geometry.alg.convexhull.simple.SequentialConvexHull2D;
import com.chudakov.geometry.common.Point2D;
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
@Measurement(iterations = 8, time = 10)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ConvexHull2DPerformance {

    @Benchmark
    public List<Point2D> testSequential(ConvexHull2DState state) {
        return new SequentialConvexHull2D().solve(state.points);
    }

    @Benchmark
    public List<Point2D> testParallel(ConvexHull2DState state) {
        return new ParallelConvexHull2D().solve(state.points);
    }

    @State(Scope.Benchmark)
    public static class ConvexHull2DState {

        @Param({"10000", "100000", "100000"})
        int size;
        List<Point2D> points;

        @Setup(Level.Trial)
        public void generateGraph() {
            int constant = 1_000_000;
            this.points = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                points.add(new Point2D(
                        (int) (Math.random() * constant),
                        (int) (Math.random() * constant)
                ));
            }
        }
    }
}
