package com.chudakov.uae.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UAEResult {
    ConvexHull convexHull;
    QuadEdge e1;
    QuadEdge e2;
}
