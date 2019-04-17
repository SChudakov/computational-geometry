package com.chudakov.geometry.core;

public interface DaCExecutionSpecifics<IT, OT> {
    OT solve(IT points);
}
