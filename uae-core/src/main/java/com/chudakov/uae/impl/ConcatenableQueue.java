package com.chudakov.uae.impl;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ConcatenableQueue<T> implements Iterable<ConcatenableQueue.CQVertex<T>> {

    CQVertex<T> root;

    CQVertex<T> minVertex;
    CQVertex<T> maxVertex;

    Comparator<T> cmp;


    public ConcatenableQueue() {
        this(null, null, null, null);
    }

    public ConcatenableQueue(Comparator<T> cmp) {
        this(null, null, null, cmp);
    }

    private ConcatenableQueue(CQVertex<T> root, CQVertex<T> minVertex, CQVertex<T> maxVertex, Comparator<T> cmp) {
        this.root = root;
        this.minVertex = minVertex;
        this.maxVertex = maxVertex;
        this.cmp = cmp;
    }


    public void clear() {
        root = null;
        minVertex = null;
        maxVertex = null;
    }

    private static <E> void copy(ConcatenableQueue<E> from, ConcatenableQueue<E> to) {
        to.root = from.root;
        to.minVertex = from.minVertex;
        to.maxVertex = from.maxVertex;
        to.cmp = from.cmp;
    }


    public void add(T e) {
        if (root == null) {
            CQVertex<T> vertex = new CQVertex<>(e);
            root = vertex;
            minVertex = vertex;
            maxVertex = vertex;
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

    private CQVertex<T> createLeafBetween(CQVertex<T> leftVertex, CQVertex<T> rightVertex, T data) {
        CQVertex<T> result = new CQVertex<>(data, leftVertex, rightVertex);
        if (leftVertex != null) {
            leftVertex.rSon = result;
        } else {
            minVertex = result;
        }
        if (rightVertex != null) {
            rightVertex.lSon = result;
        } else {
            maxVertex = result;
        }
        return result;
    }


    public ConcatenableQueue<T> cutLeft(T e) {
        CQVertex<T> vertex = searchVertex(e);
        assertVertexFound(vertex, e);

        if (vertex.equals(minVertex)) {
            return new ConcatenableQueue<>(cmp);
        }

        CQVertex<T> splitterVertex = vertex.lSon;

        Pair<ConcatenableQueue<T>, ConcatenableQueue<T>> cuts = split(splitterVertex.value);

        ConcatenableQueue<T> left = cuts.getLeft();
        ConcatenableQueue<T> right = cuts.getRight();

        copy(right, this);

        return left;
    }

    public ConcatenableQueue<T> cutRight(T e) {
        CQVertex<T> vertex = searchVertex(e);
        assertVertexFound(vertex, e);

        if (vertex.equals(maxVertex)) {
            return new ConcatenableQueue<>(cmp);
        }

        Pair<ConcatenableQueue<T>, ConcatenableQueue<T>> cuts = split(e);

        ConcatenableQueue<T> left = cuts.getLeft();
        ConcatenableQueue<T> right = cuts.getRight();

        copy(left, this);

        return right;
    }

    private void assertVertexFound(CQVertex<T> vertex, T e) {
        if (vertex == null) {
            throw new IllegalArgumentException("queue does not contain element " + e);
        }
    }

    CQVertex<T> searchVertex(T e) {
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

        leftQueue.maxVertex.rSon = rightQueue.minVertex;
        rightQueue.minVertex.lSon = leftQueue.maxVertex;

        CQVertex<E> newRoot = concatenateVertices(leftQueue.root, rightQueue.root, leftQueue.maxVertex);

        return new ConcatenableQueue<>(newRoot, leftQueue.minVertex, rightQueue.maxVertex, leftQueue.cmp);
    }


    Pair<ConcatenableQueue<T>, ConcatenableQueue<T>> split(T e) {
        ConcatenableQueue<T> left = new ConcatenableQueue<>(cmp);
        ConcatenableQueue<T> right = new ConcatenableQueue<>(cmp);

        left.minVertex = minVertex;
        right.maxVertex = maxVertex;

        split(root, e, left, right);

        return Pair.of(left, right);
    }

    void split(CQVertex<T> vertex, T e, ConcatenableQueue<T> leftQueue, ConcatenableQueue<T> rightQueue) {
        if (vertex.isLeaf) {
            leftQueue.root = vertex;
            leftQueue.maxVertex = vertex;

            rightQueue.minVertex = vertex.rSon;

            cut(vertex);
        } else {
            if (compareImpl(e, vertex.leftSubtreeMax.value) == 0) {
                leftQueue.root = vertex.lSon;
                leftQueue.maxVertex = vertex.leftSubtreeMax;

                rightQueue.root = vertex.rSon;
                rightQueue.minVertex = vertex.leftSubtreeMax.rSon;

                cut(vertex.leftSubtreeMax);
            } else if (compareImpl(e, vertex.leftSubtreeMax.value) < 0) {
                split(vertex.lSon, e, leftQueue, rightQueue);

                rightQueue.root = concatenateVertices(rightQueue.root, vertex.rSon, vertex.leftSubtreeMax);
            } else {

                split(vertex.rSon, e, leftQueue, rightQueue);

                leftQueue.root = concatenateVertices(vertex.lSon, leftQueue.root, vertex.leftSubtreeMax);
            }
        }
    }

    void cut(CQVertex<T> leaf) {
        if (leaf.rSon != null) {
            leaf.rSon.lSon = null;
            leaf.rSon = null;
        }
    }

    static <E> CQVertex<E> concatenateVertices(CQVertex<E> leftVertex, CQVertex<E> rightVertex, CQVertex<E> lMax) {
        if (leftVertex == null) {
            return rightVertex;
        } else if (rightVertex == null) {
            return leftVertex;
        } else if (leftVertex.height == rightVertex.height) {
            CQVertex<E> result = new CQVertex<>(lMax, leftVertex, rightVertex);
            updateHeight(result);
            return result;
        } else if (leftVertex.height < rightVertex.height) {
            rightVertex.lSon = concatenateVertices(leftVertex, rightVertex.lSon, lMax);
            updateHeight(rightVertex);
            return rightVertex;
        } else {
            leftVertex.rSon = concatenateVertices(leftVertex.rSon, rightVertex, lMax);
            updateHeight(leftVertex);
            return leftVertex;
        }
    }


    static <E> void updateHeight(CQVertex<E> vertex) {
        int leftHeight = 0;
        if (vertex.lSon != null) {
            leftHeight = vertex.lSon.height;
        }
        int rightHeight = 0;
        if (vertex.lSon != null) {
            rightHeight = vertex.rSon.height;
        }
        vertex.height = Math.max(leftHeight, rightHeight) + 1;
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


    @Override
    public Iterator<CQVertex<T>> iterator() {
        return new CQIterator();
    }

    public Iterator<CQVertex<T>> reverseIterator() {
        return new CQReverseIterator();
    }


    private class CQIterator implements Iterator<CQVertex<T>> {
        private CQVertex<T> nextVertex;

        CQIterator() {
            nextVertex = minVertex;
        }

        @Override
        public boolean hasNext() {
            return nextVertex != null;
        }

        @Override
        public CQVertex<T> next() {
            if (nextVertex == null) {
                throw new NoSuchElementException();
            }
            CQVertex<T> result = nextVertex;
            nextVertex = nextVertex.rSon;
            return result;
        }
    }

    private class CQReverseIterator implements Iterator<CQVertex<T>> {
        private CQVertex<T> nextVertex;

        CQReverseIterator() {
            nextVertex = maxVertex;
        }

        @Override
        public boolean hasNext() {
            return nextVertex != null;
        }

        @Override
        public CQVertex<T> next() {
            if (nextVertex == null) {
                throw new NoSuchElementException();
            }
            CQVertex<T> result = nextVertex;
            nextVertex = nextVertex.lSon;
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
            this.leftSubtreeMax = Objects.requireNonNullElse(leftSubtreeMax, this);
            this.isLeaf = isLeaf;
        }

        @Override
        public String toString() {
            return "CQVertex{" +
                    "value=" + value +
                    ", height=" + height +
                    ", isLeaf=" + isLeaf +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CQVertex<?> cqVertex = (CQVertex<?>) o;

            return Objects.equals(value, cqVertex.value);
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }
    }
}
