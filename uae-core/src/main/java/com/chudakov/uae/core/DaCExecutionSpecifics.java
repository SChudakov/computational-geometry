package com.chudakov.uae.core;

public interface DaCExecutionSpecifics<IT, OT> {
    OT solve(IT points);
}
