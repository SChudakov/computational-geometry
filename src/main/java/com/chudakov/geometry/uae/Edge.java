package com.chudakov.geometry.uae;

import com.chudakov.geometry.common.Point2D;

public class Edge {
    Point2D org;
    Point2D dest;

    public Point2D getOrg() {
        return org;
    }

    public Point2D getDest() {
        return dest;
    }

    public Edge(Point2D org, Point2D dest) {
        this.org = org;
        this.dest = dest;
    }
}
