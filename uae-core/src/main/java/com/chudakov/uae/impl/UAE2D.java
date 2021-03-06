package com.chudakov.uae.impl;

import com.chudakov.uae.core.DaCAlgorithm;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;

import static com.chudakov.uae.impl.CH.CutData;
import static com.chudakov.uae.impl.ConcatenableQueue.CQVertex;

public class UAE2D implements DaCAlgorithm<List<UAEVertex>, UAEResult> {
    @Override
    public boolean isBaseCase(List<UAEVertex> points) {
        return points.size() <= 3;
    }

    @Override
    public int inputSize(List<UAEVertex> input) {
        return input.size();
    }

    @Override
    public List<UAEVertex> precompute(List<UAEVertex> points) {
        points = new ArrayList<>(new HashSet<>(points));
        // TODO: do not remove vertically and horizontally collinear points
        points.sort(new AntiLOPointComparator());
        removeDuplicated(points, Comparator.comparingDouble(p -> p.y));
        points.sort(UAEVertex::compareTo);
        removeDuplicated(points, Comparator.comparingDouble(p -> p.x));
        return points;
    }

    static void removeDuplicated(List<UAEVertex> points, Comparator<UAEVertex> comparator) {
        ListIterator<UAEVertex> it1 = points.listIterator();
        ListIterator<UAEVertex> it2 = points.listIterator();

        // handle empty input case
        if (!it1.hasNext()) {
            return;
        }
        it1.next();

        while (it2.hasNext()) {
            if (it2.nextIndex() > 0 && it2.nextIndex() < points.size() - 1) {
                UAEVertex previous = it2.previous();
                it2.next();
                UAEVertex current = it2.next();
                UAEVertex next = it2.next();
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


    @Override
    public UAEResult solveBaseCase(List<UAEVertex> points) {
        ConvexHull convexHull = convexHullBaseCase(points);
        Pair<QuadEdge, QuadEdge> p = delaunayTriangulationBaseCase(points);
        return new UAEResult(convexHull, p.getLeft(), p.getRight());
    }

    private ConvexHull convexHullBaseCase(List<UAEVertex> points) {
        int size = points.size();

        ConcatenableQueue<UAEVertex> upper = new ConcatenableQueue<>();
        ConcatenableQueue<UAEVertex> lower = new ConcatenableQueue<>();
        if (size == 1) {
            upper.add(points.get(0));
        } else if (size == 2) {
            if (points.get(0).y <= points.get(1).y) {
                lower.add(points.get(0));
                upper.add(points.get(1));
            } else {
                lower.add(points.get(1));
                upper.add(points.get(0));
            }
        } else if (size == 3) {
            UAEVertex first = points.get(0);
            UAEVertex second = points.get(1);
            UAEVertex third = points.get(2);
            double leftSlope = UAEVertex.getSlope(first, second);
            double rightSlope = UAEVertex.getSlope(second, third);
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

    private Pair<QuadEdge, QuadEdge> delaunayTriangulationBaseCase(List<UAEVertex> points) {
        if (points.size() == 0 || points.size() == 1) {
            return Pair.of(null, null);
        }

        if (points.size() == 2) {
            QuadEdge e = DT.makeEdge(points.get(0), points.get(1));
            return Pair.of(e, e.sym);
        }

        // points size = 3
        UAEVertex p1 = points.get(0);
        UAEVertex p2 = points.get(1);
        UAEVertex p3 = points.get(2);

        QuadEdge a = DT.makeEdge(p1, p2);
        QuadEdge b = DT.makeEdge(p2, p3);
        DT.splice(a.sym, b);

        // Close the triangle.
        if (DT.rightOf(p3, a)) {
            DT.connect(b, a);
            return Pair.of(a, b.sym);
        } else if (DT.leftOf(p3, a)) {
            QuadEdge c = DT.connect(b, a);
            return Pair.of(c.sym, c);
        } else { // the three points are collinear
            return Pair.of(a, b.sym);
        }
    }


    @Override
    public Pair<List<UAEVertex>, List<UAEVertex>> divide(List<UAEVertex> input) {
        int mid = input.size() / 2;
        return Pair.of(input.subList(0, mid), input.subList(mid, input.size()));
    }


    @Override
    public UAEResult merge(UAEResult left, UAEResult right) {
        ConcatenableQueue<UAEVertex> leftUpper = left.convexHull.upperSubhull.subhull;
        ConcatenableQueue<UAEVertex> leftLower = left.convexHull.lowerSubhull.subhull;
        ConcatenableQueue<UAEVertex> rightUpper = right.convexHull.upperSubhull.subhull;
        ConcatenableQueue<UAEVertex> rightLower = right.convexHull.lowerSubhull.subhull;

        // 1. move utmost points up and compute upper tangent
        Pair<ConcatenableQueue<UAEVertex>, ConcatenableQueue<UAEVertex>> p1 = CH.moveUtmostPointsUp(leftUpper, leftLower);
        leftUpper = p1.getLeft();
        leftLower = p1.getRight();
        Pair<ConcatenableQueue<UAEVertex>, ConcatenableQueue<UAEVertex>> p2 = CH.moveUtmostPointsUp(rightUpper, rightLower);
        rightUpper = p2.getLeft();
        rightLower = p2.getRight();
        Pair<CQVertex<UAEVertex>, CQVertex<UAEVertex>> upperTangent = CH.tangent(leftUpper, rightUpper, CH::getUpperTangentCase);

        // 2. move utmost points down and compute lower tangent
        Pair<ConcatenableQueue<UAEVertex>, ConcatenableQueue<UAEVertex>> p3 = CH.moveUtmostPointsDown(leftUpper, leftLower);
        leftUpper = p3.getLeft();
        leftLower = p3.getRight();
        Pair<ConcatenableQueue<UAEVertex>, ConcatenableQueue<UAEVertex>> p4 = CH.moveUtmostPointsDown(rightUpper, rightLower);
        rightUpper = p4.getLeft();
        rightLower = p4.getRight();
        Pair<CQVertex<UAEVertex>, CQVertex<UAEVertex>> lowerTangent = CH.tangent(leftLower, rightLower, CH::getLowerTangentCase);

        // 3. use tangents in triangulation
        Pair<QuadEdge, QuadEdge> triangulationEdges = getTriangulationEdges(left, right);

        // 4. cut convex hull w.r.t the tangents
        CutData data = CH.cutSubhulls(leftUpper, leftLower, rightUpper, rightLower, upperTangent, lowerTangent);
        leftUpper = data.leftUpper;
        leftLower = data.leftLower;
        rightUpper = data.rightUpper;
        rightLower = data.rightLower;

        // 5. concatenate upper and lower queue
        ConcatenableQueue<UAEVertex> upperResult = ConcatenableQueue.concatenate(leftUpper, rightUpper);
        ConcatenableQueue<UAEVertex> lowerResult = ConcatenableQueue.concatenate(leftLower, rightLower);

        // 6. create sub-hulls and convex hull
        ConvexSubhull upperSubhull = new ConvexSubhull(upperResult, ConvexSubhull.Type.UPPER);
        ConvexSubhull lowerSubhull = new ConvexSubhull(lowerResult, ConvexSubhull.Type.LOWER);
        ConvexHull convexHull = new ConvexHull(upperSubhull, lowerSubhull);

        return new UAEResult(convexHull, triangulationEdges.getLeft(), triangulationEdges.getRight());
    }

    private Pair<QuadEdge, QuadEdge> getTriangulationEdges(UAEResult left, UAEResult right) {
        QuadEdge ldo = left.e1;
        QuadEdge ldi = left.e2;

        QuadEdge rdi = right.e1;
        QuadEdge rdo = right.e2;

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
        QuadEdge base = DT.connect(ldi.sym, rdi);

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
            QuadEdge rcand = base.sym.onext;
            QuadEdge lcand = base.oprev;

            // If both lcand and rcand are invalid, then base is the lower common tangent.
            boolean v_rcand = DT.rightOf(rcand.dest, base);
            boolean v_lcand = DT.rightOf(lcand.dest, base);
            if (!(v_rcand || v_lcand)) {
                break;
            }

            // Delete R edges out of base.dest that fail the circle test.
            if (v_rcand) {
                while (DT.rightOf(rcand.onext.dest, base) &&
                        DT.inCircle(base.dest, base.org, rcand.dest, rcand.onext.dest)) {
                    QuadEdge t = rcand.onext;
                    DT.deleteEdge(rcand);
                    rcand = t;
                }
            }
            // Symmetrically, delete L edges.
            if (v_lcand) {
                while (DT.rightOf(lcand.oprev.dest, base) &&
                        DT.inCircle(base.dest, base.org, lcand.dest, lcand.oprev.dest)) {
                    QuadEdge t = lcand.oprev;
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

    static class AntiLOPointComparator implements Comparator<UAEVertex> {

        @Override
        public int compare(UAEVertex x, UAEVertex y) {
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
