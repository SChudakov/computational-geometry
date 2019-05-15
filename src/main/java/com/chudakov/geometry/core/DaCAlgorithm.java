package com.chudakov.geometry.core;

import com.chudakov.geometry.util.Pair;

public interface DaCAlgorithm<IT, OT> {
    boolean isBaseCase(IT input);

    int inputSize(IT input);

    OT solveBaseCase(IT input);

    OT merge(OT first, OT second);

    Pair<IT, IT> divide(IT input);

    IT precompute(IT input);
}
