package com.chudakov.geometry.alg.perf;

import com.chudakov.geometry.common.Point2D;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.List;

public class BaseConvexHull2DPerf {

    @State(Scope.Benchmark)
    public static class ConvexHull2DState {

        @Param({"100000", "1000000", "10000000", "100000000"})
        public int size;
        @Param({"20", "30", "40", "50"})
        public int tasksPerThread;

        public int inputSizeThreshold;
        public List<Point2D> points;

        @Setup(Level.Trial)
        public void generateGraph() {
            int inputSizeThreshold = Math.max(
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
