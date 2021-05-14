package com.chudakov.uae.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

@Getter
@AllArgsConstructor
public class UAEResult {
    ConvexHull convexHull;
    QuadEdge e1;
    QuadEdge e2;
    Pair<UAEVertex, UAEVertex> closestPair;
}
