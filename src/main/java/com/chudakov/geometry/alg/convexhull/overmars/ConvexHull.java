package com.chudakov.geometry.alg.convexhull.overmars;

import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.util.Pair;

import java.util.Iterator;

public class ConvexHull implements Iterable<Point2D> {
    @FunctionalInterface
    interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }

    enum Position {LEFT, RIGHT}

    final ConvexSubhull upperSubhull;
    final ConvexSubhull lowerSubhull;

    public ConvexHull(ConvexSubhull upperSubhull, ConvexSubhull lowerSubhull) {
        if (!upperSubhull.type.equals(ConvexSubhull.Type.UPPER)) {
            throw new IllegalArgumentException("upper sub-hull improper type");
        }
        if (!lowerSubhull.type.equals(ConvexSubhull.Type.LOWER)) {
            throw new IllegalArgumentException("lower sub-hull improper type");
        }
        this.upperSubhull = upperSubhull;
        this.lowerSubhull = lowerSubhull;
    }

    public static ConvexHull join(ConvexHull left, ConvexHull right) {

        ConcatenableQueue<Point2D> leftUpper = left.upperSubhull.subhull;
        ConcatenableQueue<Point2D> leftLower = left.lowerSubhull.subhull;
        ConcatenableQueue<Point2D> rightUpper = right.upperSubhull.subhull;
        ConcatenableQueue<Point2D> rightLower = right.lowerSubhull.subhull;

        // first: prepare upper queues
        Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>> p1 = moveCornerPointsUp(leftUpper, leftLower);
        leftUpper = p1.getFirst();
        leftLower = p1.getSecond();

        Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>> p2 = moveCornerPointsUp(rightUpper, rightLower);
        rightUpper = p2.getFirst();
        rightLower = p2.getSecond();

        Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>> rests =
                cutRest(leftUpper, rightUpper, ConvexHull::getUpperTangentCase);
        ConcatenableQueue<Point2D> leftUpperRest = rests.getFirst();
        ConcatenableQueue<Point2D> rightUpperRest = rests.getSecond();

        // second: prepare lower queues
        leftLower = moveRightCornerPointDown(leftUpperRest, leftLower);
        rightLower = moveLeftCornerPointDown(rightUpperRest, rightLower);

        cutRest(leftLower, rightLower, ConvexHull::getLowerTangentCase);

        // finally: create sub-hulls
        ConcatenableQueue<Point2D> upperResult = ConcatenableQueue.concatenate(leftUpper, rightUpper);
        ConcatenableQueue<Point2D> lowerResult = ConcatenableQueue.concatenate(leftLower, rightLower);

        lowerResult = adjustLowerHull(upperResult, lowerResult);

        ConvexSubhull upperSubhull = new ConvexSubhull(upperResult, ConvexSubhull.Type.UPPER);
        ConvexSubhull lowerSubhull = new ConvexSubhull(lowerResult, ConvexSubhull.Type.LOWER);

        return new ConvexHull(upperSubhull, lowerSubhull);
    }

    static Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>> moveCornerPointsUp(
            ConcatenableQueue<Point2D> upper, ConcatenableQueue<Point2D> lower) {
        ConcatenableQueue<Point2D> lowerRest = lower;
        if ((lower.minNode != null && upper.minNode == null) ||
                (lower.minNode != null && upper.minNode != null &&
                        lower.minNode.data.x < upper.minNode.data.x)) {
            lowerRest = lower.cutRight(lower.minNode.data);
            upper = ConcatenableQueue.concatenate(lower, upper);
        }

        lower = lowerRest;
        if ((lowerRest.maxNode != null && upper.maxNode == null) ||
                (lowerRest.maxNode != null && upper.minNode != null &&
                        lowerRest.maxNode.data.x > upper.maxNode.data.x)) {
            lower = lowerRest.cutLeft(lowerRest.maxNode.data);
            upper = ConcatenableQueue.concatenate(upper, lowerRest);
        }
        return Pair.of(upper, lower);
    }

    static ConcatenableQueue<Point2D> moveLeftCornerPointDown(
            ConcatenableQueue<Point2D> upperRest, ConcatenableQueue<Point2D> lower) {
        if ((upperRest.minNode != null && lower.minNode == null) ||
                (upperRest.minNode != null && lower.minNode != null &&
                        upperRest.minNode.data.x < lower.minNode.data.x/* &&
                        upperRest.minNode.data.y > lower.minNode.data.y*/)) {
            upperRest.cutRight(upperRest.minNode.data);
            lower = ConcatenableQueue.concatenate(upperRest, lower);
        }
        return lower;
    }

    static ConcatenableQueue<Point2D> moveRightCornerPointDown(
            ConcatenableQueue<Point2D> upperRest, ConcatenableQueue<Point2D> lower) {
        if ((upperRest.maxNode != null && lower.maxNode == null) ||
                (upperRest.maxNode != null && lower.maxNode != null &&
                        upperRest.maxNode.data.x > lower.maxNode.data.x /*&&
                        upperRest.maxNode.data.y > lower.maxNode.data.y*/)) {
            upperRest.cutLeft(upperRest.maxNode.data);
            lower = ConcatenableQueue.concatenate(lower, upperRest);
        }
        return lower;
    }


    static Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>> cutRest(
            ConcatenableQueue<Point2D> left, ConcatenableQueue<Point2D> right,
            TriFunction<ConcatenableQueue.Node<Point2D>, Double, Position, Integer> casesFunction) {
        if (left.root == null || right.root == null) {
            return new Pair<>(new ConcatenableQueue<>(left.cmp), new ConcatenableQueue<>(right.cmp));
        }
        Pair<ConcatenableQueue.Node<Point2D>, ConcatenableQueue.Node<Point2D>> p =
                tangent(left, right, casesFunction);

        ConcatenableQueue.Node<Point2D> leftBase = p.getFirst();
        ConcatenableQueue.Node<Point2D> rightBase = p.getSecond();

        ConcatenableQueue<Point2D> leftRest = left.cutRight(leftBase.data);
        ConcatenableQueue<Point2D> rightRest = right.cutLeft(rightBase.data);

        return Pair.of(leftRest, rightRest);
    }

    static Pair<ConcatenableQueue.Node<Point2D>, ConcatenableQueue.Node<Point2D>> tangent(
            ConcatenableQueue<Point2D> lHull,
            ConcatenableQueue<Point2D> rHull,
            TriFunction<ConcatenableQueue.Node<Point2D>, Double, Position, Integer> casesFunction) {

        //locate the appropriate pointers on both hulls
        ConcatenableQueue.Node<Point2D> leftIt = lHull.root;
        ConcatenableQueue.Node<Point2D> rightIt = rHull.root;

        boolean done = false;
        double middleX = (lHull.maxNode.data.x + rHull.minNode.data.x) / 2.0;

        while (!done) {
            double tangentSlope = computeSlope(leftIt.lMax, rightIt.lMax);
            int leftCase = casesFunction.apply(leftIt.lMax, tangentSlope, Position.LEFT);
            int rightCase = casesFunction.apply(rightIt.lMax, tangentSlope, Position.RIGHT);

            switch (leftCase) {
                case -1:
                    switch (rightCase) {
                        case -1:
                            rightIt = rightIt.right;
                            break;
                        case 0:
                            leftIt = leftIt.right;
                            if (!rightIt.isLeaf && rightIt.right != null) {
                                rightIt = rightIt.right;
                            }
                            break;
                        case +1: //the most difficult one
                            double leftHeight = leftIt.lMax.data.y +
                                    computeSlope(leftIt.lMax, leftIt.lMax.right) * (middleX - leftIt.lMax.data.x);
                            double rightHeight = rightIt.lMax.data.y +
                                    computeSlope(rightIt.lMax.left, rightIt.lMax) * (middleX - rightIt.lMax.data.x);
                            if (leftHeight <= rightHeight) {
                                rightIt = rightIt.left;
                            } else {
                                leftIt = leftIt.right;
                            }
                            break;
                    }
                    break;
                case 0:
                    switch (rightCase) {
                        case -1:
                            if (!leftIt.isLeaf && leftIt.left != null) {
                                leftIt = leftIt.left;
                            }
                            rightIt = rightIt.right;
                            break;
                        case 0: // done
                            leftIt = leftIt.lMax;
                            rightIt = rightIt.lMax;
                            done = true;
                            break;
                        case +1:
                            if (!leftIt.isLeaf && leftIt.left != null) {
                                leftIt = leftIt.left;
                            }
                            rightIt = rightIt.left;
                            break;
                    }
                    break;
                case +1:
                    switch (rightCase) {
                        case -1:
                            leftIt = leftIt.left;
                            rightIt = rightIt.right;
                            break;
                        case 0:
                            leftIt = leftIt.left;
                            if (!rightIt.isLeaf && rightIt.right != null) {
                                rightIt = rightIt.right;
                            }
                            break;
                        case +1:
                            leftIt = leftIt.left;
                            break;
                    }
                    break;
            }
        }

        return Pair.of(leftIt, rightIt);
    }


    static double computeSlope(ConcatenableQueue.Node<Point2D> leftNode, ConcatenableQueue.Node<Point2D> rightNode) {
        assert leftNode != null && rightNode != null : "leftN != null && rightNode != null";
        assert leftNode.isLeaf && rightNode.isLeaf : "leftN.isLeaf && rightNode.isLeaf";
        return computeSlope(leftNode.data, rightNode.data);
    }

    static double computeSlope(Point2D left, Point2D right) {
        assert (right.x - left.x) >= 0 : "angle is defined form left to right";

        return (right.y - left.y) / (right.x - left.x);
    }

    static int getUpperTangentCase(ConcatenableQueue.Node<Point2D> node, double tangentSlope, Position position) {
        assert node != null : "n != null";
        assert node.isLeaf : "n.isLeaf";

        boolean leftSlopeGreater = true;
        boolean rightSlopeGreater = false;

        if (node.left != null) {
            double leftSlope = computeSlope(node.left, node);
            if ((position.equals(Position.LEFT) && leftSlope <= tangentSlope) ||
                    (position.equals(Position.RIGHT) && leftSlope < tangentSlope)) {
                leftSlopeGreater = false;
            }
        }

        if (node.right != null) {
            double rightSlope = computeSlope(node, node.right);
            if ((position.equals(Position.LEFT) && rightSlope > tangentSlope) ||
                    (position.equals(Position.RIGHT) && rightSlope >= tangentSlope)) {
                rightSlopeGreater = true;
            }
        }

        if (leftSlopeGreater && rightSlopeGreater) {
            return -1;
        } else if (!leftSlopeGreater && !rightSlopeGreater) {
            return +1;
        } else {
            assert leftSlopeGreater && !rightSlopeGreater : "concave";
            // corner case for single-node-subhull also comes here
            return 0;
        }
    }

    static int getLowerTangentCase(ConcatenableQueue.Node<Point2D> node, double tangentSlope, Position position) {
        assert node != null : "n != null";
        assert node.isLeaf : "n.isLeaf";

        boolean leftSlopeGreater = false;
        boolean rightSlopeGreater = true;

        if (node.left != null) {
            double leftSlope = computeSlope(node.left, node);
            if ((position.equals(Position.LEFT) && leftSlope >= tangentSlope) ||
                    (position.equals(Position.RIGHT) && leftSlope > tangentSlope)) {
                leftSlopeGreater = true;
            }
        }

        if (node.right != null) {
            double rightSlope = computeSlope(node, node.right);
            if ((position.equals(Position.LEFT) && rightSlope < tangentSlope) ||
                    (position.equals(Position.RIGHT) && rightSlope <= tangentSlope)) {
                rightSlopeGreater = false;
            }
        }

        if (leftSlopeGreater && rightSlopeGreater) {
            return +1;
        } else if (!leftSlopeGreater && !rightSlopeGreater) {
            return -1;
        } else {
            assert !leftSlopeGreater && rightSlopeGreater : "concave";

            // corner case for single-node-SubHull also comes here
            return 0;
        }
    }


    static ConcatenableQueue<Point2D> adjustLowerHull(ConcatenableQueue<Point2D> upperHull, ConcatenableQueue<Point2D> lowerHull) {
        if (lowerHull.root == null) {
            return lowerHull;
        }
        ConcatenableQueue.Node<Point2D> leftBaseNode = getBaseNode(lowerHull, upperHull.minNode, Position.LEFT, false);
        ConcatenableQueue.Node<Point2D> rightBaseNode = getBaseNode(lowerHull, upperHull.maxNode, Position.RIGHT, true);

        // base case
        if (leftBaseNode.data.compareTo(rightBaseNode.data) > 0) {
            return new ConcatenableQueue<>(lowerHull.cmp);
        }

        lowerHull.cutLeft(leftBaseNode.data);
        lowerHull.cutRight(rightBaseNode.data);

        double leftSlope = computeSlope(upperHull.minNode, lowerHull.minNode);
        double rightSlope = computeSlope(lowerHull.maxNode, upperHull.maxNode);

        if (leftSlope >= rightSlope) {
            return new ConcatenableQueue<>(lowerHull.cmp);
        }

        return lowerHull;
    }


    static ConcatenableQueue.Node<Point2D> getBaseNode(ConcatenableQueue<Point2D> lowerHull,
                                                       ConcatenableQueue.Node<Point2D> compareNode,
                                                       Position position,
                                                       boolean reversed) {
        // locate pointer on the hull
        ConcatenableQueue.Node<Point2D> hullIt = lowerHull.root;

        boolean done = false;
        while (!done) {
            double tangentSlope;
            if (reversed) {
                tangentSlope = computeSlope(hullIt.lMax, compareNode);
            } else {
                tangentSlope = computeSlope(compareNode, hullIt.lMax);
            }
            int slopeCase = getLowerBaseCase(hullIt.lMax, tangentSlope, position);

            if (slopeCase == -1) {
                hullIt = hullIt.right;
            } else if (slopeCase == 0) { // done
                done = true;
                hullIt = hullIt.lMax;
            } else { // slopeCase == 1
                hullIt = hullIt.left;
            }
        }
        return hullIt;
    }


    static int getLowerBaseCase(ConcatenableQueue.Node<Point2D> node,
                                double tangentSlope,
                                Position baseNodePosition) {
        assert node != null : "n != null";
        assert node.isLeaf : "n.isLeaf";

        boolean leftSlopeGreater = false;
        boolean rightSlopeGreater = true;

        if (node.left != null) {
            double leftSlope = computeSlope(node.left, node);
            if ((baseNodePosition.equals(Position.LEFT) && leftSlope > tangentSlope) ||
                    (baseNodePosition.equals(Position.RIGHT) && leftSlope >= tangentSlope)) {
                leftSlopeGreater = true;
            }
        }

        if (node.right != null) {
            double rightSlope = computeSlope(node, node.right);
            if ((baseNodePosition.equals(Position.LEFT) && rightSlope <= tangentSlope) ||
                    (baseNodePosition.equals(Position.RIGHT) && rightSlope < tangentSlope)) {
                rightSlopeGreater = false;
            }
        }

        if (leftSlopeGreater && rightSlopeGreater) {
            return +1;
        } else if (!leftSlopeGreater && !rightSlopeGreater) {
            return -1;
        } else {
            assert !leftSlopeGreater && rightSlopeGreater : "concave";
            // corner case for single-node-subhull also comes here
            return 0;
        }
    }


    @Override
    public String toString() {
        Iterator<Point2D> iterator = this.iterator();
        if (!iterator.hasNext()) {
            return "[]";
        } else {
            StringBuilder var2 = new StringBuilder();
            var2.append('[');

            while (true) {
                Point2D element = iterator.next();
                var2.append(element);
                if (!iterator.hasNext()) {
                    return var2.append(']').toString();
                }

                var2.append(',').append(' ');
            }
        }
    }

    @Override
    public Iterator<Point2D> iterator() {
        return new CHIterator();
    }

    private class CHIterator implements Iterator<Point2D> {
        Iterator<Point2D> iterator;
        ConvexSubhull.Type hullType;

        CHIterator() {
            iterator = upperSubhull.subhull.iterator();
            hullType = ConvexSubhull.Type.UPPER;
            changeIteratorIfNeeded();
        }

        @Override
        public boolean hasNext() {
            changeIteratorIfNeeded();
            return iterator.hasNext();
        }

        @Override
        public Point2D next() {
            changeIteratorIfNeeded();
            return iterator.next();
        }

        private void changeIteratorIfNeeded() {
            if (!iterator.hasNext() && hullType.equals(ConvexSubhull.Type.UPPER)) {
                iterator = lowerSubhull.subhull.reverseIterator();
                hullType = ConvexSubhull.Type.LOWER;
            }
        }
    }
}
