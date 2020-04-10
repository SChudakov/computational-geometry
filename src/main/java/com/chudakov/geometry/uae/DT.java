package com.chudakov.geometry.uae;

import com.chudakov.geometry.common.Point2D;

import java.util.List;

public class DT {

    static DTEdge makeEdge(Point2D org, Point2D dest) {
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

    static void splice(DTEdge a, DTEdge b) {
        if (a.equals(b)) {
            System.out.println("Splicing edge with itself, ignored: " + a + ".");
            return;
        }

        a.onext.oprev = b;
        b.onext.oprev = a;

        a.onext = b.onext;
        b.onext = a.onext;
    }

    static DTEdge connect(DTEdge a, DTEdge b) {
        DTEdge e = makeEdge(a.dest, b.org);
        splice(e, a.sym.oprev);
        splice(e.sym, b);
        return e;
    }

    static void deleteEdge(DTEdge e) {
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

    static boolean leftOf(Point2D p, DTEdge e) {
        throw new UnsupportedOperationException("not implemented");
    }

    static boolean rightOf(Point2D p, DTEdge e) {
        throw new UnsupportedOperationException("not implemented");
    }
}