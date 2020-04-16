package com.chudakov.visualisation;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.chudakov.geometry.uae.SequentialUAE2D;
import com.chudakov.geometry.uae.Vertex2D;
import com.chudakov.geometry.uae.ConvexHull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Semen
 */
public class JFrame extends javax.swing.JFrame {
    private SequentialUAE2D convexHull2D;
    private List<Vertex2D> generated;
    private List<Vertex2D> convexHull;

    /**
     * Creates new form JFrame
     */
    public JFrame() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        this.convexHull2D = new SequentialUAE2D();

        pointsPanel = new DrawingPanel();

        buttonsPanel = new javax.swing.JPanel();
        numOfPointsTextField = new javax.swing.JTextField();
        buildButton = new javax.swing.JButton();
        generateButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout pointsPanelLayout = new javax.swing.GroupLayout(pointsPanel);
        pointsPanel.setLayout(pointsPanelLayout);
        pointsPanelLayout.setHorizontalGroup(
                pointsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 666, Short.MAX_VALUE)
        );
        pointsPanelLayout.setVerticalGroup(
                pointsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 782, Short.MAX_VALUE)
        );

        buildButton.setText("Build");
        buildButton.setActionCommand("");
        buildButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildButtonActionPerformed(evt);
            }
        });

        generateButton.setText("Generate");
        generateButton.setAutoscrolls(true);
        generateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(numOfPointsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(buildButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(generateButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(numOfPointsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(buildButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(generateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(pointsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(buttonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(pointsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
//        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new JFrame().setVisible(true);
        });
    }


    private void generateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateButtonActionPerformed
        try {
            String numOfPointsText = this.numOfPointsTextField.getText();
            int numOfPoints = Integer.valueOf(numOfPointsText);
            int width = this.pointsPanel.getWidth();
            int height = this.pointsPanel.getHeight();

            generated = generatePoints(numOfPoints, width, height);

            pointsPanel.generated = new ArrayList<>(generated);
            pointsPanel.convexHull = null;
            new Thread(() -> {
                pointsPanel.paintComponent(pointsPanel.getGraphics());
                pointsPanel.repaint();
                this.getContentPane().repaint();
            }).run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }//GEN-LAST:event_generateButtonActionPerformed

    private void buildButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (generated != null) {
            List<Vertex2D> convexHullPoints = new ArrayList<>();
            ConvexHull hull = this.convexHull2D.solve(this.generated).getConvexHull();
            for (Vertex2D vertex2D : hull) {
                convexHullPoints.add(vertex2D);
            }
            pointsPanel.convexHull = convexHullPoints;
            new Thread(() -> {
                pointsPanel.paintComponent(pointsPanel.getGraphics());
                pointsPanel.repaint();
                this.getContentPane().repaint();
            }).run();
        }
    }//GEN-LAST:event_jButton2ActionPerformed


    private List<Vertex2D> generatePoints(int size, int width, int height) {
        List<Vertex2D> result = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            result.add(new Vertex2D(
                    (int) (Math.random() * width),
                    (int) (Math.random() * height)
            ));
        }
        return result;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton generateButton;
    private javax.swing.JButton buildButton;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JTextField numOfPointsTextField;
    private DrawingPanel pointsPanel;
    // End of variables declaration//GEN-END:variables
}
