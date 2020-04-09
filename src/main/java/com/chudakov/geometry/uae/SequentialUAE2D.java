package com.chudakov.geometry.uae;

import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.core.SequentialDaCExecutionSpecifics;
import com.chudakov.geometry.datastructure.ConvexHull;

import java.util.List;

public class SequentialUAE2D extends SequentialDaCExecutionSpecifics<List<Point2D>, ConvexHull> {
    public SequentialUAE2D() {
        super(new UAE2D());
    }
}
