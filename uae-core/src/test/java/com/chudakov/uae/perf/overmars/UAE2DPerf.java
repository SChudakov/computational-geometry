package com.chudakov.uae.perf.overmars;

import com.chudakov.uae.impl.ParallelUAE2D;
import com.chudakov.uae.impl.SequentialUAE2D;
import com.chudakov.uae.impl.UAEState;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 3, time = 10)
@Measurement(iterations = 3, time = 10)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class UAE2DPerf {
    @Benchmark
    public UAEState benchmarkSequential(SequentialState state) {
        return new SequentialUAE2D().solve(new ArrayList<>(state.vertices));
    }

    @Benchmark
    public UAEState benchmarkParallel(ParallelState state) {
        return new ParallelUAE2D(state.inputSizeThreshold).solve(new ArrayList<>(state.vertices));
    }

    public static void main(String[] args) throws RunnerException, RunnerException {
        Options opt = new OptionsBuilder()
                .mode(Mode.AverageTime)
                .include(UAE2DPerf.class.getName() + ".*")

                .forks(1)
                .warmupForks(0)

                .warmupIterations(3)
//                .warmupBatchSize(5)
//                .warmupTime(new TimeValue(10, TimeUnit.MINUTES))
                .measurementIterations(5)
//                .measurementTime(new TimeValue(10, TimeUnit.MINUTES))
//                .measurementBatchSize(5)
                .timeUnit(TimeUnit.MILLISECONDS)
                .jvmArgs("-Xms7g", "-Xmx7g")
//                .output("~/ch benchmarks/liechtenstein_50")
                .build();
        System.out.println(opt.getIncludes());
        new Runner(opt).run();
    }
}
