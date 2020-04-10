package com.chudakov.geometry.uae;

import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.core.DaCAlgorithm;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

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
        return new UAEResult(convexHull, p.getLeft(), p.getRight());
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
        ConvexHull convexHull = new ConvexHull(upperSubhull, lowerSubhull);

        Pair<DTEdge, DTEdge> p = getTriangulationEdges(left, right);
        return new UAEResult(convexHull, p.getLeft(), p.getRight());
    }

    private Pair<DTEdge, DTEdge> getTriangulationEdges(UAEResult left, UAEResult right) {
        throw new UnsupportedOperationException("not implemented");
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
        return deleteCocircularPoints(points);
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

    static List<Point2D> deleteCocircularPoints(List<Point2D> points) {
        throw new UnsupportedOperationException("not implemented");
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
