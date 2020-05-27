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
public class PerformanceState {

    @Param({"100000", "500000", "1000000", "5000000", "10000000"/*, "25000000", "50000000", "70000000"*/})
    public int size;
//    @Param({/*"20", "30", "40", "50"*/})
//    public int tasksPerThread;

    public int inputSizeThreshold;
    public List<Point> points;
    public List<Vertex> vertices;

    @Setup(Level.Trial)
    public void generatePoints() {
//        inputSizeThreshold = size / Runtime.getRuntime().availableProcessors() / tasksPerThread;
        int upperLimit = 1_000_000;
        this.points = new ArrayList<>(size);
        this.vertices = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int x = (int) (Math.random() * upperLimit);
            int y = (int) (Math.random() * upperLimit);
            Vertex v = new Vertex(x, y);
            points.add(v);
            vertices.add(v);
        }
    }
}