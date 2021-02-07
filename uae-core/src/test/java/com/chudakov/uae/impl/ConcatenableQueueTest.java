package com.chudakov.uae.impl;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ConcatenableQueueTest {

    private Comparator<Integer> cmp = Integer::compare;
    private Comparator<Integer> reversedCmp = (i1, i2) -> -Integer.compare(i1, i2);


    @Test
    public void testAdd1() {
        ConcatenableQueue<Integer> queue = new ConcatenableQueue<>();
        CQUtil.assertSameFields(queue, null, null, null, null);

        queue.add(1);
        ConcatenableQueue.CQVertex<Integer> root = queue.root;
        CQUtil.assertSameFields(root, 1, null, null, root, 0, true);

        CQUtil.assertSameFields(queue, root, root, root, null);

        queue.add(2);
        root = queue.root;
        ConcatenableQueue.CQVertex<Integer> left = root.lSon;
        ConcatenableQueue.CQVertex<Integer> right = root.rSon;
        CQUtil.assertSameFields(root, null, left, right, left, 1, false);
        CQUtil.assertSameFields(left, 1, null, right, left, 0, true);
        CQUtil.assertSameFields(right, 2, left, null, right, 0, true);

        CQUtil.assertSameFields(queue, root, left, right, null);
    }

    @Test
    public void testAdd2() {
        ConcatenableQueue<Integer> queue = new ConcatenableQueue<>(reversedCmp);
        queue.add(1);
        queue.add(2);
        ConcatenableQueue.CQVertex<Integer> root = queue.root;
        ConcatenableQueue.CQVertex<Integer> left = root.lSon;
        ConcatenableQueue.CQVertex<Integer> right = root.rSon;
        CQUtil.assertSameFields(root, null, left, right, left, 1, false);
        CQUtil.assertSameFields(left, 2, null, right, left, 0, true);
        CQUtil.assertSameFields(right, 1, left, null, right, 0, true);
        CQUtil.assertSameFields(queue, root, left, right, reversedCmp);
    }

    @Test
    public void testAdd3() {
        ConcatenableQueue<Integer> queue = new ConcatenableQueue<>(cmp);
        queue.add(2);
        queue.add(3);
        queue.add(1);
        queue.add(4);
        ConcatenableQueue.CQVertex<Integer> root = queue.root;
        ConcatenableQueue.CQVertex<Integer> rootLeft = root.lSon;
        ConcatenableQueue.CQVertex<Integer> rootRight = root.rSon;
        ConcatenableQueue.CQVertex<Integer> leaf1 = rootLeft.lSon;
        ConcatenableQueue.CQVertex<Integer> leaf2 = rootLeft.rSon;
        ConcatenableQueue.CQVertex<Integer> leaf3 = rootRight.lSon;
        ConcatenableQueue.CQVertex<Integer> leaf4 = rootRight.rSon;

        CQUtil.assertSameFields(queue, root, leaf1, leaf4, cmp);

        CQUtil.assertSameFields(root, null, rootLeft, rootRight, leaf2, 2, false);
        CQUtil.assertSameFields(rootLeft, null, leaf1, leaf2, leaf1, 1, false);
        CQUtil.assertSameFields(rootRight, null, leaf3, leaf4, leaf3, 1, false);
        CQUtil.assertSameFields(leaf1, 1, null, leaf2, leaf1, 0, true);
        CQUtil.assertSameFields(leaf2, 2, leaf1, leaf3, leaf2, 0, true);
        CQUtil.assertSameFields(leaf3, 3, leaf2, leaf4, leaf3, 0, true);
        CQUtil.assertSameFields(leaf4, 4, leaf3, null, leaf4, 0, true);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testCutLeft1() {
        ConcatenableQueue<Integer> queue = new ConcatenableQueue<>();
        queue.cutLeft(1);
    }

    @Test
    public void testCutLeft2() {
        ConcatenableQueue<Integer> queue = new ConcatenableQueue<>(cmp);

        queue.add(1);
        ConcatenableQueue.CQVertex<Integer> root = queue.root;

        ConcatenableQueue<Integer> left = queue.cutLeft(1);
        CQUtil.assertSameFields(queue, root, root, root, cmp);
        CQUtil.assertSameFields(left, null, null, null, cmp);
    }

    @Test
    public void testCutLeft3() {
        ConcatenableQueue<Integer> queue1 = new ConcatenableQueue<>(cmp);
        queue1.add(1);
        queue1.add(2);
        ConcatenableQueue.CQVertex<Integer> root1 = queue1.root;

        ConcatenableQueue<Integer> leftQueue1 = queue1.cutLeft(1);
        CQUtil.assertSameFields(queue1, root1, root1.lSon, root1.rSon, cmp);
        CQUtil.assertSameFields(leftQueue1, null, null, null, cmp);


        ConcatenableQueue<Integer> queue2 = new ConcatenableQueue<>(cmp);
        queue2.add(1);
        queue2.add(2);
        ConcatenableQueue.CQVertex<Integer> left2 = queue2.root.lSon;
        ConcatenableQueue.CQVertex<Integer> right2 = queue2.root.rSon;

        ConcatenableQueue<Integer> leftQueue2 = queue2.cutLeft(2);
        CQUtil.assertSameFields(queue2, right2, right2, right2, cmp);
        CQUtil.assertSameFields(leftQueue2, left2, left2, left2, cmp);
    }

    @Test
    public void testCurLeft4() {
        ConcatenableQueue<Integer> queue1 = getConcatenableQueue1();
        ConcatenableQueue<Integer> left1 = queue1.cutLeft(2);
        CQUtil.assertSameFields(left1, null, null, null, cmp);

        ConcatenableQueue<Integer> queue2 = getConcatenableQueue1();

        ConcatenableQueue.CQVertex<Integer> node2_5 = queue2.root.rSon;
        ConcatenableQueue.CQVertex<Integer> node2_6 = queue2.root.rSon.rSon;
        ConcatenableQueue.CQVertex<Integer> node2_3 = queue2.root.lSon.rSon;

        ConcatenableQueue.CQVertex<Integer> leaf2_12 = queue2.root.rSon.lSon;

        ConcatenableQueue.CQVertex<Integer> node2_22 = queue2.maxNode;
        ConcatenableQueue.CQVertex<Integer> node2_2 = queue2.minNode;
        ConcatenableQueue.CQVertex<Integer> node2_4 = queue2.minNode.rSon;

        ConcatenableQueue<Integer> left2 = queue2.cutLeft(4);

        CQUtil.assertSameFields(queue2, node2_5, node2_4, node2_22, cmp);
        CQUtil.assertSameFields(left2, node2_2, node2_2, node2_2, cmp);
        CQUtil.assertSameFields(queue2.root, null, node2_3, node2_6, leaf2_12, 4, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCutRight1() {
        ConcatenableQueue<Integer> queue = new ConcatenableQueue<>();
        queue.cutRight(1);
    }

    @Test
    public void testCutRight2() {
        ConcatenableQueue<Integer> queue = new ConcatenableQueue<>(cmp);

        queue.add(1);
        ConcatenableQueue.CQVertex<Integer> root = queue.root;

        ConcatenableQueue<Integer> right = queue.cutRight(1);
        CQUtil.assertSameFields(queue, root, root, root, cmp);
        CQUtil.assertSameFields(right, null, null, null, cmp);
    }

    @Test
    public void testCutRight3() {
        ConcatenableQueue<Integer> queue1 = new ConcatenableQueue<>(cmp);
        queue1.add(1);
        queue1.add(2);
        ConcatenableQueue.CQVertex<Integer> root1 = queue1.root;

        ConcatenableQueue<Integer> rightQueue1 = queue1.cutRight(2);
        CQUtil.assertSameFields(queue1, root1, root1.lSon, root1.rSon, cmp);
        CQUtil.assertSameFields(rightQueue1, null, null, null, cmp);


        ConcatenableQueue<Integer> queue2 = new ConcatenableQueue<>(cmp);
        queue2.add(1);
        queue2.add(2);
        ConcatenableQueue.CQVertex<Integer> left2 = queue2.root.lSon;
        ConcatenableQueue.CQVertex<Integer> right2 = queue2.root.rSon;

        ConcatenableQueue<Integer> leftQueue2 = queue2.cutRight(1);
        CQUtil.assertSameFields(queue2, left2, left2, left2, cmp);
        CQUtil.assertSameFields(leftQueue2, right2, right2, right2, cmp);
    }

    @Test
    public void testSearchNode1() {
        ConcatenableQueue<Integer> queue = new ConcatenableQueue<>();

        ConcatenableQueue.CQVertex<Integer> node1 = queue.searchNode(1);
        assertNull(node1);

        queue.add(1);
        ConcatenableQueue.CQVertex<Integer> node2 = queue.searchNode(1);
        ConcatenableQueue.CQVertex<Integer> node3 = queue.searchNode(0);
        ConcatenableQueue.CQVertex<Integer> node4 = queue.searchNode(2);
        CQUtil.assertSameFields(node2, 1, null, null, node2, 0, true);
        assertNull(node3);
        assertNull(node4);
    }

    @Test
    public void testSearchNode2() {
        ConcatenableQueue<Integer> queue = new ConcatenableQueue<>();
        queue.add(4);
        queue.add(6);
        queue.add(2);
        queue.add(8);
        ConcatenableQueue.CQVertex<Integer> node1 = queue.searchNode(1);
        ConcatenableQueue.CQVertex<Integer> node2 = queue.searchNode(2);
        ConcatenableQueue.CQVertex<Integer> node3 = queue.searchNode(3);
        ConcatenableQueue.CQVertex<Integer> node4 = queue.searchNode(4);
        ConcatenableQueue.CQVertex<Integer> node5 = queue.searchNode(5);
        ConcatenableQueue.CQVertex<Integer> node6 = queue.searchNode(6);
        ConcatenableQueue.CQVertex<Integer> node7 = queue.searchNode(7);
        ConcatenableQueue.CQVertex<Integer> node8 = queue.searchNode(8);
        ConcatenableQueue.CQVertex<Integer> node9 = queue.searchNode(9);
        assertNull(node1);
        assertEquals((Integer) 2, node2.value);
        assertNull(node3);
        assertEquals((Integer) 4, node4.value);
        assertNull(node5);
        assertEquals((Integer) 6, node6.value);
        assertNull(node7);
        assertEquals((Integer) 8, node8.value);
        assertNull(node9);
    }

    @Test
    public void testSearchNode3() {
        ConcatenableQueue<Integer> queue = getConcatenableQueue1();

        List<ConcatenableQueue.CQVertex<Integer>> searchResults = new ArrayList<>();


        for (int i = 1; i <= 23; i++) {
            searchResults.add(queue.searchNode(i));
        }

        int dataValue = 2;
        for (int i = 0; i < searchResults.size(); i++) {
            if (i % 2 == 0) {
                assertNull(searchResults.get(i));
            } else {
                assertNotNull(searchResults.get(i));
                int data = searchResults.get(i).value;
                assertEquals(dataValue, data);
                dataValue += 2;
            }
        }

    }

    private ConcatenableQueue<Integer> getConcatenableQueue1() {
        ConcatenableQueue.CQVertex<Integer> node0 = new ConcatenableQueue.CQVertex<>(null);
        ConcatenableQueue.CQVertex<Integer> node1 = new ConcatenableQueue.CQVertex<>(null);
        ConcatenableQueue.CQVertex<Integer> node2 = new ConcatenableQueue.CQVertex<>(null);
        ConcatenableQueue.CQVertex<Integer> node3 = new ConcatenableQueue.CQVertex<>(null);
        ConcatenableQueue.CQVertex<Integer> node4 = new ConcatenableQueue.CQVertex<>(null);
        ConcatenableQueue.CQVertex<Integer> node5 = new ConcatenableQueue.CQVertex<>(null);
        ConcatenableQueue.CQVertex<Integer> node6 = new ConcatenableQueue.CQVertex<>(null);
        ConcatenableQueue.CQVertex<Integer> node7 = new ConcatenableQueue.CQVertex<>(null);
        ConcatenableQueue.CQVertex<Integer> node8 = new ConcatenableQueue.CQVertex<>(null);
        ConcatenableQueue.CQVertex<Integer> node9 = new ConcatenableQueue.CQVertex<>(null);

        ConcatenableQueue.CQVertex<Integer> leaf1 = new ConcatenableQueue.CQVertex<>(2);
        ConcatenableQueue.CQVertex<Integer> leaf2 = new ConcatenableQueue.CQVertex<>(4);
        ConcatenableQueue.CQVertex<Integer> leaf3 = new ConcatenableQueue.CQVertex<>(6);
        ConcatenableQueue.CQVertex<Integer> leaf4 = new ConcatenableQueue.CQVertex<>(8);
        ConcatenableQueue.CQVertex<Integer> leaf5 = new ConcatenableQueue.CQVertex<>(10);
        ConcatenableQueue.CQVertex<Integer> leaf6 = new ConcatenableQueue.CQVertex<>(12);
        ConcatenableQueue.CQVertex<Integer> leaf7 = new ConcatenableQueue.CQVertex<>(14);
        ConcatenableQueue.CQVertex<Integer> leaf8 = new ConcatenableQueue.CQVertex<>(16);
        ConcatenableQueue.CQVertex<Integer> leaf9 = new ConcatenableQueue.CQVertex<>(18);
        ConcatenableQueue.CQVertex<Integer> leaf10 = new ConcatenableQueue.CQVertex<>(20);
        ConcatenableQueue.CQVertex<Integer> leaf11 = new ConcatenableQueue.CQVertex<>(22);

        CQUtil.setFields(node0, node1, node5, leaf5, 5, false);
        CQUtil.setFields(node1, node2, node3, leaf2, 3, false);
        CQUtil.setFields(node2, leaf1, leaf2, leaf1, 1, false);
        CQUtil.setFields(node3, node4, leaf5, leaf4, 2, false);
        CQUtil.setFields(node4, leaf3, leaf4, leaf3, 1, false);
        CQUtil.setFields(node5, leaf6, node6, leaf6, 4, false);
        CQUtil.setFields(node6, node7, node8, leaf8, 3, false);
        CQUtil.setFields(node7, leaf7, leaf8, leaf7, 1, false);
        CQUtil.setFields(node8, leaf9, node9, leaf9, 2, false);
        CQUtil.setFields(node9, leaf10, leaf11, leaf10, 1, false);

        CQUtil.setFields(leaf1, null, leaf2, leaf1, 0, true);
        CQUtil.setFields(leaf2, leaf1, leaf3, leaf2, 0, true);
        CQUtil.setFields(leaf3, leaf2, leaf4, leaf3, 0, true);
        CQUtil.setFields(leaf4, leaf3, leaf5, leaf4, 0, true);
        CQUtil.setFields(leaf5, leaf4, leaf6, leaf5, 0, true);
        CQUtil.setFields(leaf6, leaf5, leaf7, leaf6, 0, true);
        CQUtil.setFields(leaf7, leaf6, leaf8, leaf7, 0, true);
        CQUtil.setFields(leaf8, leaf7, leaf9, leaf8, 0, true);
        CQUtil.setFields(leaf9, leaf8, leaf10, leaf9, 0, true);
        CQUtil.setFields(leaf10, leaf9, leaf11, leaf10, 0, true);
        CQUtil.setFields(leaf11, leaf10, null, leaf11, 0, true);

        ConcatenableQueue<Integer> result = new ConcatenableQueue<>();
        CQUtil.setFields(result, node0, leaf1, leaf11, cmp);

        return result;
    }
}