package com.chudakov.simple.ch;

import com.chudakov.uae.impl.UAEVertex;
import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public class Point implements Comparable<Point> {
    private static final double DEFAULT_EPSILON = 1e-6;

    public final double x;
    public final double y;

    public static double getSlope(UAEVertex left, UAEVertex right) {
        if (right.x - left.x < 0) {
            throw new IllegalArgumentException("invalid positioning of points");
        }
        return (right.y - left.y) / (right.x - left.x);
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
