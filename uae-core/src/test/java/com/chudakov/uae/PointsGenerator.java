package com.chudakov.uae;

import java.nio.file.Paths;

public class PointsGenerator {
    private static final String TESTCASES_ROOT = "/home/semen/drive/university/extra/3_masters-diploma-work/computational-geometry/uae-core/src/test/resources/testcases/";
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
            "dt/",
            "vd/",
            "cp/"
    };
    private static final int[][] filesData = {
            {100, 5, 0},
            {100, 10, 0},
            {100, 10, 1},
            {100, 20, 0},
            {100, 20, 1},
            {100, 40, 0},
            {100, 40, 1},
            {100, 100, 0},
            {100, 100, 1}
    };

    public static void main(String[] args) {
        for (int i = 0; i < testDirs.length; ++i) {
            String testDir = testDirs[i];
            String inputDir = Paths.get(testDir, subdirs[0]).toString();

            for (int j = 0; j < subdirs.length; ++j) {
                String problemDir = Paths.get(testDir, subdirs[j]).toString();
                TestUtils.createDirIfNotExists(problemDir);
                TestUtils.cleanDirectory(problemDir);
            }

            int numberOfFiles = filesData[i][0];
            int numberOfPoints = filesData[i][1];
            int integerOrDouble = filesData[i][2];

            System.out.println("Generating directory: " + inputDir);

            TestUtils.writePoints(inputDir, numberOfFiles, numberOfPoints, integerOrDouble);
        }
    }
}
