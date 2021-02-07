package com.chudakov.visualisation;

import com.chudakov.uae.impl.Vertex;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.List;

public class DrawingPanel extends JPanel {
    private static final Color DEFAULT_POINTS_COLOR = Color.BLUE;
    private static final Color DEFAULT_LINES_COLOR = Color.RED;

    List<Vertex> generated;
    List<Vertex> convexHull;

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        ((Graphics2D) graphics).setStroke(new BasicStroke(5));
        if (generated != null) {
            drawPoints(graphics, generated);
        }
        if (convexHull != null) {
            drawLines(graphics, convexHull);
        }
    }

    private void drawPoints(Graphics graphics, List<Vertex> vertices) {
        for (Vertex vertex : vertices) {
            drawPoint(graphics, vertex, DEFAULT_POINTS_COLOR);
        }
    }

    private void drawLines(Graphics graphics, List<Vertex> vertices) {
        for (int i = 0; i < vertices.size() - 1; ++i) {
            drawLine(graphics, vertices.get(i),
                    vertices.get(i + 1),
                    DEFAULT_LINES_COLOR);
        }
        drawLine(graphics, vertices.get(0),
                vertices.get(vertices.size() - 1),
                DEFAULT_LINES_COLOR);
    }

    public void drawPoint(Graphics graphics, Vertex vertex, Color color) {
        int diameter = 15;
        graphics.setColor(color);
        Graphics2D graphics2D = (Graphics2D) graphics;
        Ellipse2D.Double circle = new Ellipse2D.Double(vertex.x - diameter / 2,
                vertex.y - diameter / 2,
                diameter, diameter);
        graphics2D.fill(circle);
    }

    public void drawLine(Graphics graphics, Vertex point1, Vertex point2, Color color) {
        graphics.setColor(color);
        graphics.setColor(color);
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.drawLine((int) point1.x, (int) point1.y, (int) point2.x, (int) point2.y);
    }

}
