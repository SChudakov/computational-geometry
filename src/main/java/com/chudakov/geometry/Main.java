package com.chudakov.geometry;

import com.chudakov.geometry.alg.convexhull.simple.ParallelConvexHull2D;
import com.chudakov.geometry.common.Point2D;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Point2D> a = new ArrayList<>();
        a.add(new Point2D(0, 0));
        a.add(new Point2D(1, -4));
        a.add(new Point2D(-1, -5));
        a.add(new Point2D(-5, -3));
        a.add(new Point2D(-3, -1));
        a.add(new Point2D(-1, -3));
        a.add(new Point2D(-2, -2));
        a.add(new Point2D(-1, -1));
        a.add(new Point2D(-2, -1));
        a.add(new Point2D(-1, 1));

        ParallelConvexHull2D convexHull = new ParallelConvexHull2D();
        List<Point2D> ans = convexHull.solve(a);

        System.out.print("convex hull: " + ans);
    }
}
