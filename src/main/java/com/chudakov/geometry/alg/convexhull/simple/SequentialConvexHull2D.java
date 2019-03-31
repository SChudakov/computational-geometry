package com.chudakov.geometry.alg.convexhull.simple;

import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.framework.SequentialDaCExecutionSpecifics;

public class SequentialConvexHull2D extends SequentialDaCExecutionSpecifics<Point2D> {
    public SequentialConvexHull2D() {
        super(new ConvexHull2D());
    }
}
