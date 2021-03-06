package com.chudakov.uae.impl;

import com.chudakov.simple.ch.Point;
import lombok.Getter;

@Getter
public class UAEVertex extends Point {
    QuadEdge edge;

    public UAEVertex(double x, double y) {
        super(x, y);
    }
}
