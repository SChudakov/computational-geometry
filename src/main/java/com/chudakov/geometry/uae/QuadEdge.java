package com.chudakov.geometry.uae;

import com.chudakov.geometry.common.Point2D;

public class QuadEdge {
    Point2D org;
    Point2D dest;

    QuadEdge onext;
    QuadEdge oprev;

    QuadEdge sym;

    Object data;

    public QuadEdge(Point2D org, Point2D dest) {
        this.org = org;
        this.dest = dest;
    }

    @Override
    public String toString() {
        return "QuadEdge{" +
                "org=" + org +
                ", dest=" + dest +
                ", data=" + data +
                '}';
    }
}
