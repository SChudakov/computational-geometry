package com.chudakov.uae.impl;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

import static com.chudakov.uae.impl.ConcatenableQueue.CQVertex;
import static com.chudakov.uae.impl.ConvexHull.Position;

public class CH {
    @FunctionalInterface
    interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }

    public static List<UAEVertex> convert(ConvexHull convexHull) {
        List<UAEVertex> result = new ArrayList<>();
        for (UAEVertex point : convexHull) {
            result.add(point);
        }
        return result;
    }


    static CutData cutSubhulls(ConcatenableQueue<UAEVertex> leftUpper,
                               ConcatenableQueue<UAEVertex> leftLower,
                               ConcatenableQueue<UAEVertex> rightUpper,
                               ConcatenableQueue<UAEVertex> rightLower,
                               Pair<CQVertex<UAEVertex>, CQVertex<UAEVertex>> upperTangent,
                               Pair<CQVertex<UAEVertex>, CQVertex<UAEVertex>> lowerTangent) {
        CQVertex<UAEVertex> ul = upperTangent.getLeft();
        CQVertex<UAEVertex> ur = upperTangent.getRight();
        CQVertex<UAEVertex> ll = lowerTangent.getLeft();
        CQVertex<UAEVertex> lr = lowerTangent.getRight();

        // 1. cut left subhulls
        if (ul.equals(leftLower.minNode)) {
            leftUpper.clear();
            leftLower.cutRight(ll.value);
        } else if (ul.equals(leftLower.maxNode)) {
            Pair<ConcatenableQueue<UAEVertex>, ConcatenableQueue<UAEVertex>> p = moveRightmostPointUp(leftUpper, leftLower);
            leftUpper = p.getLeft();
            leftLower = p.getRight();
            leftLower.cutRight(ll.value);
        } else {
            leftUpper.cutRight(ul.value);
            leftLower.cutRight(ll.value);
        }

        // 2. cut right subhulls
        if (ur.equals(rightLower.minNode)) {
            Pair<ConcatenableQueue<UAEVertex>, ConcatenableQueue<UAEVertex>> p = moveLeftmostPointUp(rightUpper, rightLower);
            rightUpper = p.getLeft();
            rightLower = p.getRight();
            rightLower.cutLeft(lr.value);
        } else if (ur.equals(rightLower.maxNode)) {
            rightUpper.clear();
            rightLower.cutLeft(lr.value);
        } else {
            rightUpper.cutLeft(ur.value);
            rightLower.cutLeft(lr.value);
        }

        return new CutData(leftUpper, leftLower, rightUpper, rightLower);
    }

    static Pair<ConcatenableQueue<UAEVertex>, ConcatenableQueue<UAEVertex>>
    moveUtmostPointsUp(ConcatenableQueue<UAEVertex> upper, ConcatenableQueue<UAEVertex> lower) {
        Pair<ConcatenableQueue<UAEVertex>, ConcatenableQueue<UAEVertex>> p1
                = moveLeftmostPointUp(upper, lower);
        upper = p1.getLeft();
        lower = p1.getValue();
        return moveRightmostPointUp(upper, lower);
    }

    static Pair<ConcatenableQueue<UAEVertex>, ConcatenableQueue<UAEVertex>>
    moveUtmostPointsDown(ConcatenableQueue<UAEVertex> upper, ConcatenableQueue<UAEVertex> lower) {
        ConcatenableQueue<UAEVertex> upperRest1 = upper;
        boolean moveLeftmostPoint = upper.minNode != null && lower.minNode == null;
        moveLeftmostPoint |= upper.minNode != null && lower.minNode != null
                && upper.minNode.value.x < lower.minNode.value.x;
        if (moveLeftmostPoint) {
            upperRest1 = upper.cutRight(upper.minNode.value);
            lower = ConcatenableQueue.concatenate(upper, lower);
        }

        ConcatenableQueue<UAEVertex> upperRest2 = upperRest1;
        boolean moveRightmostPoint = upperRest1.maxNode != null && lower.maxNode == null;
        moveRightmostPoint |= upperRest1.maxNode != null && lower.maxNode != null
                && upperRest1.maxNode.value.x > lower.maxNode.value.x;
        if (moveRightmostPoint) {
            upperRest2 = upperRest1.cutLeft(upperRest1.maxNode.value);
            lower = ConcatenableQueue.concatenate(lower, upperRest1);
        }
        return Pair.of(upperRest2, lower);
    }

    static Pair<ConcatenableQueue<UAEVertex>, ConcatenableQueue<UAEVertex>>
    moveLeftmostPointUp(ConcatenableQueue<UAEVertex> upper, ConcatenableQueue<UAEVertex> lower) {
        ConcatenableQueue<UAEVertex> lowerRest = lower;
        boolean moveLeftmostPoint = lower.minNode != null && upper.minNode == null;
        moveLeftmostPoint |= lower.minNode != null && upper.minNode != null
                && lower.minNode.value.x < upper.minNode.value.x;
        if (moveLeftmostPoint) {
            lowerRest = lower.cutRight(lower.minNode.value);
            upper = ConcatenableQueue.concatenate(lower, upper);
        }
        return Pair.of(upper, lowerRest);
    }

    static Pair<ConcatenableQueue<UAEVertex>, ConcatenableQueue<UAEVertex>>
    moveRightmostPointUp(ConcatenableQueue<UAEVertex> upper, ConcatenableQueue<UAEVertex> lower) {
        ConcatenableQueue<UAEVertex> lowerRest = lower;
        boolean moveRightmostPoint = lower.maxNode != null && upper.maxNode == null;
        moveRightmostPoint |= lower.maxNode != null && upper.minNode != null
                && lower.maxNode.value.x > upper.maxNode.value.x;
        if (moveRightmostPoint) {
            lowerRest = lower.cutLeft(lower.maxNode.value);
            upper = ConcatenableQueue.concatenate(upper, lower);
        }
        return Pair.of(upper, lowerRest);
    }

    static class CutData {
        ConcatenableQueue<UAEVertex> leftUpper;
        ConcatenableQueue<UAEVertex> leftLower;
        ConcatenableQueue<UAEVertex> rightUpper;
        ConcatenableQueue<UAEVertex> rightLower;

        public CutData(ConcatenableQueue<UAEVertex> leftUpper,
                       ConcatenableQueue<UAEVertex> leftLower,
                       ConcatenableQueue<UAEVertex> rightUpper,
                       ConcatenableQueue<UAEVertex> rightLower) {
            this.leftUpper = leftUpper;
            this.leftLower = leftLower;
            this.rightUpper = rightUpper;
            this.rightLower = rightLower;
        }
    }


    static Pair<CQVertex<UAEVertex>, CQVertex<UAEVertex>> tangent(
            ConcatenableQueue<UAEVertex> leftHull,
            ConcatenableQueue<UAEVertex> rightHull,
            TriFunction<CQVertex<UAEVertex>, Double, Position, Integer> casesFunction) {

        CQVertex<UAEVertex> leftIterator = leftHull.root;
        CQVertex<UAEVertex> rightIterator = rightHull.root;

        boolean done = false;
        double middleX = (leftHull.maxNode.value.x + rightHull.minNode.value.x) / 2.0;

        while (!done) {
            double tangentSlope = getSlope(leftIterator.leftSubtreeMax, rightIterator.leftSubtreeMax);
            int leftCase = casesFunction.apply(leftIterator.leftSubtreeMax, tangentSlope, Position.LEFT);
            int rightCase = casesFunction.apply(rightIterator.leftSubtreeMax, tangentSlope, Position.RIGHT);

            if (leftCase == -1) {
                if (rightCase == -1) {
                    rightIterator = rightIterator.rSon;
                } else if (rightCase == 0) {
                    leftIterator = leftIterator.rSon;
                    if (!rightIterator.isLeaf && rightIterator.rSon != null) {
                        rightIterator = rightIterator.rSon;
                    }
                } else {
                    double leftHeight = leftIterator.leftSubtreeMax.value.y +
                            getSlope(leftIterator.leftSubtreeMax, leftIterator.leftSubtreeMax.rSon) * (middleX - leftIterator.leftSubtreeMax.value.x);
                    double rightHeight = rightIterator.leftSubtreeMax.value.y +
                            getSlope(rightIterator.leftSubtreeMax.lSon, rightIterator.leftSubtreeMax) * (middleX - rightIterator.leftSubtreeMax.value.x);
                    if (leftHeight <= rightHeight) {
                        rightIterator = rightIterator.lSon;
                    } else { // rightCase == 1
                        leftIterator = leftIterator.rSon;
                    }
                }
            } else if (leftCase == 0) {
                if (rightCase == -1) {
                    if (!leftIterator.isLeaf && leftIterator.lSon != null) {
                        leftIterator = leftIterator.lSon;
                    }
                    rightIterator = rightIterator.rSon;
                } else if (rightCase == 0) {
                    leftIterator = leftIterator.leftSubtreeMax;
                    rightIterator = rightIterator.leftSubtreeMax;
                    done = true;
                } else { // rightCase == 1
                    if (!leftIterator.isLeaf && leftIterator.lSon != null) {
                        leftIterator = leftIterator.lSon;
                    }
                    rightIterator = rightIterator.lSon;
                }
            } else { // leftCase == 1
                if (rightCase == -1) {
                    leftIterator = leftIterator.lSon;
                    rightIterator = rightIterator.rSon;
                } else if (rightCase == 0) {
                    leftIterator = leftIterator.lSon;
                    if (!rightIterator.isLeaf && rightIterator.rSon != null) {
                        rightIterator = rightIterator.rSon;
                    }
                } else { // rightCase == 1
                    leftIterator = leftIterator.lSon;
                }
            }
        }
        return Pair.of(leftIterator, rightIterator);
    }


    static double getSlope(CQVertex<UAEVertex> leftNode, CQVertex<UAEVertex> rightNode) {
        return UAEVertex.getSlope(leftNode.value, rightNode.value);
    }


    static int getUpperTangentCase(CQVertex<UAEVertex> node, double tangentSlope,
                                   Position subHullPosition) {
        boolean leftSlopeGreater = true;
        boolean rightSlopeGreater = false;

        if (node.lSon != null) {
            double leftSlope = getSlope(node.lSon, node);
            if ((subHullPosition.equals(Position.LEFT) && leftSlope <= tangentSlope) ||
                    (subHullPosition.equals(Position.RIGHT) && leftSlope < tangentSlope)) {
                leftSlopeGreater = false;
            }
        }

        if (node.rSon != null) {
            double rightSlope = getSlope(node, node.rSon);
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

    static int getLowerTangentCase(CQVertex<UAEVertex> node, double tangentSlope,
                                   Position subHullPosition) {
        boolean leftSlopeGreater = false;
        boolean rightSlopeGreater = true;

        if (node.lSon != null) {
            double leftSlope = getSlope(node.lSon, node);
            if ((subHullPosition.equals(Position.LEFT) && leftSlope >= tangentSlope) ||
                    (subHullPosition.equals(Position.RIGHT) && leftSlope > tangentSlope)) {
                leftSlopeGreater = true;
            }
        }

        if (node.rSon != null) {
            double rightSlope = getSlope(node, node.rSon);
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


    static ConcatenableQueue<UAEVertex> adjustLowerHull(ConcatenableQueue<UAEVertex> upperHull,
                                                        ConcatenableQueue<UAEVertex> lowerHull) {
        if (lowerHull.root == null) {
            return lowerHull;
        }
        CQVertex<UAEVertex> leftBaseNode = getBaseNode(lowerHull, upperHull.minNode, Position.LEFT);
        CQVertex<UAEVertex> rightBaseNode = getBaseNode(lowerHull, upperHull.maxNode, Position.RIGHT);

        // corner case
        if (leftBaseNode.value.compareTo(rightBaseNode.value) > 0) {
            return new ConcatenableQueue<>(lowerHull.cmp);
        }

        lowerHull.cutLeft(leftBaseNode.value);
        lowerHull.cutRight(rightBaseNode.value);

        double leftSlope = getSlope(upperHull.minNode, lowerHull.minNode);
        double rightSlope = getSlope(lowerHull.maxNode, upperHull.maxNode);

        if (leftSlope >= rightSlope) {
            return new ConcatenableQueue<>(lowerHull.cmp);
        }

        return lowerHull;
    }


    static CQVertex<UAEVertex> getBaseNode(ConcatenableQueue<UAEVertex> lowerHull,
                                           CQVertex<UAEVertex> compareNode,
                                           Position compareNodePosition) {
        CQVertex<UAEVertex> hullIterator = lowerHull.root;

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
                hullIterator = hullIterator.rSon;
            } else if (slopeCase == 0) { // done
                done = true;
                hullIterator = hullIterator.leftSubtreeMax;
            } else { // slopeCase == 1
                hullIterator = hullIterator.lSon;
            }
        }
        return hullIterator;
    }


    static int getLowerBaseCase(CQVertex<UAEVertex> node, double tangentSlope, Position nodePosition) {
        boolean leftSlopeGreater = false;
        boolean rightSlopeGreater = true;

        if (node.lSon != null) {
            double leftSlope = getSlope(node.lSon, node);
            if ((nodePosition.equals(Position.LEFT) && leftSlope > tangentSlope) ||
                    (nodePosition.equals(Position.RIGHT) && leftSlope >= tangentSlope)) {
                leftSlopeGreater = true;
            }
        }

        if (node.rSon != null) {
            double rightSlope = getSlope(node, node.rSon);
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
