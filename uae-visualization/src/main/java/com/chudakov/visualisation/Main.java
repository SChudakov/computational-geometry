package com.chudakov.visualisation;

import com.chudakov.uae.impl.SequentialUAE2D;
import com.chudakov.uae.impl.UAE2D;
import com.chudakov.uae.impl.UAEConverter;
import com.chudakov.uae.impl.UAESolutions;
import com.chudakov.uae.impl.UAEState;
import com.chudakov.uae.impl.UAEVertex;

import javax.swing.ImageIcon;
import java.awt.BorderLayout;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Main {
    private static final long SEED = 19L;
    private static final Random RND = new Random(SEED);

    private static final String ICON_PATH = "image/icon.jpg";

    private static final double VISUALISATION_MARGIN = 0.1;
    private static final int NUMBER_OF_ITERATIONS = 10;
    private static final int NUMBER_OF_POINTS = 100;


    public static void main(String[] args) {
        ConvexHullVisualisation visualisation = new ConvexHullVisualisation();
        setImage(visualisation);
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

                sleep(3000);

                SequentialUAE2D convexHull = new SequentialUAE2D();
                UAEState state = convexHull.solve(generated);
                UAESolutions solutions = UAEConverter.convert(state);
                panel.convexHull = solutions.getConvexHull();
                panel.triangulation = solutions.getDelaunayTriangulation();
                panel.voronoiDiagram = solutions.getVoronoiDiagram();
                panel.closesPair = solutions.getClosesPair();
                panel.paintComponent(panel.getGraphics());

                sleep(10000);
            }
        }).start();
    }

    private static List<UAEVertex> generatePoints(int size, int width, int height) {
        int maxX = (int) (width * (1 - 2 * VISUALISATION_MARGIN));
        int maxY = (int) (height * (1 - 2 * VISUALISATION_MARGIN));
        int xOffset = (int) (width * VISUALISATION_MARGIN);
        int yOffset = (int) (height * VISUALISATION_MARGIN);
        List<UAEVertex> result = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            int x = (int) (RND.nextDouble() * maxX) + xOffset;
            int y = (int) (RND.nextDouble() * maxY) + yOffset;
            result.add(new UAEVertex(x, y));
        }
        return new UAE2D().precompute(result);
    }

    private static void setImage(ConvexHullVisualisation frame) {
        URL iconPath = Objects.requireNonNull(
                JFrame.class.getClassLoader().getResource(ICON_PATH),
                "Failed to load icon");
        frame.setIconImage(new ImageIcon(iconPath.getFile()).getImage());
    }


    private static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
