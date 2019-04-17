package com.chudakov.geometry.alg.convexhull.simple;

import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.core.ParallelDaCExecutionSpecifics;

import java.util.List;

public class ParallelConvexHull2D extends ParallelDaCExecutionSpecifics<List<Point2D>, List<Point2D>> {
    public ParallelConvexHull2D() {
        super(new ConvexHull2D());
    }
}
