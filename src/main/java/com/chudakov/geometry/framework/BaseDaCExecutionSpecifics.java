package com.chudakov.geometry.framework;

import java.util.List;

public abstract class BaseDaCExecutionSpecifics<PT> implements DaCExecutionSpecifics<PT> {
    protected DaCAlgorithm<PT> algorithmSpecifics;

    protected BaseDaCExecutionSpecifics(DaCAlgorithm<PT> algorithmSpecifics) {
        this.algorithmSpecifics = algorithmSpecifics;
    }

    @Override
    public List<PT> solve(List<PT> points) {
//        System.out.println("pre sort: " + points);
        algorithmSpecifics.precompute(points);
//        System.out.println("after sort: " + points);
        return solveRecursively(points);
    }

    protected abstract List<PT> solveRecursively(List<PT> points);
}
