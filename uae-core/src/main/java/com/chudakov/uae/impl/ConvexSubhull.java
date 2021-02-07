package com.chudakov.uae.impl;

public class ConvexSubhull {
    public enum Type {UPPER, LOWER}

    final ConcatenableQueue<Vertex> subhull;
    final Type type;

    public ConvexSubhull(ConcatenableQueue<Vertex> subhull, Type type) {
        this.subhull = subhull;
        this.type = type;
    }
}
