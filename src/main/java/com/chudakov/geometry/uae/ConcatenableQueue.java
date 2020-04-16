package com.chudakov.geometry.uae;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ConcatenableQueue<T> implements Iterable<T> {

    CQVertex<T> root;

    CQVertex<T> minNode;
    CQVertex<T> maxNode;

    Comparator<T> cmp;


    public ConcatenableQueue() {
        this(null, null, null, null);
    }

    public ConcatenableQueue(Comparator<T> cmp) {
        this(null, null, null, cmp);
    }

    ConcatenableQueue(CQVertex<T> root, CQVertex<T> minNode, CQVertex<T> maxNode, Comparator<T> cmp) {
        this.root = root;
        this.minNode = minNode;
        this.maxNode = maxNode;
        this.cmp = cmp;
    }


    private CQVertex<T> createLeafBetween(CQVertex<T> leftNode, CQVertex<T> rightNode, T data) {
        CQVertex<T> result = new CQVertex<>(data, leftNode, rightNode);
        if (leftNode != null) {
            leftNode.rSon = result;
        } else {
            minNode = result;
        }
        if (rightNode != null) {
            rightNode.lSon = result;
        } else {
            maxNode = result;
        }
        return result;
    }

    public void add(T e) {
        if (root == null) {
            CQVertex<T> node = new CQVertex<>(e);
            root = node;
            minNode = node;
            maxNode = node;
        } else {
            root = insert(root, e);
        }
    }

    private CQVertex<T> insert(CQVertex<T> vertex, T data) {
        CQVertex<T> result = null;
        if (compareImpl(data, vertex.leftSubtreeMax.value) > 0) {
            if (!vertex.isLeaf) {
                vertex.rSon = insert(vertex.rSon, data);
            } else {
                CQVertex<T> createdLeaf = createLeafBetween(vertex, vertex.rSon, data);
                result = new CQVertex<>(vertex, vertex, createdLeaf);
            }
        } else {
            if (!vertex.isLeaf) {
                vertex.lSon = insert(vertex.lSon, data);
            } else {
                if (compareImpl(data, vertex.value) != 0) {
                    CQVertex<T> createdLeaf = createLeafBetween(vertex.lSon, vertex, data);
                    result = new CQVertex<>(createdLeaf, createdLeaf, vertex);
                } else {
                    vertex.value = data;
                }
            }
        }

        if (result == null) {
            result = vertex;
        }

        updateHeight(result);
        return result;
    }


    public ConcatenableQueue<T> cutLeft(T e) {
        CQVertex<T> node = searchNode(e);
        assertCorrectSearchResult(node, e);

        if (node.equals(minNode)) {
            return new ConcatenableQueue<>(cmp);
        }

        CQVertex<T> splitterNode = node.lSon;

        Pair<ConcatenableQueue<T>, ConcatenableQueue<T>> cuts = split(splitterNode.value);

        ConcatenableQueue<T> left = cuts.getLeft();
        ConcatenableQueue<T> right = cuts.getRight();

        copy(right, this);

        return left;
    }

    public ConcatenableQueue<T> cutRight(T e) {
        CQVertex<T> node = searchNode(e);
        assertCorrectSearchResult(node, e);

        if (node.equals(maxNode)) {
            return new ConcatenableQueue<>(cmp);
        }

        Pair<ConcatenableQueue<T>, ConcatenableQueue<T>> cuts = split(e);

        ConcatenableQueue<T> left = cuts.getLeft();
        ConcatenableQueue<T> right = cuts.getRight();

        copy(left, this);

        return right;
    }

    private void assertCorrectSearchResult(CQVertex<T> node, T e) {
        if (node == null) {
            throw new IllegalArgumentException("queue does not contain element " + e);
        }
    }

    CQVertex<T> searchNode(T e) {
        CQVertex<T> it = root;
        while (it != null && !it.isLeaf) {
            if (compareImpl(e, it.leftSubtreeMax.value) <= 0) {
                it = it.lSon;
            } else {
                it = it.rSon;
            }
        }
        if (it == null || compareImpl(it.value, e) != 0) {
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
        Objects.requireNonNull(leftQueue, "leftQueue is null!");
        Objects.requireNonNull(rightQueue, "rightQueue is null!");

        assertSameComparator(leftQueue, rightQueue);

        if (leftQueue.root == null) {
            return rightQueue;
        }
        if (rightQueue.root == null) {
            return leftQueue;
        }

        leftQueue.maxNode.rSon = rightQueue.minNode;
        rightQueue.minNode.lSon = leftQueue.maxNode;

        CQVertex<E> newRoot = concatenateNodes(leftQueue.root, rightQueue.root, leftQueue.maxNode);

        return new ConcatenableQueue<>(newRoot, leftQueue.minNode, rightQueue.maxNode, null);
    }


    Pair<ConcatenableQueue<T>, ConcatenableQueue<T>> split(T e) {
        ConcatenableQueue<T> left = new ConcatenableQueue<>(cmp);
        ConcatenableQueue<T> right = new ConcatenableQueue<>(cmp);

        left.minNode = minNode;
        right.maxNode = maxNode;

        split(root, e, left, right);

        return Pair.of(left, right);
    }

    void split(CQVertex<T> node, T e, ConcatenableQueue<T> leftQueue, ConcatenableQueue<T> rightQueue) {
        if (node.isLeaf) {
            leftQueue.root = node;
            leftQueue.maxNode = node;

            rightQueue.minNode = node.rSon;

            cut(node);
        } else {
            if (compareImpl(e, node.leftSubtreeMax.value) == 0) {
                leftQueue.root = node.lSon;
                leftQueue.maxNode = node.leftSubtreeMax;

                rightQueue.root = node.rSon;
                rightQueue.minNode = node.leftSubtreeMax.rSon;

                cut(node.leftSubtreeMax);
            } else if (compareImpl(e, node.leftSubtreeMax.value) < 0) {
                split(node.lSon, e, leftQueue, rightQueue);

                rightQueue.root = concatenateNodes(rightQueue.root, node.rSon, node.leftSubtreeMax);
            } else {

                split(node.rSon, e, leftQueue, rightQueue);

                leftQueue.root = concatenateNodes(node.lSon, leftQueue.root, node.leftSubtreeMax);
            }
        }
    }

    void cut(CQVertex<T> leaf) {
        if (leaf.rSon != null) {
            leaf.rSon.lSon = null;
            leaf.rSon = null;
        }
    }

    static <E> CQVertex<E> concatenateNodes(CQVertex<E> leftNode, CQVertex<E> rightNode, CQVertex<E> lMax) {
        if (leftNode == null) {
            return rightNode;
        } else if (rightNode == null) {
            return leftNode;
        } else if (leftNode.height == rightNode.height) {
            CQVertex<E> result = new CQVertex<>(lMax, leftNode, rightNode);
            updateHeight(result);
            return result;
        } else if (leftNode.height < rightNode.height) {
            rightNode.lSon = concatenateNodes(leftNode, rightNode.lSon, lMax);
            updateHeight(rightNode);
            return rightNode;
        } else {
            leftNode.rSon = concatenateNodes(leftNode.rSon, rightNode, lMax);
            updateHeight(leftNode);
            return leftNode;
        }
    }


    static <E> void updateHeight(CQVertex<E> node) {
        int leftHeight = 0;
        if (node.lSon != null) {
            leftHeight = node.lSon.height;
        }
        int rightHeight = 0;
        if (node.lSon != null) {
            rightHeight = node.rSon.height;
        }
        node.height = Math.max(leftHeight, rightHeight) + 1;
    }

    private int compareImpl(T e1, T e2) {
        if (cmp == null) {
            @SuppressWarnings("unchecked")
            Comparable<T> casted = (Comparable<T>) e1;
            return casted.compareTo(e2);
        }

        return cmp.compare(e1, e2);
    }

    private static <E> void assertSameComparator(ConcatenableQueue<E> q1, ConcatenableQueue<E> q2) {
        if ((q1.cmp == null && q2.cmp != null) || (q1.cmp != null && q2.cmp == null)
                || (q1.cmp != null && q2.cmp != null && !q1.cmp.equals(q2.cmp))) {
            throw new IllegalArgumentException("queues have different comparators!");
        }
    }


    public void clear() {
        root = null;
        minNode = null;
        maxNode = null;
    }

    @Override
    public Iterator<T> iterator() {
        return new CQIterator();
    }

    public Iterator<T> reverseIterator() {
        return new CQReverseIterator();
    }

    private class CQIterator implements Iterator<T> {
        private CQVertex<T> nextNode;

        CQIterator() {
            nextNode = minNode;
        }

        @Override
        public boolean hasNext() {
            return nextNode != null;
        }

        @Override
        public T next() {
            if (nextNode == null) {
                throw new NoSuchElementException();
            }
            T result = nextNode.value;
            nextNode = nextNode.rSon;
            return result;
        }
    }

    private class CQReverseIterator implements Iterator<T> {
        private CQVertex<T> nextNode;

        CQReverseIterator() {
            nextNode = maxNode;
        }

        @Override
        public boolean hasNext() {
            return nextNode != null;
        }

        @Override
        public T next() {
            if (nextNode == null) {
                throw new NoSuchElementException();
            }
            T result = nextNode.value;
            nextNode = nextNode.lSon;
            return result;
        }
    }

    static class CQVertex<E> {
        E value;

        int height;

        CQVertex<E> lSon;
        CQVertex<E> rSon;

        CQVertex<E> leftSubtreeMax;

        boolean isLeaf;

        public CQVertex() {
        }

        CQVertex(E value) {
            this(value, null, null, null, true);
        }

        CQVertex(E value, CQVertex<E> leftLeaf, CQVertex<E> rightLeaf) {
            this(value, leftLeaf, rightLeaf, null, true);
        }

        CQVertex(CQVertex<E> leftSubtreeMax, CQVertex<E> leftLeaf, CQVertex<E> rightLeaf) {
            this(null, leftLeaf, rightLeaf, leftSubtreeMax, false);
        }

        CQVertex(E value, CQVertex<E> lSon, CQVertex<E> rSon, CQVertex<E> leftSubtreeMax, boolean isLeaf) {
            this.value = value;
            this.lSon = lSon;
            this.rSon = rSon;
            if (leftSubtreeMax == null) {
                this.leftSubtreeMax = this;
            } else {
                this.leftSubtreeMax = leftSubtreeMax;
            }
            this.isLeaf = isLeaf;
        }

        @Override
        public String toString() {
            return "CQNode{" +
                    "data=" + value +
                    ", height=" + height +
                    ", isLeaf=" + isLeaf +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CQVertex<?> cqVertex = (CQVertex<?>) o;

            return value != null ? value.equals(cqVertex.value) : cqVertex.value == null;
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }
    }
}
