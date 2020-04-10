package com.chudakov.geometry.uae;

import com.chudakov.geometry.datastructure.ConvexHull;

import java.util.List;

public class UAEResult {
    ConvexHull convexHull;

    DTEdge e1;
    DTEdge e2;

    public ConvexHull getConvexHull() {
        return convexHull;
    }

    public DTEdge getE1() {
        return e1;
    }

    public DTEdge getE2() {
        return e2;
    }

    public UAEResult(ConvexHull convexHull, DTEdge e1, DTEdge e2) {
        this.convexHull = convexHull;
        this.e1 = e1;
        this.e2 = e2;
    }
}
