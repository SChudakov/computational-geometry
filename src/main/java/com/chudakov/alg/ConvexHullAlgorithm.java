package com.chudakov.alg;

import com.chudakov.geometry.uae.Point;

import java.util.List;

public interface ConvexHullAlgorithm {
    List<Point> computeConvexHull(List<Point> points);
}
