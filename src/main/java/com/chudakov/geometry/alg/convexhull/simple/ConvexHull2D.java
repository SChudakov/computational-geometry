package com.chudakov.geometry.alg.convexhull.simple;

import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.core.DaCAlgorithm;
import com.chudakov.geometry.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConvexHull2D implements DaCAlgorithm<List<Point2D>, List<Point2D>> {
    @Override
    public boolean isBaseCase(List<Point2D> input) {
        return input.size() <= 5;
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
            if (a.get(i).x > a.get(ia).x) {
                ia = i;
            }
        }

        // ib -> leftmost point of b
        for (int i = 1; i < n2; i++) {
            if (b.get(i).x < b.get(ib).x) {
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
    public Pair<List<Point2D>, List<Point2D>> divide(List<Point2D> input) {
        int mid = input.size() / 2;
        return Pair.of(input.subList(0, mid), input.subList(mid, input.size()));
    }

    @Override
    public List<Point2D> precompute(List<Point2D> input) {
        input.sort(new LexicographicalOrderPointComparator());
        return input;
    }

    @Override
    public List<Point2D> solveBaseCase(List<Point2D> input) {
//        System.out.println("input: " + points);
        Set<Point2D> s = new HashSet<>();

        for (int i = 0; i < input.size(); i++) {
            for (int j = i + 1; j < input.size(); j++) {

                double x1 = input.get(i).x;
                double y1 = input.get(i).y;

                double x2 = input.get(j).x;
                double y2 = input.get(j).y;

                double a1 = y1 - y2;
                double b1 = x2 - x1;
                double c1 = x1 * y2 - y1 * x2;
                int pos = 0, neg = 0;
                for (Point2D point : input) {
                    double v = a1 * point.x + b1 * point.y + c1;
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
                if (pos == input.size() || neg == input.size()) {
                    s.add(input.get(i));
                    s.add(input.get(j));
                }
            }
        }

        List<Point2D> result = new ArrayList<>();
        int n = s.size();

        double midFirst = 0.0;
        double midSecond = 0.0;
        for (Point2D p : s) {
            midFirst += p.x;
            midSecond += p.y;

            result.add(new Point2D(p.x * n, p.y * n));
        }

        Point2D mid = new Point2D(midFirst, midSecond);
        result.sort(new AntiClockwiseOrderPointComparator(mid));

        for (int i = 0; i < n; i++) {
            result.set(i, new Point2D(result.get(i).x / n, result.get(i).y / n));
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
        double result = (b.y - a.y) * (c.x - b.x) -
                (c.y - b.y) * (b.x - a.x);

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
            if (x.x < y.x || (x.x == y.x && x.y < y.y)) {
                return -1;
            }
            if (x.x == y.x && x.y == y.y) {
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
            double x1 = p1.x - mid.x;
            double y1 = p1.y - mid.y;

            double x2 = q1.x - mid.x;
            double y2 = q1.y - mid.y;

            int one = quad(x1, y1);
            int two = quad(x2, y2);

            if (one != two) {
                return Integer.compare(one, two);
            }
            return Double.compare(y1 * x2, x1 * y2);
        }
    }
}
