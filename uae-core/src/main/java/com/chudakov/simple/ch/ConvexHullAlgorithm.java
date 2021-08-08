package com.chudakov.simple.ch;

import com.chudakov.uae.impl.Point;

import java.util.List;

public interface ConvexHullAlgorithm {
    List<Point> computeConvexHull(List<Point> points);
}
