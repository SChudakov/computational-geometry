package com.chudakov.geometry.alg.convexhull;

import com.chudakov.geometry.alg.convexhull.simple.ParallelConvexHull2D;
import com.chudakov.geometry.alg.convexhull.simple.SequentialConvexHull2D;
import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.framework.DaCExecutionSpecifics;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class ConvexHull2DTest {
    private static final String TEST_CASES_INPUT_DIR = "/home/semen/drive/workspace.java/computational-geometry/src/test/resources/convexhull/input";
    private static final String TEST_CASES_OUTPUT_DIR = "/home/semen/drive/workspace.java/computational-geometry/src/test/resources/convexhull/output";

    @Test
    public void tesSequentialConvexHull() {
        testConvexHull(new SequentialConvexHull2D());
    }

    @Test
    public void tesParallelConvexHull() {
        testConvexHull(new ParallelConvexHull2D());
    }

    private void testConvexHull(DaCExecutionSpecifics<Point2D> specifics) {

        File testCasesInputsDir = new File(TEST_CASES_INPUT_DIR);

        File[] testCasesInputs = Objects.requireNonNull(testCasesInputsDir.listFiles());

        PointReader reader = new PointReader();
        try {
            for (File inputFile : testCasesInputs) {
                String testCaseName = inputFile.getName();

                System.out.println(testCaseName);

                String inputFilePath = inputFile.getPath();
                String outputFilePath = Paths.get(TEST_CASES_OUTPUT_DIR, testCaseName).toString();


                List<Point2D> input = reader.readPoints(inputFilePath);
                System.out.println("input: " + input);
                List<Point2D> expectedOutput = reader.readPoints(outputFilePath);
                System.out.println("expected output: " + expectedOutput);

                List<Point2D> actualOutput = specifics.solve(input);
                System.out.println("actual output: " + expectedOutput);

                assertEqual(expectedOutput, actualOutput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testOnRandomPoints() {
        Random random = new Random(17);
        testOnRandomPoints(new SequentialConvexHull2D(), random);
        testOnRandomPoints(new ParallelConvexHull2D(), random);
    }

    private void testOnRandomPoints(DaCExecutionSpecifics<Point2D> specifics, Random random) {
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

    private void assertEqual(List<Point2D> first, List<Point2D> second) {
        assertEquals(first.size(), second.size());
        Iterator<Point2D> firstIt = first.iterator();
        Iterator<Point2D> secondIt = second.iterator();
        while (firstIt.hasNext() && secondIt.hasNext()) {
            Point2D firstPoint = firstIt.next();
            Point2D secondPoint = secondIt.next();
            assertEquals(firstPoint.first, secondPoint.first, 1e-9);
            assertEquals(firstPoint.second, secondPoint.second, 1e-9);
        }
    }

    private static class PointReader {
        List<Point2D> readPoints(String path) throws IOException {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            List<Point2D> result = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals("")) {
                    continue;
                }
                String[] coordinates = line.split(",");
                if (coordinates.length != 2) {
                    throw new RuntimeException("illegal line format: " + line);
                }
                result.add(new Point2D(
                        Double.valueOf(coordinates[0]), Double.valueOf(coordinates[1]))
                );
            }
            return result;
        }
    }
}