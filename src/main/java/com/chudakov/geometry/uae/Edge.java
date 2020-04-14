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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        if (!org.equals(edge.org)) return false;
        return dest.equals(edge.dest);
    }

    @Override
    public int hashCode() {
        int result = org.hashCode();
        result = 31 * result + dest.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "[" + org + " - " + dest + ']';
    }
}
