package com.chudakov.geometry.alg.convexhull.overmars;

import com.chudakov.geometry.common.Point2D;
import com.chudakov.geometry.util.Pair;

public class ConvexSubhull {
    public enum Type {LEFT, RIGHT}

    final ConcatenableQueue<Point2D> subhull;
    final Type type;

    public ConvexSubhull(ConcatenableQueue<Point2D> subhull, Type type) {
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
        Pair<ConcatenableQueue.Node, ConcatenableQueue.Node> p =
                leftTangent(lower.subhull, upper.subhull);

        ConcatenableQueue.Node lowerBase = p.getFirst();
        ConcatenableQueue.Node upperBase = p.getSecond();

        lower.subhull.cutRight(lowerBase);
        upper.subhull.cutLeft(upperBase);

        ConcatenableQueue<Point2D> result = ConcatenableQueue.concatenate(lower.subhull, upper.subhull);
        return new ConvexSubhull(result, Type.LEFT);
    }

    private static ConvexSubhull joinRight(ConvexSubhull lower, ConvexSubhull upper) {
        Pair<ConcatenableQueue.Node, ConcatenableQueue.Node> p =
                rightTangent(lower.subhull, upper.subhull);

        ConcatenableQueue.Node lowerBase = p.getFirst();
        ConcatenableQueue.Node upperBase = p.getSecond();

        upper.subhull.cutRight(upperBase);
        lower.subhull.cutLeft(lowerBase);

        ConcatenableQueue<Point2D> result = ConcatenableQueue.concatenate(upper.subhull, lower.subhull);
        return new ConvexSubhull(result, Type.RIGHT);
    }

    private static Pair<ConcatenableQueue.Node, ConcatenableQueue.Node> leftTangent(
            ConcatenableQueue lower, ConcatenableQueue upper) {
        return null;
    }

    private static Pair<ConcatenableQueue.Node, ConcatenableQueue.Node> rightTangent(
            ConcatenableQueue lower, ConcatenableQueue upper) {
        return null;
    }
}
