package com.chudakov.geometry.datastructure;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CQUtil {


    static <T> void setFields(ConcatenableQueue.CQNode<T> node,
                              ConcatenableQueue.CQNode<T> left,
                              ConcatenableQueue.CQNode<T> right,
                              ConcatenableQueue.CQNode<T> lMax,
                              int height, boolean isLeaf) {
        node.left = left;
        node.right = right;
        node.leftSubtreeMax = lMax;
        node.height = height;
        node.isLeaf = isLeaf;
    }

    static <T> void setFields(ConcatenableQueue<T> queue,
                              ConcatenableQueue.CQNode<T> root,
                              ConcatenableQueue.CQNode<T> minNode,
                              ConcatenableQueue.CQNode<T> maxNode,
                              Comparator<T> cmp) {

        queue.root = root;
        queue.minNode = minNode;
        queue.maxNode = maxNode;
        queue.cmp = cmp;
    }

    static <T> void assertSameFields(ConcatenableQueue.CQNode<T> node,
                                     T nodeData,
                                     ConcatenableQueue.CQNode<T> left,
                                     ConcatenableQueue.CQNode<T> right,
                                     ConcatenableQueue.CQNode<T> lmax,
                                     int height, boolean isLeaf) {
        assertNotNull(node);
        assertEquals(nodeData, node.data);
        assertEquals(left, node.left);
        assertEquals(right, node.right);
        assertEquals(height, node.height);
        assertEquals(isLeaf, node.isLeaf);
        assertEquals(lmax, node.leftSubtreeMax);
    }

    static <T> void assertSameFields(ConcatenableQueue<T> queue,
                                     ConcatenableQueue.CQNode<T> root,
                                     ConcatenableQueue.CQNode<T> minNode,
                                     ConcatenableQueue.CQNode<T> maxNode,
                                     Comparator<T> comparator) {
        assertNotNull(queue);
        assertEquals(root, queue.root);
        assertEquals(minNode, queue.minNode);
        assertEquals(maxNode, queue.maxNode);
        assertEquals(comparator, queue.cmp);
    }
}
