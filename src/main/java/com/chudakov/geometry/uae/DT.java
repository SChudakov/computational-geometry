package com.chudakov.geometry.uae;

import com.chudakov.geometry.common.Point2D;

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
        Set<Point2D> visited = new HashSet<>();

        dfs(quadEdge, visited, result);

        return result;
    }

    private static void dfs(QuadEdge quadEdge, Set<Point2D> visited, List<Edge> result) {
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


    static QuadEdge makeEdge(Point2D org, Point2D dest) {
        QuadEdge e = new QuadEdge(org, dest);
        QuadEdge es = new QuadEdge(dest, org);

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
        splice(e, e.oprev);
        splice(e.sym, e.sym.oprev);

        e.data = true;
        e.sym.data = true;
    }


    static boolean inCircle(Point2D a, Point2D b, Point2D c, Point2D d) {
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

    static boolean rightOf(Point2D p, QuadEdge e) {
        Point2D a = e.org;
        Point2D b = e.dest;
        double det = (a.x - p.x) * (b.y - p.y) - (a.y - p.y) * (b.x - p.x);
        return det > 0;
    }

    static boolean leftOf(Point2D p, QuadEdge e) {
        Point2D a = e.org;
        Point2D b = e.dest;
        double det = (a.x - p.x) * (b.y - p.y) - (a.y - p.y) * (b.x - p.x);
        return det < 0;
    }
}
