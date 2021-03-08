package com.chudakov.uae.impl;

import com.chudakov.uae.core.DaCAlgorithm;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;

public class UAE2D implements DaCAlgorithm<List<UAEVertex>, UAEResult> {
    @Override
    public boolean isBaseCase(List<UAEVertex> points) {
        return points.size() <= 3;
    }

    @Override
    public int inputSize(List<UAEVertex> input) {
        return input.size();
    }

    @Override
    public List<UAEVertex> precompute(List<UAEVertex> points) {
        points = new ArrayList<>(new HashSet<>(points));
        // TODO: do not remove vertically and horizontally collinear points
        points.sort(new AntiLOPointComparator());
        removeDuplicated(points, Comparator.comparingDouble(p -> p.y));
        points.sort(UAEVertex::compareTo);
        removeDuplicated(points, Comparator.comparingDouble(p -> p.x));
        return points;
    }

    static void removeDuplicated(List<UAEVertex> points, Comparator<UAEVertex> comparator) {
        ListIterator<UAEVertex> it1 = points.listIterator();
        ListIterator<UAEVertex> it2 = points.listIterator();

        // handle empty input case
        if (!it1.hasNext()) {
            return;
        }
        it1.next();

        while (it2.hasNext()) {
            if (it2.nextIndex() > 0 && it2.nextIndex() < points.size() - 1) {
                UAEVertex previous = it2.previous();
                it2.next();
                UAEVertex current = it2.next();
                UAEVertex next = it2.next();
                it2.previous();

                if (!(comparator.compare(previous, current) == 0 && comparator.compare(current, next) == 0)) {
                    it1.set(current);
                    if (it1.hasNext()) {
                        it1.next();
                    }
                }
            } else if (it2.nextIndex() == 0) {
                // one-element list case
                if (it1.hasNext()) {
                    it1.next();
                }
                it2.next();
            } else {
                it1.set(it2.next());
            }
        }
        if (it1.hasNext()) {
            int it1NextIndex = it1.nextIndex();
            while (points.size() != it1NextIndex) {
                points.remove(points.size() - 1);
            }
        }
    }


    @Override
    public UAEResult solveBaseCase(List<UAEVertex> points) {
        ConvexHull convexHull = CH.baseCaseCH(points);
        Pair<QuadEdge, QuadEdge> p = DT.baseCaseDT(points);
        return new UAEResult(convexHull, p.getLeft(), p.getRight());
    }


    @Override
    public Pair<List<UAEVertex>, List<UAEVertex>> divide(List<UAEVertex> input) {
        int mid = input.size() / 2;
        return Pair.of(input.subList(0, mid), input.subList(mid, input.size()));
    }


    @Override
    public UAEResult merge(UAEResult left, UAEResult right) {
        ConvexHull mergedCH = CH.mergeCH(left, right);
        Pair<QuadEdge, QuadEdge> mergedDT = DT.mergeDT(left, right);
        return new UAEResult(mergedCH, mergedDT.getLeft(), mergedDT.getRight());
    }


    static class AntiLOPointComparator implements Comparator<UAEVertex> {

        @Override
        public int compare(UAEVertex x, UAEVertex y) {
            if (x.y < y.y || (x.y == y.y && x.x < y.x)) {
                return -1;
            }
            if (x.x == y.x && x.y == y.y) {
                return 0;
            }
            return 1;
        }
    }
}
