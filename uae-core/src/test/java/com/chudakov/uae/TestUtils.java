package com.chudakov.uae;

import com.chudakov.uae.impl.UAE2D;
import com.chudakov.uae.impl.UAEEdge;
import com.chudakov.uae.impl.UAEVertex;

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
import java.util.Random;

public class TestUtils {
    private static final long SEED = 17L;

    private static UAE2D uae = new UAE2D();

    public static List<List<UAEVertex>> readPointsDir(String directory) {
        File[] files = new File(directory).listFiles();
        if (files == null) {
            return Collections.emptyList();
        }

        List<List<UAEVertex>> result = new ArrayList<>(Collections.nCopies(files.length, null));
        for (File file : files) {
            String filePath = file.getAbsolutePath();
            String fileName = file.getName();
            int fileIndex = fileIndex(fileName);

            List<UAEVertex> points = readPointsFile(filePath);
            result.set(fileIndex, points);
        }

        return result;
    }

    public static List<UAEVertex> readPointsFile(String file) {
        List<UAEVertex> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals("")) {
                    continue;
                }

                String[] coordinates = line.split(" ");
                if (coordinates.length != 2) {
                    throw new RuntimeException("illegal points format: " + line);
                }

                result.add(new UAEVertex(Double.parseDouble(coordinates[0]),
                        Double.parseDouble(coordinates[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static List<List<UAEEdge>> readEdgesDir(String directory) {
        File[] files = new File(directory).listFiles();
        if (files == null) {
            return Collections.emptyList();
        }

        List<List<UAEEdge>> result = new ArrayList<>(Collections.nCopies(files.length, null));
        for (File file : files) {
            String filePath = file.getAbsolutePath();
            String fileName = file.getName();
            int fileIndex = fileIndex(fileName);

            List<UAEEdge> UAEEdges = readEdgesFile(filePath);
            result.set(fileIndex, UAEEdges);
        }

        return result;
    }

    public static List<UAEEdge> readEdgesFile(String file) {
        List<UAEEdge> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] coordinates = line.split(" ");
                UAEVertex org = new UAEVertex(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]));
                UAEVertex dest = new UAEVertex(Double.parseDouble(coordinates[2]), Double.parseDouble(coordinates[3]));
                UAEEdge UAEEdge = new UAEEdge(org, dest);
                result.add(UAEEdge);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void writePoints(String directory, int numberOfFiles, int numberOfPoints, int integerOrDouble) {
        Random random = new Random(SEED);

        for (int i = 0; i < numberOfFiles; ++i) {
            String fileName = fileName(i);
            String filePath = Paths.get(directory, fileName).toString();

            List<UAEVertex> points = generatePoints(numberOfPoints, random, integerOrDouble);
            writePoints(filePath, points);
        }
    }

    public static void writePoints(String filePath, List<UAEVertex> points) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (UAEVertex point : points) {
                writer.write(String.valueOf(point.x));
                writer.write(" ");
                writer.write(String.valueOf(point.y));
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeEdgesDir(String directory, List<List<UAEEdge>> edges) {
        for (int i = 0; i < edges.size(); ++i) {
            String fileName = fileName(i);
            String filePath = Paths.get(directory, fileName).toString();

            List<UAEEdge> es = edges.get(i);
            writeEdgesFile(filePath, es);
        }
    }


    public static void writeEdgesFile(String file, List<UAEEdge> UAEEdges) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (UAEEdge UAEEdge : UAEEdges) {
                writer.write(String.valueOf(UAEEdge.getOrg().x));
                writer.write(' ');
                writer.write(String.valueOf(UAEEdge.getOrg().y));
                writer.write(' ');
                writer.write(String.valueOf(UAEEdge.getDest().x));
                writer.write(' ');
                writer.write(String.valueOf(UAEEdge.getDest().y));
                writer.write('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String fileName(int fileIndex) {
        return String.valueOf(fileIndex);
    }

    private static int fileIndex(String fileName) {
        return Integer.parseInt(fileName);
    }

    private static List<UAEVertex> generatePoints(int numberOfPoints, Random random, int integerOrDouble) {
        List<UAEVertex> result = new ArrayList<>(numberOfPoints);
        while (result.size() < numberOfPoints) {
            double x;
            double y;
            if (integerOrDouble == 0) {
                x = random.nextInt(1000);
                y = random.nextInt(1000);
            } else {
                x = trimDouble(random.nextDouble()*1000);
                y = trimDouble(random.nextDouble()*1000);
            }
            UAEVertex point = new UAEVertex(x, y);
            result.add(point);

            result = uae.precompute(result);
        }
        return result;
    }

    private static double trimDouble(double d) {
        return ((int) (d * 1_000.0)) / 1_000.0;
    }

    public static void cleanDirectory(String directory) {
        System.out.println("Cleaning directory: " + directory);
        File file = new File(directory);
        if (!file.exists()) {
            throw new IllegalArgumentException("directory does not exist");
        }
        if (file.isFile()) {
            throw new IllegalArgumentException("path points to a file");
        }
        for (String s : file.list()) {
            String absolutePath = Paths.get(directory, s).toString();
            boolean success = new File(absolutePath).delete();
            if (!success) {
                throw new RuntimeException("Failed to delete file: " + absolutePath);
            }
        }
    }

    public static void createDirIfNotExists(String path) {
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("Creating directory: " + path);
            boolean success = file.mkdirs();
            if (!success) {
                throw new RuntimeException("failed to create directory: " + path);
            }
        }
    }
}
