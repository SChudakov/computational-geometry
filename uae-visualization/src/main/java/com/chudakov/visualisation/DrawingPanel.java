package com.chudakov.visualisation;

import com.chudakov.uae.impl.UAEVertex;

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

    List<UAEVertex> generated;
    List<UAEVertex> convexHull;

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

    private void drawPoints(Graphics graphics, List<UAEVertex> vertices) {
        for (UAEVertex UAEVertex : vertices) {
            drawPoint(graphics, UAEVertex, DEFAULT_POINTS_COLOR);
        }
    }

    private void drawLines(Graphics graphics, List<UAEVertex> vertices) {
        for (int i = 0; i < vertices.size() - 1; ++i) {
            drawLine(graphics, vertices.get(i),
                    vertices.get(i + 1),
                    DEFAULT_LINES_COLOR);
        }
        drawLine(graphics, vertices.get(0),
                vertices.get(vertices.size() - 1),
                DEFAULT_LINES_COLOR);
    }

    public void drawPoint(Graphics graphics, UAEVertex UAEVertex, Color color) {
        int diameter = 15;
        graphics.setColor(color);
        Graphics2D graphics2D = (Graphics2D) graphics;
        Ellipse2D.Double circle = new Ellipse2D.Double(UAEVertex.x - diameter / 2,
                UAEVertex.y - diameter / 2,
                diameter, diameter);
        graphics2D.fill(circle);
    }

    public void drawLine(Graphics graphics, UAEVertex point1, UAEVertex point2, Color color) {
        graphics.setColor(color);
        graphics.setColor(color);
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.drawLine((int) point1.x, (int) point1.y, (int) point2.x, (int) point2.y);
    }

}
