package com.chudakov.geometry.alg.convexhull.overmars;

import com.chudakov.geometry.alg.convexhull.BaseConvexHull2DTest;
import com.chudakov.geometry.common.Point2D;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConvexHull2DTest extends BaseConvexHull2DTest {
    @Test
    public void testSimple1() {
        List<Point2D> points = new ArrayList<>(8);
        points.add(new Point2D(1, 2));
        points.add(new Point2D(2, 5));
        points.add(new Point2D(3, 1));
        points.add(new Point2D(5, 7));
        points.add(new Point2D(9, 8));
        points.add(new Point2D(12, 1));
        points.add(new Point2D(13, 3));

        ConvexHull hull = new SequentialConvexHull2D().solve(points);

        assertEqual(points, hull);
    }

    @Test
    public void testSimple2() {
        List<Point2D> points = new ArrayList<>(12);
        points.add(new Point2D(2, 4));
        points.add(new Point2D(4, 4));
        points.add(new Point2D(6, 4));
        points.add(new Point2D(8, 4));
        points.add(new Point2D(2, 6));
        points.add(new Point2D(8, 6));
        points.add(new Point2D(2, 8));
        points.add(new Point2D(8, 8));
        points.add(new Point2D(2, 10));
        points.add(new Point2D(4, 10));
        points.add(new Point2D(6, 10));
        points.add(new Point2D(8, 10));


        List<Point2D> expected = new ArrayList<>(4);
        expected.add(new Point2D(2, 4));
        expected.add(new Point2D(8, 4));
        expected.add(new Point2D(2, 10));
        expected.add(new Point2D(8, 10));


        ConvexHull hull = new SequentialConvexHull2D().solve(points);

        assertEqual(expected, hull);
    }

    @Test
    public void testSimple3() {
        List<Point2D> points = new ArrayList<>(12);
        points.add(new Point2D(1, 1));
        points.add(new Point2D(2, 1));
        points.add(new Point2D(3, 1));
        points.add(new Point2D(4, 1));
        points.add(new Point2D(5, 1));
        points.add(new Point2D(6, 1));
        points.add(new Point2D(7, 1));
        points.add(new Point2D(8, 1));


        List<Point2D> expected = new ArrayList<>(2);
        expected.add(new Point2D(1, 1));
        expected.add(new Point2D(8, 1));

        ConvexHull hull = new SequentialConvexHull2D().solve(points);

        assertEqual(expected, hull);
    }

    @Test
    public void testSimple4() {
        List<Point2D> points = new ArrayList<>(12);
        points.add(new Point2D(1, 1));
        points.add(new Point2D(1, 2));
        points.add(new Point2D(1, 3));
        points.add(new Point2D(1, 4));
        points.add(new Point2D(1, 5));
        points.add(new Point2D(1, 6));
        points.add(new Point2D(1, 7));
        points.add(new Point2D(1, 8));


        List<Point2D> expected = new ArrayList<>(2);
        expected.add(new Point2D(1, 1));
        expected.add(new Point2D(1, 8));

        ConvexHull hull = new SequentialConvexHull2D().solve(points);

        assertEqual(expected, hull);
    }


    @Test
    public void testRemoveDuplicated() {
        ConvexHull2D convexHull = new ConvexHull2D();

        List<Point2D> points1 = new ArrayList<>();
        convexHull.removeDuplicated(points1, Point2D::compareTo);
        assertTrue(points1.isEmpty());

        List<Point2D> points2 = new ArrayList<>();
        points2.add(new Point2D(1, 1));
        convexHull.removeDuplicated(points2, Point2D::compareTo);
        assertEquals(Collections.singletonList(
                new Point2D(1, 1)
        ), points2);

        List<Point2D> points3 = new ArrayList<>();
        points3.add(new Point2D(1, 1));
        points3.add(new Point2D(1, 1));
        convexHull.removeDuplicated(points3, Point2D::compareTo);
        assertEquals(Arrays.asList(
                new Point2D(1, 1),
                new Point2D(1, 1)
        ), points3);

        List<Point2D> points4 = new ArrayList<>();
        points4.add(new Point2D(1, 1));
        points4.add(new Point2D(1, 2));
        convexHull.removeDuplicated(points4, Point2D::compareTo);
        assertEquals(Arrays.asList(
                new Point2D(1, 1),
                new Point2D(1, 2)
        ), points4);


        List<Point2D> points5 = new ArrayList<>();
        points5.add(new Point2D(1, 1));
        points5.add(new Point2D(1, 1));
        points5.add(new Point2D(1, 1));
        convexHull.removeDuplicated(points5, Point2D::compareTo);
        assertEquals(Arrays.asList(
                new Point2D(1, 1),
                new Point2D(1, 1)
        ), points5);


        List<Point2D> points6 = new ArrayList<>();
        points6.add(new Point2D(1, 1));
        points6.add(new Point2D(1, 1));
        points6.add(new Point2D(1, 1));
        points6.add(new Point2D(2, 2));
        points6.add(new Point2D(2, 2));
        points6.add(new Point2D(2, 2));
        points6.add(new Point2D(3, 3));
        points6.add(new Point2D(3, 3));
        points6.add(new Point2D(4, 4));
        convexHull.removeDuplicated(points6, Point2D::compareTo);
        assertEquals(Arrays.asList(
                new Point2D(1, 1),
                new Point2D(1, 1),
                new Point2D(2, 2),
                new Point2D(2, 2),
                new Point2D(3, 3),
                new Point2D(3, 3),
                new Point2D(4, 4)
        ), points6);


        List<Point2D> points7 = new ArrayList<>();
        points7.add(new Point2D(1, 1));
        points7.add(new Point2D(2, 2));
        points7.add(new Point2D(3, 3));
        points7.add(new Point2D(3, 3));
        points7.add(new Point2D(3, 3));
        points7.add(new Point2D(4, 4));
        points7.add(new Point2D(5, 5));
        points7.add(new Point2D(5, 5));
        points7.add(new Point2D(5, 5));
        convexHull.removeDuplicated(points7, Point2D::compareTo);
        assertEquals(Arrays.asList(
                new Point2D(1, 1),
                new Point2D(2, 2),
                new Point2D(3, 3),
                new Point2D(3, 3),
                new Point2D(4, 4),
                new Point2D(5, 5),
                new Point2D(5, 5)
        ), points7);
    }

    @Test
    public void testPrecompute() {
        List<Point2D> points = new ArrayList<>();
        points.add(new Point2D(1, 7));
        points.add(new Point2D(1, 8));
        points.add(new Point2D(1, 9));
        points.add(new Point2D(4, 8));
        points.add(new Point2D(6, 2));
        points.add(new Point2D(7, 9));
        points.add(new Point2D(8, 5));
        points.add(new Point2D(8, 7));
        points.add(new Point2D(8, 8));
        points.add(new Point2D(9, 0));


        points.add(new Point2D(1, 7));
        points.add(new Point2D(1, 9));
        points.add(new Point2D(4, 8));
        points.add(new Point2D(7, 9));


    }

    @Test
    public void tesSequentialConvexHull() {
        testConvexHull2(new SequentialConvexHull2D());
    }

    @Test
    public void tesParallelConvexHull() {
        testConvexHull2(new ParallelConvexHull2D());
    }
}