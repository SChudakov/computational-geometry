package com.chudakov.geometry.alg.convexhull.overmars;

import com.chudakov.geometry.common.Point2D;

public class ConvexSubhull {
    public enum Type {UPPER, LOWER}

    final ConcatenableQueue<Point2D> subhull;
    final Type type;

    public ConvexSubhull(ConcatenableQueue<Point2D> subhull, Type type) {
        this.subhull = subhull;
        this.type = type;
    }
}
