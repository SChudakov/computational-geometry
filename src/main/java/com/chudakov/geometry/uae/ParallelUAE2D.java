package com.chudakov.geometry.uae;

import com.chudakov.geometry.core.ParallelDaCExecutionSpecifics;

import java.util.List;

public class ParallelUAE2D extends ParallelDaCExecutionSpecifics<List<Vertex2D>, UAEResult> {
    public ParallelUAE2D() {
        super(new UAE2D());
    }

    public ParallelUAE2D(int inputSizeThreshold) {
        super(new UAE2D(), inputSizeThreshold);
    }
}
