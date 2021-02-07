package com.chudakov.uae.core;


import org.apache.commons.lang3.tuple.Pair;

public class SequentialDaCExecutionSpecifics<IT, OT> extends BaseDaCExecutionSpecifics<IT, OT> {
    protected SequentialDaCExecutionSpecifics(DaCAlgorithm<IT, OT> algorithmSpecifics) {
        super(algorithmSpecifics);
    }

    @Override
    protected OT solveRecursively(IT input) {
        if (algorithmSpecifics.isBaseCase(input)) {
            return algorithmSpecifics.solveBaseCase(input);
        }

        Pair<IT, IT> p = algorithmSpecifics.divide(input);
        IT left = p.getLeft();
        IT right = p.getRight();

        OT leftHull = solveRecursively(left);
        OT rightHull = solveRecursively(right);

        return algorithmSpecifics.merge(leftHull, rightHull);
    }
}
