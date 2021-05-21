package com.chudakov.visualisation;

import com.chudakov.uae.impl.SequentialUAE2D;
import com.chudakov.uae.impl.UAE2D;
import com.chudakov.uae.impl.UAEConverter;
import com.chudakov.uae.impl.UAESolutions;
import com.chudakov.uae.impl.UAEState;
import com.chudakov.uae.impl.UAEVertex;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    private static final long SEED = 19L;
    private static final Random RND = new Random(SEED);

    private static final int NUMBER_OF_ITERATIONS = 10;
    private static final int NUMBER_OF_POINTS = 10;


    public static void main(String[] args) {
        ConvexHullVisualisation visualisation = new ConvexHullVisualisation();
        DrawingPanel panel = new DrawingPanel();
        visualisation.getContentPane().add(panel, BorderLayout.CENTER);
        visualisation.setVisible(true);

        new Thread(() -> {
            for (int i = 0; i < NUMBER_OF_ITERATIONS; ++i) {
                int width = panel.getWidth();
                int height = panel.getHeight();

                List<UAEVertex> generated = generatePoints(NUMBER_OF_POINTS, width, height);
                panel.generated = generated;
                panel.convexHull = null;
                panel.triangulation = null;
                panel.voronoiDiagram = null;
                panel.closesPair = null;
                panel.paintComponent(panel.getGraphics());

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                SequentialUAE2D convexHull = new SequentialUAE2D();
                UAEState state = convexHull.solve(generated);
                UAESolutions solutions = UAEConverter.convert(state);
                panel.convexHull = solutions.getConvexHull();
                panel.triangulation = solutions.getDelaunayTriangulation();
                panel.voronoiDiagram = solutions.getVoronoiDiagram();
                panel.closesPair = solutions.getClosesPair();
                panel.paintComponent(panel.getGraphics());
                try {
                    Thread.sleep(10000);
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
                    (int) (RND.nextDouble() * width),
                    (int) (RND.nextDouble() * height)
            ));
        }
        return new UAE2D().precompute(result);
    }

}
