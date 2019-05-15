package com.chudakov.geometry.alg.convexhull.overmars;

import com.chudakov.geometry.util.Pair;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ConcatenableQueue<E> implements Iterable<E> {

    Node<E> root;

    Node<E> minNode;
    Node<E> maxNode;

    Comparator<E> cmp;


    public ConcatenableQueue() {
        this(null, null, null, null);
    }

    public ConcatenableQueue(Comparator<E> cmp) {
        this(null, null, null, cmp);
    }

    ConcatenableQueue(Node<E> root, Node<E> minNode, Node<E> maxNode, Comparator<E> cmp) {
        this.root = root;
        this.minNode = minNode;
        this.maxNode = maxNode;
        this.cmp = cmp;
    }


    public void add(E e) {
        if (root == null) {
            Node<E> node = new Node<>(e);
            root = node;
            minNode = node;
            maxNode = node;
        } else {
            root = insert(root, e);
        }
    }

    private Node<E> insert(Node<E> node, E e) {
        Node<E> result = null;
        if (compareImpl(e, node.lMax.data) <= 0) { // go to the left sub-tree
            if (node.isLeaf) {
                if (compareImpl(e, node.data) == 0) {  // corner case, replace old value by the new one
                    node.data = e;
                } else { // create new leaf between n.left & n, hence data < node.lMax.data
                    Node<E> createdLeaf = createLeafBetween(e, node.left, node);
                    result = new Node<>(createdLeaf, createdLeaf, node);
                }
            } else { // go left & update node.left pointer
                node.left = insert(node.left, e);
            }
        } else {
            if (node.isLeaf) { // create new leaf between n & n.right, hence data > node.lMax.data
                Node<E> createdLeaf = createLeafBetween(e, node, node.right);
                result = new Node<>(node, node, createdLeaf);
            } else { // go left & update node.right pointer
                node.right = insert(node.right, e);
            }
        }

        if (result == null) {
            result = node;
        }

        updateHeight(result);
        return result;
    }

    private Node<E> createLeafBetween(E data, Node<E> leftNode, Node<E> rightNode) {
        Node<E> result = new Node<>(data, leftNode, rightNode);
        if (leftNode != null) {
            leftNode.right = result;
        } else {
            minNode = result;
        }
        if (rightNode != null) {
            rightNode.left = result;
        } else {
            maxNode = result;
        }
        return result;
    }


    public ConcatenableQueue<E> cutLeft(E e) {
        Node<E> node = searchNode(e);
        assertCorrectSearchResult(node, e);

        // base case: return empty queue
        if (node.equals(minNode)) {
            return new ConcatenableQueue<>(cmp);
        }

        Node<E> splitterNode = node.left;

        Pair<ConcatenableQueue<E>, ConcatenableQueue<E>> cuts = splitAt(splitterNode.data);

        ConcatenableQueue<E> left = cuts.getFirst();
        ConcatenableQueue<E> right = cuts.getSecond();

        copy(right, this);

        return left;
    }

    public ConcatenableQueue<E> cutRight(E e) {
        Node<E> node = searchNode(e);
        assertCorrectSearchResult(node, e);

        // base case: return empty queue
        if (node.equals(maxNode)) {
            return new ConcatenableQueue<>(cmp);
        }

        Pair<ConcatenableQueue<E>, ConcatenableQueue<E>> cuts = splitAt(e);

        ConcatenableQueue<E> left = cuts.getFirst();
        ConcatenableQueue<E> right = cuts.getSecond();

        copy(left, this);

        return right;
    }

    private void assertCorrectSearchResult(Node<E> node, E e) {
        if (node == null) {
            throw new IllegalArgumentException("queue does not contain element " + e);
        }
    }

    Node<E> searchNode(E e) {
        Node<E> it = root;
        while (it != null && !it.isLeaf) {
            if (compareImpl(e, it.lMax.data) <= 0) { // go left
                it = it.left;
            } else { // go right
                it = it.right;
            }
        }
        if (it == null || compareImpl(it.data, e) != 0) { // not found
            return null;
        }
        return it;
    }


    private static <E> void copy(ConcatenableQueue<E> from, ConcatenableQueue<E> to) {
        to.root = from.root;
        to.minNode = from.minNode;
        to.maxNode = from.maxNode;
        to.cmp = from.cmp;
    }


    public static <E> ConcatenableQueue<E> concatenate(ConcatenableQueue<E> leftQueue, ConcatenableQueue<E> rightQueue) {
        Objects.requireNonNull(leftQueue, "qLeft is null!");
        Objects.requireNonNull(rightQueue, "qRight is null!");

        assertSameComparator(leftQueue, rightQueue);

        // corner cases
        if (leftQueue.root == null) {
            return rightQueue;
        }
        if (rightQueue.root == null) {
            return leftQueue;
        }

        // concatenate the leafs
        leftQueue.maxNode.right = rightQueue.minNode;
        rightQueue.minNode.left = leftQueue.maxNode;

        // concatenate the two trees
        Node<E> newRoot = glueTree(leftQueue.root, rightQueue.root, leftQueue.maxNode);

        return new ConcatenableQueue<>(newRoot, leftQueue.minNode, rightQueue.maxNode, null);
    }


    // when called e is for sure present in the queue
    Pair<ConcatenableQueue<E>, ConcatenableQueue<E>> splitAt(E e) {
        ConcatenableQueue<E> left = new ConcatenableQueue<>(cmp);
        ConcatenableQueue<E> right = new ConcatenableQueue<>(cmp);

        left.minNode = minNode;
        right.maxNode = maxNode;

        splitAt(root, e, left, right);

        return Pair.of(left, right);
    }

    void splitAt(Node<E> node, E e, ConcatenableQueue<E> leftQueue, ConcatenableQueue<E> rightQueue) {
        if (node.isLeaf) { // data`s node found (right leaf)
            // e == node.data
            // node has already proper height
            leftQueue.root = node;
            leftQueue.maxNode = node;

            rightQueue.minNode = node.right;

            cutAt(node); // cut the queue connections
        } else {
            if (compareImpl(e, node.lMax.data) == 0) { // base case, also if data`s node is a left leaf
                leftQueue.root = node.left;
                leftQueue.maxNode = node.lMax;

                rightQueue.root = node.right;
                rightQueue.minNode = node.lMax.right;

                cutAt(node.lMax);
            } else if (compareImpl(e, node.lMax.data) < 0) { // to the left subtree
                // lMax remains the same for node

                splitAt(node.left, e, leftQueue, rightQueue);

                rightQueue.root = glueTree(rightQueue.root, node.right, node.lMax);
            } else { // to the right subtree

                splitAt(node.right, e, leftQueue, rightQueue);

                leftQueue.root = glueTree(node.left, leftQueue.root, node.lMax);
            }
        }
    }

    void cutAt(Node<E> leaf) {
        if (leaf.right != null) {
            leaf.right.left = null;
            leaf.right = null;
        }
    }

    static <E> Node<E> glueTree(Node<E> leftNode, Node<E> rightNode, Node<E> lMax) {
        if (leftNode == null) { // left sub-tree is empty
            return rightNode;
        } else if (rightNode == null) { // right sub-tree is empty
            return leftNode;
        } else if (leftNode.height == rightNode.height) { // same height => create a new node
            Node<E> result = new Node<>(lMax, leftNode, rightNode);
            updateHeight(result);
            return result;
        } else if (leftNode.height < rightNode.height) { // right sub-tree is higher
            rightNode.left = glueTree(leftNode, rightNode.left, lMax);
            updateHeight(rightNode);
            return rightNode;
        } else { // right sub-tree is higher
            leftNode.right = glueTree(leftNode.right, rightNode, lMax);
            updateHeight(leftNode);
            return leftNode;
        }
    }


    static <E> void updateHeight(Node<E> node) {
        int leftHeight = 0;
        if (node.left != null) {
            leftHeight = node.left.height;
        }
        int rightHeight = 0;
        if (node.left != null) {
            rightHeight = node.right.height;
        }
        node.height = Math.max(leftHeight, rightHeight) + 1;
    }

    private int compareImpl(E e1, E e2) {
        if (cmp == null) { // use Comparable interface for comparisons
            @SuppressWarnings("unchecked")
            Comparable<E> casted = (Comparable<E>) e1;
            return casted.compareTo(e2);
        }
        // use comparator for comparisons
        return cmp.compare(e1, e2);
    }

    private static <E> void assertSameComparator(ConcatenableQueue<E> q1, ConcatenableQueue<E> q2) {
        if ((q1.cmp == null && q2.cmp != null) || (q1.cmp != null && q2.cmp == null)
                || (q1.cmp != null && q2.cmp != null && !q1.cmp.equals(q2.cmp))) {
            throw new IllegalArgumentException("queues have different comparators!");
        }
    }


    @Override
    public Iterator<E> iterator() {
        return new CQIterator();
    }

    public Iterator<E> reverseIterator() {
        return new CQReverseIterator();
    }

    private class CQIterator implements Iterator<E> {
        private Node<E> nextNode;

        CQIterator() {
            nextNode = minNode;
        }

        @Override
        public boolean hasNext() {
            return nextNode != null;
        }

        @Override
        public E next() {
            if (nextNode == null) {
                throw new NoSuchElementException();
            }
            E result = nextNode.data;
            nextNode = nextNode.right;
            return result;
        }
    }

    private class CQReverseIterator implements Iterator<E> {
        private Node<E> nextNode;

        CQReverseIterator() {
            nextNode = maxNode;
        }

        @Override
        public boolean hasNext() {
            return nextNode != null;
        }

        @Override
        public E next() {
            if (nextNode == null) {
                throw new NoSuchElementException();
            }
            E result = nextNode.data;
            nextNode = nextNode.left;
            return result;
        }
    }

    static class Node<E> {
        E data;

        // height in terms of amount of maximum
        // amount of steps to reach a leaf
        int height;

        // if isLeaf == true, mean left & right neighboring leaves
        // else means left and right sons in the tree structure
        Node<E> left;
        Node<E> right;

        Node<E> lMax;

        boolean isLeaf;

        public Node() {
        }

        Node(E data) {
            this(data, null, null, null, true);
        }

        Node(E data, Node<E> leftLeaf, Node<E> rightLeaf) {
            this(data, leftLeaf, rightLeaf, null, true);
        }

        Node(Node<E> lMax, Node<E> leftLeaf, Node<E> rightLeaf) {
            this(null, leftLeaf, rightLeaf, lMax, false);
        }

        Node(E data, Node<E> left, Node<E> right, Node<E> lMax, boolean isLeaf) {
            this.data = data;
            this.left = left;
            this.right = right;
            if (lMax == null) {
                this.lMax = this;
            } else {
                this.lMax = lMax;
            }
            this.isLeaf = isLeaf;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "data=" + data +
                    ", height=" + height +
                    ", isLeaf=" + isLeaf +
                    '}';
        }
    }
}
