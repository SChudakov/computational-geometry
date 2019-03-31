package com.chudakov.geometry.framework;

import java.util.List;

public interface DaCExecutionSpecifics<PT> {
    List<PT> solve(List<PT> points);
}
