package com.chudakov.uae.core;

import org.apache.commons.lang3.tuple.Pair;

public interface DaCAlgorithm<IT, OT> {
    boolean isBaseCase(IT input);

    int inputSize(IT input);

    IT precompute(IT input);

    OT solveBaseCase(IT input);

    Pair<IT, IT> divide(IT input);

    OT merge(OT first, OT second);
}
