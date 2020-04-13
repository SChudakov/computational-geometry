package com.chudakov.geometry.core;

public abstract class BaseDaCExecutionSpecifics<IT, OT> implements DaCExecutionSpecifics<IT, OT> {
    protected DaCAlgorithm<IT, OT> algorithmSpecifics;

    protected BaseDaCExecutionSpecifics(DaCAlgorithm<IT, OT> algorithmSpecifics) {
        this.algorithmSpecifics = algorithmSpecifics;
    }

    @Override
    public OT solve(IT points) {
        points = algorithmSpecifics.precompute(points);
        return solveRecursively(points);
    }

    protected abstract OT solveRecursively(IT points);
}
