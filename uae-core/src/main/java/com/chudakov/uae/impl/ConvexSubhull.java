package com.chudakov.uae.impl;

public class ConvexSubhull {
    public enum Type {UPPER, LOWER}

    final ConcatenableQueue<UAEVertex> subhull;
    final Type type;

    public ConvexSubhull(ConcatenableQueue<UAEVertex> subhull, Type type) {
        this.subhull = subhull;
        this.type = type;
    }
}
