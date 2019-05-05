package com.chudakov.geometry.alg.convexhull.simple;

import com.chudakov.geometry.alg.convexhull.BaseConvexHull2DTest;
import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.core.DaCExecutionSpecifics;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConvexHull2DTest extends BaseConvexHull2DTest {

    @Test
    public void tesSequentialConvexHull() {
        testConvexHull1(new SequentialConvexHull2D());
    }

    @Test
    public void tesParallelConvexHull() {
        testConvexHull1(new ParallelConvexHull2D());
    }

    @Test
    public void testOnRandomPoints() {
        Random random = new Random(17);
        testOnRandomPoints(new SequentialConvexHull2D(), random);
        testOnRandomPoints(new ParallelConvexHull2D(), random);
    }

    private void testOnRandomPoints(DaCExecutionSpecifics<List<Point2D>, List<Point2D>> specifics, Random random) {
        for (int i = 0; i < 10; i++) {
            List<Point2D> points = getRandomPoints(10000, random);
            specifics.solve(points);
        }
    }

    private List<Point2D> getRandomPoints(int size, Random random) {
        List<Point2D> points = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            points.add(new Point2D((int) (random.nextDouble() * 1_000_000), (int) (random.nextDouble() * 1_000_000)));
        }
        return points;
    }
}