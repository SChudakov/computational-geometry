package com.chudakov.geometry.alg.convexhull.overmars;

import com.chudakov.geometry.common.Point2D;

import java.util.Iterator;

public class ConcatenableQueue implements Iterable<Point2D> {

    private CQNode root;

    public ConcatenableQueue(Point2D point) {

    }

    public ConcatenableQueue(Point2D left, Point2D right) {

    }

    public static ConcatenableQueue concatenate(ConcatenableQueue left, ConcatenableQueue right) {
        return null;
    }

    public void cutUp(CQNode node) {

    }

    public void balanceUp(CQNode node) {

    }

    private void rotateLeft(CQNode node) {

    }

    private void rotateRight(CQNode node) {

    }


    @Override
    public Iterator<Point2D> iterator() {
        return null;
    }

    static class CQNode {
        private CQNode left;
        private CQNode right;

        private CQNode leftSon;
        private CQNode rightSon;
        private CQNode parent;

        private Point2D point;
    }
}
