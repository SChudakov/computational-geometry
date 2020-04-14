package com.chudakov.geometry.uae;

public class UAEResult {
    ConvexHull convexHull;

    QuadEdge e1;
    QuadEdge e2;

    public ConvexHull getConvexHull() {
        return convexHull;
    }

    public QuadEdge getE1() {
        return e1;
    }

    public QuadEdge getE2() {
        return e2;
    }

    public UAEResult(ConvexHull convexHull, QuadEdge e1, QuadEdge e2) {
        this.convexHull = convexHull;
        this.e1 = e1;
        this.e2 = e2;
    }
}
