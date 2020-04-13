package com.chudakov.geometry;

public class PointsGenerator {
    private static final String BASE = "./src/test/resources/convexhull/";
    public static final String[] INPUT_DIRS = {
            BASE + "10/input/",
            BASE + "10_d/input/",
            BASE + "20/input/",
            BASE + "20_d/input/",
            BASE + "40/input/",
            BASE + "40_d/input/",
            BASE + "100/input/",
            BASE + "100_d/input/",
    };
    public static final String[] OUTPUT_DIRS = {
            BASE + "10/output/",
            BASE + "10_d/output/",
            BASE + "20/output/",
            BASE + "20_d/output/",
            BASE + "40/output/",
            BASE + "40_d/output/",
            BASE + "100/output/",
            BASE + "100_d/output/",
    };

    private static final int[][] FILES_DATA = {
            {1000, 10, 0},
            {1000, 10, 1},
            {1000, 20, 0},
            {1000, 20, 1},
            {1000, 40, 0},
            {1000, 40, 1},
            {1000, 100, 0},
            {1000, 100, 1}
    };

    public static void main(String[] args) {
        for (String directory : INPUT_DIRS) {
            TestUtils.cleanDirectory(directory);
        }
        for (String directory : OUTPUT_DIRS) {
            TestUtils.cleanDirectory(directory);
        }

        assert INPUT_DIRS.length == FILES_DATA.length;
        for (int i = 0; i < INPUT_DIRS.length; ++i) {
            String directory = INPUT_DIRS[i];
            int numberOfFiles = FILES_DATA[i][0];
            int numberOfPoints = FILES_DATA[i][1];
            int integerOrDouble = FILES_DATA[i][2];

            System.out.println("Generating directory: " + directory);

            TestUtils.writePoints(directory, numberOfFiles, numberOfPoints, integerOrDouble);
        }
    }
}
