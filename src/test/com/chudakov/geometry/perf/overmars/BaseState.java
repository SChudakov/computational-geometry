package com.chudakov.geometry.perf.overmars;

import com.chudakov.geometry.uae.Point;
import com.chudakov.geometry.uae.Vertex;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.List;

@State(Scope.Benchmark)
public class BaseState {
    @Param({/*"100000", "500000", "1000000", "5000000",*/ "10000000"})
    public int size;

    public List<Point> points;
    public List<Vertex> vertices;

    protected void generatePoints() {
        int upperLimit = 1_000_000;
        this.points = new ArrayList<>(size);
        this.vertices = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            double x = (Math.random() /** upperLimit*/);
            double y = (Math.random() /** upperLimit*/);
            Vertex v = new Vertex(x, y);
            points.add(v);
            vertices.add(v);
        }
    }
}