package com.chudakov.uae.core;

import com.chudakov.uae.impl.UAEState;
import com.chudakov.uae.impl.UAEVertex;

import java.util.List;

public interface DaCExecutionSpecifics {
    UAEState solve(final List<UAEVertex> points);
}
