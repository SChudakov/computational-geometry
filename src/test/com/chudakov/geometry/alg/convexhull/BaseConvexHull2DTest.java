package com.chudakov.geometry.alg.convexhull;

import com.chudakov.geometry.alg.common.PointReader;
import com.chudakov.geometry.alg.convexhull.overmars.ConvexHull;
import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.core.DaCExecutionSpecifics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class BaseConvexHull2DTest {
    private static final String TEST_CASES_INPUT_DIR = "/home/semen/drive/workspace.java/computational-geometry/src/test/resources/convexhull/100_d/input/";
    private static final String TEST_CASES_OUTPUT_DIR = "/home/semen/drive/workspace.java/computational-geometry/src/test/resources/convexhull/100_d/output/";

    protected void testConvexHull1(DaCExecutionSpecifics<List<Point2D>, List<Point2D>> specifics) {
        PointReader reader = new PointReader();

        File testCasesInputsDir = new File(TEST_CASES_INPUT_DIR);
        File[] testCasesInputs = Objects.requireNonNull(testCasesInputsDir.listFiles());
        int numberOfTestCases = testCasesInputs.length;

        try {
            for (int i = 0; i < numberOfTestCases; ++i) {
                System.out.println("test: " + i);

                String testCaseName = i + ".txt";
                String inputFilePath = Paths.get(TEST_CASES_INPUT_DIR, testCaseName).toString();
                String outputFilePath = Paths.get(TEST_CASES_OUTPUT_DIR, testCaseName).toString();


                List<Point2D> input = reader.readPoints(inputFilePath);
                System.out.println("input: " + input);
                List<Point2D> expectedOutput = reader.readPoints(outputFilePath);
                System.out.println("expected output: " + expectedOutput);

                List<Point2D> actualOutput = specifics.solve(input);
                System.out.println("actual output: " + actualOutput);

                assertEqual(expectedOutput, actualOutput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void testConvexHull2(DaCExecutionSpecifics<List<Point2D>, ConvexHull> specifics) {
        PointReader reader = new PointReader();

        File testCasesInputsDir = new File(TEST_CASES_INPUT_DIR);
        File[] testCasesInputs = Objects.requireNonNull(testCasesInputsDir.listFiles());
        int numberOfTestCases = testCasesInputs.length;

        try {
            for (int i = 0; i < numberOfTestCases; ++i) {
//                if (i == 33767) {
                System.out.println("test: " + i);

                String testCaseName = i + ".txt";
                String inputFilePath = Paths.get(TEST_CASES_INPUT_DIR, testCaseName).toString();
                String outputFilePath = Paths.get(TEST_CASES_OUTPUT_DIR, testCaseName).toString();


                List<Point2D> input = reader.readPoints(inputFilePath);
                System.out.println("input: " + input);
                List<Point2D> expectedOutput = reader.readPoints(outputFilePath);

                ConvexHull actualOutput = specifics.solve(input);

                assertEqual(expectedOutput, actualOutput);
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void assertEqual(List<Point2D> expected, ConvexHull actual) {
        Set<Point2D> expectedPoints = new HashSet<>(expected);
        List<Point2D> actualList = new ArrayList<>();
        for (Point2D point : actual) {
            actualList.add(point);
        }

        expected.sort(Point2D::compareTo);
//        System.out.println("expected output: " + expected);
//        System.out.println("actual output: " + actualList);

        Set<Point2D> actualPoints = new HashSet<>(actualList);

        assertEquals(expectedPoints, actualPoints);
    }

    private void assertEqual(List<Point2D> first, List<Point2D> second) {
        assertEquals(first.size(), second.size());
        Iterator<Point2D> firstIt = first.iterator();
        Iterator<Point2D> secondIt = second.iterator();
        while (firstIt.hasNext() && secondIt.hasNext()) {
            Point2D firstPoint = firstIt.next();
            Point2D secondPoint = secondIt.next();
            assertEquals(firstPoint.x, secondPoint.x, 1e-9);
            assertEquals(firstPoint.y, secondPoint.y, 1e-9);
        }
    }
}
