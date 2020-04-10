package com.chudakov.geometry.uae;

import com.chudakov.geometry.common.Point2D;
import org.apache.commons.lang3.tuple.Pair;

public class CH {
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
                        upperRest.minNode.data.x < lower.minNode.data.x)) {
            upperRest.cutRight(upperRest.minNode.data);
            lower = ConcatenableQueue.concatenate(upperRest, lower);
        }
        return lower;
    }

    static ConcatenableQueue<Point2D> moveRightCornerPointDown(
            ConcatenableQueue<Point2D> upperRest, ConcatenableQueue<Point2D> lower) {
        if ((upperRest.maxNode != null && lower.maxNode == null) ||
                (upperRest.maxNode != null && lower.maxNode != null &&
                        upperRest.maxNode.data.x > lower.maxNode.data.x)) {
            upperRest.cutLeft(upperRest.maxNode.data);
            lower = ConcatenableQueue.concatenate(lower, upperRest);
        }
        return lower;
    }


    static Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>> cutRest(
            ConcatenableQueue<Point2D> left, ConcatenableQueue<Point2D> right,
            ConvexHull.TriFunction<ConcatenableQueue.CQNode<Point2D>, Double, ConvexHull.Position, Integer> casesFunction) {
        if (left.root == null || right.root == null) {
            return Pair.of(new ConcatenableQueue<>(left.cmp), new ConcatenableQueue<>(right.cmp));
        }
        Pair<ConcatenableQueue.CQNode<Point2D>, ConcatenableQueue.CQNode<Point2D>> p =
                tangent(left, right, casesFunction);

        ConcatenableQueue.CQNode<Point2D> leftBase = p.getLeft();
        ConcatenableQueue.CQNode<Point2D> rightBase = p.getRight();

        ConcatenableQueue<Point2D> leftRest = left.cutRight(leftBase.data);
        ConcatenableQueue<Point2D> rightRest = right.cutLeft(rightBase.data);

        return Pair.of(leftRest, rightRest);
    }

    static Pair<ConcatenableQueue.CQNode<Point2D>, ConcatenableQueue.CQNode<Point2D>> tangent(
            ConcatenableQueue<Point2D> leftHull,
            ConcatenableQueue<Point2D> rightHull,
            ConvexHull.TriFunction<ConcatenableQueue.CQNode<Point2D>, Double, ConvexHull.Position, Integer> casesFunction) {

        //locate the appropriate pointers on both hulls
        ConcatenableQueue.CQNode<Point2D> leftIterator = leftHull.root;
        ConcatenableQueue.CQNode<Point2D> rightIterator = rightHull.root;

        boolean done = false;
        double middleX = (leftHull.maxNode.data.x + rightHull.minNode.data.x) / 2.0;

        while (!done) {
            double tangentSlope = getSlope(leftIterator.leftSubtreeMax, rightIterator.leftSubtreeMax);
            int leftCase = casesFunction.apply(leftIterator.leftSubtreeMax, tangentSlope, ConvexHull.Position.LEFT);
            int rightCase = casesFunction.apply(rightIterator.leftSubtreeMax, tangentSlope, ConvexHull.Position.RIGHT);

            if (leftCase == -1) {
                if (rightCase == -1) {
                    rightIterator = rightIterator.right;
                } else if (rightCase == 0) {
                    leftIterator = leftIterator.right;
                    if (!rightIterator.isLeaf && rightIterator.right != null) {
                        rightIterator = rightIterator.right;
                    }
                } else {
                    double leftHeight = leftIterator.leftSubtreeMax.data.y +
                            getSlope(leftIterator.leftSubtreeMax, leftIterator.leftSubtreeMax.right) * (middleX - leftIterator.leftSubtreeMax.data.x);
                    double rightHeight = rightIterator.leftSubtreeMax.data.y +
                            getSlope(rightIterator.leftSubtreeMax.left, rightIterator.leftSubtreeMax) * (middleX - rightIterator.leftSubtreeMax.data.x);
                    if (leftHeight <= rightHeight) {
                        rightIterator = rightIterator.left;
                    } else { // rightCase == 1
                        leftIterator = leftIterator.right;
                    }
                }
            } else if (leftCase == 0) {
                if (rightCase == -1) {
                    if (!leftIterator.isLeaf && leftIterator.left != null) {
                        leftIterator = leftIterator.left;
                    }
                    rightIterator = rightIterator.right;
                } else if (rightCase == 0) {
                    leftIterator = leftIterator.leftSubtreeMax;
                    rightIterator = rightIterator.leftSubtreeMax;
                    done = true;
                } else { // rightCase == 1
                    if (!leftIterator.isLeaf && leftIterator.left != null) {
                        leftIterator = leftIterator.left;
                    }
                    rightIterator = rightIterator.left;
                }
            } else { // leftCase == 1
                if (rightCase == -1) {
                    leftIterator = leftIterator.left;
                    rightIterator = rightIterator.right;
                } else if (rightCase == 0) {
                    leftIterator = leftIterator.left;
                    if (!rightIterator.isLeaf && rightIterator.right != null) {
                        rightIterator = rightIterator.right;
                    }
                } else { // rightCase == 1
                    leftIterator = leftIterator.left;
                }
            }
        }
        return Pair.of(leftIterator, rightIterator);
    }


    static double getSlope(ConcatenableQueue.CQNode<Point2D> leftNode, ConcatenableQueue.CQNode<Point2D> rightNode) {
        return Point2D.getSlope(leftNode.data, rightNode.data);
    }


    static int getUpperTangentCase(ConcatenableQueue.CQNode<Point2D> node, double tangentSlope, ConvexHull.Position subHullPosition) {
        boolean leftSlopeGreater = true;
        boolean rightSlopeGreater = false;

        if (node.left != null) {
            double leftSlope = getSlope(node.left, node);
            if ((subHullPosition.equals(ConvexHull.Position.LEFT) && leftSlope <= tangentSlope) ||
                    (subHullPosition.equals(ConvexHull.Position.RIGHT) && leftSlope < tangentSlope)) {
                leftSlopeGreater = false;
            }
        }

        if (node.right != null) {
            double rightSlope = getSlope(node, node.right);
            if ((subHullPosition.equals(ConvexHull.Position.LEFT) && rightSlope > tangentSlope) ||
                    (subHullPosition.equals(ConvexHull.Position.RIGHT) && rightSlope >= tangentSlope)) {
                rightSlopeGreater = true;
            }
        }

        if (leftSlopeGreater && rightSlopeGreater) {
            return -1;
        } else if (!leftSlopeGreater && !rightSlopeGreater) {
            return +1;
        } else {
            if (!leftSlopeGreater && rightSlopeGreater) {
                throw new IllegalArgumentException("concave hull segment");
            }
            return 0;
        }
    }

    static int getLowerTangentCase(ConcatenableQueue.CQNode<Point2D> node, double tangentSlope, ConvexHull.Position subHullPosition) {
        boolean leftSlopeGreater = false;
        boolean rightSlopeGreater = true;

        if (node.left != null) {
            double leftSlope = getSlope(node.left, node);
            if ((subHullPosition.equals(ConvexHull.Position.LEFT) && leftSlope >= tangentSlope) ||
                    (subHullPosition.equals(ConvexHull.Position.RIGHT) && leftSlope > tangentSlope)) {
                leftSlopeGreater = true;
            }
        }

        if (node.right != null) {
            double rightSlope = getSlope(node, node.right);
            if ((subHullPosition.equals(ConvexHull.Position.LEFT) && rightSlope < tangentSlope) ||
                    (subHullPosition.equals(ConvexHull.Position.RIGHT) && rightSlope <= tangentSlope)) {
                rightSlopeGreater = false;
            }
        }

        if (leftSlopeGreater && rightSlopeGreater) {
            return +1;
        } else if (!leftSlopeGreater && !rightSlopeGreater) {
            return -1;
        } else {
            if (leftSlopeGreater && !rightSlopeGreater) {
                throw new IllegalArgumentException("concave hull segment");
            }
            return 0;
        }
    }


    static ConcatenableQueue<Point2D> adjustLowerHull(ConcatenableQueue<Point2D> upperHull, ConcatenableQueue<Point2D> lowerHull) {
        if (lowerHull.root == null) {
            return lowerHull;
        }
        ConcatenableQueue.CQNode<Point2D> leftBaseNode = getBaseNode(lowerHull, upperHull.minNode, ConvexHull.Position.LEFT);
        ConcatenableQueue.CQNode<Point2D> rightBaseNode = getBaseNode(lowerHull, upperHull.maxNode, ConvexHull.Position.RIGHT);

        // corner case
        if (leftBaseNode.data.compareTo(rightBaseNode.data) > 0) {
            return new ConcatenableQueue<>(lowerHull.cmp);
        }

        lowerHull.cutLeft(leftBaseNode.data);
        lowerHull.cutRight(rightBaseNode.data);

        double leftSlope = getSlope(upperHull.minNode, lowerHull.minNode);
        double rightSlope = getSlope(lowerHull.maxNode, upperHull.maxNode);

        if (leftSlope >= rightSlope) {
            return new ConcatenableQueue<>(lowerHull.cmp);
        }

        return lowerHull;
    }


    static ConcatenableQueue.CQNode<Point2D> getBaseNode(ConcatenableQueue<Point2D> lowerHull,
                                                         ConcatenableQueue.CQNode<Point2D> compareNode,
                                                         ConvexHull.Position compareNodePosition) {
        ConcatenableQueue.CQNode<Point2D> hullIterator = lowerHull.root;

        boolean done = false;
        while (!done) {
            double tangentSlope;
            if (compareNodePosition.equals(ConvexHull.Position.LEFT)) {
                tangentSlope = getSlope(compareNode, hullIterator.leftSubtreeMax);
            } else {
                tangentSlope = getSlope(hullIterator.leftSubtreeMax, compareNode);
            }
            int slopeCase = getLowerBaseCase(hullIterator.leftSubtreeMax, tangentSlope, compareNodePosition);

            if (slopeCase == -1) {
                hullIterator = hullIterator.right;
            } else if (slopeCase == 0) { // done
                done = true;
                hullIterator = hullIterator.leftSubtreeMax;
            } else { // slopeCase == 1
                hullIterator = hullIterator.left;
            }
        }
        return hullIterator;
    }


    static int getLowerBaseCase(ConcatenableQueue.CQNode<Point2D> node,
                                double tangentSlope,
                                ConvexHull.Position nodePosition) {
        boolean leftSlopeGreater = false;
        boolean rightSlopeGreater = true;

        if (node.left != null) {
            double leftSlope = getSlope(node.left, node);
            if ((nodePosition.equals(ConvexHull.Position.LEFT) && leftSlope > tangentSlope) ||
                    (nodePosition.equals(ConvexHull.Position.RIGHT) && leftSlope >= tangentSlope)) {
                leftSlopeGreater = true;
            }
        }

        if (node.right != null) {
            double rightSlope = getSlope(node, node.right);
            if ((nodePosition.equals(ConvexHull.Position.LEFT) && rightSlope <= tangentSlope) ||
                    (nodePosition.equals(ConvexHull.Position.RIGHT) && rightSlope < tangentSlope)) {
                rightSlopeGreater = false;
            }
        }

        if (leftSlopeGreater && rightSlopeGreater) {
            return +1;
        } else if (!leftSlopeGreater && !rightSlopeGreater) {
            return -1;
        } else {
            if (leftSlopeGreater && !rightSlopeGreater) {
                throw new IllegalArgumentException("concave hull segment");
            }
            return 0;
        }
    }
}
