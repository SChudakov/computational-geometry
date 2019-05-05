package com.chudakov.geometry.alg.convexhull.overmars;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CQUtil {


    static <T> void setFields(ConcatenableQueue.Node<T> node,
                              ConcatenableQueue.Node<T> left,
                              ConcatenableQueue.Node<T> right,
                              ConcatenableQueue.Node<T> lMax,
                              int height, boolean isLeaf) {
        node.left = left;
        node.right = right;
        node.lMax = lMax;
        node.height = height;
        node.isLeaf = isLeaf;
    }

    static <T> void setFields(ConcatenableQueue<T> queue,
                              ConcatenableQueue.Node<T> root,
                              ConcatenableQueue.Node<T> minNode,
                              ConcatenableQueue.Node<T> maxNode,
                              Comparator<T> cmp) {

        queue.root = root;
        queue.minNode = minNode;
        queue.maxNode = maxNode;
        queue.cmp = cmp;
    }

    static <T> void assertSameFields(ConcatenableQueue.Node<T> node,
                                     T nodeData,
                                     ConcatenableQueue.Node<T> left,
                                     ConcatenableQueue.Node<T> right,
                                     ConcatenableQueue.Node<T> lmax,
                                     int height, boolean isLeaf) {
        assertNotNull(node);
        assertEquals(nodeData, node.data);
        assertEquals(left, node.left);
        assertEquals(right, node.right);
        assertEquals(height, node.height);
        assertEquals(isLeaf, node.isLeaf);
        assertEquals(lmax, node.lMax);
    }

    static <T> void assertSameFields(ConcatenableQueue<T> queue,
                                     ConcatenableQueue.Node<T> root,
                                     ConcatenableQueue.Node<T> minNode,
                                     ConcatenableQueue.Node<T> maxNode,
                                     Comparator<T> comparator) {
        assertNotNull(queue);
        assertEquals(root, queue.root);
        assertEquals(minNode, queue.minNode);
        assertEquals(maxNode, queue.maxNode);
        assertEquals(comparator, queue.cmp);
    }
}
