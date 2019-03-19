package com.chudakov;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConvexHull {

    private int quad(Point p) {
        if (p.first >= 0 && p.second >= 0)
            return 1;
        if (p.first <= 0 && p.second >= 0)
            return 2;
        if (p.first <= 0 && p.second <= 0)
            return 3;
        return 4;
    }

    private int orientation(Point a, Point b, Point c) {
        double res = (b.second - a.second) * (c.first - b.first) -
                (c.second - b.second) * (b.first - a.first);

        if (res == 0)
            return 0;
        if (res > 0)
            return 1;
        return -1;
    }

    private List<Point> merger(List<Point> a, List<Point> b) {
        // n1 -> number of points in polygon a 
        // n2 -> number of points in polygon b 
        int n1 = a.size(), n2 = b.size();

        int ia = 0, ib = 0;
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
        int inda = ia, indb = ib;
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

        int uppera = inda, upperb = indb;
        inda = ia;
        indb = ib;
        done = false;
        int g = 0;
        while (!done)//finding the lower tangent 
        {
            done = true;
            while (orientation(a.get(inda), b.get(indb), b.get((indb + 1) % n2)) >= 0) {
                indb = (indb + 1) % n2;
            }
            while (orientation(b.get(indb), a.get(inda), a.get((n1 + inda - 1) % n1)) <= 0) {
                inda = (n1 + inda - 1) % n1;
                done = false;
            }
        }

        int lowera = inda, lowerb = indb;
        List<Point> ret = new ArrayList<>();

        //ret contains the convex hull after merging the two convex hulls 
        //with the points sorted in anti-clockwise order 
        int ind = uppera;
        ret.add(a.get(uppera));
        while (ind != lowera) {
            ind = (ind + 1) % n1;
            ret.add(a.get(ind));
        }

        ind = lowerb;
        ret.add(b.get(lowerb));
        while (ind != upperb) {
            ind = (ind + 1) % n2;
            ret.add(b.get(ind));
        }
        return ret;

    }

    private List<Point> bruteHull(List<Point> a) {
        System.out.print("input: ");
        for (Point point : a) {
            System.out.print(point);
        }
        System.out.println();
        // Take any pair of points from the set and check 
        // whether it is the edge of the convex hull or not. 
        // if all the remaining points are on the same side 
        // of the line then the line is the edge of convex 
        // hull otherwise not 
        Set<Point> s = new HashSet<>();

        for (int i = 0; i < a.size(); i++) {
            for (int j = i + 1; j < a.size(); j++) {
                double x1 = a.get(i).first;
                double y1 = a.get(i).second;
                double x2 = a.get(j).first;
                double y2 = a.get(j).second;

                double a1 = y1 - y2;
                double b1 = x2 - x1;
                double c1 = x1 * y2 - y1 * x2;
                int pos = 0, neg = 0;
                for (Point point : a) {
                    double v = a1 * point.first + b1 * point.second + c1;
                    if (v <= 0) {
                        neg++;
                    }
                    if (v >= 0) {
                        pos++;
                    }
                }
                if (pos == a.size() || neg == a.size()) {
                    s.add(a.get(i));
                    s.add(a.get(j));
                }
            }
        }

        List<Point> ret = new ArrayList<>();
        int n = s.size();
        double midFirst = 0;
        double midSecond = 0;
        for (Point p : s) {
            midFirst += p.first;
            midSecond += p.second;
            ret.add(new Point(p.first * n, p.second * n));
        }
        Point mid = new Point(midFirst, midSecond);

        ret.sort((o1, o2) -> {
            Point p = new Point(o1.first - mid.first, o1.second - mid.second);
            Point q = new Point(o2.first - mid.first, o2.second - mid.second);

            int one = quad(p);
            int two = quad(q);

            if (one != two) {
                return one < two ? 1 : 0;
            }
            return p.second * q.first < q.second * p.first ? 1 : 0;
        });
        for (int i = 0; i < n; i++) {
            ret.set(i, new Point(ret.get(i).first / n, ret.get(i).second / n));
        }

        System.out.print("output: ");
        for (Point point : ret) {
            System.out.print(point);
        }
        System.out.println();
        return ret;
    }

    public List<Point> divide(List<Point> a) {
        // If the number of points is less than 6 then the 
        // function uses the brute algorithm to find the 
        // convex hull 
        if (a.size() <= 5)
            return bruteHull(a);

        // left contains the left half points 
        // right contains the right half points 
        List<Point> left = new ArrayList<>();
        List<Point> right = new ArrayList<>();
        for (int i = 0; i < a.size() / 2; i++) {
            left.add(a.get(i));
        }
        for (int i = a.size() / 2; i < a.size(); i++) {
            right.add(a.get(i));
        }
        // convex hull for the left and right sets 
        List<Point> left_hull = divide(left);
        List<Point> right_hull = divide(right);

        // merging the convex hulls 
        return merger(left_hull, right_hull);
    }
}
