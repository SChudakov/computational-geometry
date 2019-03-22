package com.chudakov.alg;

import com.chudakov.common.Point2D;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

public class ConvexHullTest {
    private static final String TEST_CASES_INPUT_DIR = "/home/semen/drive/workspace.java/computational-geometry/src/test/resources/convexhull/input";
    private static final String TEST_CASES_OUTPUT_DIR = "/home/semen/drive/workspace.java/computational-geometry/src/test/resources/convexhull/output";

    @Test
    public void testConvexHull() {
        ConvexHull hull = new ConvexHull();

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

                List<Point2D> output = hull.getConvexHull(input);

                assertEqual(expectedOutput, output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void assertEqual(List<Point2D> first, List<Point2D> second) {
        assertEquals(first.size(), second.size());
        Iterator<Point2D> fit = first.iterator();
        Iterator<Point2D> sit = second.iterator();
        while (fit.hasNext()) {
            Point2D f = fit.next();
            Point2D s = sit.next();
            assertEquals(f.first, s.first, 1e-9);
            assertEquals(f.second, s.second, 1e-9);
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