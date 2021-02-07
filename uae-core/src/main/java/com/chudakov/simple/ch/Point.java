package com.chudakov.simple.ch;

import com.chudakov.uae.impl.Vertex;

import java.util.Objects;

public class Point implements Comparable<Point> {
    public final double x;
    public final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static double getSlope(Vertex left, Vertex right) {
        if (right.x - left.x < 0) {
            throw new IllegalArgumentException("invalid positioning of points");
        }
        return (right.y - left.y) / (right.x - left.x);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return Double.compare(vertex.x, x) == 0 &&
                Double.compare(vertex.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ";" + y + ")";
    }

    @Override
    public int compareTo(Point p) {
        if (x < p.x || (x == p.x && y < p.y)) {
            return -1;
        }
        if (x == p.x && y == p.y) {
            return 0;
        }
        return 1;
    }
}
