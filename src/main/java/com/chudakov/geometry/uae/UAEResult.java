package com.chudakov.geometry.uae;

import com.chudakov.geometry.datastructure.ConvexHull;

import java.util.List;

public class UAEResult {
    ConvexHull convexHull;

    UAEEdge e1;
    UAEEdge e2;

    List<UAEEdge> edges;

    public ConvexHull getConvexHull() {
        return convexHull;
    }

    public List<UAEEdge> getEdges() {
        return edges;
    }

    public UAEEdge getE1() {
        return e1;
    }

    public UAEEdge getE2() {
        return e2;
    }

    public UAEResult(ConvexHull convexHull, UAEEdge e1, UAEEdge e2, List<UAEEdge> edges) {
        this.convexHull = convexHull;
        this.e1 = e1;
        this.e2 = e2;
        this.edges = edges;
    }
}
