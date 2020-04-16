package com.chudakov.geometry.uae;

import com.chudakov.geometry.core.DaCAlgorithm;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;

import static com.chudakov.geometry.uae.CH.CutData;
import static com.chudakov.geometry.uae.ConcatenableQueue.CQNode;

public class UAE2D implements DaCAlgorithm<List<Vertex2D>, UAEResult> {
    @Override
    public boolean isBaseCase(List<Vertex2D> points) {
        return points.size() <= 3;
    }

    @Override
    public int inputSize(List<Vertex2D> input) {
        return input.size();
    }

    @Override
    public UAEResult solveBaseCase(List<Vertex2D> points) {
        ConvexHull convexHull = convexHullBaseCase(points);
        Pair<QuadEdge, QuadEdge> p = delaunayTriangulationBaseCase(points);
        return new UAEResult(convexHull, p.getLeft(), p.getRight());
    }

    private ConvexHull convexHullBaseCase(List<Vertex2D> points) {
        System.out.println("base case: " + points);
        int size = points.size();

        ConcatenableQueue<Vertex2D> upper = new ConcatenableQueue<>();
        ConcatenableQueue<Vertex2D> lower = new ConcatenableQueue<>();
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
            Vertex2D first = points.get(0);
            Vertex2D second = points.get(1);
            Vertex2D third = points.get(2);
            double leftSlope = Vertex2D.getSlope(first, second);
            double rightSlope = Vertex2D.getSlope(second, third);
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

    private Pair<QuadEdge, QuadEdge> delaunayTriangulationBaseCase(List<Vertex2D> points) {
        if (points.size() == 0 || points.size() == 1) {
            return Pair.of(null, null);
        }

        if (points.size() == 2) {
            QuadEdge e = DT.makeEdge(points.get(0), points.get(1));
            return Pair.of(e, e.sym);
        }

        // points size = 3
        Vertex2D p1 = points.get(0);
        Vertex2D p2 = points.get(1);
        Vertex2D p3 = points.get(2);

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
    public UAEResult merge(UAEResult left, UAEResult right) {
        ConcatenableQueue<Vertex2D> leftUpper = left.convexHull.upperSubhull.subhull;
        ConcatenableQueue<Vertex2D> leftLower = left.convexHull.lowerSubhull.subhull;
        ConcatenableQueue<Vertex2D> rightUpper = right.convexHull.upperSubhull.subhull;
        ConcatenableQueue<Vertex2D> rightLower = right.convexHull.lowerSubhull.subhull;

        // 1. move utmost points up and compute upper tangent
        Pair<ConcatenableQueue<Vertex2D>, ConcatenableQueue<Vertex2D>> p1 = CH.moveUtmostPointsUp(leftUpper, leftLower);
        leftUpper = p1.getLeft();
        leftLower = p1.getRight();
        Pair<ConcatenableQueue<Vertex2D>, ConcatenableQueue<Vertex2D>> p2 = CH.moveUtmostPointsUp(rightUpper, rightLower);
        rightUpper = p2.getLeft();
        rightLower = p2.getRight();
        Pair<CQNode<Vertex2D>, CQNode<Vertex2D>> upperTangent = CH.tangent(leftUpper, rightUpper, CH::getUpperTangentCase);

        // 2. move utmost points down and compute lower tangent
        Pair<ConcatenableQueue<Vertex2D>, ConcatenableQueue<Vertex2D>> p3 = CH.moveUtmostPointsDown(leftUpper, leftLower);
        leftUpper = p3.getLeft();
        leftLower = p3.getRight();
        Pair<ConcatenableQueue<Vertex2D>, ConcatenableQueue<Vertex2D>> p4 = CH.moveUtmostPointsDown(rightUpper, rightLower);
        rightUpper = p4.getLeft();
        rightLower = p4.getRight();
        Pair<CQNode<Vertex2D>, CQNode<Vertex2D>> lowerTangent = CH.tangent(leftLower, rightLower, CH::getLowerTangentCase);

        // 3. use tangents in triangulation
        Pair<QuadEdge, QuadEdge> triangulationEdges = getTriangulationEdges(left, right, upperTangent, lowerTangent);

        // 4. cut convex hull w.r.t the tangents
        CutData data = CH.cutSubhulls(leftUpper, leftLower, rightUpper, rightLower, upperTangent, lowerTangent);
        leftUpper = data.leftUpper;
        leftLower = data.leftLower;
        rightUpper = data.rightUpper;
        rightLower = data.rightLower;

        // 5. concatenate upper and lower queue
        ConcatenableQueue<Vertex2D> upperResult = ConcatenableQueue.concatenate(leftUpper, rightUpper);
        ConcatenableQueue<Vertex2D> lowerResult = ConcatenableQueue.concatenate(leftLower, rightLower);

        // 6. create sub-hulls and convex hull
        ConvexSubhull upperSubhull = new ConvexSubhull(upperResult, ConvexSubhull.Type.UPPER);
        ConvexSubhull lowerSubhull = new ConvexSubhull(lowerResult, ConvexSubhull.Type.LOWER);
        ConvexHull convexHull = new ConvexHull(upperSubhull, lowerSubhull);

        return new UAEResult(convexHull, triangulationEdges.getLeft(), triangulationEdges.getRight());
    }


    private Pair<QuadEdge, QuadEdge> getTriangulationEdges(UAEResult left, UAEResult right,
                                                           Pair<CQNode<Vertex2D>, CQNode<Vertex2D>> upperTangent,
                                                           Pair<CQNode<Vertex2D>, CQNode<Vertex2D>> lowerTangent) {
        System.out.println("merge");
        System.out.println("left e1: " + left.e1);
        System.out.println("left e2: " + left.e2);
        System.out.println("right e1: " + right.e1);
        System.out.println("right e2: " + right.e2);
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

        System.out.println("ldo: " + ldo);
        System.out.println("ldi: " + ldi);
        System.out.println("rdi: " + rdi);
        System.out.println("rdo: " + rdo);

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

    @Override
    public Pair<List<Vertex2D>, List<Vertex2D>> divide(List<Vertex2D> input) {
        int mid = input.size() / 2;
        return Pair.of(input.subList(0, mid), input.subList(mid, input.size()));
    }

    @Override
    public List<Vertex2D> precompute(List<Vertex2D> points) {
        points = new ArrayList<>(new HashSet<>(points));
        // TODO: do not remove vertically and horizontally collinear points
        points.sort(new AntiLOPointComparator());
        removeDuplicated(points, Comparator.comparingDouble(p -> p.y));
        points.sort(Vertex2D::compareTo);
        removeDuplicated(points, Comparator.comparingDouble(p -> p.x));
        return points;
    }


    static void removeDuplicated(List<Vertex2D> points, Comparator<Vertex2D> comparator) {
        ListIterator<Vertex2D> it1 = points.listIterator();
        ListIterator<Vertex2D> it2 = points.listIterator();

        // handle empty input case
        if (!it1.hasNext()) {
            return;
        }
        it1.next();

        while (it2.hasNext()) {
            if (it2.nextIndex() > 0 && it2.nextIndex() < points.size() - 1) {
                Vertex2D previous = it2.previous();
                it2.next();
                Vertex2D current = it2.next();
                Vertex2D next = it2.next();
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

    static class AntiLOPointComparator implements Comparator<Vertex2D> {

        @Override
        public int compare(Vertex2D x, Vertex2D y) {
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
