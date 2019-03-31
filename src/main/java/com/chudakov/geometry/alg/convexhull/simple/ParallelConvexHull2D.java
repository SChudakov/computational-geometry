package com.chudakov.geometry.alg.convexhull.simple;

import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.framework.ParallelDaCExecutionSpecifics;

public class ParallelConvexHull2D extends ParallelDaCExecutionSpecifics<Point2D> {
    public ParallelConvexHull2D() {
        super(new ConvexHull2D());
    }
}
