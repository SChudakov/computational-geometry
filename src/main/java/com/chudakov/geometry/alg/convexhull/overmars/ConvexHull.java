package com.chudakov.geometry.alg.convexhull.overmars;

public class ConvexHull {
    final ConvexSubhull leftSubhull;
    final ConvexSubhull rightSubhull;

    public ConvexHull(ConvexSubhull leftSubhull, ConvexSubhull rightSubhull) {
        this.leftSubhull = leftSubhull;
        this.rightSubhull = rightSubhull;
    }

    public static ConvexHull join(ConvexHull lower, ConvexHull upper) {
        return new ConvexHull(
                ConvexSubhull.join(lower.leftSubhull, upper.leftSubhull),
                ConvexSubhull.join(lower.rightSubhull, upper.rightSubhull)
        );
    }
}
