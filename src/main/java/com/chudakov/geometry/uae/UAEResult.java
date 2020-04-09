package com.chudakov.geometry.uae;

import com.chudakov.geometry.datastructure.ConvexHull;

import java.util.List;

public class UAEResult {
    ConvexHull convexHull;

    List<UAEEdge> triangulation;
    UAEEdge e1;
    UAEEdge e2;
}
