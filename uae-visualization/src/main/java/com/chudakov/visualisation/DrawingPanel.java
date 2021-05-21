package com.chudakov.visualisation;

import com.chudakov.uae.impl.UAEEdge;
import com.chudakov.uae.impl.UAEVertex;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.List;

public class DrawingPanel extends JPanel {
    private static final double DEFAULT_POINT_DIAMETER = 15.0;
    private static final double CP_POINT_DIAMETER = 15.0;

    private static final Color DEFAULT_POINTS_COLOR = Color.DARK_GRAY;
    private static final Color DEFAULT_CH_COLOR = Color.BLACK;
    private static final Color DEFAULT_DT_COLOR = Color.RED;
    private static final Color DEFAULT_VD_LINES_COLOR = Color.MAGENTA;
    private static final Color DEFAULT_VD_POINTS_COLOR = Color.BLUE;
    private static final Color DEFAULT_CP_POINTS_COLOR = Color.GREEN;

    List<UAEVertex> generated;
    List<UAEVertex> convexHull;
    List<UAEEdge> triangulation;
    List<UAEEdge> voronoiDiagram;
    Pair<UAEVertex, UAEVertex> closesPair;

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        ((Graphics2D) graphics).setStroke(new BasicStroke(3));
        if (generated != null) {
            drawPoints(graphics, generated, DEFAULT_POINTS_COLOR);
        }
        if (triangulation != null) {
            drawLines(graphics, triangulation, DEFAULT_DT_COLOR);
        }
        if (convexHull != null) {
            drawLinesInCircle(graphics, convexHull, DEFAULT_CH_COLOR);
        }
        if (voronoiDiagram != null) {
            drawLines(graphics, voronoiDiagram, DEFAULT_VD_LINES_COLOR);
            drawLinesPoints(graphics, voronoiDiagram, DEFAULT_VD_POINTS_COLOR);
        }
        if (closesPair != null) {
            drawPoint(graphics, closesPair.getLeft(), DEFAULT_CP_POINTS_COLOR, CP_POINT_DIAMETER);
            drawPoint(graphics, closesPair.getRight(), DEFAULT_CP_POINTS_COLOR, CP_POINT_DIAMETER);
        }
    }

    private void drawLinesInCircle(Graphics graphics, List<UAEVertex> vertices, Color color) {
        for (int i = 0; i < vertices.size() - 1; ++i) {
            drawLine(graphics, vertices.get(i), vertices.get(i + 1), color);
        }
        drawLine(graphics, vertices.get(0), vertices.get(vertices.size() - 1), color);
    }

    private void drawLines(Graphics graphics, List<UAEEdge> lines, Color color) {
        for (UAEEdge line : lines) {
            drawLine(graphics, line.getOrg(), line.getDest(), color);
        }
    }

    private void drawLinesPoints(Graphics graphics, List<UAEEdge> lines, Color color) {
        for (UAEEdge edge : lines) {
            drawPoint(graphics, edge.getOrg(), color, DEFAULT_POINT_DIAMETER);
            drawPoint(graphics, edge.getDest(), color, DEFAULT_POINT_DIAMETER);
        }
    }

    private void drawPoints(Graphics graphics, List<UAEVertex> vertices, Color color) {
        for (UAEVertex UAEVertex : vertices) {
            drawPoint(graphics, UAEVertex, color, DEFAULT_POINT_DIAMETER);
        }
    }

    public void drawPoint(Graphics graphics, UAEVertex UAEVertex, Color color, double diameter) {
        graphics.setColor(color);
        Graphics2D graphics2D = (Graphics2D) graphics;
        Ellipse2D.Double circle = new Ellipse2D.Double(
                UAEVertex.x - diameter / 2, UAEVertex.y - diameter / 2, diameter, diameter);
        graphics2D.fill(circle);
    }

    public void drawLine(Graphics graphics, UAEVertex point1, UAEVertex point2, Color color) {
        graphics.setColor(color);
        graphics.setColor(color);
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.drawLine((int) point1.x, (int) point1.y, (int) point2.x, (int) point2.y);
    }

}
