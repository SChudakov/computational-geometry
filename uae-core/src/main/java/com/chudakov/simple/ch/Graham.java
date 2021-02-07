package com.chudakov.simple.ch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

public class Graham implements ConvexHullAlgorithm{
    private final Stack<Point> hull = new Stack<>();

    @Override
    public List<Point> computeConvexHull(List<Point> pts) {
        impl(pts);

        // form result
        List<Point> s = new ArrayList<>(this.hull.size());
        s.addAll(hull);
        return s;
    }

    private void impl(List<Point> pts) {
        // defensive copy
        int N = pts.size();
        Point[] points = new Point[N];
        for (int i = 0; i < N; i++) {
            points[i] = pts.get(i);
        }
        Arrays.sort(points);

        Arrays.sort(points, 1, N, new PolarOrder(points[0]));

        hull.push(points[0]); // p[0] is first extreme point
        int k1;
        for (k1 = 1; k1 < N; k1++)
            if (!points[0].equals(points[k1]))
                break;
        if (k1 == N)
            return; // all points equal

        int k2;
        for (k2 = k1 + 1; k2 < N; k2++)
            if (ccw(points[0], points[k1], points[k2]) != 0)
                break;
        hull.push(points[k2 - 1]); // points[k2-1] is second extreme point

        for (int i = k2; i < N; i++) {
            Point top = hull.pop();
            while (ccw(hull.peek(), top, points[i]) <= 0) {
                top = hull.pop();
            }
            hull.push(top);
            hull.push(points[i]);
        }
    }

    private static class PolarOrder implements Comparator<Point> {
        private final Point p;
        private final double x;
        private final double y;

        public PolarOrder(Point p) {
            this.p = p;
            this.x = p.x;
            this.y = p.y;
        }

        public int compare(Point q1, Point q2) {
            double dx1 = q1.x - x;
            double dy1 = q1.y - y;
            double dx2 = q2.x - x;
            double dy2 = q2.y - y;

            if (dy1 >= 0 && dy2 < 0)
                return -1; // q1 above; q2 below
            else if (dy2 >= 0 && dy1 < 0)
                return +1; // q1 below; q2 above
            else if (dy1 == 0 && dy2 == 0) { // 3-collinear and horizontal
                if (dx1 >= 0 && dx2 < 0)
                    return -1;
                else if (dx2 >= 0 && dx1 < 0)
                    return +1;
                else
                    return 0;
            } else
                return -ccw(p, q1, q2); // both above or below
        }
    }

    public static int ccw(Point a, Point b, Point c) {
        double area2 = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
        if (area2 < 0)
            return -1;
        else if (area2 > 0)
            return +1;
        else
            return 0;
    }
}