package com.chudakov.uae.impl;

import com.chudakov.simple.ch.Point;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.chudakov.uae.impl.ConcatenableQueue.CQVertex;


public class UAEConverter {
    public static UAESolutions convert(final UAEState uaeState) {
        Pair<List<UAEEdge>, List<UAEEdge>> dtAndVd = convertUAE(uaeState.e1);
        return new UAESolutions(getConvexHull(uaeState), dtAndVd.getLeft(), dtAndVd.getRight(), uaeState.closestPair);
    }

    private static List<UAEVertex> getConvexHull(final UAEState uaeState) {
        List<UAEVertex> ch = new ArrayList<>();
        for (CQVertex<UAEVertex> cqVertex : uaeState.convexHull) {
            ch.add(cqVertex.value);
        }
        return ch;
    }

    private static Pair<List<UAEEdge>, List<UAEEdge>> convertUAE(final QuadEdge quadEdge) {
        if (quadEdge == null) {
            return Pair.of(Collections.emptyList(), Collections.emptyList());
        }

        List<UAEEdge> dt = new ArrayList<>();
        List<UAEEdge> vd = new ArrayList<>();
        Set<UAEVertex> visited = new HashSet<>();

        dfsUAE(quadEdge, visited, dt, vd);

        return Pair.of(dt, vd);
    }

    private static void dfsUAE(QuadEdge quadEdge, Set<UAEVertex> visited, List<UAEEdge> dt, List<UAEEdge> vd) {
        if (visited.contains(quadEdge.org)) {
            return;
        }
        visited.add(quadEdge.org);
        dt.add(new UAEEdge(quadEdge.org, quadEdge.dest));
        vd.add(getVDEdge(quadEdge, quadEdge.dualOrg, quadEdge.dualDest));
        dfsUAE(quadEdge.sym, visited, dt, vd);

        QuadEdge it = quadEdge.onext;
        while (!it.equals(quadEdge)) {
            dt.add(new UAEEdge(it.org, it.dest));
            vd.add(getVDEdge(it, it.dualOrg, it.dualDest));
            dfsUAE(it.sym, visited, dt, vd);
            it = it.onext;
        }
    }

    private static UAEEdge getVDEdge(QuadEdge dtEdge, Point vdOrg, Point vdDest) {
        Point resultOrg = vdOrg;
        Point resultDest = vdDest;
        Point dtMiddle = DT.middle(dtEdge);
        Point direction = new Point(dtEdge.org.y - dtEdge.dest.y, dtEdge.dest.x - dtEdge.org.x);
//      ccw(dtEdge.org, direction, dtEdge.dest) = false
        if (vdOrg.equals(vdDest)) {
            if (DT.ccw(dtEdge.org, vdOrg, dtEdge.dest)) {
                resultDest = resultDest.subtract(direction.mult(100));
            } else {
                resultDest = resultDest.add(direction.mult(100));
            }
        } else {
            if (vdOrg.equals(dtMiddle) || vdDest.equals(dtMiddle)){
                if (vdOrg.equals(dtMiddle)) {
                    Point tmp = vdOrg;
                    vdOrg = vdDest;
                    vdDest = tmp;
                }

                if (DT.ccw(dtEdge.org, vdOrg, dtEdge.dest)) {
                    resultDest = resultDest.add(direction.mult(100));
                } else {
                    resultDest = resultDest.subtract(direction.mult(100));
                }
            }
        }
        return new UAEEdge(toUAEVertex(resultOrg), toUAEVertex(resultDest));
    }

    private static UAEVertex toUAEVertex(Point p) {
        return new UAEVertex(p.x, p.y);
    }
}
