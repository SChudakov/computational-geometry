package com.chudakov.geometry.uae;

import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.core.DaCAlgorithm;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import static com.chudakov.geometry.uae.ConcatenableQueue.CQNode;

public class UAE2D implements DaCAlgorithm<List<Point2D>, UAEResult> {
    @Override
    public boolean isBaseCase(List<Point2D> points) {
        int size = points.size();
        return size == 2 || size == 3;
    }

    @Override
    public int inputSize(List<Point2D> input) {
        return input.size();
    }

    @Override
    public UAEResult solveBaseCase(List<Point2D> points) {
        ConvexHull convexHull = convexHullBaseCase(points);
        Pair<DTEdge, DTEdge> p = delaunayTriangulationBaseCase(points);
        return new UAEResult(convexHull, p.getLeft(), p.getRight()/*null,null*/);
    }

    private ConvexHull convexHullBaseCase(List<Point2D> points) {
        int size = points.size();

        ConcatenableQueue<Point2D> upper = new ConcatenableQueue<>();
        ConcatenableQueue<Point2D> lower = new ConcatenableQueue<>();
        if (size == 2) {
            if (points.get(0).y <= points.get(1).y) {
                lower.add(points.get(0));
                upper.add(points.get(1));
            } else {
                lower.add(points.get(1));
                upper.add(points.get(0));
            }
        } else {
            Point2D first = points.get(0);
            Point2D second = points.get(1);
            Point2D third = points.get(2);
            double leftSlope = Point2D.getSlope(first, second);
            double rightSlope = Point2D.getSlope(second, third);
            if (leftSlope < rightSlope) {
                if (first.y < second.y) {
                    upper.add(first);
                    upper.add(third);
                    lower.add(second);
                } else {
                    upper.add(first);
                    upper.add(third);
                    lower.add(second);
                }
            } else {//leftSlope > rightSlope
                if (first.y < second.y) {
                    upper.add(second);
                    upper.add(third);
                    lower.add(first);
                } else {
                    upper.add(first);
                    upper.add(second);
                    lower.add(third);
                }
            }
        }

        ConvexSubhull upperSubhull = new ConvexSubhull(upper, ConvexSubhull.Type.UPPER);
        ConvexSubhull lowerSubhull = new ConvexSubhull(lower, ConvexSubhull.Type.LOWER);

        return new ConvexHull(upperSubhull, lowerSubhull);
    }

    private Pair<DTEdge, DTEdge> delaunayTriangulationBaseCase(List<Point2D> points) {
        if (points.size() == 2) {
            DTEdge e = DT.makeEdge(points.get(0), points.get(1));
            return Pair.of(e, e.sym);
        }

        // points size = 3
        Point2D p1 = points.get(0);
        Point2D p2 = points.get(1);
        Point2D p3 = points.get(2);

        DTEdge a = DT.makeEdge(p1, p2);
        DTEdge b = DT.makeEdge(p2, p3);
        DT.splice(a.sym, b);

        // Close the triangle.
        if (DT.leftOf(p3, a)) {
            DT.connect(b, a);
            return Pair.of(a, b.sym);
        } else if (DT.leftOf(p3, a)) {
            DTEdge c = DT.connect(b, a);
            return Pair.of(c.sym, c);
        } else { // the three points are collinear
            return Pair.of(a, b.sym);
        }
    }

    @Override
    public UAEResult merge(UAEResult left, UAEResult right) {
        ConcatenableQueue<Point2D> leftUpper = left.convexHull.upperSubhull.subhull;
        ConcatenableQueue<Point2D> leftLower = left.convexHull.lowerSubhull.subhull;
        ConcatenableQueue<Point2D> rightUpper = right.convexHull.upperSubhull.subhull;
        ConcatenableQueue<Point2D> rightLower = right.convexHull.lowerSubhull.subhull;

        // 1. move utmost points up and compute upper tangent
        Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>> p1 = moveUtmostPointsUp(leftUpper, leftLower);
        leftUpper = p1.getLeft();
        leftLower = p1.getRight();
        Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>> p2 = moveUtmostPointsUp(rightUpper, rightLower);
        rightUpper = p2.getLeft();
        rightLower = p2.getRight();
        Pair<CQNode<Point2D>, CQNode<Point2D>> upperTangent = CH.tangent(leftUpper, rightUpper, CH::getUpperTangentCase);

        // 2. move utmost points down and compute lower tangent
        Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>> p3 = moveUtmostPointsDown(leftUpper, leftLower);
        leftUpper = p3.getLeft();
        leftLower = p3.getRight();
        Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>> p4 = moveUtmostPointsDown(rightUpper, rightLower);
        rightUpper = p4.getLeft();
        rightLower = p4.getRight();
        Pair<CQNode<Point2D>, CQNode<Point2D>> lowerTangent = CH.tangent(leftUpper, rightUpper, CH::getLowerTangentCase);

        // 3. use tangents in triangulation
        Pair<DTEdge, DTEdge> triangulationEdges = getTriangulationEdges(left, right, upperTangent, lowerTangent);

        // 4. cut convex hull w.r.t the tangents
        CutData data = cutSubhulls(leftUpper, leftLower, rightUpper, rightLower, upperTangent, lowerTangent);
        leftUpper = data.leftUpper;
        leftLower = data.leftLower;
        rightUpper = data.rightUpper;
        rightLower = data.rightLower;

        // 5. concatenate upper and lower queue
        ConcatenableQueue<Point2D> upperResult = ConcatenableQueue.concatenate(leftUpper, rightUpper);
        ConcatenableQueue<Point2D> lowerResult = ConcatenableQueue.concatenate(leftLower, rightLower);

        // 6. create sub-hulls and convex hull
        ConvexSubhull upperSubhull = new ConvexSubhull(upperResult, ConvexSubhull.Type.UPPER);
        ConvexSubhull lowerSubhull = new ConvexSubhull(lowerResult, ConvexSubhull.Type.LOWER);
        ConvexHull convexHull = new ConvexHull(upperSubhull, lowerSubhull);

        return new UAEResult(convexHull, triangulationEdges.getLeft(), triangulationEdges.getRight());
    }

