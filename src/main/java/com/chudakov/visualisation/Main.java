package com.chudakov.visualisation;

import com.chudakov.geometry.uae.SequentialUAE2D;
import com.chudakov.geometry.uae.Vertex2D;
import com.chudakov.geometry.uae.ConvexHull;

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

                List<Vertex2D> generated = generatePoints(numOfPoints, width, height);
                panel.generated = generated;
                panel.convexHull = null;
                panel.paintComponent(panel.getGraphics());

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                SequentialUAE2D convexHull = new SequentialUAE2D();
                ConvexHull hull = convexHull.solve(generated).getConvexHull();
                List<Vertex2D> convex = new ArrayList<>();
                for (Vertex2D vertex2D : hull) {
                    convex.add(vertex2D);
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

    private static List<Vertex2D> generatePoints(int size, int width, int height) {
        List<Vertex2D> result = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            result.add(new Vertex2D(
                    (int) (Math.random() * width),
                    (int) (Math.random() * height)
            ));
        }
        return result;
    }

}
