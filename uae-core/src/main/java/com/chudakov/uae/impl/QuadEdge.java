package com.chudakov.uae.impl;

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

    public Point middle() {
        return Point.middle(org, dest);
    }


    @Override
    public String toString() {
        return "QuadEdge{" +
                "org=" + org +
                ", dest=" + dest +
                '}';
    }
}
