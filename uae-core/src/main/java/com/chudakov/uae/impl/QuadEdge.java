package com.chudakov.uae.impl;

import lombok.Getter;

@Getter
public class QuadEdge {
    UAEVertex org;
    UAEVertex dest;

    QuadEdge onext;
    QuadEdge oprev;

    QuadEdge sym;

    public QuadEdge(UAEVertex org, UAEVertex dest) {
        this.org = org;
        this.dest = dest;
    }

    @Override
    public String toString() {
        return "QuadEdge{" +
                "org=" + org +
                ", dest=" + dest +
                '}';
    }
}
