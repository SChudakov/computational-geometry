package com.chudakov.geometry.uae;

import java.util.Iterator;

public class ConvexHull implements Iterable<Vertex2D> {
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
        Iterator<Vertex2D> iterator = this.iterator();
        if (!iterator.hasNext()) {
            return "[]";
        } else {
            StringBuilder result = new StringBuilder();
            result.append('[');

            while (true) {
                Vertex2D element = iterator.next();
                result.append(element);
                if (!iterator.hasNext()) {
                    return result.append(']').toString();
                }

                result.append(',').append(' ');
            }
        }
    }

    @Override
    public Iterator<Vertex2D> iterator() {
        return new CHIterator();
    }

    private class CHIterator implements Iterator<Vertex2D> {
        Iterator<Vertex2D> iterator;
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
        public Vertex2D next() {
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
