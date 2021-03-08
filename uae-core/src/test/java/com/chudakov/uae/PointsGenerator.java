package com.chudakov.uae;

public class PointsGenerator {
    private static final String TESTCASES_ROOT = "./uae-core/src/test/resources/testcases/";
    public static final String[] testDirs = {
            TESTCASES_ROOT + "5/",
            TESTCASES_ROOT + "10/",
            TESTCASES_ROOT + "10_d/",
            TESTCASES_ROOT + "20/",
            TESTCASES_ROOT + "20_d/",
            TESTCASES_ROOT + "40/",
            TESTCASES_ROOT + "40_d/",
            TESTCASES_ROOT + "100/",
            TESTCASES_ROOT + "100_d/",
    };
    public static final String[] subdirs = {
            "input/",
            "ch/",
            "dt/"
    };
    private static final int[][] filesData = {
            {10, 5, 0},
            {10, 10, 0},
            {10, 10, 1},
            {10, 20, 0},
            {10, 20, 1},
            {10, 40, 0},
            {10, 40, 1},
            {10, 100, 0},
            {10, 100, 1}
    };

    public static void main(String[] args) {
        for (int i = 0; i < testDirs.length; ++i) {
            String testDir = testDirs[i];
            String inputDir = testDir + subdirs[0];
            String chDir = testDir + subdirs[1];
            String dtDir = testDir + subdirs[2];

            TestUtils.createDirIfNotExists(inputDir);
            TestUtils.createDirIfNotExists(chDir);
            TestUtils.createDirIfNotExists(dtDir);

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
