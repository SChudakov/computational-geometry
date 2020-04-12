package com.chudakov.geometry;

import com.chudakov.geometry.common.Point2D;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class TestUtils {
    private static final long SEED = 17L;

    public static void writePoints(String directory, int numberOfFiles, int numberOfPoints) {
        Random random = new Random(SEED);

        for (int i = 0; i < numberOfFiles; ++i) {
            String fileName = fileName(i);
            String filePath = Paths.get(directory, fileName).toString();

            List<Point2D> points = generatePoints(numberOfPoints, random);
            writePoints(filePath, points);
        }
    }

    public static void writePoints(String filePath, List<Point2D> points) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Point2D point : points) {
                writer.write(String.valueOf(point.x));
                writer.write(" ");
                writer.write(String.valueOf(point.y));
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<List<Point2D>> readPointsDir(String directory) {
        File[] files = Objects.requireNonNull(new File(directory).listFiles());

        List<List<Point2D>> result = new ArrayList<>(Collections.nCopies(files.length, null));
        for (File file : files) {
            String filePath = file.getAbsolutePath();
            String fileName = file.getName();
            int fileIndex = fileIndex(fileName);

            List<Point2D> points = readPointsFile(filePath);
            result.set(fileIndex, points);
        }

        return result;
    }

    public static List<Point2D> readPointsFile(String path) {
        List<Point2D> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals("")) {
                    continue;
                }

                String[] coordinates = line.split(" ");
                if (coordinates.length != 2) {
                    throw new RuntimeException("illegal points format: " + line);
                }

                result.add(new Point2D(Double.parseDouble(coordinates[0]),
                        Double.parseDouble(coordinates[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static String fileName(int fileIndex) {
        return String.valueOf(fileIndex);
    }

    private static int fileIndex(String fileName) {
        return Integer.parseInt(fileName);
    }

    private static List<Point2D> generatePoints(int numberOfPoints, Random random) {
        List<Point2D> result = new ArrayList<>(numberOfPoints);
        for (int i = 0; i < numberOfPoints; ++i) {
            double x = trimDouble(random.nextDouble());
            double y = trimDouble(random.nextDouble());
//            int x = random.nextInt(100);
//            int y = random.nextInt(100);
            Point2D point = new Point2D(x, y);
            result.add(point);
        }
        return result;
    }

    private static double trimDouble(double d) {
        return ((int) (d * 1_000.0)) / 1_000.0;
    }

    public static void cleanDirectory(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException("directory does not exist");
        }
        if (file.isFile()) {
            throw new IllegalArgumentException("path points to a file");
        }
        for (String s : file.list()) {
            String absolutePath = Paths.get(path, s).toString();
            System.out.println(absolutePath);
            new File(absolutePath).delete();
        }
    }
}
