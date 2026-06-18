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

package io.github.eckig.libavoid;

/**
 * A bounding box, represented by the top-left and bottom-right corners.
 */
public class Box {
    /** top left */
    public Point min;
    /** bottom right */
    public Point max;

    public Box() {
        this.min = new Point();
        this.max = new Point();
    }

    public Box(Point min, Point max) {
        this.min = min;
        this.max = max;
    }

    public double length(int dimension) {
        if (dimension == 0) {
            return max.x - min.x;
        }
        return max.y - min.y;
    }

    public double width() {
        return max.x - min.x;
    }

    public double height() {
        return max.y - min.y;
    }
}
