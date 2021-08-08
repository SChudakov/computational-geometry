package com.chudakov.uae.impl;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CQUtil {


    static <T> void setFields(ConcatenableQueue.CQVertex<T> vertex,
                              ConcatenableQueue.CQVertex<T> left,
                              ConcatenableQueue.CQVertex<T> right,
                              ConcatenableQueue.CQVertex<T> lMax,
                              int height, boolean isLeaf) {
        vertex.lSon = left;
        vertex.rSon = right;
        vertex.leftSubtreeMax = lMax;
        vertex.height = height;
        vertex.isLeaf = isLeaf;
    }

    static <T> void setFields(ConcatenableQueue<T> queue,
                              ConcatenableQueue.CQVertex<T> root,
                              ConcatenableQueue.CQVertex<T> minVertex,
                              ConcatenableQueue.CQVertex<T> maxVertex,
                              Comparator<T> cmp) {

        queue.root = root;
        queue.minVertex = minVertex;
        queue.maxVertex = maxVertex;
        queue.cmp = cmp;
    }

    static <T> void assertSameFields(ConcatenableQueue.CQVertex<T> vertex,
                                     T vertexData,
                                     ConcatenableQueue.CQVertex<T> left,
                                     ConcatenableQueue.CQVertex<T> right,
                                     ConcatenableQueue.CQVertex<T> lmax,
                                     int height, boolean isLeaf) {
        assertNotNull(vertex);
        assertEquals(vertexData, vertex.value);
        assertEquals(left, vertex.lSon);
        assertEquals(right, vertex.rSon);
        assertEquals(height, vertex.height);
        assertEquals(isLeaf, vertex.isLeaf);
        assertEquals(lmax, vertex.leftSubtreeMax);
    }

    static <T> void assertSameFields(ConcatenableQueue<T> queue,
                                     ConcatenableQueue.CQVertex<T> root,
                                     ConcatenableQueue.CQVertex<T> minVertex,
                                     ConcatenableQueue.CQVertex<T> maxVertex,
                                     Comparator<T> comparator) {
        assertNotNull(queue);
        assertEquals(root, queue.root);
        assertEquals(minVertex, queue.minVertex);
        assertEquals(maxVertex, queue.maxVertex);
        assertEquals(comparator, queue.cmp);
    }
}
