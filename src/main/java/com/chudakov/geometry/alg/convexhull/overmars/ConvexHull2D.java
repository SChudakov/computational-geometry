package com.chudakov.geometry.alg.convexhull.overmars;

import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.framework.DaCAlgorithm;
import com.chudakov.geometry.util.Pair;

import java.util.List;

public class ConvexHull2D implements DaCAlgorithm<Point2D> {
    @Override
    public boolean isBaseCase(List<Point2D> points) {
        int size = points.size();
        return size == 2 || size == 3;
    }

    @Override
    public List<Point2D> solveBaseCase(List<Point2D> points) {
        int size = points.size();

        ConcatenableQueue left;
        ConcatenableQueue right;
        if (size == 2) {
            left = new ConcatenableQueue(points.get(0));
            right = new ConcatenableQueue(points.get(1));

        } else {
            left = new ConcatenableQueue(points.get(0), points.get(1));
            right = new ConcatenableQueue(points.get(2));
        }
        ConvexSubhull leftSubhull = new ConvexSubhull(left, ConvexSubhull.Type.LEFT);
        ConvexSubhull rightSubhull = new ConvexSubhull(right, ConvexSubhull.Type.RIGHT);

        ConvexHull result = new ConvexHull(leftSubhull, rightSubhull);

        return result;
    }

    @Override
    public List<Point2D> merge(List<Point2D> first, List<Point2D> second) {
        return null;
    }

    @Override
    public Pair<List<Point2D>, List<Point2D>> divide(List<Point2D> points) {
        return null;
    }

    @Override
    public void precompute(List<Point2D> points) {

    }
}
