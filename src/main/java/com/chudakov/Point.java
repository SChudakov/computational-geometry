package com.chudakov;

public class Point {
    final double first;
    final double second;

    Point(double x, double y) {
        this.first = x;
        this.second = y;
    }

    @Override
    public String toString() {
        return "(" + first + ";" + second + ")";
    }
}
