package com.chudakov.geometry.uae;


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
        Set<Vertex> visited = new HashSet<>();

        dfs(quadEdge, visited, result);

        return result;
    }

    private static void dfs(QuadEdge quadEdge, Set<Vertex> visited, List<Edge> result) {
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


    static QuadEdge makeEdge(Vertex org, Vertex dest) {
        QuadEdge e = new QuadEdge(org, dest);
        QuadEdge es = new QuadEdge(dest, org);
        if (org.edge == null) {
            org.edge = e;
        }
        if (dest.edge == null) {
            dest.edge = es;
        }

//        System.out.println("create: " + e);

        // make edges mutually symmetrical
        e.sym = es;
        es.sym = e;

        e.onext = e;
        e.oprev = e;

        es.onext = es;
        es.oprev = es;

        return e;
    }

    static void splice(QuadEdge a, QuadEdge b) {
        if (a.equals(b)) {
            System.out.println("Splicing edge with itself, ignored: " + a + ".");
            return;
        }

        a.onext.oprev = b;
        b.onext.oprev = a;

        QuadEdge tmp = a.onext;
        a.onext = b.onext;
        b.onext = tmp;
    }

    static QuadEdge connect(QuadEdge a, QuadEdge b) {
        QuadEdge e = makeEdge(a.dest, b.org);
        splice(e, a.sym.oprev);
        splice(e.sym, b);
        return e;
    }

    static void deleteEdge(QuadEdge e) {
//        System.out.println("delete: " + e);

        if (e.org.edge.equals(e)) {
            e.org.edge = e.oprev;
        }
        if (e.dest.edge.equals(e.sym)) {
            e.dest.edge = e.sym.oprev;
        }

        splice(e, e.oprev);
        splice(e.sym, e.sym.oprev);
    }


    static boolean inCircle(Vertex a, Vertex b, Vertex c, Vertex d) {
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

    static boolean rightOf(Vertex p, QuadEdge e) {
        Vertex a = e.org;
        Vertex b = e.dest;
        double det = (a.x - p.x) * (b.y - p.y) - (a.y - p.y) * (b.x - p.x);
        return det > 0;
    }

    static boolean leftOf(Vertex p, QuadEdge e) {
        Vertex a = e.org;
        Vertex b = e.dest;
        double det = (a.x - p.x) * (b.y - p.y) - (a.y - p.y) * (b.x - p.x);
        return det < 0;
    }


    static Pair<QuadEdge, QuadEdge> getTangentEdges(Vertex left, Vertex right) {
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

    private static double distance(Vertex a, Vertex b) {
        return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }
}
