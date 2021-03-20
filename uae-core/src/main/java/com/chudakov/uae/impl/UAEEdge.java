package com.chudakov.uae.impl;

import lombok.Getter;

@Getter
public class UAEEdge {
    UAEVertex org;
    UAEVertex dest;

    public UAEEdge(UAEVertex org, UAEVertex dest) {
        this.org = org;
        this.dest = dest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UAEEdge UAEEdge = (UAEEdge) o;

        if (!org.equals(UAEEdge.org)) return false;
        return dest.equals(UAEEdge.dest);
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
