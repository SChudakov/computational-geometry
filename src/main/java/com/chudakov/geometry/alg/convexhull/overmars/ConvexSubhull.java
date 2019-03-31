package com.chudakov.geometry.alg.convexhull.overmars;

import com.chudakov.geometry.util.Pair;

public class ConvexSubhull {
    public enum Type {LEFT, RIGHT}

    final ConcatenableQueue subhull;
    final Type type;

    public ConvexSubhull(ConcatenableQueue subhull, Type type) {
        this.subhull = subhull;
        this.type = type;
    }

    public static ConvexSubhull join(ConvexSubhull left, ConvexSubhull right) {
        if (!left.type.equals(right.type)) {
            throw new IllegalArgumentException("hulls type mismatch");
        }
        if (left.type.equals(Type.LEFT)) {
            return joinLeft(left, right);
        } else {
            return joinRight(left, right);
        }
    }

    private static ConvexSubhull joinLeft(ConvexSubhull lower, ConvexSubhull upper) {
        return null;
    }

    private static ConvexSubhull joinRight(ConvexSubhull lower, ConvexSubhull upper) {
        return null;
    }

    public static Pair<ConcatenableQueue.CQNode, ConcatenableQueue.CQNode> leftTungent(
            ConcatenableQueue lower, ConcatenableQueue upper){
        return null;
    }

    public static Pair<ConcatenableQueue.CQNode, ConcatenableQueue.CQNode> rightTungent(
            ConcatenableQueue lower, ConcatenableQueue upper){
        return null;
    }
}
