package com.chudakov.geometry.uae;

import com.chudakov.geometry.common.Point2D;

import java.util.Objects;

public class DTEdge {
    Point2D org;
    Point2D dest;

    DTEdge onext;
    DTEdge oprev;

    DTEdge sym;

    Object data;

    public DTEdge(Point2D org, Point2D dest) {
        this.org = org;
        this.dest = dest;
    }
}
