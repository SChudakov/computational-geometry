package com.chudakov.uae.impl;

import com.chudakov.simple.ch.Point;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.chudakov.uae.impl.ConcatenableQueue.CQVertex;

public class CP {
    public static Pair<UAEVertex, UAEVertex> baseCaseCP(final List<UAEVertex> vertices) {
        if (vertices.size() == 2) {
            return Pair.of(vertices.get(0), vertices.get(1));
        } else if (vertices.size() == 3) {
            UAEVertex v1 = vertices.get(0);
            UAEVertex v2 = vertices.get(1);
            UAEVertex v3 = vertices.get(2);
            double d1 = Point.distance(v1, v2);
            double d2 = Point.distance(v2, v3);
            double d3 = Point.distance(v3, v1);
            if (d1 <= d2 && d1 <= d3) {
                return Pair.of(v1, v2);
            } else if (d2 <= d1 && d2 <= d3) {
                return Pair.of(v2, v3);
            } else if (d3 <= d1 && d3 <= d2) {
                return Pair.of(v1, v3);
            }
        }
        throw new RuntimeException();
    }

    public static Pair<UAEVertex, UAEVertex> mergeCP(final UAEState left, final UAEState right) {
        UAEVertex rightmostLeft = getRightmost(left.convexHull);
        UAEVertex leftmostRight = getLeftmost(right.convexHull);
        double middleLine = (rightmostLeft.x + leftmostRight.x) / 2;
        double leftDistance = Point.distance(left.closestPair.getLeft(), left.closestPair.getRight());
        double rightDistance = Point.distance(right.closestPair.getLeft(), right.closestPair.getRight());
        double minimumDistance = Math.min(leftDistance, rightDistance);
        Pair<UAEVertex, UAEVertex> closesPair = null;
        if (minimumDistance == leftDistance) {
            closesPair = left.closestPair;
        } else {
            closesPair = right.closestPair;
        }

        List<UAEVertex> strip = new ArrayList<>();
        getPointsCloserThan(left.points, middleLine, minimumDistance, strip);
        getPointsCloserThan(right.points, middleLine, minimumDistance, strip);

        Pair<UAEVertex, UAEVertex> stripClosesPair = getClosestInString(strip, minimumDistance);
        if (stripClosesPair == null) {
            return closesPair;
        } else {
            return stripClosesPair;
        }
    }

    private static Pair<UAEVertex, UAEVertex> getClosestInString(List<UAEVertex> strip, double minimumDistance) {
        strip.sort(Comparator.comparingDouble(v -> v.y));

        Pair<UAEVertex, UAEVertex> result = null;
        double resultDistance = Double.POSITIVE_INFINITY;
        for (int i = 0; i < strip.size(); ++i) {
            for (int j = i + 1; j < strip.size() && (strip.get(j).y - strip.get(i).y) < minimumDistance; ++j) {
                UAEVertex ith = strip.get(i);
                UAEVertex jth = strip.get(j);
                double distance = Point.distance(ith, jth);
                if (distance < minimumDistance && distance < resultDistance) {
                    result = Pair.of(ith, jth);
                    resultDistance = distance;
                }
            }
        }
        return result;
    }

    private static void getPointsCloserThan(List<UAEVertex> vertices, double fromX,
                                            double distance, List<UAEVertex> result) {
        for (UAEVertex vertex : vertices) {
            if (Math.abs(fromX - vertex.x) <= distance) {
                result.add(vertex);
            }
        }
    }

    private static UAEVertex getRightmost(final ConvexHull ch) {
        UAEVertex result = null;
        for (CQVertex<UAEVertex> v : ch) {
            if (result == null || result.x < v.value.x) {
                result = v.value;
            }
        }
        return result;
    }

    private static UAEVertex getLeftmost(final ConvexHull ch) {
        UAEVertex result = null;
        for (CQVertex<UAEVertex> v : ch) {
            if (result == null || result.x > v.value.x) {
                result = v.value;
            }
        }
        return result;
    }
}
