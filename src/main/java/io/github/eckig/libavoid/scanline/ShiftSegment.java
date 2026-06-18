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

package io.github.eckig.libavoid.scanline;

import io.github.eckig.libavoid.Point;

/**
 * ShiftSegment interface - represents a segment that can be shifted
 * during the nudging phase.
 * Translated from scanline.h in libavoid C++.
 */
public abstract class ShiftSegment {
    public int dimension;
    public double minSpaceLimit;
    public double maxSpaceLimit;

    public ShiftSegment(int dim) {
        this.dimension = dim;
        this.minSpaceLimit = -CHANNEL_MAX;
        this.maxSpaceLimit = CHANNEL_MAX;
    }

    public abstract Point lowPoint();
    public abstract Point highPoint();
    public abstract boolean overlapsWith(ShiftSegment rhs, int dim);
    public abstract boolean immovable();

    public static final double CHANNEL_MAX = 100000000;
}
