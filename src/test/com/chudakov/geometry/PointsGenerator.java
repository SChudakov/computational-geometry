package com.chudakov.geometry;

public class PointsGenerator {
    private static final String TEST_CASES_INPUT_DIR = "./src/test/resources/convexhull/100/input/";

    public static void main(String[] args) {
        TestUtils.writePoints(TEST_CASES_INPUT_DIR, 100, 100);
    }
}
