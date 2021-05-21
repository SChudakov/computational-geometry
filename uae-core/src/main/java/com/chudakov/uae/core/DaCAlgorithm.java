package com.chudakov.uae.core;

import com.chudakov.uae.impl.UAEState;
import com.chudakov.uae.impl.UAEVertex;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface DaCAlgorithm {
    boolean isBaseCase(final List<UAEVertex> points);

    List<UAEVertex> precompute(final List<UAEVertex> points);

    UAEState solveBaseCase(final List<UAEVertex> points);

    Pair<List<UAEVertex>, List<UAEVertex>> divide(final List<UAEVertex> points);

    UAEState merge(UAEState first, UAEState second);
}
