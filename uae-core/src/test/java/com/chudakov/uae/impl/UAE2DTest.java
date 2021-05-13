package com.chudakov.uae.impl;

import com.chudakov.simple.ch.Point;
import com.chudakov.uae.PointsGenerator;
import com.chudakov.uae.TestUtils;
import com.chudakov.uae.core.DaCExecutionSpecifics;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class UAE2DTest {

    @Test
    public void testCH1() {
        double[][] input = new double[][]{{1, 2}, {2, 5}, {3, 1}, {5, 7}, {9, 8}, {12, 1}, {13, 3}};
        List<UAEVertex> points = readPoints(input);

        ConvexHull hull = new SequentialUAE2D().solve(points).convexHull;

        assertEqualCH(points, hull);
    }

    @Test
    public void testCH2() {
        double[][] input = new double[][]{
                {2, 4}, {4, 4}, {6, 4}, {8, 4},
                {2, 6}, {8, 6},
                {2, 8}, {8, 8},
                {2, 10}, {4, 10}, {6, 10}, {8, 10}};
        double[][] output = new double[][]{{2, 4}, {8, 4}, {2, 10}, {8, 10}};

        List<UAEVertex> points = readPoints(input);
        List<UAEVertex> expected = readPoints(output);

        ConvexHull hull = new SequentialUAE2D().solve(points).convexHull;

        assertEqualCH(expected, hull);
    }

    @Test
    public void testCH3() {
        double[][] input = new double[][]{{1, 1}, {2, 1}, {3, 1}, {4, 1}, {5, 1}, {6, 1}, {7, 1}, {8, 1}};
        double[][] output = new double[][]{{1, 1}, {8, 1}};
        List<UAEVertex> points = readPoints(input);
        List<UAEVertex> expected = readPoints(output);

        ConvexHull hull = new SequentialUAE2D().solve(points).convexHull;

        assertEqualCH(expected, hull);
    }

    @Test
    public void testCH4() {
        double[][] input = new double[][]{{1, 1}, {1, 2}, {1, 3}, {1, 4}, {1, 5}, {1, 6}, {1, 7}, {1, 8}};
        double[][] output = new double[][]{{1, 1}, {1, 8}};
        List<UAEVertex> points = readPoints(input);
        List<UAEVertex> expected = readPoints(output);

        ConvexHull hull = new SequentialUAE2D().solve(points).convexHull;

        assertEqualCH(expected, hull);
    }


    @Test
    public void testDT1() {
        SequentialUAE2D uae = new SequentialUAE2D();

        List<UAEVertex> input1 = Collections.emptyList();
        List<UAEEdge> output1 = Collections.emptyList();
        List<UAEEdge> expected1 = UAEConverter.convert(uae.solve(input1)).delaunayTriangulation;
        assertEqualEdgesLists(expected1, output1, this::edgesEqual);


        List<UAEVertex> input2 = Collections.singletonList(new UAEVertex(1, 1));
        List<UAEEdge> output2 = Collections.emptyList();
        List<UAEEdge> expected2 = UAEConverter.convert(uae.solve(input2)).delaunayTriangulation;
        assertEqualEdgesLists(expected2, output2, this::edgesEqual);


        List<UAEVertex> input3 = Arrays.asList(new UAEVertex(1, 1), new UAEVertex(2, 2));
        List<UAEEdge> output3 = Arrays.asList(new UAEEdge(input3.get(0), input3.get(1)), new UAEEdge(input3.get(1), input3.get(0)));
        List<UAEEdge> expected3 = UAEConverter.convert(uae.solve(input3)).delaunayTriangulation;
        assertEqualEdgesLists(expected3, output3, this::edgesEqual);
    }

    @Test
    public void testDT2() {
        SequentialUAE2D uae = new SequentialUAE2D();

        double[][] points1 = new double[][]{{1, 1}, {2, 2}, {3, 1}};
        double[][] edges1 = new double[][]{{1, 1, 2, 2}, {2, 2, 3, 1}, {3, 1, 1, 1}};
        List<UAEVertex> input1 = readPoints(points1);
        List<UAEEdge> output1 = readEdges(edges1);
        List<UAEEdge> actual1 = UAEConverter.convert(uae.solve(input1)).delaunayTriangulation;
        assertEqualEdgesLists(output1, actual1, this::edgesEqual);

        double[][] points2 = new double[][]{{1, 1}, {2, 2}, {3, 3}};
        double[][] edges2 = new double[][]{{1, 1, 2, 2}, {2, 2, 3, 3}};
        List<UAEVertex> input2 = readPoints(points2);
        List<UAEEdge> output2 = readEdges(edges2);
        List<UAEEdge> actual2 = UAEConverter.convert(uae.solve(input2)).delaunayTriangulation;
        assertEqualEdgesLists(output2, actual2, this::edgesEqual);
    }

    @Test
    public void testDT3() {
        SequentialUAE2D uae = new SequentialUAE2D();

        double[][] points1 = new double[][]{{1, 1}, {2, 4}, {7, 6}, {6, 0}};
        double[][] edges1 = new double[][]{{1, 1, 2, 4}, {1, 1, 6, 0}, {2, 4, 6, 0}, {2, 4, 7, 6}, {7, 6, 6, 0}};
        List<UAEVertex> input1 = readPoints(points1);
        List<UAEEdge> output1 = readEdges(edges1);
        List<UAEEdge> actual1 = UAEConverter.convert(uae.solve(input1)).delaunayTriangulation;
        assertEqualEdgesLists(output1, actual1, this::edgesEqual);

        double[][] points2 = new double[][]{{1, 1}, {4, 3}, {4, 6}, {7, 1}};
        double[][] edges2 = new double[][]{
                {1, 1, 4, 6}, {1, 1, 4, 3}, {1, 1, 7, 1},
                {4, 3, 4, 6}, {4, 3, 7, 1},
                {4, 6, 7, 1}};
        List<UAEVertex> input2 = readPoints(points2);
        List<UAEEdge> output2 = readEdges(edges2);
        List<UAEEdge> actual2 = UAEConverter.convert(uae.solve(input2)).delaunayTriangulation;
        assertEqualEdgesLists(output2, actual2, this::edgesEqual);
    }


    @Test
    public void testRemoveDuplicated() {
        List<UAEVertex> points1 = new ArrayList<>();
        UAE2D.removeDuplicated(points1, UAEVertex::compareTo);
        assertTrue(points1.isEmpty());

        List<UAEVertex> points2 = new ArrayList<>();
        points2.add(new UAEVertex(1, 1));
        UAE2D.removeDuplicated(points2, UAEVertex::compareTo);
        assertEquals(Collections.singletonList(
                new UAEVertex(1, 1)
        ), points2);

        List<UAEVertex> points3 = new ArrayList<>();
        points3.add(new UAEVertex(1, 1));
        points3.add(new UAEVertex(1, 1));
        UAE2D.removeDuplicated(points3, UAEVertex::compareTo);
        assertEquals(Arrays.asList(
                new UAEVertex(1, 1),
                new UAEVertex(1, 1)
        ), points3);

        List<UAEVertex> points4 = new ArrayList<>();
        points4.add(new UAEVertex(1, 1));
        points4.add(new UAEVertex(1, 2));
        UAE2D.removeDuplicated(points4, UAEVertex::compareTo);
        assertEquals(Arrays.asList(
                new UAEVertex(1, 1),
                new UAEVertex(1, 2)
        ), points4);


        List<UAEVertex> points5 = new ArrayList<>();
        points5.add(new UAEVertex(1, 1));
        points5.add(new UAEVertex(1, 1));
        points5.add(new UAEVertex(1, 1));
        UAE2D.removeDuplicated(points5, UAEVertex::compareTo);
        assertEquals(Arrays.asList(
                new UAEVertex(1, 1),
                new UAEVertex(1, 1)
        ), points5);


        List<UAEVertex> points6 = new ArrayList<>();
        points6.add(new UAEVertex(1, 1));
        points6.add(new UAEVertex(1, 1));
        points6.add(new UAEVertex(1, 1));
        points6.add(new UAEVertex(2, 2));
        points6.add(new UAEVertex(2, 2));
        points6.add(new UAEVertex(2, 2));
        points6.add(new UAEVertex(3, 3));
        points6.add(new UAEVertex(3, 3));
        points6.add(new UAEVertex(4, 4));
        UAE2D.removeDuplicated(points6, UAEVertex::compareTo);
        assertEquals(Arrays.asList(
                new UAEVertex(1, 1),
                new UAEVertex(1, 1),
                new UAEVertex(2, 2),
                new UAEVertex(2, 2),
                new UAEVertex(3, 3),
                new UAEVertex(3, 3),
                new UAEVertex(4, 4)
        ), points6);


        List<UAEVertex> points7 = new ArrayList<>();
        points7.add(new UAEVertex(1, 1));
        points7.add(new UAEVertex(2, 2));
        points7.add(new UAEVertex(3, 3));
        points7.add(new UAEVertex(3, 3));
        points7.add(new UAEVertex(3, 3));
        points7.add(new UAEVertex(4, 4));
        points7.add(new UAEVertex(5, 5));
        points7.add(new UAEVertex(5, 5));
        points7.add(new UAEVertex(5, 5));
        UAE2D.removeDuplicated(points7, UAEVertex::compareTo);
        assertEquals(Arrays.asList(
                new UAEVertex(1, 1),
                new UAEVertex(2, 2),
                new UAEVertex(3, 3),
                new UAEVertex(3, 3),
                new UAEVertex(4, 4),
                new UAEVertex(5, 5),
                new UAEVertex(5, 5)
        ), points7);
    }


    @Test
    public void tesSequentialUAE2D() {
        testE2E(new SequentialUAE2D());
    }

    @Test
    public void tesParallelUAE2D() {
        testE2E(new ParallelUAE2D());
    }


    //    @Test
    public void helperCH() {
        //  (0.866;0.013) (0.905;0.064) (0.931;0.098)
        //  (0.905;0.064)
        String inputDir = "/home/semen/drive/java/computational-geometry/src/test/resources/testcases/100_d/input/348";
        String chDir = "/home/semen/drive/java/computational-geometry/src/test/resources/testcases/100_d/ch/348";

        List<UAEVertex> input = TestUtils.readPointsFile(inputDir);
        List<UAEVertex> expectedCH = TestUtils.readPointsFile(chDir);

        ConvexHull actualCH = new SequentialUAE2D().solve(input).convexHull;
        assertEqualCH(expectedCH, actualCH);
    }

    //    @Test
    public void helperDT() {
        String inputDir = "/home/semen/drive/java/computational-geometry/src/test/resources/testcases/40/input/287";
        String outputDir = "/home/semen/drive/java/computational-geometry/src/test/resources/testcases/40/dt/287";
        String path = "/home/semen/drive/python/points-visualization/uaeDT";

        List<UAEVertex> input = TestUtils.readPointsFile(inputDir);
        List<UAEEdge> expectedDT = TestUtils.readEdgesFile(outputDir);
        TestUtils.writeEdgesFile("/home/semen/drive/python/points-visualization/cgalDT", expectedDT);

        List<UAEEdge> actualDT = UAEConverter.convert(new SequentialUAE2D().solve(input)).delaunayTriangulation;
        TestUtils.writeEdgesFile(path, actualDT);
        assertEqualEdgesLists(expectedDT, actualDT, this::edgesEqual);
    }

    @Test
    public void helperVD() {
        String pointFile = "./src/test/resources/testcases/100/input/11";
        String dtFile = "./src/test/resources/testcases/100/dt/11";
        String vdFile = "./src/test/resources/testcases/100/vd/11";

        List<UAEVertex> points = TestUtils.readPointsFile(pointFile);
        List<UAEEdge> expectedDT = TestUtils.readEdgesFile(dtFile);
        List<UAEEdge> expectedVD = TestUtils.readEdgesFile(vdFile);
        TestUtils.writePoints("/home/semen/drive/python/points-visualization/points", points);
        TestUtils.writeEdgesFile("/home/semen/drive/python/points-visualization/cgalDT", expectedDT);
        TestUtils.writeEdgesFile("/home/semen/drive/python/points-visualization/cgalVD", expectedVD);

        UAEOutput output = UAEConverter.convert(new SequentialUAE2D().solve(points));
        List<UAEEdge> actualDT = output.delaunayTriangulation;
        List<UAEEdge> actualVD = output.voronoiDiagram;

        TestUtils.writeEdgesFile("/home/semen/drive/python/points-visualization/uaeDT", actualDT);
        TestUtils.writeEdgesFile("/home/semen/drive/python/points-visualization/uaeVD", actualVD);

//        assertEqualEdgesLists(expectedDT, actualDT, this::edgesEqual);
//        assertEqualEdgesLists(expectedVD, actualVD, this::edgesEqual);
    }


    private void testE2E(DaCExecutionSpecifics<List<UAEVertex>, UAEResult> specifics) {
        for (int i = 0; i < PointsGenerator.testDirs.length; ++i) {
            String testDir = PointsGenerator.testDirs[i];
            String inputDir = testDir + PointsGenerator.subdirs[0];
            String chDir = testDir + PointsGenerator.subdirs[1];
            String dtDir = testDir + PointsGenerator.subdirs[2];
            String vdDir = testDir + PointsGenerator.subdirs[3];

            System.out.println(inputDir);

            List<List<UAEVertex>> inputs = TestUtils.readPointsDir(inputDir);
            List<List<UAEVertex>> expectedCHs = TestUtils.readPointsDir(chDir);
            List<List<UAEEdge>> expectedDTs = TestUtils.readEdgesDir(dtDir);
            List<List<UAEEdge>> expectedVDs = TestUtils.readEdgesDir(vdDir);

            for (int j = 0; j < inputs.size(); ++j) {
                // TODO: find why collinear points are present in expected test case
//                if (Arrays.asList(88, 114, 185, 251, 657, 751, 943, 320, 490, 287, 746, 348, 426).contains(j)) {
//                    continue;
//                }
                System.out.printf("Test case %d%n", j);

                List<UAEVertex> input = inputs.get(j);
                List<UAEVertex> expectedCH = expectedCHs.get(j);
                List<UAEEdge> expectedDT = expectedDTs.get(j);
                List<UAEEdge> expectedVD = expectedVDs.get(j);


                UAEOutput output = UAEConverter.convert(specifics.solve(input));
                List<UAEVertex> actualCH = output.convexHull;
                List<UAEEdge> actualDT = output.delaunayTriangulation;
                List<UAEEdge> actualVD = output.voronoiDiagram;
                expectedVD = expectedVD.stream().filter(e->!e.org.equals(e.dest)).collect(Collectors.toList());

                assertEqualEdgesLists(expectedCH, actualCH, Point::equals);
                assertEqualEdgesLists(expectedDT, actualDT, this::edgesEqual);
                assertEqualEdgesLists(expectedVD, actualVD, this::edgesEqual);
            }
        }
    }

    private void assertEqualCH(List<UAEVertex> expectedCH, ConvexHull actualCH) {
        List<UAEVertex> actualList = CH.convert(actualCH);
        actualList = removeCollinear(actualList);

        Set<UAEVertex> expectedPoints = new HashSet<>(expectedCH);
        Set<UAEVertex> actualPoints = new HashSet<>(actualList);

        assertEquals(expectedPoints, actualPoints);
    }

    private <T> void assertEqualEdgesLists(List<T> expected, List<T> actual, BiFunction<T, T, Boolean> equals) {
        for (T expectedT : expected) {
            boolean found = false;
            for (T actualT : actual) {
                if (equals.apply(expectedT, actualT)) {
                    found = true;
                }
            }
            if (!found) {
                System.out.println("Expected: " + expected);
                System.out.println("Actual: " + actual);
                fail(expectedT + " is not contained in the actual set of edges");
            }
        }
    }

    private boolean edgesEqual(UAEEdge e1, UAEEdge e2) {
        return (pointsEqual(e1.org, e2.org) && pointsEqual(e1.dest, e2.dest))
                || (pointsEqual(e1.org, e2.dest) && pointsEqual(e1.dest, e2.org));
    }

    private boolean pointsEqual(UAEVertex v1, UAEVertex v2) {
        return Math.abs(v1.x - v2.x) < 1 && Math.abs(v1.y - v2.y) < 1;
    }

    private Set<UAEEdge> restOfSet(Set<UAEEdge> of, Set<UAEEdge> in) {
        Set<UAEEdge> result = new HashSet<>();
        for (UAEEdge UAEEdge : of) {
            if (!in.contains(UAEEdge)) {
                result.add(UAEEdge);
            }
        }
        return result;
    }


    private List<UAEVertex> readPoints(double[][] points) {
        List<UAEVertex> result = new ArrayList<>(points.length);
        for (double[] point : points) {
            result.add(new UAEVertex(point[0], point[1]));
        }
        return result;
    }

    private List<UAEEdge> readEdges(double[][] points) {
        List<UAEEdge> result = new ArrayList<>(points.length);
        for (double[] point : points) {
            UAEVertex org = new UAEVertex(point[0], point[1]);
            UAEVertex dest = new UAEVertex(point[2], point[3]);
            result.add(new UAEEdge(org, dest));
            result.add(new UAEEdge(dest, org));
        }
        return result;
    }

    private List<UAEVertex> removeCollinear(List<UAEVertex> points) {
        boolean[] removed = new boolean[points.size()];
        for (int i = 0; i + 2 < points.size(); ++i) {
            UAEVertex a = points.get(i);
            UAEVertex b = points.get(i + 1);
            UAEVertex c = points.get(i + 2);
            if (a.x >= b.x && b.x >= c.x) {
                UAEVertex tmp = a;
                a = c;
                c = tmp;
            }
            if (a.x <= b.x && b.x <= c.x) {
                double slope1 = UAEVertex.getSlope(a, b);
                double slope2 = UAEVertex.getSlope(b, c);
                if (slope1 == slope2) {
                    removed[i + 1] = true;
                }
            }
        }
        List<UAEVertex> result = new ArrayList<>();
        for (int i = 0; i < points.size(); ++i) {
            if (!removed[i]) {
                result.add(points.get(i));
            }
        }
        return result;
    }
}