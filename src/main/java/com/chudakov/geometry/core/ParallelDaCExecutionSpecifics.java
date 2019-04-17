package com.chudakov.geometry.core;

import com.chudakov.geometry.util.Pair;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ParallelDaCExecutionSpecifics<IT, OT> extends BaseDaCExecutionSpecifics<IT, OT> {

    protected ParallelDaCExecutionSpecifics(DaCAlgorithm<IT, OT> algorithmSpecifics) {
        super(algorithmSpecifics);
    }

    @Override
    protected OT solveRecursively(IT points) {
        ExecutionTask task = new ExecutionTask(points);

        ForkJoinPool.commonPool().submit(task);

        return task.join();
    }

    private class ExecutionTask extends RecursiveTask<OT> {
        IT input;

        ExecutionTask(IT input) {
            this.input = input;
        }

        @Override
        protected OT compute() {
            if (algorithmSpecifics.isBaseCase(input)) {
                return algorithmSpecifics.solveBaseCase(input);
            } else {
                Pair<IT, IT> p = algorithmSpecifics.divide(input);
                IT left = p.getFirst();
                IT right = p.getSecond();

                ExecutionTask t1 = new ExecutionTask(left);
                t1.fork();
                ExecutionTask t2 = new ExecutionTask(right);

                OT leftResult = t1.join();
                OT rightResult = t2.compute();
                return algorithmSpecifics.merge(leftResult, rightResult);
            }
        }
    }
}
