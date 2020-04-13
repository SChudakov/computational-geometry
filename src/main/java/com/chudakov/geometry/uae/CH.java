package com.chudakov.geometry.uae;

import com.chudakov.geometry.common.Point2D;
import org.apache.commons.lang3.tuple.Pair;

import static com.chudakov.geometry.uae.ConcatenableQueue.CQNode;
import static com.chudakov.geometry.uae.ConvexHull.Position;

public class CH {
    @FunctionalInterface
    interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }

    static CutData cutSubhulls(ConcatenableQueue<Point2D> leftUpper,
                               ConcatenableQueue<Point2D> leftLower,
                               ConcatenableQueue<Point2D> rightUpper,
                               ConcatenableQueue<Point2D> rightLower,
                               Pair<CQNode<Point2D>, CQNode<Point2D>> upperTangent,
                               Pair<CQNode<Point2D>, CQNode<Point2D>> lowerTangent) {
        CQNode<Point2D> ul = upperTangent.getLeft();
        CQNode<Point2D> ur = upperTangent.getRight();
        CQNode<Point2D> ll = lowerTangent.getLeft();
        CQNode<Point2D> lr = lowerTangent.getRight();

        // 1. cut left subhulls
        if (ul.equals(leftLower.minNode)) {
            leftUpper.clear();
            leftLower.cutRight(ll.data);
        } else if (ul.equals(leftLower.maxNode)) {
            Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>> p = moveRightmostPointUp(leftUpper, leftLower);
            leftUpper = p.getLeft();
            leftLower = p.getRight();
            leftLower.cutRight(ll.data);
        } else {
            leftUpper.cutRight(ul.data);
            leftLower.cutRight(ll.data);
        }

        // 2. cut right subhulls
        if (ur.equals(rightLower.minNode)) {
            Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>> p = moveLeftmostPointUp(rightUpper, rightLower);
            rightUpper = p.getLeft();
            rightLower = p.getRight();
            rightLower.cutLeft(lr.data);
        } else if (ur.equals(rightLower.maxNode)) {
            rightUpper.clear();
            rightLower.cutLeft(lr.data);
        } else {
            rightUpper.cutLeft(ur.data);
            rightLower.cutLeft(lr.data);
        }

        return new CutData(leftUpper, leftLower, rightUpper, rightLower);
    }

    static Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>>
    moveUtmostPointsUp(ConcatenableQueue<Point2D> upper, ConcatenableQueue<Point2D> lower) {
        Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>> p1
                = moveLeftmostPointUp(upper, lower);
        upper = p1.getLeft();
        lower = p1.getValue();
        return moveRightmostPointUp(upper, lower);
    }

    static Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>>
    moveUtmostPointsDown(ConcatenableQueue<Point2D> upper, ConcatenableQueue<Point2D> lower) {
        ConcatenableQueue<Point2D> upperRest1 = upper;
        boolean moveLeftmostPoint = upper.minNode != null && lower.minNode == null;
        moveLeftmostPoint |= upper.minNode != null && lower.minNode != null
                && upper.minNode.data.x < lower.minNode.data.x;
        if (moveLeftmostPoint) {
            upperRest1 = upper.cutRight(upper.minNode.data);
            lower = ConcatenableQueue.concatenate(upper, lower);
        }

        ConcatenableQueue<Point2D> upperRest2 = upperRest1;
        boolean moveRightmostPoint = upperRest1.maxNode != null && lower.maxNode == null;
        moveRightmostPoint |= upperRest1.maxNode != null && lower.maxNode != null
                && upperRest1.maxNode.data.x > lower.maxNode.data.x;
        if (moveRightmostPoint) {
            upperRest2 = upperRest1.cutLeft(upperRest1.maxNode.data);
            lower = ConcatenableQueue.concatenate(lower, upperRest1);
        }
        return Pair.of(upperRest2, lower);
    }

    static Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>>
    moveLeftmostPointUp(ConcatenableQueue<Point2D> upper, ConcatenableQueue<Point2D> lower) {
        ConcatenableQueue<Point2D> lowerRest = lower;
        boolean moveLeftmostPoint = lower.minNode != null && upper.minNode == null;
        moveLeftmostPoint |= lower.minNode != null && upper.minNode != null
                && lower.minNode.data.x < upper.minNode.data.x;
        if (moveLeftmostPoint) {
            lowerRest = lower.cutRight(lower.minNode.data);
            upper = ConcatenableQueue.concatenate(lower, upper);
        }
        return Pair.of(upper, lowerRest);
    }

    static Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>>
    moveRightmostPointUp(ConcatenableQueue<Point2D> upper, ConcatenableQueue<Point2D> lower) {
        ConcatenableQueue<Point2D> lowerRest = lower;
        boolean moveRightmostPoint = lower.maxNode != null && upper.maxNode == null;
        moveRightmostPoint |= lower.maxNode != null && upper.minNode != null
                && lower.maxNode.data.x > upper.maxNode.data.x;
        if (moveRightmostPoint) {
            lowerRest = lower.cutLeft(lower.maxNode.data);
            upper = ConcatenableQueue.concatenate(upper, lower);
        }
        return Pair.of(upper, lowerRest);
    }

    static class CutData {
        ConcatenableQueue<Point2D> leftUpper;
        ConcatenableQueue<Point2D> leftLower;
        ConcatenableQueue<Point2D> rightUpper;
        ConcatenableQueue<Point2D> rightLower;

        public CutData(ConcatenableQueue<Point2D> leftUpper,
                       ConcatenableQueue<Point2D> leftLower,
                       ConcatenableQueue<Point2D> rightUpper,
                       ConcatenableQueue<Point2D> rightLower) {
            this.leftUpper = leftUpper;
            this.leftLower = leftLower;
            this.rightUpper = rightUpper;
            this.rightLower = rightLower;
        }
    }


    static Pair<CQNode<Point2D>, CQNode<Point2D>> tangent(
            ConcatenableQueue<Point2D> leftHull,
            ConcatenableQueue<Point2D> rightHull,
            TriFunction<CQNode<Point2D>, Double, Position, Integer> casesFunction) {

        //locate the appropriate pointers on both hulls
        CQNode<Point2D> leftIterator = leftHull.root;
        CQNode<Point2D> rightIterator = rightHull.root;

        boolean done = false;
        double middleX = (leftHull.maxNode.data.x + rightHull.minNode.data.x) / 2.0;

        while (!done) {
            double tangentSlope = getSlope(leftIterator.leftSubtreeMax, rightIterator.leftSubtreeMax);
            int leftCase = casesFunction.apply(leftIterator.leftSubtreeMax, tangentSlope, Position.LEFT);
            int rightCase = casesFunction.apply(rightIterator.leftSubtreeMax, tangentSlope, Position.RIGHT);

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


    static double getSlope(CQNode<Point2D> leftNode, CQNode<Point2D> rightNode) {
        return Point2D.getSlope(leftNode.data, rightNode.data);
    }


    static int getUpperTangentCase(CQNode<Point2D> node, double tangentSlope,
                                   Position subHullPosition) {
        boolean leftSlopeGreater = true;
        boolean rightSlopeGreater = false;

        if (node.left != null) {
            double leftSlope = getSlope(node.left, node);
            if ((subHullPosition.equals(Position.LEFT) && leftSlope <= tangentSlope) ||
                    (subHullPosition.equals(Position.RIGHT) && leftSlope < tangentSlope)) {
                leftSlopeGreater = false;
            }
        }

        if (node.right != null) {
            double rightSlope = getSlope(node, node.right);
            if ((subHullPosition.equals(Position.LEFT) && rightSlope > tangentSlope) ||
                    (subHullPosition.equals(Position.RIGHT) && rightSlope >= tangentSlope)) {
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

    static int getLowerTangentCase(CQNode<Point2D> node, double tangentSlope,
                                   Position subHullPosition) {
        boolean leftSlopeGreater = false;
        boolean rightSlopeGreater = true;

        if (node.left != null) {
            double leftSlope = getSlope(node.left, node);
            if ((subHullPosition.equals(Position.LEFT) && leftSlope >= tangentSlope) ||
                    (subHullPosition.equals(Position.RIGHT) && leftSlope > tangentSlope)) {
                leftSlopeGreater = true;
            }
        }

        if (node.right != null) {
            double rightSlope = getSlope(node, node.right);
            if ((subHullPosition.equals(Position.LEFT) && rightSlope < tangentSlope) ||
                    (subHullPosition.equals(Position.RIGHT) && rightSlope <= tangentSlope)) {
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


    static ConcatenableQueue<Point2D> adjustLowerHull(ConcatenableQueue<Point2D> upperHull,
                                                      ConcatenableQueue<Point2D> lowerHull) {
        if (lowerHull.root == null) {
            return lowerHull;
        }
        CQNode<Point2D> leftBaseNode = getBaseNode(lowerHull, upperHull.minNode, Position.LEFT);
        CQNode<Point2D> rightBaseNode = getBaseNode(lowerHull, upperHull.maxNode, Position.RIGHT);

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


    static CQNode<Point2D> getBaseNode(ConcatenableQueue<Point2D> lowerHull,
                                       CQNode<Point2D> compareNode,
                                       Position compareNodePosition) {
        CQNode<Point2D> hullIterator = lowerHull.root;

        boolean done = false;
        while (!done) {
            double tangentSlope;
            if (compareNodePosition.equals(Position.LEFT)) {
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


    static int getLowerBaseCase(CQNode<Point2D> node, double tangentSlope, Position nodePosition) {
        boolean leftSlopeGreater = false;
        boolean rightSlopeGreater = true;

        if (node.left != null) {
            double leftSlope = getSlope(node.left, node);
            if ((nodePosition.equals(Position.LEFT) && leftSlope > tangentSlope) ||
                    (nodePosition.equals(Position.RIGHT) && leftSlope >= tangentSlope)) {
                leftSlopeGreater = true;
            }
        }

        if (node.right != null) {
            double rightSlope = getSlope(node, node.right);
            if ((nodePosition.equals(Position.LEFT) && rightSlope <= tangentSlope) ||
                    (nodePosition.equals(Position.RIGHT) && rightSlope < tangentSlope)) {
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
