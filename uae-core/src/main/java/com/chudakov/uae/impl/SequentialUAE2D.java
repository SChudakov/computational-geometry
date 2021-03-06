package com.chudakov.uae.impl;

import com.chudakov.uae.core.SequentialDaCExecutionSpecifics;

import java.util.List;

public class SequentialUAE2D extends SequentialDaCExecutionSpecifics<List<UAEVertex>, UAEResult> {
    public SequentialUAE2D() {
        super(new UAE2D());
    }
}
