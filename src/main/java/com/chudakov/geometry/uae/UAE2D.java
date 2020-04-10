package com.chudakov.geometry.uae;

import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.core.DaCAlgorithm;
import com.chudakov.geometry.datastructure.ConcatenableQueue;
import com.chudakov.geometry.datastructure.ConvexHull;
import com.chudakov.geometry.datastructure.ConvexSubhull;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        Triple<DTEdge, DTEdge, List<DTEdge>> t = delaunayTriangulationBaseCase(points);
        return new UAEResult(convexHull, t.getLeft(), t.getMiddle(), t.getRight());
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

    private Triple<DTEdge, DTEdge, List<DTEdge>> delaunayTriangulationBaseCase(List<Point2D> points) {
        if (points.size() == 2) {
            DTEdge e = makeEdge(points.get(0), points.get(1));
            return Triple.of(e, e.sym, Collections.singletonList(e));
        }

        // points size = 3
        Point2D p1 = points.get(0);
        Point2D p2 = points.get(1);
        Point2D p3 = points.get(2);

        DTEdge a = makeEdge(p1, p2);
        DTEdge b = makeEdge(p2, p3);
        splice(a.sym, b);

        // Close the triangle.
        if (leftOf(p3, a)) {
            connect(b, a);
            return Triple.of(a, b.sym, Arrays.asList(a, b));
        } else if (leftOf(p3, a)) {
            DTEdge c = connect(b, a);
            return Triple.of(c.sym, c, Arrays.asList(a, b, c));
        } else { // the three points are collinear
            return Triple.of(a, b.sym, Arrays.asList(a, b));
        }
    }

    @Override
    public UAEResult merge(UAEResult left, UAEResult right) {
        Pair<DTEdge, DTEdge> p = getTriangulationEdges(left, right);

        ConvexHull convexHull = ConvexHull.join(left.convexHull, right.convexHull);

        List<DTEdge> edges = new ArrayList<>(left.edges.size() + right.edges.size());
        edges.addAll(left.edges);
        edges.addAll(right.edges);

        return new UAEResult(convexHull, p.getLeft(), p.getRight(), edges);
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


    private List<Point2D> deleteCocircularPoints(List<Point2D> points) {
        throw new UnsupportedOperationException("not implemented");
    }

    void removeDuplicated(List<Point2D> points, Comparator<Point2D> comparator) {
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


    private DTEdge makeEdge(Point2D org, Point2D dest) {
        DTEdge e = new DTEdge(org, dest);
        DTEdge es = new DTEdge(dest, org);

        // make edges mutually symmetrical
        e.sym = es;
        es.sym = e;

        e.onext = e;
        e.oprev = e;

        es.onext = es;
        es.oprev = es;

        return e;
    }

    private void splice(DTEdge a, DTEdge b) {
        if (a.equals(b)) {
            System.out.println("Splicing edge with itself, ignored: " + a + ".");
            return;
        }

        a.onext.oprev = b;
        b.onext.oprev = a;

        a.onext = b.onext;
        b.onext = a.onext;
    }

    private DTEdge connect(DTEdge a, DTEdge b) {
        DTEdge e = makeEdge(a.dest, b.org);
        splice(e, a.sym.oprev);
        splice(e.sym, b);
        return e;
    }

    private void deleteEdge(DTEdge e) {
        splice(e, e.oprev);
        splice(e.sym, e.sym.oprev);

        e.data = true;
        e.sym.data = true;
    }


    private boolean inCircle(Point2D a, Point2D b, Point2D c, Point2D d) {
        double a1 = a.x - d.x;
        double a2 = a.y - d.y;

        double b1 = b.x - d.x;
        double b2 = b.y - d.y;

        double c1 = c.x - d.x;
        double c2 = c.y - d.y;

        double a3 = a1 * a1 + a2 * a2;
        double b3 = b1 * b1 + b2 * b2;
        double c3 = c1 * c1 + c2 * c2;

        double det = a1 * b2 * c3
                + a2 * b3 * c1
                + a3 * b1 * c2
                - (a3 * b2 * c1 + a1 * b3 * c2 + a2 * b1 * c3);

        return det < 0;
    }

    private boolean leftOf(Point2D p, DTEdge e) {
        throw new UnsupportedOperationException("not implemented");
    }

    private boolean rightOf(Point2D p, DTEdge e) {
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
