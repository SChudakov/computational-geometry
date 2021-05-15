package com.chudakov.uae.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UAESolutions {
    List<UAEVertex> convexHull;
    List<UAEEdge> delaunayTriangulation;
    List<UAEEdge> voronoiDiagram;
    Pair<UAEVertex, UAEVertex> closesPair;
}
