/*
 * libavoid - Fast, Incremental, Object-avoiding Line Router
 *
 * Copyright (C) 2004-2011  Monash University
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * See the file LICENSE.LGPL distributed with the library.
 *
 * Licensees holding a valid commercial license may use this file in
 * accordance with the commercial license agreement provided with the
 * library.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * Author(s):  Michael Wybrow
 *
 */

package io.github.eckig.libavoid.vpsc;

/**
 * PositionStats - accumulates weighted position statistics for block variables.
 * Translated from vpsc.h/vpsc.cpp in libavoid C++.
 */
public class PositionStats {
    public double scale;
    public double AB;
    public double AD;
    public double A2;

    public PositionStats() {
        scale = 0;
        AB = 0;
        AD = 0;
        A2 = 0;
    }

    /**
     * Add a variable's contribution to the position statistics.
     * C++: void PositionStats::addVariable(Variable* const v)
     */
    public void addVariable(Variable v) {
        double ai = scale / v.scale;
        double bi = v.offset / v.scale;
        double wi = v.weight.weight;
        AB += wi * ai * bi;
        AD += wi * ai * v.desiredPosition;
        A2 += wi * ai * ai;
    }
}
