package com.chudakov.uae.impl;

import lombok.Getter;

@Getter
public class UAEVertex extends Point {
    QuadEdge edge;

    public UAEVertex(double x, double y) {
        super(x, y);
    }
}
