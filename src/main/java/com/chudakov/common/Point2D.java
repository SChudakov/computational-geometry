package com.chudakov.common;

public class Point2D {
    public final double first;
    public final double second;

    public Point2D(double x, double y) {
        this.first = x;
        this.second = y;
    }

    @Override
    public String toString() {
        return "(" + first + ";" + second + ")";
    }
}
