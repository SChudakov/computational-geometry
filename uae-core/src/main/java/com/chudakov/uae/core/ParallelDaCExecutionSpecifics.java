package com.chudakov.uae.core;

import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ParallelDaCExecutionSpecifics<IT, OT> extends BaseDaCExecutionSpecifics<IT, OT> {
    private static final int DEFAULT_INPUT_SIZE_THRESHOLD = 1 << 15; // 32768
    private long inputSizeThreshold;

    protected ParallelDaCExecutionSpecifics(DaCAlgorithm<IT, OT> algorithmSpecifics) {
        this(algorithmSpecifics, DEFAULT_INPUT_SIZE_THRESHOLD);
    }

    protected ParallelDaCExecutionSpecifics(DaCAlgorithm<IT, OT> algorithmSpecifics, int inputSizeThreshold) {
        super(algorithmSpecifics);
        if (inputSizeThreshold <= 0) {
            throw new IllegalArgumentException("inputSizeThreshold should be positive!");
        }
        this.inputSizeThreshold = inputSizeThreshold;
    }

    @Override
    protected OT solveRecursively(IT points) {
        ExecutionTask task = new ExecutionTask(points);

        ForkJoinPool.commonPool().submit(task);

        return task.join();
    }

    private class ExecutionTask extends RecursiveTask<OT> {
        IT input;
        SequentialDaCExecutionSpecifics<IT, OT> sequentialExecution;

        ExecutionTask(IT input) {
            this.input = input;
            this.sequentialExecution = new SequentialDaCExecutionSpecifics<>(algorithmSpecifics);
        }

        @Override
        protected OT compute() {
            if (algorithmSpecifics.inputSize(input) <= inputSizeThreshold) {
                return sequentialExecution.solveRecursively(input);
            } else {
                Pair<IT, IT> p = algorithmSpecifics.divide(input);
                IT left = p.getLeft();
                IT right = p.getRight();

                ExecutionTask t1 = new ExecutionTask(left);
                t1.fork();
                ExecutionTask t2 = new ExecutionTask(right);

                OT rightResult = t2.compute();
                OT leftResult = t1.join();
                return algorithmSpecifics.merge(leftResult, rightResult);
            }
        }
    }
}
