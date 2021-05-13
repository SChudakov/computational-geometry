package com.chudakov.uae.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UAEOutput {
    List<UAEVertex> convexHull;
    List<UAEEdge> delaunayTriangulation;
    List<UAEEdge> voronoiDiagram;
}
