package com.chudakov.uae.impl;

import com.chudakov.uae.core.ParallelDaCExecutionSpecifics;

import java.util.List;

public class ParallelUAE2D extends ParallelDaCExecutionSpecifics<List<UAEVertex>, UAEResult> {
    public ParallelUAE2D() {
        super(new UAE2D());
    }

    public ParallelUAE2D(int inputSizeThreshold) {
        super(new UAE2D(), inputSizeThreshold);
    }
}
