package com.chudakov.geometry.uae;

public class ConvexSubhull {
    public enum Type {UPPER, LOWER}

    final ConcatenableQueue<Vertex2D> subhull;
    final Type type;

    public ConvexSubhull(ConcatenableQueue<Vertex2D> subhull, Type type) {
        this.subhull = subhull;
        this.type = type;
    }
}
