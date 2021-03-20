package com.chudakov.uae.impl;

import com.chudakov.simple.ch.Point;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuadEdge {
    UAEVertex org;
    UAEVertex dest;

    QuadEdge onext;
    QuadEdge oprev;

    QuadEdge sym;

    Point dualOrg;
    Point dualDest;

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
