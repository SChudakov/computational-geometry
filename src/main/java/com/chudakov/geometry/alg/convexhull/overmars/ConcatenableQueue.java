package com.chudakov.geometry.alg.convexhull.overmars;

import com.chudakov.geometry.util.Pair;

import java.util.Comparator;
import java.util.Iterator;

public class ConcatenableQueue<E> implements Iterable<E> {

    Node root;

    Node minNode;
    Node maxNode;

    int height; // height in terms of the amount of nodes

    private Comparator<E> cmp;

    public ConcatenableQueue() {

    }

    public ConcatenableQueue(Comparator<E> cmp) {
        this.cmp = cmp;
    }

    public void add(E e) {
        if (root == null) {
            Node node = new Node(e);
            root = node;
            minNode = node;
            maxNode = node;
            height = 1;
        } else {
            insertAt(root, e);
        }
    }

    private Node insertAt(Node n, E e) {
        if (compareImpl(e, n.lMax.data) <= 0) {
            if (n.isLeaf) {
                if (compareImpl(e, n.data) == 0) {  // corner case, replace old value by the new one
                    n.data = e;
                } else {
                    Node nNew = addLeaf(e, n.left, n);
                    n = new Node(nNew, nNew, n);
                }
            } else {
                n.left = insertAt(n.left, e);
            }
        } else {
            if (n.isLeaf) {
                Node nNew = addLeaf(e, n, n.right);
                n = new Node(n, n, nNew);
            } else {
                n.right = insertAt(n.right, e);
            }
        }

        return n;
    }

    private Node addLeaf(E e, Node leftNeighbor, Node rightNeighbor) {
        Node node = new Node(e);

        node.left = leftNeighbor;
        node.right = rightNeighbor;

        return node;
    }

    private int compareImpl(E e1, E e2) {
        if (cmp == null) { // use interface for comparisons
            @SuppressWarnings("unchecked")
            Comparable<E> casted = (Comparable<E>) e1;
            return casted.compareTo(e2);
        }
        // use comparator for comparisons
        return cmp.compare(e1, e2);
    }


    public ConcatenableQueue<E> cutLeft(E e) {
        // base cases: this queue is empty / return empty result
        if (root == null || compareImpl(e, minNode.data) <= 0) {
            return new ConcatenableQueue<>();
        } else if (compareImpl(e, maxNode.data) > 0) {
            ConcatenableQueue<E> result = new ConcatenableQueue<>();
            move(this, result);
            return result;
        }

        Node node = searchNode(e);
        if (node == null) {
            throw new IllegalArgumentException("element " + e + " is not contained in the queue");
        }
        Node splitter = node.left;

        Pair<ConcatenableQueue<E>, ConcatenableQueue<E>> cuts = splitAt(splitter.data);

        ConcatenableQueue<E> left = cuts.getFirst();
        ConcatenableQueue<E> right = cuts.getFirst();

        copy(right, this);

        return left;
    }

    public ConcatenableQueue<E> cutRight(E e) {
        // corner cases
        if (root == null || compareImpl(e, maxNode.data) >= 0) {
            return new ConcatenableQueue<>();
        } else if (compareImpl(e, maxNode.data) < 0) {
            ConcatenableQueue<E> result = new ConcatenableQueue<>();
            move(this, result);
            return result;
        }
        Node node = searchNode(e);
        if (node == null) {
            throw new IllegalArgumentException("queue does not contain element " + e);
        }
        Pair<ConcatenableQueue<E>, ConcatenableQueue<E>> cuts = splitAt(e);

        ConcatenableQueue<E> left = cuts.getFirst();
        ConcatenableQueue<E> right = cuts.getFirst();

        copy(left, this);

        return right;
    }

    private Node searchNode(E e) {
        Node it = root;
        while (it != null && !it.isLeaf) {
            if (compareImpl(e, it.lMax.data) <= 0) {
                it = it.left;
            } else {
                it = it.right;
            }
        }
        if (it == null || compareImpl(it.data, e) != 0) {
            return null;
        }
        return it;
    }


    private void copy(ConcatenableQueue<E> from, ConcatenableQueue<E> to) {
        to.root = from.root;
        to.minNode = from.minNode;
        to.maxNode = from.maxNode;
        to.height = from.height;
    }

    private void move(ConcatenableQueue<E> from, ConcatenableQueue<E> to) {
        copy(from, to);

        from.root = null;
        from.minNode = null;
        from.maxNode = null;
        from.height = 0;
    }

