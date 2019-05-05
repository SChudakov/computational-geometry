package com.chudakov.geometry.alg.convexhull.simple;

import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.core.SequentialDaCExecutionSpecifics;

import java.util.List;

public class SequentialConvexHull2D extends SequentialDaCExecutionSpecifics<List<Point2D>, List<Point2D>> {
    public SequentialConvexHull2D() {
        super(new ConvexHull2D());
    }
}