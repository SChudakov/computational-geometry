package com.chudakov.geometry.uae;

import com.chudakov.geometry.common.Point2D;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PointReader {
    public List<Point2D> readPoints(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        List<Point2D> result = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().equals("")) {
                continue;
            }
            String[] coordinates = line.split(" ");
            if (coordinates.length != 2) {
                throw new RuntimeException("illegal line format: " + line);
            }
            result.add(new Point2D(
                    Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]))
            );
        }
        return result;
    }
}

