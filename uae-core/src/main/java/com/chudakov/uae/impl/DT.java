package com.chudakov.uae.impl;


import com.chudakov.simple.ch.Point;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DT {
    static Pair<QuadEdge, QuadEdge> baseCaseDT(List<UAEVertex> points) {
        if (points.size() == 0 || points.size() == 1) {
            return Pair.of(null, null);
        }

        if (points.size() == 2) {
            QuadEdge e = DT.makeEdge(points.get(0), points.get(1));
            setDualEdge(e);
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
            QuadEdge c = DT.connect(b, a);
            setDualEdges(a, b, c);
            return Pair.of(a, b.sym);
        } else if (DT.leftOf(p3, a)) {
            QuadEdge c = DT.connect(b, a);
            setDualEdges(a, b, c);
            return Pair.of(c.sym, c);
        } else { // the three points are collinear
            setDualEdge(a);
            setDualEdge(b);
            return Pair.of(a, b.sym);
        }
    }

    static Point middle(QuadEdge edge) {
        return Point.middle(edge.org, edge.dest);
    }

    static void setDualEdge(QuadEdge edge, Point dualOrg, Point dualDest) {
        edge.dualOrg = dualOrg;
        edge.dualDest = dualDest;
        edge.sym.dualOrg = dualOrg;
        edge.sym.dualDest = dualDest;
    }

    static void setDualEdge(QuadEdge a) {
        Point middle = middle(a);
        setDualEdge(a, middle, middle);
    }

    static void setDualEdgeOnDelete(QuadEdge delete, QuadEdge b, QuadEdge c) {
        Point center = circumCenter(delete, b);

        // set dual edge for b
        if (b.dualOrg.toleranceEquals(center)) {
            setDualEdge(b, middle(b), b.dualDest);
        } else if (b.dualDest.toleranceEquals(center)) {
            setDualEdge(b, b.dualOrg, middle(b));
        } else {
            throw new RuntimeException();
        }

        // set dual edge for c
        if (c.dualOrg.toleranceEquals(center)) {
            setDualEdge(c, middle(c), c.dualDest);
        } else if (c.dualDest.toleranceEquals(center)) {
            setDualEdge(c, c.dualOrg, middle(c));
        } else {
            throw new RuntimeException();
        }
    }

    static Triple<Point, Point, Point> trianglePoints(QuadEdge a, QuadEdge b) {
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

    public static boolean outOf(Point a, Point b, Point o, Point p) {
        boolean first = ccw(a, b, o);
        boolean second = ccw(a, b, p);
        return (!first && second) || (first && !second);
    }

    static Point third(QuadEdge a, QuadEdge b) {
        if (b.org.toleranceEquals(a.org) || b.org.toleranceEquals(a.dest)) {
            return b.dest;
        } else {
            return b.org;
        }
    }

    static void setDualEdges(QuadEdge a, QuadEdge b, QuadEdge c) {
        Point centre = circumCenter(a, b);

        Point aMiddle = middle(a);
        if (outOf(a.org, a.dest, third(a, b), centre)) {
            setDualEdge(a, centre, centre);
        } else {
            setDualEdge(a, centre, aMiddle);
        }

        Point bMiddle = middle(b);
        if (outOf(b.org, b.dest, third(b, a), centre)) {
            setDualEdge(b, centre, centre);
        } else {
            setDualEdge(b, centre, bMiddle);
        }

        Point cMiddle = middle(c);
        if (outOf(c.org, c.dest, third(c, a), centre)) {
            setDualEdge(c, centre, centre);
        } else {
            setDualEdge(c, centre, cMiddle);
        }
    }

    static Point mergeSetDualEdges(QuadEdge base, QuadEdge nextBase, QuadEdge c, Point prev) {
        Point center = circumCenter(base, nextBase);

        // set dual edges for base
        if (outOf(base.org, base.dest, third(base, nextBase), center)) {
            if (base.dualOrg == null && base.dualDest == null) {
                setDualEdge(base, center, center);
            } else {
                setDualEdge(base, center, prev);
            }
        } else {
            setDualEdge(base, center, prev);
        }

        // set dual edges for c
        if (outOf(c.org, c.dest, third(c, base), center)) {
            if (c.dualOrg.toleranceEquals(middle(c)) && c.dualDest.toleranceEquals(middle(c))) {
                setDualEdge(c, center, center);
            } else if (c.dualOrg.toleranceEquals(middle(c))) {
                setDualEdge(c, center, c.dualDest);
            } else if (c.dualDest.toleranceEquals(middle(c))) {
                setDualEdge(c, center, c.dualOrg);
            } else {
//                if (!c.dualOrg.toleranceEquals(c.dualDest)) {
//                    throw new RuntimeException();
//                }
                setDualEdge(c, center, c.dualDest);
            }
        } else {
            if (c.dualOrg.toleranceEquals(middle(c)) && c.dualDest.toleranceEquals(middle(c))) {
                setDualEdge(c, center, middle(c));
            } else if (c.dualOrg.toleranceEquals(middle(c))) {
                setDualEdge(c, center, c.dualDest);
            } else if (c.dualDest.toleranceEquals(middle(c))) {
                setDualEdge(c, center, c.dualOrg);
            } else {
//                if (!c.dualOrg.toleranceEquals(c.dualDest)) {
//                    throw new RuntimeException();
//                }
                setDualEdge(c, center, c.dualOrg);
            }
        }

        // set dual edges for next base
        if (outOf(nextBase.org, nextBase.dest, third(nextBase, base), center)) {
            setDualEdge(nextBase, center, center);
        } else {
            setDualEdge(nextBase, center, middle(nextBase));
        }

        return center;
    }

    static Point circumCenter(QuadEdge a, QuadEdge b) {
        Triple<Point, Point, Point> points = trianglePoints(a, b);
        return circumCenter(points.getLeft(), points.getMiddle(), points.getRight());
    }

    static Point circumCenter(Point p0, Point p1, Point p2) {
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

    static Pair<QuadEdge, QuadEdge> mergeDT(UAEResult left, UAEResult right) {
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
        Point p = middle(base);

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
                    setDualEdgeOnDelete(rcand, rcand.onext, rcand.sym.oprev);
                    QuadEdge t = rcand.onext;
                    DT.deleteEdge(rcand);
                    rcand = t;
                }
            }
            // Symmetrically, delete L edges.
            if (v_lcand) {
                while (DT.rightOf(lcand.oprev.dest, base) &&
                        DT.inCircle(base.dest, base.org, lcand.dest, lcand.oprev.dest)) {
                    setDualEdgeOnDelete(lcand, lcand.oprev, lcand.sym.onext);
                    QuadEdge t = lcand.oprev;
                    DT.deleteEdge(lcand);
                    lcand = t;
                }
            }
            // The next cross edge is to be connected to either lcand.dest or rcand.dest.
            // If both are valid, then choose the appropriate one using the in_circle test.
            QuadEdge nextBase;
            if (!v_rcand ||
                    (v_lcand && DT.inCircle(rcand.dest, rcand.org, lcand.org, lcand.dest))) {
                // Add cross edge base from rcand.dest to base.dest.
                nextBase = DT.connect(lcand, base.sym);
                p = mergeSetDualEdges(base, nextBase, lcand, p);
            } else {
                // Add cross edge base from base.org to lcand.dest
                nextBase = DT.connect(base.sym, rcand.sym);
                p = mergeSetDualEdges(base, nextBase, rcand, p);
            }
            base = nextBase;
        }
        return Pair.of(ldo, rdo);
    }

    public static boolean ccw(Point a, Point b, Point c) {
        double area2 = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
        return !(area2 <= 0);
    }

    static QuadEdge makeEdge(UAEVertex org, UAEVertex dest) {
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

    static void splice(QuadEdge edge1, QuadEdge edge2) {
        if (edge1.equals(edge2)) {
            System.out.println("Splicing edge with itself, ignored: " + edge1 + ".");
            return;
        }

        edge1.onext.oprev = edge2;
        edge2.onext.oprev = edge1;

        QuadEdge tmp = edge1.onext;
        edge1.onext = edge2.onext;
        edge2.onext = tmp;
    }

    static QuadEdge connect(QuadEdge edge1, QuadEdge edge2) {
        QuadEdge e = makeEdge(edge1.dest, edge2.org);
        splice(e, edge1.sym.oprev);
        splice(e.sym, edge2);
        return e;
    }

    static void deleteEdge(QuadEdge edge) {
        if (edge.org.edge.equals(edge)) {
            edge.org.edge = edge.oprev;
        }
        if (edge.dest.edge.equals(edge.sym)) {
            edge.dest.edge = edge.sym.oprev;
        }

        splice(edge, edge.oprev);
        splice(edge.sym, edge.sym.oprev);
    }


    static boolean inCircle(UAEVertex a, UAEVertex b, UAEVertex c, UAEVertex d) {
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

    static boolean rightOf(UAEVertex p, QuadEdge e) {
        UAEVertex a = e.org;
        UAEVertex b = e.dest;
        double det = (a.x - p.x) * (b.y - p.y) - (a.y - p.y) * (b.x - p.x);
        return det > 0;
    }

    static boolean leftOf(UAEVertex p, QuadEdge e) {
        UAEVertex a = e.org;
        UAEVertex b = e.dest;
        double det = (a.x - p.x) * (b.y - p.y) - (a.y - p.y) * (b.x - p.x);
        return det < 0;
    }


    static Pair<QuadEdge, QuadEdge> getTangentEdges(UAEVertex left, UAEVertex right) {
        // compute left tangent edge
        QuadEdge leftEdge = left.edge;
        while (!leftEdge.equals(leftEdge.onext) &&
                (rightOf(leftEdge.onext.dest, leftEdge) ||
                        rightOf(leftEdge.oprev.dest, leftEdge))) {
            if (rightOf(leftEdge.onext.dest, leftEdge)) {
                leftEdge = leftEdge.onext;
            } else {
                leftEdge = leftEdge.oprev;
            }
        }

        // compute right tangent edge
        QuadEdge rightEdge = right.edge;
        while (!rightEdge.equals(rightEdge.onext) &&
                (leftOf(rightEdge.onext.dest, rightEdge) ||
                        leftOf(rightEdge.oprev.dest, rightEdge))) {
            if (leftOf(rightEdge.onext.dest, rightEdge)) {
                rightEdge = rightEdge.onext;
            } else {
                rightEdge = rightEdge.oprev;
            }
        }

        return Pair.of(leftEdge, rightEdge);
    }

    private static double angle(QuadEdge e1, QuadEdge e2) {
        double a = Point.distance(e1.org, e1.dest);
        double b = Point.distance(e2.org, e2.dest);
        double c = Point.distance(e1.dest, e2.dest);

        double cosAlpha = (a * a + b * b + c * c) / 2 * a * b;
        return Math.acos(cosAlpha);
    }
}
