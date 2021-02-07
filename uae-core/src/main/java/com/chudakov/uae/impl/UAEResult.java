package com.chudakov.uae.impl;

public class UAEResult {
    ConvexHull convexHull;

    QuadEdge e1;
    QuadEdge e2;

    public ConvexHull getConvexHull() {
        return convexHull;
    }

    public UAEResult(ConvexHull convexHull, QuadEdge e1, QuadEdge e2) {
        this.convexHull = convexHull;
        this.e1 = e1;
        this.e2 = e2;
    }
}
