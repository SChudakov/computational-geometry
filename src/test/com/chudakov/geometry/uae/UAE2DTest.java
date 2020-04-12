package com.chudakov.geometry.uae;

import com.chudakov.geometry.TestUtils;
import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.core.DaCExecutionSpecifics;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UAE2DTest {

    private static final String BASE = "./src/test/resources/convexhull/10/";
    private static final String TEST_CASES_INPUT_DIR = BASE + "input/";
    private static final String TEST_CASES_OUTPUT_DIR = BASE + "output/";

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

        ConvexHull hull = new SequentialUAE2D().solve(points).convexHull;

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


        ConvexHull hull = new SequentialUAE2D().solve(points).convexHull;

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

        ConvexHull hull = new SequentialUAE2D().solve(points).convexHull;

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

        ConvexHull hull = new SequentialUAE2D().solve(points).convexHull;

        assertEqual(expected, hull);
    }


    @Test
    public void testRemoveDuplicated() {
        List<Point2D> points1 = new ArrayList<>();
        UAE2D.removeDuplicated(points1, Point2D::compareTo);
        assertTrue(points1.isEmpty());

        List<Point2D> points2 = new ArrayList<>();
        points2.add(new Point2D(1, 1));
        UAE2D.removeDuplicated(points2, Point2D::compareTo);
        assertEquals(Collections.singletonList(
                new Point2D(1, 1)
        ), points2);

        List<Point2D> points3 = new ArrayList<>();
        points3.add(new Point2D(1, 1));
        points3.add(new Point2D(1, 1));
        UAE2D.removeDuplicated(points3, Point2D::compareTo);
        assertEquals(Arrays.asList(
                new Point2D(1, 1),
                new Point2D(1, 1)
        ), points3);

        List<Point2D> points4 = new ArrayList<>();
        points4.add(new Point2D(1, 1));
        points4.add(new Point2D(1, 2));
        UAE2D.removeDuplicated(points4, Point2D::compareTo);
        assertEquals(Arrays.asList(
                new Point2D(1, 1),
                new Point2D(1, 2)
        ), points4);


        List<Point2D> points5 = new ArrayList<>();
        points5.add(new Point2D(1, 1));
        points5.add(new Point2D(1, 1));
        points5.add(new Point2D(1, 1));
        UAE2D.removeDuplicated(points5, Point2D::compareTo);
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
        UAE2D.removeDuplicated(points6, Point2D::compareTo);
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
        UAE2D.removeDuplicated(points7, Point2D::compareTo);
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
    public void tesSequentialConvexHull() {
        testConvexHull2(new SequentialUAE2D());
    }

    @Test
    public void tesParallelConvexHull() {
        testConvexHull2(new ParallelUAE2D());
    }

    private void testConvexHull2(DaCExecutionSpecifics<List<Point2D>, UAEResult> specifics) {
        List<List<Point2D>> inputs = TestUtils.readPointsDir(TEST_CASES_INPUT_DIR);
        List<List<Point2D>> expectedOutputs = TestUtils.readPointsDir(TEST_CASES_OUTPUT_DIR);

        for (int i = 0; i < inputs.size(); ++i) {
            List<Point2D> input = inputs.get(i);
            List<Point2D> expectedOutput = expectedOutputs.get(i);

            ConvexHull actualOutput = specifics.solve(input).convexHull;
            assertEqual(expectedOutput, actualOutput);
        }
    }

    private void assertEqual(List<Point2D> expected, ConvexHull actual) {
        Set<Point2D> expectedPoints = new HashSet<>(expected);
        List<Point2D> actualList = new ArrayList<>();
        for (Point2D point : actual) {
            actualList.add(point);
        }

        expected.sort(Point2D::compareTo);

        Set<Point2D> actualPoints = new HashSet<>(actualList);

        assertEquals(expectedPoints, actualPoints);
    }
}