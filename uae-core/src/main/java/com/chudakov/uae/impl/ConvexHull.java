package com.chudakov.uae.impl;

import java.util.Iterator;

public class ConvexHull implements Iterable<UAEVertex> {
    enum Position {LEFT, RIGHT}

    final ConvexSubhull upperSubhull;
    final ConvexSubhull lowerSubhull;

    public ConvexHull(ConvexSubhull upperSubhull, ConvexSubhull lowerSubhull) {
        if (!upperSubhull.type.equals(ConvexSubhull.Type.UPPER)) {
            throw new IllegalArgumentException("upper sub-hull improper type");
        }
        if (!lowerSubhull.type.equals(ConvexSubhull.Type.LOWER)) {
            throw new IllegalArgumentException("lower sub-hull improper type");
        }
        this.upperSubhull = upperSubhull;
        this.lowerSubhull = lowerSubhull;
    }


    @Override
    public String toString() {
        Iterator<UAEVertex> iterator = this.iterator();
        if (!iterator.hasNext()) {
            return "[]";
        } else {
            StringBuilder result = new StringBuilder();
            result.append('[');

            while (true) {
                UAEVertex element = iterator.next();
                result.append(element);
                if (!iterator.hasNext()) {
                    return result.append(']').toString();
                }

                result.append(',').append(' ');
            }
        }
    }

    @Override
    public Iterator<UAEVertex> iterator() {
        return new CHIterator();
    }


    private class CHIterator implements Iterator<UAEVertex> {
        Iterator<UAEVertex> iterator;
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
        public UAEVertex next() {
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
