package com.chudakov.uae.impl;


import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

public class DTandVD {
    static Pair<QuadEdge, QuadEdge> baseCase(List<UAEVertex> points) {
        if (points.size() == 0 || points.size() == 1) {
            return Pair.of(null, null);
        }

        if (points.size() == 2) {
            QuadEdge e = DTandVD.makeEdge(points.get(0), points.get(1));
            setDualEdge(e);
            return Pair.of(e, e.sym);
        }

        // points size = 3
        UAEVertex p1 = points.get(0);
        UAEVertex p2 = points.get(1);
        UAEVertex p3 = points.get(2);

        QuadEdge a = DTandVD.makeEdge(p1, p2);
        QuadEdge b = DTandVD.makeEdge(p2, p3);
        DTandVD.splice(a.sym, b);

        // Close the triangle.
        if (DTandVD.rightOf(p3, a)) {
            QuadEdge c = DTandVD.connect(b, a);
            setDualEdges(a, b, c);
            return Pair.of(a, b.sym);
        } else if (DTandVD.leftOf(p3, a)) {
            QuadEdge c = DTandVD.connect(b, a);
            setDualEdges(a, b, c);
            return Pair.of(c.sym, c);
        } else { // the three points are collinear
            setDualEdge(a);
            setDualEdge(b);
            return Pair.of(a, b.sym);
        }
    }

