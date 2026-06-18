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
 * A Rectangle, a simpler way to define the polygon for square or rectangular shapes.
 */
public class Rectangle extends Polygon {

    public Rectangle(Point topLeft, Point bottomRight) {
        super(4);
        double xMin = Math.min(topLeft.x, bottomRight.x);
        double xMax = Math.max(topLeft.x, bottomRight.x);
        double yMin = Math.min(topLeft.y, bottomRight.y);
        double yMax = Math.max(topLeft.y, bottomRight.y);

        ps.set(0, new Point(xMax, yMin));
        ps.set(1, new Point(xMax, yMax));
        ps.set(2, new Point(xMin, yMax));
        ps.set(3, new Point(xMin, yMin));
    }

    public Rectangle(Point centre, double width, double height) {
        super(4);
        double halfWidth = width / 2.0;
        double halfHeight = height / 2.0;
        double xMin = centre.x - halfWidth;
        double xMax = centre.x + halfWidth;
        double yMin = centre.y - halfHeight;
        double yMax = centre.y + halfHeight;

        ps.set(0, new Point(xMax, yMin));
        ps.set(1, new Point(xMax, yMax));
        ps.set(2, new Point(xMin, yMax));
        ps.set(3, new Point(xMin, yMin));
    }
}
