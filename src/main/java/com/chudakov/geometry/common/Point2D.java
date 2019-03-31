package com.chudakov.geometry.common;

import java.util.Objects;

public class Point2D {
    public final double first;
    public final double second;

    public Point2D(double x, double y) {
        this.first = x;
        this.second = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point2D point2D = (Point2D) o;
        return Double.compare(point2D.first, first) == 0 &&
                Double.compare(point2D.second, second) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "(" + first + ";" + second + ")";
    }
}
