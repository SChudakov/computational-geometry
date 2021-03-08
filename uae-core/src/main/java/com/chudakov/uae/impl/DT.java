package com.chudakov.uae.impl;


import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DT {

    public static List<Edge> convert(QuadEdge quadEdge) {
        if (quadEdge == null) {
            return Collections.emptyList();
        }

        List<Edge> result = new ArrayList<>();
        Set<UAEVertex> visited = new HashSet<>();

        dfs(quadEdge, visited, result);

        return result;
    }

    private static void dfs(QuadEdge quadEdge, Set<UAEVertex> visited, List<Edge> result) {
        if (visited.contains(quadEdge.org)) {
            return;
        }
        visited.add(quadEdge.org);
        result.add(new Edge(quadEdge.org, quadEdge.dest));
        dfs(quadEdge.sym, visited, result);

        QuadEdge it = quadEdge.onext;
        while (!it.equals(quadEdge)) {
            result.add(new Edge(it.org, it.dest));
            dfs(it.sym, visited, result);
            it = it.onext;
        }
    }


    static Pair<QuadEdge, QuadEdge> baseCaseDT(List<UAEVertex> points) {
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
        double a = distance(e1.org, e1.dest);
        double b = distance(e2.org, e2.dest);
        double c = distance(e1.dest, e2.dest);

        double cosAlpha = (a * a + b * b + c * c) / 2 * a * b;
        return Math.acos(cosAlpha);
    }

    private static double distance(UAEVertex a, UAEVertex b) {
        return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }
}
