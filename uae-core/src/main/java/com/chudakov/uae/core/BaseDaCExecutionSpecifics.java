package com.chudakov.uae.core;

import com.chudakov.uae.impl.UAEState;
import com.chudakov.uae.impl.UAEVertex;

import java.util.List;

public abstract class BaseDaCExecutionSpecifics implements DaCExecutionSpecifics {
    protected DaCAlgorithm algorithmSpecifics;

    protected BaseDaCExecutionSpecifics(DaCAlgorithm algorithmSpecifics) {
        this.algorithmSpecifics = algorithmSpecifics;
    }

    @Override
    public UAEState solve(List<UAEVertex> points) {
        points = algorithmSpecifics.precompute(points);
        return solveRecursively(points);
    }

    protected abstract UAEState solveRecursively(final List<UAEVertex> points);
}
