package com.chudakov.geometry.uae;

import java.util.Objects;

public class Vertex2D implements Comparable<Vertex2D> {
    public final double x;
    public final double y;

    QuadEdge edge;

    public Vertex2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static double getSlope(Vertex2D left, Vertex2D right) {
        if (right.x - left.x < 0) {
            throw new IllegalArgumentException("invalid positioning of points");
        }
        return (right.y - left.y) / (right.x - left.x);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex2D vertex2D = (Vertex2D) o;
        return Double.compare(vertex2D.x, x) == 0 &&
                Double.compare(vertex2D.y, y) == 0;
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
    public int compareTo(Vertex2D p) {
        if (x < p.x || (x == p.x && y < p.y)) {
            return -1;
        }
        if (x == p.x && y == p.y) {
            return 0;
        }
        return 1;
    }
}
