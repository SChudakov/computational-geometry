package com.chudakov.geometry.uae;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CQUtil {


    static <T> void setFields(ConcatenableQueue.CQVertex<T> node,
                              ConcatenableQueue.CQVertex<T> left,
                              ConcatenableQueue.CQVertex<T> right,
                              ConcatenableQueue.CQVertex<T> lMax,
                              int height, boolean isLeaf) {
        node.lSon = left;
        node.rSon = right;
        node.leftSubtreeMax = lMax;
        node.height = height;
        node.isLeaf = isLeaf;
    }

    static <T> void setFields(ConcatenableQueue<T> queue,
                              ConcatenableQueue.CQVertex<T> root,
                              ConcatenableQueue.CQVertex<T> minNode,
                              ConcatenableQueue.CQVertex<T> maxNode,
                              Comparator<T> cmp) {

        queue.root = root;
        queue.minNode = minNode;
        queue.maxNode = maxNode;
        queue.cmp = cmp;
    }

    static <T> void assertSameFields(ConcatenableQueue.CQVertex<T> node,
                                     T nodeData,
                                     ConcatenableQueue.CQVertex<T> left,
                                     ConcatenableQueue.CQVertex<T> right,
                                     ConcatenableQueue.CQVertex<T> lmax,
                                     int height, boolean isLeaf) {
        assertNotNull(node);
        assertEquals(nodeData, node.value);
        assertEquals(left, node.lSon);
        assertEquals(right, node.rSon);
        assertEquals(height, node.height);
        assertEquals(isLeaf, node.isLeaf);
        assertEquals(lmax, node.leftSubtreeMax);
    }

    static <T> void assertSameFields(ConcatenableQueue<T> queue,
                                     ConcatenableQueue.CQVertex<T> root,
                                     ConcatenableQueue.CQVertex<T> minNode,
                                     ConcatenableQueue.CQVertex<T> maxNode,
                                     Comparator<T> comparator) {
        assertNotNull(queue);
        assertEquals(root, queue.root);
        assertEquals(minNode, queue.minNode);
        assertEquals(maxNode, queue.maxNode);
        assertEquals(comparator, queue.cmp);
    }
}
