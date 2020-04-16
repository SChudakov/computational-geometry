package com.chudakov.geometry.uae;

public class QuadEdge {
    Vertex org;
    Vertex dest;

    QuadEdge onext;
    QuadEdge oprev;

    QuadEdge sym;

    public QuadEdge(Vertex org, Vertex dest) {
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
