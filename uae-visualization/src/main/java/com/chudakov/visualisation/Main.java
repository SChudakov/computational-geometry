package com.chudakov.visualisation;

import com.chudakov.uae.impl.SequentialUAE2D;
import com.chudakov.uae.impl.UAEVertex;
import com.chudakov.uae.impl.ConvexHull;

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

                List<UAEVertex> generated = generatePoints(numOfPoints, width, height);
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
                List<UAEVertex> convex = new ArrayList<>();
                for (UAEVertex UAEVertex : hull) {
                    convex.add(UAEVertex);
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

    private static List<UAEVertex> generatePoints(int size, int width, int height) {
        List<UAEVertex> result = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            result.add(new UAEVertex(
                    (int) (Math.random() * width),
                    (int) (Math.random() * height)
            ));
        }
        return result;
    }

}
