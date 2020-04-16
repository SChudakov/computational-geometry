package com.chudakov.geometry.uae;

public class QuadEdge {
    Vertex2D org;
    Vertex2D dest;

    QuadEdge onext;
    QuadEdge oprev;

    QuadEdge sym;

    Object data;

    public QuadEdge(Vertex2D org, Vertex2D dest) {
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
