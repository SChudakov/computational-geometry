package com.chudakov.geometry.framework;

import com.chudakov.geometry.util.Pair;

import java.util.List;

public class SequentialDaCExecutionSpecifics<PT> extends BaseDaCExecutionSpecifics<PT> {
    protected SequentialDaCExecutionSpecifics(DaCAlgorithm<PT> algorithmSpecifics) {
        super(algorithmSpecifics);
    }

    @Override
    protected List<PT> solveRecursively(List<PT> points) {
        if (algorithmSpecifics.isBaseCase(points)) {
            return algorithmSpecifics.solveBaseCase(points);
        }

        Pair<List<PT>, List<PT>> p = algorithmSpecifics.divide(points);
        List<PT> left = p.getFirst();
        List<PT> right = p.getSecond();

        List<PT> leftHull = solveRecursively(left);
        List<PT> rightHull = solveRecursively(right);

        return algorithmSpecifics.merge(leftHull, rightHull);
    }
}
