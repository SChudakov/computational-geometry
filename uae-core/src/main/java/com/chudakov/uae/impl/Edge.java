package com.chudakov.uae.impl;

import lombok.Getter;

@Getter
public class Edge {
    UAEVertex org;
    UAEVertex dest;

    public Edge(UAEVertex org, UAEVertex dest) {
        this.org = org;
        this.dest = dest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        if (!org.equals(edge.org)) return false;
        return dest.equals(edge.dest);
    }

    @Override
    public int hashCode() {
        int result = org.hashCode();
        result = 31 * result + dest.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "[" + org + " -> " + dest + ']';
    }
}
