package com.chudakov.visualisation;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;

public class ConvexHullVisualisation extends JFrame {

    private static final double FRAME_SIZE_COEFFICIENT = 1;

    public ConvexHullVisualisation() throws HeadlessException {
        super();
        configureFrameSize();
        setTitle("UAE visualisation");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void configureFrameSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        double frameWidth = width * FRAME_SIZE_COEFFICIENT;
        double frameHeight = height * FRAME_SIZE_COEFFICIENT;
        this.setSize(
                (int) frameWidth,
                (int) frameHeight
        );
    }
}
