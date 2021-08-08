package com.chudakov.uae.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        UAEVertex point1 = new UAEVertex(3, 1);
        UAEVertex point2 = new UAEVertex(7, 1);
        UAEVertex point3 = new UAEVertex(12, 1);

        ConcatenableQueue<UAEVertex> left = new ConcatenableQueue<>();
        left.add(point1);

        ConcatenableQueue<UAEVertex> right = new ConcatenableQueue<>();
        right.add(point2);
        right.add(point3);

        Pair<ConcatenableQueue.CQVertex<UAEVertex>, ConcatenableQueue.CQVertex<UAEVertex>> tangent =
                CH.tangent(left, right, CH::getLowerTangentCase);


        assertEquals(point1, tangent.getLeft().value);
        assertEquals(point3, tangent.getRight().value);
    }

    @Test
    public void testTangents2() {
        UAEVertex point1 = new UAEVertex(2, 4);
        UAEVertex point2 = new UAEVertex(2, 6);
        UAEVertex point3 = new UAEVertex(2, 8);
        UAEVertex point4 = new UAEVertex(2, 10);
        UAEVertex point5 = new UAEVertex(4, 10);
        UAEVertex point6 = new UAEVertex(4, 4);

        ConcatenableQueue<UAEVertex> left = new ConcatenableQueue<>();
        left.add(point2);
        left.add(point3);
        left.add(point1);

        ConcatenableQueue<UAEVertex> right1 = new ConcatenableQueue<>();
        right1.add(point4);
        right1.add(point5);

        ConcatenableQueue<UAEVertex> right2 = new ConcatenableQueue<>();
        right2.add(point6);

        ConcatenableQueue<UAEVertex> right = ConcatenableQueue.concatenate(right1, right2);

        Pair<ConcatenableQueue.CQVertex<UAEVertex>, ConcatenableQueue.CQVertex<UAEVertex>> tangent =
                CH.tangent(left, right, CH::getUpperTangentCase);


        assertEquals(point1, tangent.getLeft().value);
        assertEquals(point4, tangent.getRight().value);
    }

    @Test
    public void testDetermineCase() {

//      ----------------------------------   one point case -------------------------------------------------

        ConcatenableQueue.CQVertex<UAEVertex> vertex1_1 = new ConcatenableQueue.CQVertex<>(new UAEVertex(1, 1));
        CQUtil.setFields(vertex1_1, null, null, vertex1_1, 0, true);


        assertEquals(0, CH.getUpperTangentCase(vertex1_1, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getUpperTangentCase(vertex1_1, 0, ConvexHull.Position.RIGHT));

        assertEquals(0, CH.getLowerTangentCase(vertex1_1, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getLowerTangentCase(vertex1_1, 0, ConvexHull.Position.RIGHT));

//        ----------------------------------   two point case -------------------------------------------------


        ConcatenableQueue.CQVertex<UAEVertex> vertex2_1 = new ConcatenableQueue.CQVertex<>();
        ConcatenableQueue.CQVertex<UAEVertex> vertex2_2 = new ConcatenableQueue.CQVertex<>();
        CQUtil.setFields(vertex2_1, null, vertex2_2, vertex2_1, 0, true);
        CQUtil.setFields(vertex2_2, vertex2_1, null, vertex2_2, 0, true);

        vertex2_1.value = new UAEVertex(1, 1);
        vertex2_2.value = new UAEVertex(2, 2);

        assertEquals(-1, CH.getUpperTangentCase(vertex2_1, 0, ConvexHull.Position.LEFT));
        assertEquals(-1, CH.getUpperTangentCase(vertex2_1, 0, ConvexHull.Position.RIGHT));
        assertEquals(0, CH.getUpperTangentCase(vertex2_2, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getUpperTangentCase(vertex2_2, 0, ConvexHull.Position.RIGHT));

        assertEquals(0, CH.getLowerTangentCase(vertex2_1, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getLowerTangentCase(vertex2_1, 0, ConvexHull.Position.RIGHT));
        assertEquals(+1, CH.getLowerTangentCase(vertex2_2, 0, ConvexHull.Position.LEFT));
        assertEquals(+1, CH.getLowerTangentCase(vertex2_2, 0, ConvexHull.Position.RIGHT));

        vertex2_1.value = new UAEVertex(2, 2);
        vertex2_2.value = new UAEVertex(3, 2);

        assertEquals(0, CH.getUpperTangentCase(vertex2_1, 0, ConvexHull.Position.LEFT));
        assertEquals(-1, CH.getUpperTangentCase(vertex2_1, 0, ConvexHull.Position.RIGHT));
        assertEquals(+1, CH.getUpperTangentCase(vertex2_2, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getUpperTangentCase(vertex2_2, 0, ConvexHull.Position.RIGHT));

        assertEquals(0, CH.getLowerTangentCase(vertex2_1, 0, ConvexHull.Position.LEFT));
        assertEquals(-1, CH.getLowerTangentCase(vertex2_1, 0, ConvexHull.Position.RIGHT));
        assertEquals(+1, CH.getLowerTangentCase(vertex2_2, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getLowerTangentCase(vertex2_2, 0, ConvexHull.Position.RIGHT));

        vertex2_1.value = new UAEVertex(2, 2);
        vertex2_2.value = new UAEVertex(3, 1);

        assertEquals(0, CH.getUpperTangentCase(vertex2_1, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getUpperTangentCase(vertex2_1, 0, ConvexHull.Position.RIGHT));
        assertEquals(+1, CH.getUpperTangentCase(vertex2_2, 0, ConvexHull.Position.LEFT));
        assertEquals(+1, CH.getUpperTangentCase(vertex2_2, 0, ConvexHull.Position.RIGHT));

        assertEquals(-1, CH.getLowerTangentCase(vertex2_1, 0, ConvexHull.Position.LEFT));
        assertEquals(-1, CH.getLowerTangentCase(vertex2_1, 0, ConvexHull.Position.RIGHT));
        assertEquals(0, CH.getLowerTangentCase(vertex2_2, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getLowerTangentCase(vertex2_2, 0, ConvexHull.Position.RIGHT));


//      ----------------------------------   three point case -------------------------------------------------


        ConcatenableQueue.CQVertex<UAEVertex> vertex3_1 = new ConcatenableQueue.CQVertex<>();
        ConcatenableQueue.CQVertex<UAEVertex> vertex3_2 = new ConcatenableQueue.CQVertex<>();
        ConcatenableQueue.CQVertex<UAEVertex> vertex3_3 = new ConcatenableQueue.CQVertex<>();
        CQUtil.setFields(vertex3_1, null, vertex3_2, vertex3_1, 0, true);
        CQUtil.setFields(vertex3_2, vertex3_1, vertex3_3, vertex3_2, 0, true);
        CQUtil.setFields(vertex3_3, vertex3_2, null, vertex3_3, 0, true);


//      ---------------------------------------      1st row     -----------------------------------------------------


        vertex3_1.value = new UAEVertex(1, 1);
        vertex3_2.value = new UAEVertex(2, 3);
        vertex3_3.value = new UAEVertex(3, 4);

        assertEquals(-1, CH.getUpperTangentCase(vertex3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(-1, CH.getUpperTangentCase(vertex3_2, 0, ConvexHull.Position.RIGHT));


        vertex3_1.value = new UAEVertex(1, 1);
        vertex3_2.value = new UAEVertex(2, 3);
        vertex3_3.value = new UAEVertex(3, 3);

        assertEquals(0, CH.getUpperTangentCase(vertex3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(-1, CH.getUpperTangentCase(vertex3_2, 0, ConvexHull.Position.RIGHT));


        vertex3_1.value = new UAEVertex(1, 1);
        vertex3_2.value = new UAEVertex(2, 2);
        vertex3_3.value = new UAEVertex(3, 1);

        assertEquals(0, CH.getUpperTangentCase(vertex3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getUpperTangentCase(vertex3_2, 0, ConvexHull.Position.RIGHT));

//        ---------------------------------------      2nd row     -----------------------------------------------------

        vertex3_1.value = new UAEVertex(1, 2);
        vertex3_2.value = new UAEVertex(2, 2);
        vertex3_3.value = new UAEVertex(3, 3);

        assertEquals(+1, CH.getLowerTangentCase(vertex3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getLowerTangentCase(vertex3_2, 0, ConvexHull.Position.RIGHT));


        vertex3_1.value = new UAEVertex(1, 2);
        vertex3_2.value = new UAEVertex(2, 2);
        vertex3_3.value = new UAEVertex(3, 2);

        assertEquals(+1, CH.getUpperTangentCase(vertex3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(-1, CH.getUpperTangentCase(vertex3_2, 0, ConvexHull.Position.RIGHT));
        assertEquals(+1, CH.getLowerTangentCase(vertex3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(-1, CH.getLowerTangentCase(vertex3_2, 0, ConvexHull.Position.RIGHT));


        vertex3_1.value = new UAEVertex(1, 2);
        vertex3_2.value = new UAEVertex(2, 2);
        vertex3_3.value = new UAEVertex(3, 1);

        assertEquals(+1, CH.getUpperTangentCase(vertex3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getUpperTangentCase(vertex3_2, 0, ConvexHull.Position.RIGHT));


//      ---------------------------------------      3rd row     -----------------------------------------------------


        vertex3_1.value = new UAEVertex(1, 2);
        vertex3_2.value = new UAEVertex(2, 1);
        vertex3_3.value = new UAEVertex(3, 2);

        assertEquals(0, CH.getLowerTangentCase(vertex3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(0, CH.getLowerTangentCase(vertex3_2, 0, ConvexHull.Position.RIGHT));

        vertex3_1.value = new UAEVertex(1, 2);
        vertex3_2.value = new UAEVertex(2, 1);
        vertex3_3.value = new UAEVertex(3, 1);

        assertEquals(0, CH.getLowerTangentCase(vertex3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(-1, CH.getLowerTangentCase(vertex3_2, 0, ConvexHull.Position.RIGHT));


        vertex3_1.value = new UAEVertex(1, 2);
        vertex3_2.value = new UAEVertex(2, 1);
        vertex3_3.value = new UAEVertex(3, 0);

        assertEquals(-1, CH.getLowerTangentCase(vertex3_2, 0, ConvexHull.Position.LEFT));
        assertEquals(-1, CH.getLowerTangentCase(vertex3_2, 0, ConvexHull.Position.RIGHT));
    }
}