    static Pair<QuadEdge, QuadEdge> merge(UAEState left, UAEState right) {
        QuadEdge ldo = left.e1;
        QuadEdge ldi = left.e2;

        QuadEdge rdi = right.e1;
        QuadEdge rdo = right.e2;

        // Compute the upper common tangent of L and R.
        while (true) {
            if (DTandVD.rightOf(rdi.org, ldi)) {
                ldi = ldi.sym.onext;
            } else if (DTandVD.leftOf(ldi.org, rdi)) {
                rdi = rdi.sym.oprev;
            } else {
                break;
            }
        }

        // Create a first cross edge base from rdi.org to ldi.org.
        QuadEdge base = DTandVD.connect(ldi.sym, rdi);
        Point p = base.middle();

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
            boolean v_rcand = DTandVD.rightOf(rcand.dest, base);
            boolean v_lcand = DTandVD.rightOf(lcand.dest, base);
            if (!(v_rcand || v_lcand)) {
                break;
            }

            // Delete R edges out of base.dest that fail the circle test.
            if (v_rcand) {
                while (DTandVD.rightOf(rcand.onext.dest, base) &&
                        DTandVD.inCircle(base.dest, base.org, rcand.dest, rcand.onext.dest)) {
                    setDualEdgeOnDelete(rcand, rcand.onext, rcand.sym.oprev);
                    QuadEdge t = rcand.onext;
                    DTandVD.deleteEdge(rcand);
                    rcand = t;
                }
            }
            // Symmetrically, delete L edges.
            if (v_lcand) {
                while (DTandVD.rightOf(lcand.oprev.dest, base) &&
                        DTandVD.inCircle(base.dest, base.org, lcand.dest, lcand.oprev.dest)) {
                    setDualEdgeOnDelete(lcand, lcand.oprev, lcand.sym.onext);
                    QuadEdge t = lcand.oprev;
                    DTandVD.deleteEdge(lcand);
                    lcand = t;
                }
            }
            // The next cross edge is to be connected to either lcand.dest or rcand.dest.
            // If both are valid, then choose the appropriate one using the in_circle test.
            QuadEdge nextBase;
            if (!v_rcand ||
                    (v_lcand && DTandVD.inCircle(rcand.dest, rcand.org, lcand.org, lcand.dest))) {
                // Add cross edge base from rcand.dest to base.dest.
                nextBase = DTandVD.connect(lcand, base.sym);
                p = mergeSetDualEdges(base, nextBase, lcand, p);
            } else {
                // Add cross edge base from base.org to lcand.dest
                nextBase = DTandVD.connect(base.sym, rcand.sym);
                p = mergeSetDualEdges(base, nextBase, rcand, p);
            }
            base = nextBase;
        }
        return Pair.of(ldo, rdo);
    }


    private static void setDualEdge(QuadEdge edge, Point dualOrg, Point dualDest) {
        edge.dualOrg = dualOrg;
        edge.dualDest = dualDest;
        edge.sym.dualOrg = dualOrg;
        edge.sym.dualDest = dualDest;
    }

    private static void setDualEdge(QuadEdge a) {
        Point middle = a.middle();
        setDualEdge(a, middle, middle);
    }

    private static void setDualEdgeOnDelete(QuadEdge delete, QuadEdge b, QuadEdge c) {
        Point center = circumcenter(delete, b);

        // set dual edge for b
        if (b.dualOrg.toleranceEquals(center)) {
            setDualEdge(b, b.middle(), b.dualDest);
        } else if (b.dualDest.toleranceEquals(center)) {
            setDualEdge(b, b.dualOrg, b.middle());
        } else {
            throw new RuntimeException();
        }

        // set dual edge for c
        if (c.dualOrg.toleranceEquals(center)) {
            setDualEdge(c, c.middle(), c.dualDest);
        } else if (c.dualDest.toleranceEquals(center)) {
            setDualEdge(c, c.dualOrg, c.middle());
        } else {
            throw new RuntimeException();
        }
    }

    private static Triple<Point, Point, Point> trianglePoints(QuadEdge a, QuadEdge b) {
        Point ap = a.org;
        Point bp = a.dest;
        Point cp;
        if (b.org.toleranceEquals(ap) || b.org.toleranceEquals(bp)) {
            cp = b.dest;
        } else {
            cp = b.org;
        }
        return Triple.of(ap, bp, cp);
    }

    private static boolean outOf(Point a, Point b, Point o, Point p) {
        boolean first = Point.ccw(a, b, o);
        boolean second = Point.ccw(a, b, p);
        return (!first && second) || (first && !second);
    }

    private static Point thirdPoint(QuadEdge a, QuadEdge b) {
        if (b.org.toleranceEquals(a.org) || b.org.toleranceEquals(a.dest)) {
            return b.dest;
        } else {
            return b.org;
        }
    }

    private static void setDualEdges(QuadEdge a, QuadEdge b, QuadEdge c) {
        Point centre = circumcenter(a, b);

        Point aMiddle = a.middle();
        if (outOf(a.org, a.dest, thirdPoint(a, b), centre)) {
            setDualEdge(a, centre, centre);
        } else {
            setDualEdge(a, centre, aMiddle);
        }

        Point bMiddle = b.middle();
        if (outOf(b.org, b.dest, thirdPoint(b, a), centre)) {
            setDualEdge(b, centre, centre);
        } else {
            setDualEdge(b, centre, bMiddle);
        }

        Point cMiddle = c.middle();
        if (outOf(c.org, c.dest, thirdPoint(c, a), centre)) {
            setDualEdge(c, centre, centre);
        } else {
            setDualEdge(c, centre, cMiddle);
        }
    }

    private static Point mergeSetDualEdges(QuadEdge base, QuadEdge nextBase, QuadEdge c, Point prev) {
        Point center = circumcenter(base, nextBase);

        // set dual edges for base
        if (outOf(base.org, base.dest, thirdPoint(base, nextBase), center)) {
            if (base.dualOrg == null && base.dualDest == null) {
                setDualEdge(base, center, center);
            } else {
                setDualEdge(base, center, prev);
            }
        } else {
            setDualEdge(base, center, prev);
        }

        // set dual edges for c
        if (outOf(c.org, c.dest, thirdPoint(c, base), center)) {
            if (c.dualOrg.toleranceEquals(c.middle()) && c.dualDest.toleranceEquals(c.middle())) {
                setDualEdge(c, center, center);
            } else if (c.dualOrg.toleranceEquals(c.middle())) {
                setDualEdge(c, center, c.dualDest);
            } else if (c.dualDest.toleranceEquals(c.middle())) {
                setDualEdge(c, center, c.dualOrg);
            } else {
                setDualEdge(c, center, c.dualDest);
            }
        } else {
            if (c.dualOrg.toleranceEquals(c.middle()) && c.dualDest.toleranceEquals(c.middle())) {
                setDualEdge(c, center, c.middle());
            } else if (c.dualOrg.toleranceEquals(c.middle())) {
                setDualEdge(c, center, c.dualDest);
            } else if (c.dualDest.toleranceEquals(c.middle())) {
                setDualEdge(c, center, c.dualOrg);
            } else {
                setDualEdge(c, center, c.dualOrg);
            }
        }

        // set dual edges for next base
        if (outOf(nextBase.org, nextBase.dest, thirdPoint(nextBase, base), center)) {
            setDualEdge(nextBase, center, center);
        } else {
            setDualEdge(nextBase, center, nextBase.middle());
        }

        return center;
    }

    private static Point circumcenter(QuadEdge a, QuadEdge b) {
        Triple<Point, Point, Point> points = trianglePoints(a, b);
        return circumcenter(points.getLeft(), points.getMiddle(), points.getRight());
    }

    private static Point circumcenter(Point p0, Point p1, Point p2) {
        double xl = p0.x;
        double yl = p0.y;
        double xk = p1.x;
        double yk = p1.y;
        double xm = p2.x;
        double ym = p2.y;

        double xlk = xl - xk;
        double ylk = yl - yk;
        double xmk = xm - xk;
        double ymk = ym - yk;
        double det = xlk * ymk - xmk * ylk;

        double detinv = 0.5 / det;
        double rlksq = xlk * xlk + ylk * ylk;
        double rmksq = xmk * xmk + ymk * ymk;
        double xcc = detinv * (rlksq * ymk - rmksq * ylk);
        double ycc = detinv * (xlk * rmksq - xmk * rlksq);
        return new Point(xcc + xk, ycc + yk);
    }


    private static QuadEdge makeEdge(UAEVertex org, UAEVertex dest) {
        QuadEdge edge = new QuadEdge(org, dest);
        QuadEdge symEdge = new QuadEdge(dest, org);
        if (org.edge == null) {
            org.edge = edge;
        }
        if (dest.edge == null) {
            dest.edge = symEdge;
        }

        // make edges mutually symmetrical
        edge.sym = symEdge;
        symEdge.sym = edge;

        edge.onext = edge;
        edge.oprev = edge;

        symEdge.onext = symEdge;
        symEdge.oprev = symEdge;

        return edge;
    }

    private static void splice(QuadEdge edge1, QuadEdge edge2) {
        if (edge1.equals(edge2)) {
            System.out.printf("Splicing edge with itself, ignored: %s.%n", edge1);
            return;
        }

        edge1.onext.oprev = edge2;
        edge2.onext.oprev = edge1;

        QuadEdge tmp = edge1.onext;
        edge1.onext = edge2.onext;
        edge2.onext = tmp;
    }

    private static QuadEdge connect(QuadEdge edge1, QuadEdge edge2) {
        QuadEdge e = makeEdge(edge1.dest, edge2.org);
        splice(e, edge1.sym.oprev);
        splice(e.sym, edge2);
        return e;
    }

    private static void deleteEdge(QuadEdge edge) {
        if (edge.org.edge.equals(edge)) {
            edge.org.edge = edge.oprev;
        }
        if (edge.dest.edge.equals(edge.sym)) {
            edge.dest.edge = edge.sym.oprev;
        }

        splice(edge, edge.oprev);
        splice(edge.sym, edge.sym.oprev);
    }


    private static boolean inCircle(UAEVertex a, UAEVertex b, UAEVertex c, UAEVertex d) {
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

    private static boolean rightOf(UAEVertex p, QuadEdge e) {
        UAEVertex a = e.org;
        UAEVertex b = e.dest;
        double det = (a.x - p.x) * (b.y - p.y) - (a.y - p.y) * (b.x - p.x);
        return det > 0;
    }

    private static boolean leftOf(UAEVertex p, QuadEdge e) {
        UAEVertex a = e.org;
        UAEVertex b = e.dest;
        double det = (a.x - p.x) * (b.y - p.y) - (a.y - p.y) * (b.x - p.x);
        return det < 0;
    }
}
