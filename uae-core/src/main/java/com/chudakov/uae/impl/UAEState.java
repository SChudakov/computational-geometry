package com.chudakov.uae.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UAEState {
    List<UAEVertex> points;
    ConvexHull convexHull;
    QuadEdge e1;
    QuadEdge e2;
    Pair<UAEVertex, UAEVertex> closestPair;
}
