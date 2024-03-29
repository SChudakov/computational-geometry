package com.chudakov.uae.impl;

import java.util.Iterator;
import static com.chudakov.uae.impl.ConcatenableQueue.CQVertex;

public class ConvexHull implements Iterable<CQVertex<UAEVertex>> {
    enum Position {LEFT, RIGHT}

    final ConvexSubhull upperSubhull;
    final ConvexSubhull lowerSubhull;

    public ConvexHull(ConvexSubhull upperSubhull, ConvexSubhull lowerSubhull) {
        if (!upperSubhull.type.equals(ConvexSubhull.Type.UPPER)) {
            throw new IllegalArgumentException("invalid type of upper sub-hull");
        }
        if (!lowerSubhull.type.equals(ConvexSubhull.Type.LOWER)) {
            throw new IllegalArgumentException("invalid type of lower sub-hull");
        }
        this.upperSubhull = upperSubhull;
        this.lowerSubhull = lowerSubhull;
    }


    @Override
    public String toString() {
        Iterator<CQVertex<UAEVertex>> iterator = this.iterator();
        if (!iterator.hasNext()) {
            return "[]";
        } else {
            StringBuilder result = new StringBuilder();
            result.append('[');

            while (true) {
                UAEVertex element = iterator.next().value;
                result.append(element);
                if (!iterator.hasNext()) {
                    return result.append(']').toString();
                }

                result.append(',').append(' ');
            }
        }
    }

    @Override
    public Iterator<CQVertex<UAEVertex>> iterator() {
        return new CHIterator();
    }


    private class CHIterator implements Iterator<CQVertex<UAEVertex>> {
        Iterator<CQVertex<UAEVertex>> iterator;
        ConvexSubhull.Type hullType;

        CHIterator() {
            iterator = upperSubhull.subhull.iterator();
            hullType = ConvexSubhull.Type.UPPER;
            changeIteratorIfNeeded();
        }

        @Override
        public boolean hasNext() {
            changeIteratorIfNeeded();
            return iterator.hasNext();
        }

        @Override
        public CQVertex<UAEVertex> next() {
            changeIteratorIfNeeded();
            return iterator.next();
        }

        private void changeIteratorIfNeeded() {
            if (!iterator.hasNext() && hullType.equals(ConvexSubhull.Type.UPPER)) {
                iterator = lowerSubhull.subhull.reverseIterator();
                hullType = ConvexSubhull.Type.LOWER;
            }
        }
    }
}
