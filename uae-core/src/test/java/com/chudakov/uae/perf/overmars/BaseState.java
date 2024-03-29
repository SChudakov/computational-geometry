package com.chudakov.uae.perf.overmars;

import com.chudakov.uae.impl.Point;
import com.chudakov.uae.impl.UAEVertex;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.List;

@State(Scope.Benchmark)
public class BaseState {
    @Param({/*"100000", "500000", "1000000", "5000000",*/ "10000000"})
    public int size;

    public List<Point> points;
    public List<UAEVertex> vertices;

    protected void generatePoints() {
        int upperLimit = 1_000_000;
        this.points = new ArrayList<>(size);
        this.vertices = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            double x = (Math.random() /** upperLimit*/);
            double y = (Math.random() /** upperLimit*/);
            UAEVertex v = new UAEVertex(x, y);
            points.add(v);
            vertices.add(v);
        }
    }
}