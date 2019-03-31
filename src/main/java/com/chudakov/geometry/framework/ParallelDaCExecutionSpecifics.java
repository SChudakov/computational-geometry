package com.chudakov.geometry.framework;

import com.chudakov.geometry.util.Pair;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ParallelDaCExecutionSpecifics<PT> extends BaseDaCExecutionSpecifics<PT> {

    protected ParallelDaCExecutionSpecifics(DaCAlgorithm<PT> algorithmSpecifics) {
        super(algorithmSpecifics);
    }

    @Override
    protected List<PT> solveRecursively(List<PT> points) {
        ExecutionTask task = new ExecutionTask(points);

        ForkJoinPool.commonPool().submit(task);

        return task.join();
    }

    private class ExecutionTask extends RecursiveTask<List<PT>> {
        List<PT> points;

        ExecutionTask(List<PT> points) {
            this.points = points;
        }

        @Override
        protected List<PT> compute() {
            if (algorithmSpecifics.isBaseCase(points)) {
                return algorithmSpecifics.solveBaseCase(points);
            } else {
                Pair<List<PT>, List<PT>> p = algorithmSpecifics.divide(points);
                List<PT> left = p.getFirst();
                List<PT> right = p.getSecond();

                ExecutionTask t1 = new ExecutionTask(left);
                t1.fork();
                ExecutionTask t2 = new ExecutionTask(right);

                List<PT> leftResult = t1.join();
                List<PT> rightResult = t2.compute();
                return algorithmSpecifics.merge(leftResult, rightResult);
            }
        }
    }
}
