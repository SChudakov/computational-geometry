package com.chudakov.uae;

public class PointsGenerator {
    private static final String root = "./src/test/resources/testcases/";
    public static final String[] testDirs = {
            root + "5/",
            root + "10/",
            root + "10_d/",
            root + "20/",
            root + "20_d/",
            root + "40/",
            root + "40_d/",
            root + "100/",
            root + "100_d/",
    };
    public static final String[] subdirs = {
            "input/",
            "ch/",
            "dt/"
    };
    private static final int[][] filesData = {
            {1000, 5, 0},
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
        for (int i = 0; i < testDirs.length; ++i) {
            String testDir = testDirs[i];
            String inputDir = testDir + subdirs[0];
            String chDir = testDir + subdirs[1];
            String dtDir = testDir + subdirs[2];

            TestUtils.cleanDirectory(inputDir);
            TestUtils.cleanDirectory(chDir);
            TestUtils.cleanDirectory(dtDir);

            int numberOfFiles = filesData[i][0];
            int numberOfPoints = filesData[i][1];
            int integerOrDouble = filesData[i][2];

            System.out.println("Generating directory: " + inputDir);

            TestUtils.writePoints(inputDir, numberOfFiles, numberOfPoints, integerOrDouble);
        }
    }
}
