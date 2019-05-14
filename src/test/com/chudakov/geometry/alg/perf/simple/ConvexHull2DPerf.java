package com.chudakov.geometry.alg.perf.simple;

import com.chudakov.geometry.alg.convexhull.simple.ParallelConvexHull2D;
import com.chudakov.geometry.alg.convexhull.simple.SequentialConvexHull2D;
import com.chudakov.geometry.alg.perf.BaseConvexHull2DPerf;
import com.chudakov.geometry.common.Point2D;
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
@Measurement(iterations = 8, time = 10)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ConvexHull2DPerf extends BaseConvexHull2DPerf {
    @Benchmark
    public List<Point2D> testSequential(ConvexHull2DState state) {
        return new SequentialConvexHull2D().solve(state.points);
    }

    @Benchmark
    public List<Point2D> testParallel(ConvexHull2DState state) {
        return new ParallelConvexHull2D().solve(state.points);
    }
}
