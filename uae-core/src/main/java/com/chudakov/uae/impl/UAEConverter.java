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
    public static UAEOutput convert(final UAEResult uaeResult) {
        Pair<List<UAEEdge>, List<UAEEdge>> dtAndVd = convertUAE(uaeResult.e1);
        return new UAEOutput(getConvexHull(uaeResult), dtAndVd.getLeft(), dtAndVd.getRight(), uaeResult.closestPair);
    }

    private static List<UAEVertex> getConvexHull(final UAEResult uaeResult) {
        List<UAEVertex> ch = new ArrayList<>();
        for (CQVertex<UAEVertex> cqVertex : uaeResult.convexHull) {
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
        vd.add(new UAEEdge(toUAEVertex(quadEdge.dualOrg), toUAEVertex(quadEdge.dualDest)));
        dfsUAE(quadEdge.sym, visited, dt, vd);

        QuadEdge it = quadEdge.onext;
        while (!it.equals(quadEdge)) {
            dt.add(new UAEEdge(it.org, it.dest));
            vd.add(new UAEEdge(toUAEVertex(it.dualOrg), toUAEVertex(it.dualDest)));
            dfsUAE(it.sym, visited, dt, vd);
            it = it.onext;
        }
    }

    private static UAEVertex toUAEVertex(Point p) {
        return new UAEVertex(p.x, p.y);
    }

//    private static void dfsDT(QuadEdge quadEdge, Set<UAEVertex> visited, List<UAEEdge> result) {
//        if (visited.contains(quadEdge.org)) {
//            return;
//        }
//        visited.add(quadEdge.org);
//        result.add(new UAEEdge(quadEdge.org, quadEdge.dest));
//        dfsDT(quadEdge.sym, visited, result);
//
//        QuadEdge it = quadEdge.onext;
//        while (!it.equals(quadEdge)) {
//            result.add(new UAEEdge(it.org, it.dest));
//            dfsDT(it.sym, visited, result);
//            it = it.onext;
//        }
//    }


//    public static List<UAEEdge> convertVD(QuadEdge quadEdge) {
//        if (quadEdge == null) {
//            return Collections.emptyList();
//        }
//
//        List<UAEEdge> result = new ArrayList<>();
//        Set<UAEVertex> visited = new HashSet<>();
//
//        dfsVD(quadEdge, visited, result);
//
//        return result;
//    }

//    private static void dfsVD(QuadEdge quadEdge, Set<UAEVertex> visited, List<UAEEdge> result) {
//        if (visited.contains(quadEdge.org)) {
//            return;
//        }
//        visited.add(quadEdge.org);
//        result.add(new UAEEdge(toUAEVertex(quadEdge.dualOrg), toUAEVertex(quadEdge.dualDest)));
//        dfsVD(quadEdge.sym, visited, result);
//
//        QuadEdge it = quadEdge.onext;
//        while (!it.equals(quadEdge)) {
//            result.add(new UAEEdge(toUAEVertex(it.dualOrg), toUAEVertex(it.dualDest)));
//            dfsVD(it.sym, visited, result);
//            it = it.onext;
//        }
//    }
}
