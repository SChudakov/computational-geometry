package com.chudakov.alg;

import com.chudakov.geometry.uae.Point;

import java.util.ArrayList;
import java.util.List;

public class Jarvis implements ConvexHullAlgorithm {
    public List<Point> computeConvexHull(List<Point> points) {
        int n = points.size();
        /** if less than 3 points return **/

        List<Point> next = new ArrayList<>();

        /** find the leftmost point **/
        int leftMost = 0;
        for (int i = 1; i < n; i++) {
            if (points.get(i).x < points.get(leftMost).x) {
                leftMost = i;
            }
        }

        int p = leftMost, q;

        next.add(points.get(p));
        /** iterate till p becomes leftMost **/
        do {
            /** wrapping **/
            q = (p + 1) % n;
            for (int i = 0; i < n; i++) {
                if (CCW(points.get(p), points.get(i), points.get(q))) {
                    q = i;
                }
            }

            next.add(points.get(q));
            p = q;
        } while (p != leftMost);

        /** Display result **/
        return next;
    }

    private static boolean CCW(Point p, Point q, Point r) {
        double val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
        return !(val >= 0);
    }
}
