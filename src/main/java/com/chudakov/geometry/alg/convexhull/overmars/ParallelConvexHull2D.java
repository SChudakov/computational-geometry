package com.chudakov.geometry.alg.convexhull.overmars;

import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.core.ParallelDaCExecutionSpecifics;

import java.util.List;

public class ParallelConvexHull2D extends ParallelDaCExecutionSpecifics<List<Point2D>, ConvexHull> {
    public ParallelConvexHull2D() {
        super(new ConvexHull2D());
    }

    public ParallelConvexHull2D(int inputSizeThreshold) {
        super(new ConvexHull2D(), inputSizeThreshold);
    }
}
