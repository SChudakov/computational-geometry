package com.chudakov.geometry.framework;

import com.chudakov.geometry.util.Pair;

import java.util.List;

public interface DaCAlgorithm<PT> {
    boolean isBaseCase(List<PT> points);

    List<PT> solveBaseCase(List<PT> points);

    List<PT> merge(List<PT> first, List<PT> second);

    Pair<List<PT>, List<PT>> divide(List<PT> points);

    void precompute(List<PT> points);
}
