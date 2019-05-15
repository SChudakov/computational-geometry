package com.chudakov.geometry.alg.convexhull.overmars;

import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.core.DaCAlgorithm;
import com.chudakov.geometry.util.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

public class ConvexHull2D implements DaCAlgorithm<List<Point2D>, ConvexHull> {
    @Override
    public boolean isBaseCase(List<Point2D> points) {
        int size = points.size();
        return size == 2 || size == 3;
    }

    @Override
    public int size(List<Point2D> input) {
        return input.size();
    }

    @Override
    public ConvexHull solveBaseCase(List<Point2D> points) {
        int size = points.size();

        ConcatenableQueue<Point2D> upper = new ConcatenableQueue<>();
        ConcatenableQueue<Point2D> lower = new ConcatenableQueue<>();
        if (size == 2) {
            if (points.get(0).y <= points.get(1).y) {
                lower.add(points.get(0));
                upper.add(points.get(1));
            } else {
                lower.add(points.get(1));
                upper.add(points.get(0));
            }
        } else {
            Point2D first = points.get(0);
            Point2D second = points.get(1);
            Point2D third = points.get(2);
            double leftSlope = ConvexHull.computeSlope(first, second);
            double rightSlope = ConvexHull.computeSlope(second, third);
            if (leftSlope < rightSlope) {
                if (first.y < second.y) {
                    upper.add(first);
                    upper.add(third);
                    lower.add(second);
                } else {
                    upper.add(first);
                    upper.add(third);
                    lower.add(second);
                }
            } else if (leftSlope > rightSlope) {
                if (first.y < second.y) {
                    upper.add(second);
                    upper.add(third);
                    lower.add(first);
                } else {
                    upper.add(first);
                    upper.add(second);
                    lower.add(third);
                }
            } else { // leftSlope == rightSlope
                if (first.y < second.y) {
                    upper.add(third);
                    lower.add(first);
                } else {
                    upper.add(first);
                    lower.add(third);
                }
            }
        }

        ConvexSubhull upperSubhull = new ConvexSubhull(upper, ConvexSubhull.Type.UPPER);
        ConvexSubhull lowerSubhull = new ConvexSubhull(lower, ConvexSubhull.Type.LOWER);

        return new ConvexHull(upperSubhull, lowerSubhull);
    }

    @Override
    public ConvexHull merge(ConvexHull left, ConvexHull right) {
        return ConvexHull.join(left, right);
    }

    @Override
    public Pair<List<Point2D>, List<Point2D>> divide(List<Point2D> input) {
        int mid = input.size() / 2;
        return Pair.of(input.subList(0, mid), input.subList(mid, input.size()));
    }

    @Override
    public List<Point2D> precompute(List<Point2D> points) {
        points.sort(new AntiLOPointComparator());
        removeDuplicated(points, Comparator.comparingDouble(p -> p.y));
        points.sort(Point2D::compareTo);
        removeDuplicated(points, Comparator.comparingDouble(p -> p.x));
//        System.out.println("precomputed: " + points);
        return points;
    }

    void removeDuplicated(List<Point2D> points, Comparator<Point2D> comparator) {
        ListIterator<Point2D> it1 = points.listIterator();
        ListIterator<Point2D> it2 = points.listIterator();

        // handle empty input case
        if (!it1.hasNext()) {
            return;
        }
        it1.next();

        while (it2.hasNext()) {
            if (it2.nextIndex() > 0 && it2.nextIndex() < points.size() - 1) {
//                System.out.println(it2.nextIndex());

                Point2D previous = it2.previous();
                it2.next();
                Point2D current = it2.next();
                Point2D next = it2.next();
                it2.previous();

                if (!(comparator.compare(previous, current) == 0 && comparator.compare(current, next) == 0)) {
                    it1.set(current);
                    if (it1.hasNext()) {
                        it1.next();
                    }
                }
            } else if (it2.nextIndex() == 0) {
                // one-element points list case
                if (it1.hasNext()) {
                    it1.next();
                }
                it2.next();
            } else {
                it1.set(it2.next());
            }
        }
        if (it1.hasNext()) {
            int it1NextIndex = it1.nextIndex();
            while (points.size() != it1NextIndex) {
                points.remove(points.size() - 1);
            }
        }
    }

    static class AntiLOPointComparator implements Comparator<Point2D> {

        @Override
        public int compare(Point2D x, Point2D y) {
            if (x.y < y.y || (x.y == y.y && x.x < y.x)) {
                return -1;
            }
            if (x.x == y.x && x.y == y.y) {
                return 0;
            }
            return 1;
        }
    }
}
