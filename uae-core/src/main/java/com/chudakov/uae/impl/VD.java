package com.chudakov.uae.impl;


import com.chudakov.simple.ch.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VD {
    public static List<UAEEdge> convert(QuadEdge quadEdge) {
        if (quadEdge == null) {
            return Collections.emptyList();
        }

        List<UAEEdge> result = new ArrayList<>();
        Set<UAEVertex> visited = new HashSet<>();

        dfs(quadEdge, visited, result);

        return result;
    }

    private static void dfs(QuadEdge quadEdge, Set<UAEVertex> visited, List<UAEEdge> result) {
        if (visited.contains(quadEdge.org)) {
            return;
        }
        visited.add(quadEdge.org);
        result.add(new UAEEdge(toUAEVertex(quadEdge.dualOrg), toUAEVertex(quadEdge.dualDest)));
        dfs(quadEdge.sym, visited, result);

        QuadEdge it = quadEdge.onext;
        while (!it.equals(quadEdge)) {
            result.add(new UAEEdge(toUAEVertex(it.dualOrg), toUAEVertex(it.dualDest)));
            dfs(it.sym, visited, result);
            it = it.onext;
        }
    }

    private static UAEVertex toUAEVertex(Point p) {
        return new UAEVertex(p.x, p.y);
    }

    //    public static List<VDEdge> buildVD(UAEVertex vertex) {
//        if (vertex == null) {
//            return Collections.emptyList();
//        }
//
//        List<VDEdge> result = new ArrayList<>();
//        Set<UAEVertex> visited = new HashSet<>();
//
//        dfsVD(vertex, visited, result);
//
//        return result;
//    }

//    private static void dfsVD(UAEVertex vertex, Set<UAEVertex> visited, List<VDEdge> result) {
//        if (visited.contains(vertex)) {
//            return;
//        }
//        visited.add(vertex);
//
//        QuadEdge it = vertex.edge;
//        Point previousCenter = null;
//        boolean proceed = true;
//        while (proceed) {
//            UAEVertex p0 = it.org;
//            UAEVertex p1 = it.dest;
//            UAEVertex p2 = it.onext.dest;
//
//            dfsVD(p1, visited, result);
//
//            Point centre = triCircumCenter(p0, p1, p2);
//            if (previousCenter != null) {
//                result.add(new VDEdge(previousCenter, centre));
//            }
//            previousCenter = centre;
//            it = it.onext;
//            if (it.equals(vertex.edge)) {
//                proceed = false;
//            }
//        }
//        result.add(new VDEdge(previousCenter, result.get(0).getDualOrg()));
//    }
}
