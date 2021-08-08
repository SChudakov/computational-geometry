package com.chudakov.uae.impl;

import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public class Point implements Comparable<Point> {
    private static final double DEFAULT_EPSILON = 1e-6;

    public final double x;
    public final double y;

    public static double getSlope(Point left, Point right) {
        if (right.x - left.x < 0) {
            throw new IllegalArgumentException("invalid positioning of points");
        }
        return (right.y - left.y) / (right.x - left.x);
    }

    public static double distance(Point a, Point b) {
        return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }

    public boolean toleranceEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point UAEVertex = (Point) o;
        return toleranceCompare(UAEVertex.x, x, DEFAULT_EPSILON) == 0 &&
                toleranceCompare(UAEVertex.y, y, DEFAULT_EPSILON) == 0;
    }

    private double toleranceCompare(double a, double b, double eps) {
        if (Math.abs(a - b) < eps) {
            return 0;
        }
        return a - b;
    }

    public Point add(Point other) {
        return new Point(x + other.x, y + other.y);
    }

    public Point subtract(Point other) {
        return new Point(x - other.x, y - other.y);
    }

    public Point mult(double factor) {
        return new Point(x * factor, y * factor);
    }

    public static UAEVertex middle(UAEVertex a, UAEVertex b) {
        double x = (a.x + b.x) / 2;
        double y = (a.y + b.y) / 2;
        return new UAEVertex(x, y);
    }

    public static boolean ccw(Point a, Point b, Point c) {
        double area2 = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
        return !(area2 <= 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point UAEVertex = (Point) o;
        return Double.compare(UAEVertex.x, x) == 0 &&
                Double.compare(UAEVertex.y, y) == 0;
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
