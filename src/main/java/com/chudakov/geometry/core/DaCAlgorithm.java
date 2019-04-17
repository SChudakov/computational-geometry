package com.chudakov.geometry.core;

import com.chudakov.geometry.util.Pair;

public interface DaCAlgorithm<IT, OT> {
    boolean isBaseCase(IT input);

    OT solveBaseCase(IT input);

    OT merge(OT first, OT second);

    Pair<IT, IT> divide(IT input);

    void precompute(IT input);
}
