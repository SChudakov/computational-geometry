package com.chudakov.visualisation;

import com.chudakov.geometry.alg.convexhull.overmars.SequentialConvexHull2D;
import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.datastructure.ConvexHull;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ConvexHullVisualisation visualisation = new ConvexHullVisualisation();
        DrawingPanel panel = new DrawingPanel();
        visualisation.getContentPane().add(panel, BorderLayout.CENTER);
        visualisation.setVisible(true);

        new Thread(() -> {
            for (int i = 0; i < 500; ++i) {
                int numOfPoints = 50;
                int width = panel.getWidth();
                int height = panel.getHeight();

                List<Point2D> generated = generatePoints(numOfPoints, width, height);
                panel.generated = generated;
                panel.convexHull = null;
                panel.paintComponent(panel.getGraphics());

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                SequentialConvexHull2D convexHull = new SequentialConvexHull2D();
                ConvexHull hull = convexHull.solve(generated);
                List<Point2D> convex = new ArrayList<>();
                for (Point2D point2D : hull) {
                    convex.add(point2D);
                }
                panel.convexHull = convex;
                panel.paintComponent(panel.getGraphics());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).run();
    }

    private static List<Point2D> generatePoints(int size, int width, int height) {
        List<Point2D> result = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            result.add(new Point2D(
                    (int) (Math.random() * width),
                    (int) (Math.random() * height)
            ));
        }
        return result;
    }

}
