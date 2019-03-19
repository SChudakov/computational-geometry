package com.chudakov;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ConvexHull hull = new ConvexHull();
        List<Point> a = new ArrayList<>();
        a.add(new Point(0, 0));
        a.add(new Point(1, -4));
        a.add(new Point(-1, -5));
        a.add(new Point(-5, -3));
        a.add(new Point(-3, -1));
        a.add(new Point(-1, -3));
        a.add(new Point(-2, -2));
        a.add(new Point(-1, -1));
        a.add(new Point(-2, -1));
        a.add(new Point(-1, 1));

        a.sort((x, y) -> {
            if (x.first < y.first || (!(y.first < x.first) && x.second < y.second)) {
                return -1;
            }
            if (x.first == y.first && x.second == y.second) {
                return 0;
            }
            return 1;
        });
//        for (Point point : a) {
//            System.out.print(point);
//        }
//        System.out.println();
        List<Point> ans = hull.divide(a);
        System.out.print("convex hull: ");
        for (Point an : ans) {
            System.out.print(an);
        }
        System.out.println();
    }
}
