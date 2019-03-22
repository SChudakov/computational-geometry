package com.chudakov;

import com.chudakov.alg.ConvexHull;
import com.chudakov.common.Point2D;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ConvexHull hull = new ConvexHull();
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

        a.sort((x, y) -> {
            if (x.first < y.first || (!(y.first < x.first) && x.second < y.second)) {
                return -1;
            }
            if (x.first == y.first && x.second == y.second) {
                return 0;
            }
            return 1;
        });
//        System.out.println("sorted points: " + a);

        List<Point2D> ans = hull.getConvexHull(a);
        System.out.print("convex hull: " + ans);
    }
}
