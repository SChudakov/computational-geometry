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
    public static final String[] CH_DIRS = {
            BASE + "10/ch/",
            BASE + "10_d/ch/",
            BASE + "20/ch/",
            BASE + "20_d/ch/",
            BASE + "40/ch/",
            BASE + "40_d/ch/",
            BASE + "100/ch/",
            BASE + "100_d/ch/",
    };
    public static final String[] DT_DIRS = {
            BASE + "10/dt/",
            BASE + "10_d/dt/",
            BASE + "20/dt/",
            BASE + "20_d/dt/",
            BASE + "40/dt/",
            BASE + "40_d/dt/",
            BASE + "100/dt/",
            BASE + "100_d/dt/",
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
        for (String directory : CH_DIRS) {
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
