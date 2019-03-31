package com.chudakov.geometry.alg.convexhull.simple;

import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.framework.DaCAlgorithm;
import com.chudakov.geometry.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConvexHull2D implements DaCAlgorithm<Point2D> {
    @Override
    public boolean isBaseCase(List<Point2D> points) {
        return points.size() <= 5;
    }

    @Override
    public List<Point2D> merge(List<Point2D> a, List<Point2D> b) {
        int n1 = a.size();
        int n2 = b.size();

        if (n1 == 0 || n2 == 0) {
            throw new RuntimeException("kek");
        }

        int ia = 0;
        int ib = 0;
        // ia -> rightmost point of b
        for (int i = 1; i < n1; i++) {
            if (a.get(i).first > a.get(ia).first) {
                ia = i;
            }
        }

        // ib -> leftmost point of b
        for (int i = 1; i < n2; i++) {
            if (b.get(i).first < b.get(ib).first) {
                ib = i;
            }
        }


        // finding the upper tangent
        int inda = ia;
        int indb = ib;
        boolean done = false;
        while (!done) {
            done = true;
            while (orientation(b.get(indb), a.get(inda), a.get((inda + 1) % n1)) >= 0) {
                inda = (inda + 1) % n1;
            }

            while (orientation(a.get(inda), b.get(indb), b.get((n2 + indb - 1) % n2)) <= 0) {
                indb = (n2 + indb - 1) % n2;
                done = false;
            }
        }

        int uppera = inda;
        int upperb = indb;
        inda = ia;
        indb = ib;
        done = false;
        while (!done) {//finding the lower tangent
            done = true;
            while (orientation(a.get(inda), b.get(indb), b.get((indb + 1) % n2)) >= 0) {
                indb = (indb + 1) % n2;
            }
            while (orientation(b.get(indb), a.get(inda), a.get((n1 + inda - 1) % n1)) <= 0) {
                inda = (n1 + inda - 1) % n1;
                done = false;
            }
        }

        int lowera = inda;
        int lowerb = indb;
        List<Point2D> result = new ArrayList<>();

        //ret contains the convex hull after merging the two convex hulls
        //with the points sorted in anti-clockwise order


        int ind = uppera;
        result.add(a.get(uppera));
        while (ind != lowera) {
            ind = (ind + 1) % n1;
            result.add(a.get(ind));
        }

        ind = lowerb;
        result.add(b.get(lowerb));
        while (ind != upperb) {
            ind = (ind + 1) % n2;
            result.add(b.get(ind));
        }
        return result;

    }

    @Override
    public Pair<List<Point2D>, List<Point2D>> divide(List<Point2D> points) {
        int mid = points.size() / 2;
        return Pair.of(points.subList(0, mid), points.subList(mid, points.size()));
    }

    @Override
    public void precompute(List<Point2D> points) {
        points.sort(new LexicographicalOrderPointComparator());
    }

    @Override
    public List<Point2D> solveBaseCase(List<Point2D> points) {
//        System.out.println("input: " + points);
        Set<Point2D> s = new HashSet<>();

        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {

                double x1 = points.get(i).first;
                double y1 = points.get(i).second;

                double x2 = points.get(j).first;
                double y2 = points.get(j).second;

                double a1 = y1 - y2;
                double b1 = x2 - x1;
                double c1 = x1 * y2 - y1 * x2;
                int pos = 0, neg = 0;
                for (Point2D point : points) {
                    double v = a1 * point.first + b1 * point.second + c1;
                    if (Math.abs(v) < 1e-10) {
                        v = 0.0;
                    }
                    if (v <= 0) {
                        neg++;
                    }
                    if (v >= 0) {
                        pos++;
                    }
                }
                if (pos == points.size() || neg == points.size()) {
                    s.add(points.get(i));
                    s.add(points.get(j));
                }
            }
        }

        List<Point2D> result = new ArrayList<>();
        int n = s.size();

        double midFirst = 0.0;
        double midSecond = 0.0;
        for (Point2D p : s) {
            midFirst += p.first;
            midSecond += p.second;

            result.add(new Point2D(p.first * n, p.second * n));
        }

        Point2D mid = new Point2D(midFirst, midSecond);
        result.sort(new AntiClockwiseOrderPointComparator(mid));

        for (int i = 0; i < n; i++) {
            result.set(i, new Point2D(result.get(i).first / n, result.get(i).second / n));
        }
//        System.out.println("output: " + result);
        return result;
    }


    private static int quad(double x, double y) {
        if (x >= 0 && y >= 0) {
            return 1;
        }
        if (x <= 0 && y >= 0) {
            return 2;
        }
        if (x <= 0 && y <= 0) {
            return 3;
        }
        return 4;
    }

    private static int orientation(Point2D a, Point2D b, Point2D c) {
        double result = (b.second - a.second) * (c.first - b.first) -
                (c.second - b.second) * (b.first - a.first);

        if (result == 0) {
            return 0;
        }
        if (result > 0) {
            return 1;
        }
        return -1;
    }


    static class LexicographicalOrderPointComparator implements Comparator<Point2D> {

        @Override
        public int compare(Point2D x, Point2D y) {
            if (x.first < y.first || (x.first == y.first && x.second < y.second)) {
                return -1;
            }
            if (x.first == y.first && x.second == y.second) {
                return 0;
            }
            return 1;
        }
    }

    static class AntiClockwiseOrderPointComparator implements Comparator<Point2D> {
        private final Point2D mid;

        AntiClockwiseOrderPointComparator(Point2D mid) {
            this.mid = mid;
        }

        @Override
        public int compare(Point2D p1, Point2D q1) {
            double x1 = p1.first - mid.first;
            double y1 = p1.second - mid.second;

            double x2 = q1.first - mid.first;
            double y2 = q1.second - mid.second;

            int one = quad(x1, y1);
            int two = quad(x2, y2);

            if (one != two) {
                return Integer.compare(one, two);
            }
            return Double.compare(y1 * x2, x1 * y2);
        }
    }
}
