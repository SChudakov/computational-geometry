package com.chudakov.visualisation;

import com.chudakov.geometry.common.Point2D;

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

    List<Point2D> generated;
    List<Point2D> convexHull;

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

    private void drawPoints(Graphics graphics, List<Point2D> point2DS) {
        for (Point2D point2D : point2DS) {
            drawPoint(graphics, point2D, DEFAULT_POINTS_COLOR);
        }
    }

    private void drawLines(Graphics graphics, List<Point2D> point2DS) {
        for (int i = 0; i < point2DS.size() - 1; ++i) {
            drawLine(graphics, point2DS.get(i),
                    point2DS.get(i + 1),
                    DEFAULT_LINES_COLOR);
        }
        drawLine(graphics, point2DS.get(0),
                point2DS.get(point2DS.size() - 1),
                DEFAULT_LINES_COLOR);
    }

    public void drawPoint(Graphics graphics, Point2D point2D, Color color) {
        int diameter = 15;
        graphics.setColor(color);
        Graphics2D graphics2D = (Graphics2D) graphics;
        Ellipse2D.Double circle = new Ellipse2D.Double(point2D.x - diameter / 2,
                point2D.y - diameter / 2,
                diameter, diameter);
        graphics2D.fill(circle);
    }

    public void drawLine(Graphics graphics, Point2D point1, Point2D point2, Color color) {
        graphics.setColor(color);
        graphics.setColor(color);
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.drawLine((int) point1.x, (int) point1.y, (int) point2.x, (int) point2.y);
    }

}