    private CutData cutSubhulls(ConcatenableQueue<Point2D> leftUpper,
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
            leftUpper.clear();
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
            rightUpper.clear();
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

    private Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>>
    moveUtmostPointsUp(ConcatenableQueue<Point2D> upper, ConcatenableQueue<Point2D> lower) {
        Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>> p1
                = moveLeftmostPointUp(upper, lower);
        upper = p1.getLeft();
        lower = p1.getValue();
        return moveRightmostPointUp(upper, lower);
    }

    private Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>>
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

    private Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>>
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

    private Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>>
    moveRightmostPointUp(ConcatenableQueue<Point2D> upper, ConcatenableQueue<Point2D> lower) {
        ConcatenableQueue<Point2D> lowerRest = lower;
        boolean moveRightmostPoint = lower.maxNode != null && upper.maxNode == null;
        moveRightmostPoint |= lower.maxNode != null && upper.minNode != null
                && lower.maxNode.data.x > upper.maxNode.data.x;
        if (moveRightmostPoint) {
            lowerRest = lower.cutLeft(lower.maxNode.data);
            upper = ConcatenableQueue.concatenate(upper, lower);
        }
        return Pair.of(lowerRest, upper);
    }


    private Pair<DTEdge, DTEdge> getTriangulationEdges(UAEResult left, UAEResult right,
                                                       Pair<CQNode<Point2D>, CQNode<Point2D>> upperTangent,
                                                       Pair<CQNode<Point2D>, CQNode<Point2D>> lowerTangent) {
        DTEdge ldo = left.e1;
        DTEdge ldi = left.e2;

        DTEdge rdi = right.e1;
        DTEdge rdo = right.e2;

        // Compute the upper common tangent of L and R.
        while (true) {
            if (DT.rightOf(rdi.org, ldi)) {
                ldi = ldi.sym.onext;
            } else if (DT.leftOf(ldi.org, rdi)) {
                rdi = rdi.sym.oprev;
            } else {
                break;
            }
        }

        // Create a first cross edge base from rdi.org to ldi.org.
        DTEdge base = DT.connect(ldi.sym, rdi);

        // Adjust ldo and rdo
        if (ldi.org.x == ldo.org.x && ldi.org.y == ldo.org.y) {
            ldo = base;
        }
        if (rdi.org.x == rdo.org.x && rdi.org.y == rdo.org.y) {
            rdo = base.sym;
        }

        // Merge
        while (true) {
            // Locate the first R and L points to be encountered by the diving bubble.
            DTEdge rcand = base.sym.onext;
            DTEdge lcand = base.oprev;

            // If both lcand and rcand are invalid, then base is the lower common tangent.
            boolean v_rcand = DT.rightOf(rcand.dest, base);
            boolean v_lcand = DT.rightOf(lcand.dest, base);
            if (!(v_rcand || v_lcand)) {
                break;
            }

            // Delete R edges out of base.dest that fail the circle test.
            if (v_rcand) {
                while (DT.rightOf(rcand.onext.dest, base) ||
                        DT.inCircle(base.dest, base.org, rcand.dest, rcand.onext.dest)) {
                    DTEdge t = rcand.onext;
                    DT.deleteEdge(rcand);
                    rcand = t;
                }
            }
            // Symmetrically, delete L edges.
            if (v_lcand) {
                while (DT.rightOf(lcand.oprev.dest, base) &&
                        DT.inCircle(base.dest, base.org, lcand.dest, lcand.oprev.dest)) {
                    DTEdge t = lcand.oprev;
                    DT.deleteEdge(lcand);
                    lcand = t;
                }
            }
            // The next cross edge is to be connected to either lcand.dest or rcand.dest.
            // If both are valid, then choose the appropriate one using the in_circle test.
            if (!v_rcand ||
                    (v_lcand && DT.inCircle(rcand.dest, rcand.org, lcand.org, lcand.dest))) {
                // Add cross edge base from rcand.dest to base.dest.
                base = DT.connect(lcand, base.sym);
            } else {
                // Add cross edge base from base.org to lcand.dest
                base = DT.connect(base.sym, rcand.sym);
            }
        }
        return Pair.of(ldo, rdo);
    }

    private static class CutData {
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


    private ConvexHull oldJoin(ConvexHull left, ConvexHull right) {
        ConcatenableQueue<Point2D> leftUpper = left.upperSubhull.subhull;
        ConcatenableQueue<Point2D> leftLower = left.lowerSubhull.subhull;
        ConcatenableQueue<Point2D> rightUpper = right.upperSubhull.subhull;
        ConcatenableQueue<Point2D> rightLower = right.lowerSubhull.subhull;

        // 1. prepare upper queues
        Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>> p1 = CH.moveCornerPointsUp(leftUpper, leftLower);
        leftUpper = p1.getLeft();
        leftLower = p1.getRight();
        Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>> p2 = CH.moveCornerPointsUp(rightUpper, rightLower);
        rightUpper = p2.getLeft();
        rightLower = p2.getRight();

        // 2. cut rests of upper queues
        Pair<ConcatenableQueue<Point2D>, ConcatenableQueue<Point2D>> rests =
                CH.cutRest(leftUpper, rightUpper, CH::getUpperTangentCase);
        ConcatenableQueue<Point2D> leftUpperRest = rests.getLeft();
        ConcatenableQueue<Point2D> rightUpperRest = rests.getRight();

        // 3. prepare lower queues
        leftLower = CH.moveRightCornerPointDown(leftUpperRest, leftLower);
        rightLower = CH.moveLeftCornerPointDown(rightUpperRest, rightLower);

        // 4: cut rests of lower queues
        CH.cutRest(leftLower, rightLower, CH::getLowerTangentCase);

        // 5. concatenate upper and lower queue
        ConcatenableQueue<Point2D> upperResult = ConcatenableQueue.concatenate(leftUpper, rightUpper);
        ConcatenableQueue<Point2D> lowerResult = ConcatenableQueue.concatenate(leftLower, rightLower);

        // 6. adjusting lower hull
        lowerResult = CH.adjustLowerHull(upperResult, lowerResult);

        // 7. create sub-hulls and convex hull
        ConvexSubhull upperSubhull = new ConvexSubhull(upperResult, ConvexSubhull.Type.UPPER);
        ConvexSubhull lowerSubhull = new ConvexSubhull(lowerResult, ConvexSubhull.Type.LOWER);

        return new ConvexHull(upperSubhull, lowerSubhull);
    }


    @Override
    public Pair<List<Point2D>, List<Point2D>> divide(List<Point2D> input) {
        int mid = input.size() / 2;
        return Pair.of(input.subList(0, mid), input.subList(mid, input.size()));
    }

    @Override
    public List<Point2D> precompute(List<Point2D> points) {
        points.sort(new AntiLOPointComparator());
        removeDuplicated(points, Comparator.comparingDouble(p -> p.y));
        points.sort(Point2D::compareTo);
        removeDuplicated(points, Comparator.comparingDouble(p -> p.x));
        return points;
    }


    static void removeDuplicated(List<Point2D> points, Comparator<Point2D> comparator) {
        ListIterator<Point2D> it1 = points.listIterator();
        ListIterator<Point2D> it2 = points.listIterator();

        // handle empty input case
        if (!it1.hasNext()) {
            return;
        }
        it1.next();

        while (it2.hasNext()) {
            if (it2.nextIndex() > 0 && it2.nextIndex() < points.size() - 1) {
                Point2D previous = it2.previous();
                it2.next();
                Point2D current = it2.next();
                Point2D next = it2.next();
                it2.previous();

                if (!(comparator.compare(previous, current) == 0 && comparator.compare(current, next) == 0)) {
                    it1.set(current);
                    if (it1.hasNext()) {
                        it1.next();
                    }
                }
            } else if (it2.nextIndex() == 0) {
                // one-element list case
                if (it1.hasNext()) {
                    it1.next();
                }
                it2.next();
            } else {
                it1.set(it2.next());
            }
        }
        if (it1.hasNext()) {
            int it1NextIndex = it1.nextIndex();
            while (points.size() != it1NextIndex) {
                points.remove(points.size() - 1);
            }
        }
    }

    static class AntiLOPointComparator implements Comparator<Point2D> {

        @Override
        public int compare(Point2D x, Point2D y) {
            if (x.y < y.y || (x.y == y.y && x.x < y.x)) {
                return -1;
            }
            if (x.x == y.x && x.y == y.y) {
                return 0;
            }
            return 1;
        }
    }
}
