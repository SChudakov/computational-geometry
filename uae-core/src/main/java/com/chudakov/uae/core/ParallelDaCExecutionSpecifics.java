package com.chudakov.uae.core;

import com.chudakov.uae.impl.UAEState;
import com.chudakov.uae.impl.UAEVertex;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ParallelDaCExecutionSpecifics extends BaseDaCExecutionSpecifics {
    private static final int DEFAULT_INPUT_SIZE_THRESHOLD = 1 << 15; // 32768
    private final long inputSizeThreshold;

    protected ParallelDaCExecutionSpecifics(DaCAlgorithm algorithmSpecifics) {
        this(algorithmSpecifics, DEFAULT_INPUT_SIZE_THRESHOLD);
    }

    protected ParallelDaCExecutionSpecifics(DaCAlgorithm algorithmSpecifics, int inputSizeThreshold) {
        super(algorithmSpecifics);
        if (inputSizeThreshold <= 0) {
            throw new IllegalArgumentException("inputSizeThreshold should be positive!");
        }
        this.inputSizeThreshold = inputSizeThreshold;
    }

    @Override
    protected UAEState solveRecursively(final List<UAEVertex> points) {
        ExecutionTask task = new ExecutionTask(points);

        ForkJoinPool.commonPool().submit(task);

        return task.join();
    }

    private class ExecutionTask extends RecursiveTask<UAEState> {
        List<UAEVertex> points;
        SequentialDaCExecutionSpecifics sequentialExecution;

        ExecutionTask(final List<UAEVertex> points) {
            this.points = points;
            this.sequentialExecution = new SequentialDaCExecutionSpecifics(algorithmSpecifics);
        }

        @Override
        protected UAEState compute() {
            if (points.size() <= inputSizeThreshold) {
                return sequentialExecution.solveRecursively(points);
            } else {
                Pair<List<UAEVertex>, List<UAEVertex>> p = algorithmSpecifics.divide(points);
                List<UAEVertex> left = p.getLeft();
                List<UAEVertex> right = p.getRight();

                ExecutionTask t1 = new ExecutionTask(left);
                t1.fork();
                ExecutionTask t2 = new ExecutionTask(right);

                UAEState rightResult = t2.compute();
                UAEState leftResult = t1.join();
                UAEState merged = algorithmSpecifics.merge(leftResult, rightResult);
                merged.setPoints(points);
                return merged;
            }
        }
    }
}
