package com.chudakov.geometry.alg.convexhull.overmars;

import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.core.SequentialDaCExecutionSpecifics;
import com.chudakov.geometry.datastructure.ConvexHull;

import java.util.List;

public class SequentialConvexHull2D extends SequentialDaCExecutionSpecifics<List<Point2D>, ConvexHull> {
    public SequentialConvexHull2D() {
        super(new ConvexHull2D());
    }
}
