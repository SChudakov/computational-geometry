package com.chudakov.uae.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConvexHullTest {

//    @Test
//    public void testJoin() {
//        Point2D p1 = new Point2D(4, 2);
//        Point2D p2 = new Point2D(5, 0);
//        Point2D p3 = new Point2D(5, 1);
//        Point2D p4 = new Point2D(6, 2);
//        ConcatenableQueue<Point2D> leftUpper = new ConcatenableQueue<>();
//        leftUpper.add(p1);
//        ConcatenableQueue<Point2D> leftLower = new ConcatenableQueue<>();
//        leftLower.add(p2);
//        ConcatenableQueue<Point2D> rightUpper = new ConcatenableQueue<>();
//        rightUpper.add(p3);
//        ConcatenableQueue<Point2D> rightLower = new ConcatenableQueue<>();
//        rightLower.add(p4);
//
//        ConvexHull left = new ConvexHull(
//                new ConvexSubhull(
//                        leftUpper,
//                        ConvexSubhull.Type.UPPER
//                ),
//                new ConvexSubhull(
//                        leftLower,
//                        ConvexSubhull.Type.LOWER
//                ));
//        ConvexHull right = new ConvexHull(
//                new ConvexSubhull(
//                        rightUpper,
//                        ConvexSubhull.Type.UPPER
//                ),
//                new ConvexSubhull(
//                        rightLower,
//                        ConvexSubhull.Type.LOWER
//                )
//        );
//
//        ConvexHull joined = ConvexHull.join(left, right);
//
//        ConcatenableQueue<Point2D> upper = joined.upperSubhull.subhull;
//        ConcatenableQueue<Point2D> lower = joined.lowerSubhull.subhull;
//
//        ConcatenableQueue.CQNode<Point2D> upperRoot = upper.root;
//        ConcatenableQueue.CQNode<Point2D> upperRootLeft = upper.root.left;
//        ConcatenableQueue.CQNode<Point2D> upperRootRight = upper.root.right;
//        CQUtil.assertSameFields(upperRoot, null, upperRootLeft, upperRootRight, upperRootLeft, 1, false);
//        assertNotNull(upperRootLeft);
//        assertNotNull(upperRootRight);
//        CQUtil.assertSameFields(upperRootLeft, p1, null, upperRootRight, upperRootLeft, 0, true);
//        CQUtil.assertSameFields(upperRootRight, p4, upperRootLeft, null, upperRootRight, 0, true);
//
//
//        ConcatenableQueue.CQNode<Point2D> lowerRoot = lower.root;
//        assertNotNull(lowerRoot);
//        CQUtil.assertSameFields(lowerRoot, p2, null, null, lowerRoot, 0, true);
//    }

    @Test
    public void testTangents1() {
        Vertex point1 = new Vertex(3, 1);
        Vertex point2 = new Vertex(7, 1);
        Vertex point3 = new Vertex(12, 1);

        ConcatenableQueue<Vertex> left = new ConcatenableQueue<>();
        left.add(point1);

        ConcatenableQueue<Vertex> right = new ConcatenableQueue<>();
        right.add(point2);
        right.add(point3);

        Pair<ConcatenableQueue.CQVertex<Vertex>, ConcatenableQueue.CQVertex<Vertex>> tangent =
                CH.tangent(left, right, CH::getLowerTangentCase);


        assertEquals(point1, tangent.getLeft().value);
        assertEquals(point3, tangent.getRight().value);
    }

    @Test
    public void testTangents2() {
        Vertex point1 = new Vertex(2, 4);
        Vertex point2 = new Vertex(2, 6);
        Vertex point3 = new Vertex(2, 8);
        Vertex point4 = new Vertex(2, 10);
        Vertex point5 = new Vertex(4, 10);
        Vertex point6 = new Vertex(4, 4);

        ConcatenableQueue<Vertex> left = new ConcatenableQueue<>();
        left.add(point2);
        left.add(point3);
        left.add(point1);

        ConcatenableQueue<Vertex> right1 = new ConcatenableQueue<>();
        right1.add(point4);
        right1.add(point5);

        ConcatenableQueue<Vertex> right2 = new ConcatenableQueue<>();
        right2.add(point6);

        ConcatenableQueue<Vertex> right = ConcatenableQueue.concatenate(right1, right2);

        Pair<ConcatenableQueue.CQVertex<Vertex>, ConcatenableQueue.CQVertex<Vertex>> tangent =
                CH.tangent(left, right, CH::getUpperTangentCase);


        assertEquals(point1, tangent.getLeft().value);
        assertEquals(point4, tangent.getRight().value);
    }

    @Test
    public void testDetermineCase() {

//      ----------------------------------   one point case -------------------------------------------------

        ConcatenableQueue.CQVertex<Vertex> node1_1 = new ConcatenableQueue.CQVertex<>(new Vertex(1, 1));
        CQUtil.setFields(node1_1, null, null, node1_1, 0, true);


        assertEquals(0, CH.getUpperTangentCase(node1_1, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getUpperTangentCase(node1_1, 0, ConvexHull.Position.RIGHT));

        assertEquals(0, CH.getLowerTangentCase(node1_1, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getLowerTangentCase(node1_1, 0, ConvexHull.Position.RIGHT));

        assertEquals(0, CH.getLowerBaseCase(node1_1, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getLowerBaseCase(node1_1, 0, ConvexHull.Position.RIGHT));


//        ----------------------------------   two point case -------------------------------------------------


        ConcatenableQueue.CQVertex<Vertex> node2_1 = new ConcatenableQueue.CQVertex<>();
        ConcatenableQueue.CQVertex<Vertex> node2_2 = new ConcatenableQueue.CQVertex<>();
        CQUtil.setFields(node2_1, null, node2_2, node2_1, 0, true);
        CQUtil.setFields(node2_2, node2_1, null, node2_2, 0, true);

        node2_1.value = new Vertex(1, 1);
        node2_2.value = new Vertex(2, 2);

        assertEquals(-1, CH.getUpperTangentCase(node2_1, 0, ConvexHull.Position.LEFT));
        assertEquals(-1, CH.getUpperTangentCase(node2_1, 0, ConvexHull.Position.RIGHT));
        assertEquals(0, CH.getUpperTangentCase(node2_2, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getUpperTangentCase(node2_2, 0, ConvexHull.Position.RIGHT));

        assertEquals(0, CH.getLowerTangentCase(node2_1, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getLowerTangentCase(node2_1, 0, ConvexHull.Position.RIGHT));
        assertEquals(+1, CH.getLowerTangentCase(node2_2, 0, ConvexHull.Position.LEFT));
        assertEquals(+1, CH.getLowerTangentCase(node2_2, 0, ConvexHull.Position.RIGHT));

        assertEquals(0, CH.getLowerBaseCase(node2_1, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getLowerBaseCase(node2_1, 0, ConvexHull.Position.RIGHT));
        assertEquals(+1, CH.getLowerBaseCase(node2_2, 0, ConvexHull.Position.LEFT));
        assertEquals(+1, CH.getLowerBaseCase(node2_2, 0, ConvexHull.Position.RIGHT));

        node2_1.value = new Vertex(2, 2);
        node2_2.value = new Vertex(3, 2);

        assertEquals(0, CH.getUpperTangentCase(node2_1, 0, ConvexHull.Position.LEFT));
        assertEquals(-1, CH.getUpperTangentCase(node2_1, 0, ConvexHull.Position.RIGHT));
        assertEquals(+1, CH.getUpperTangentCase(node2_2, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getUpperTangentCase(node2_2, 0, ConvexHull.Position.RIGHT));

        assertEquals(0, CH.getLowerTangentCase(node2_1, 0, ConvexHull.Position.LEFT));
        assertEquals(-1, CH.getLowerTangentCase(node2_1, 0, ConvexHull.Position.RIGHT));
        assertEquals(+1, CH.getLowerTangentCase(node2_2, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getLowerTangentCase(node2_2, 0, ConvexHull.Position.RIGHT));

        assertEquals(-1, CH.getLowerBaseCase(node2_1, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getLowerBaseCase(node2_1, 0, ConvexHull.Position.RIGHT));
        assertEquals(0, CH.getLowerBaseCase(node2_2, 0, ConvexHull.Position.LEFT));
        assertEquals(+1, CH.getLowerBaseCase(node2_2, 0, ConvexHull.Position.RIGHT));

        node2_1.value = new Vertex(2, 2);
        node2_2.value = new Vertex(3, 1);

        assertEquals(0, CH.getUpperTangentCase(node2_1, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getUpperTangentCase(node2_1, 0, ConvexHull.Position.RIGHT));
        assertEquals(+1, CH.getUpperTangentCase(node2_2, 0, ConvexHull.Position.LEFT));
        assertEquals(+1, CH.getUpperTangentCase(node2_2, 0, ConvexHull.Position.RIGHT));

        assertEquals(-1, CH.getLowerTangentCase(node2_1, 0, ConvexHull.Position.LEFT));
        assertEquals(-1, CH.getLowerTangentCase(node2_1, 0, ConvexHull.Position.RIGHT));
        assertEquals(0, CH.getLowerTangentCase(node2_2, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getLowerTangentCase(node2_2, 0, ConvexHull.Position.RIGHT));

        assertEquals(-1, CH.getLowerBaseCase(node2_1, 0, ConvexHull.Position.LEFT));
        assertEquals(-1, CH.getLowerBaseCase(node2_1, 0, ConvexHull.Position.RIGHT));
        assertEquals(0, CH.getLowerBaseCase(node2_2, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getLowerBaseCase(node2_2, 0, ConvexHull.Position.RIGHT));


//      ----------------------------------   three point case -------------------------------------------------


        ConcatenableQueue.CQVertex<Vertex> node3_1 = new ConcatenableQueue.CQVertex<>();
        ConcatenableQueue.CQVertex<Vertex> node3_2 = new ConcatenableQueue.CQVertex<>();
        ConcatenableQueue.CQVertex<Vertex> node3_3 = new ConcatenableQueue.CQVertex<>();
        CQUtil.setFields(node3_1, null, node3_2, node3_1, 0, true);
        CQUtil.setFields(node3_2, node3_1, node3_3, node3_2, 0, true);
        CQUtil.setFields(node3_3, node3_2, null, node3_3, 0, true);


//      ---------------------------------------      1st row     -----------------------------------------------------


        node3_1.value = new Vertex(1, 1);
        node3_2.value = new Vertex(2, 3);
        node3_3.value = new Vertex(3, 4);

        assertEquals(-1, CH.getUpperTangentCase(node3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(-1, CH.getUpperTangentCase(node3_2, 0, ConvexHull.Position.RIGHT));


        node3_1.value = new Vertex(1, 1);
        node3_2.value = new Vertex(2, 3);
        node3_3.value = new Vertex(3, 3);

        assertEquals(0, CH.getUpperTangentCase(node3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(-1, CH.getUpperTangentCase(node3_2, 0, ConvexHull.Position.RIGHT));


        node3_1.value = new Vertex(1, 1);
        node3_2.value = new Vertex(2, 2);
        node3_3.value = new Vertex(3, 1);

        assertEquals(0, CH.getUpperTangentCase(node3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getUpperTangentCase(node3_2, 0, ConvexHull.Position.RIGHT));

//        ---------------------------------------      2nd row     -----------------------------------------------------

        node3_1.value = new Vertex(1, 2);
        node3_2.value = new Vertex(2, 2);
        node3_3.value = new Vertex(3, 3);

        assertEquals(+1, CH.getLowerTangentCase(node3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getLowerTangentCase(node3_2, 0, ConvexHull.Position.RIGHT));

        assertEquals(0, CH.getLowerBaseCase(node3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(+1, CH.getLowerBaseCase(node3_2, 0, ConvexHull.Position.RIGHT));


        node3_1.value = new Vertex(1, 2);
        node3_2.value = new Vertex(2, 2);
        node3_3.value = new Vertex(3, 2);

        assertEquals(+1, CH.getUpperTangentCase(node3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(-1, CH.getUpperTangentCase(node3_2, 0, ConvexHull.Position.RIGHT));
        assertEquals(+1, CH.getLowerTangentCase(node3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(-1, CH.getLowerTangentCase(node3_2, 0, ConvexHull.Position.RIGHT));

        assertEquals(-1, CH.getLowerBaseCase(node3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(+1, CH.getLowerBaseCase(node3_2, 0, ConvexHull.Position.RIGHT));


        node3_1.value = new Vertex(1, 2);
        node3_2.value = new Vertex(2, 2);
        node3_3.value = new Vertex(3, 1);

        assertEquals(+1, CH.getUpperTangentCase(node3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getUpperTangentCase(node3_2, 0, ConvexHull.Position.RIGHT));


//      ---------------------------------------      3rd row     -----------------------------------------------------


        node3_1.value = new Vertex(1, 2);
        node3_2.value = new Vertex(2, 1);
        node3_3.value = new Vertex(3, 2);

        assertEquals(0, CH.getLowerTangentCase(node3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getLowerTangentCase(node3_2, 0, ConvexHull.Position.RIGHT));

        assertEquals(0, CH.getLowerBaseCase(node3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getLowerBaseCase(node3_2, 0, ConvexHull.Position.RIGHT));

        node3_1.value = new Vertex(1, 2);
        node3_2.value = new Vertex(2, 1);
        node3_3.value = new Vertex(3, 1);

        assertEquals(0, CH.getLowerTangentCase(node3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(-1, CH.getLowerTangentCase(node3_2, 0, ConvexHull.Position.RIGHT));

        assertEquals(-1, CH.getLowerBaseCase(node3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getLowerBaseCase(node3_2, 0, ConvexHull.Position.RIGHT));


        node3_1.value = new Vertex(1, 2);
        node3_2.value = new Vertex(2, 1);
        node3_3.value = new Vertex(3, 0);

        assertEquals(-1, CH.getLowerTangentCase(node3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(-1, CH.getLowerTangentCase(node3_2, 0, ConvexHull.Position.RIGHT));

        assertEquals(-1, CH.getLowerBaseCase(node3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(-1, CH.getLowerBaseCase(node3_2, 0, ConvexHull.Position.RIGHT));
    }
}