    // when called e is for sure present in the queue
    private Pair<ConcatenableQueue<E>, ConcatenableQueue<E>> splitAt(E e) {
        ConcatenableQueue<E> left = new ConcatenableQueue<>();
        ConcatenableQueue<E> right = new ConcatenableQueue<>();

        left.minNode = minNode;
        right.maxNode = maxNode;

        splitAt(root, 0, e, left, right);

        return Pair.of(left, right);
    }

    private void splitAt(Node node, int height, E e, ConcatenableQueue<E> qLeft, ConcatenableQueue<E> qRight) {
        assert e != null && node != null : "e != null && n != null";
        assert qLeft != null && qRight != null : "qLeft != null && qRight != null";
        assert node.color : "only split at a BLACK node";

        if (node.isLeaf) { // e`s node found
            assert height == 0 : "h == 0";

            if (compareImpl(e, node.data) < 0) {
                qRight.root = node;
                qRight.minNode = node;
                qRight.height = 0;
                qLeft.maxNode = node.left;
                cutAt(node.left);
            } else {
                qLeft.root = node;
                qLeft.maxNode = node;
                qLeft.height = 0;
                qRight.minNode = node.right;
                cutAt(node);
            }
        } else {
            if (compareImpl(e, node.lMax.data) == 0) { // another base case
                qLeft.root = node.left;
                qLeft.height = height - 1;
                qLeft.maxNode = node.lMax;
                if (qLeft.root.color == false) {//RED
                    qLeft.root.color = true;//BLACK
                    qLeft.height++;
                }
                qRight.root = node.right;
                qRight.height = height - 1;
                qRight.minNode = node.lMax.right;
                cutAt(node.lMax);
            } else if (compareImpl(e, node.lMax.data) < 0) { // to the left subtree
                if (node.left.color == false) { // RED
                    node.left.color = true; // BLACK
                    splitAt(node.left, height, e, qLeft, qRight);
                } else {
                    splitAt(node.left, height - 1, e, qLeft, qRight);
                }
                int tempHeight = qRight.height;
                qRight.root = glueTree(qRight.root, node.right, qRight.height, height - 1, node.lMax);
                qRight.height = Math.max(tempHeight, height - 1);
                if (qRight.root.color == false) { // RED
                    qRight.root.color = true; // BLACK
                    qRight.height++;
                }
            } else { // to the right subtree
                splitAt(node.right, height - 1, e, qLeft, qRight);
                if (node.left.color == false) {
                    node.left.color = true;
                    qLeft.root = glueTree(node.left, qLeft.root, height, qLeft.height, node.lMax);
                    qLeft.height = height;
                } else {
                    qLeft.root = glueTree(node.left, qLeft.root, height - 1, qLeft.height, node.lMax);
                    qLeft.height = height - 1;
                }

                if (qLeft.root.color == false) {
                    qLeft.root.color = true;
                    qLeft.height++;
                }
            }
        }
    }
//
//    private void cutAt(Node n) {
//        assert n == null || n.isLeaf : "n == null || n.isLeaf";
//
//        if (n != null && n.right != null) {
//            n.right.left = null;
//            n.right = null;
//        }
//    }

//    private Node glueTree(Node lN, Node rN, int lH, int rH, Node lMax) {
//        throw new UnsupportedOperationException();
//    }

    public static <E> ConcatenableQueue<E> concatenate(ConcatenableQueue<E> left, ConcatenableQueue<E> right) {
        return null;
    }

//    public void balanceUp(Node node) {
//        throw new UnsupportedOperationException();
//    }
//
//    private void rotateLeft(Node node) {
//        throw new UnsupportedOperationException();
//    }
//
//    private void rotateRight(Node node) {
//        throw new UnsupportedOperationException();
//    }


    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            @Override
            public boolean hasNext() {
                throw new UnsupportedOperationException();
            }

            @Override
            public E next() {
                throw new UnsupportedOperationException();
            }
        };
    }

    class Node {
        private E data;

        Node parent;

        // if isLeaf == true, mean left & right neighboring leaves
        // else means left and right sons in the tree structure
        Node left;
        Node right;

        Node lMax;

        boolean color;

        boolean isLeaf;

        Node(E data) {
            this(data, null, null, null, true);
        }

        Node(E data, Node leftLeaf, Node rightLeaf) {
            this(data, leftLeaf, rightLeaf, null, true);
        }

        Node(Node lMax, Node leftLeaf, Node rightLeaf) {
            this(null, leftLeaf, rightLeaf, lMax, false);
        }

        Node(E data, Node left, Node right, Node lMax, boolean isLeaf) {
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
    }
}
