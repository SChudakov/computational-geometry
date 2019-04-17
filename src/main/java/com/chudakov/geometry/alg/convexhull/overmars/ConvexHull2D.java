package com.chudakov.geometry.alg.convexhull.overmars;

import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.core.DaCAlgorithm;
import com.chudakov.geometry.util.Pair;

import java.util.Comparator;
import java.util.List;

public class ConvexHull2D implements DaCAlgorithm<List<Point2D>, ConvexHull> {
    @Override
    public boolean isBaseCase(List<Point2D> input) {
        int size = input.size();
        return size == 2 || size == 3;
    }

    @Override
    public ConvexHull solveBaseCase(List<Point2D> input) {
        int size = input.size();

        ConcatenableQueue<Point2D> left;
        ConcatenableQueue<Point2D> right;
        if (size == 2) {
            left = new ConcatenableQueue<>();
            left.add(input.get(0));

            right = new ConcatenableQueue<>();
            right.add(input.get(1));
        } else {
            left = new ConcatenableQueue<>();
            left.add(input.get(0));
            left.add(input.get(1));

            right = new ConcatenableQueue<>();
            right.add(input.get(2));
        }
        ConvexSubhull leftSubhull = new ConvexSubhull(left, ConvexSubhull.Type.LEFT);
        ConvexSubhull rightSubhull = new ConvexSubhull(right, ConvexSubhull.Type.RIGHT);

        return new ConvexHull(leftSubhull, rightSubhull);
    }

    @Override
    public ConvexHull merge(ConvexHull lower, ConvexHull upper) {
        return ConvexHull.join(lower, upper);
    }

    @Override
    public Pair<List<Point2D>, List<Point2D>> divide(List<Point2D> input) {
        int mid = input.size() / 2;
        return Pair.of(input.subList(0, mid), input.subList(mid, input.size()));
    }

    @Override
    public void precompute(List<Point2D> input) {
        input.sort(new AntiLOPointComparator());
    }

    static class AntiLOPointComparator implements Comparator<Point2D> {

        @Override
        public int compare(Point2D x, Point2D y) {
            if (x.second < y.second || (x.second == y.second && x.first < y.first)) {
                return -1;
            }
            if (x.first == y.first && x.second == y.second) {
                return 0;
            }
            return 1;
        }
    }
}
