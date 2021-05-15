package com.chudakov.uae.core;


import com.chudakov.uae.impl.UAEState;
import com.chudakov.uae.impl.UAEVertex;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class SequentialDaCExecutionSpecifics extends BaseDaCExecutionSpecifics {
    protected SequentialDaCExecutionSpecifics(DaCAlgorithm algorithmSpecifics) {
        super(algorithmSpecifics);
    }

    @Override
    protected UAEState solveRecursively(final List<UAEVertex> points) {
        if (algorithmSpecifics.isBaseCase(points)) {
            return algorithmSpecifics.solveBaseCase(points);
        }

        Pair<List<UAEVertex>, List<UAEVertex>> p = algorithmSpecifics.divide(points);
        List<UAEVertex> left = p.getLeft();
        List<UAEVertex> right = p.getRight();

        UAEState leftState = solveRecursively(left);
        UAEState rightState = solveRecursively(right);
        UAEState mergedState = algorithmSpecifics.merge(leftState, rightState);
        mergedState.setPoints(points);
        return mergedState;
    }
}
