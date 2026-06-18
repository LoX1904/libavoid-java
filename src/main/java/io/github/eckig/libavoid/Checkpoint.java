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
 * A Checkpoint represents a point through which a connector route must pass.
 * Corresponds to the Checkpoint class in connector.h.
 */
public class Checkpoint {

    public Point point;
    public int arrivalDirections;
    public int departureDirections;

    public Checkpoint() {
        this.point = new Point();
        this.arrivalDirections = ConnDirFlag.ConnDirAll;
        this.departureDirections = ConnDirFlag.ConnDirAll;
    }

    public Checkpoint(Point point) {
        this.point = new Point(point);
        this.arrivalDirections = ConnDirFlag.ConnDirAll;
        this.departureDirections = ConnDirFlag.ConnDirAll;
    }

    public Checkpoint(Point point, int arrivalDirections, int departureDirections) {
        this.point = new Point(point);
        this.arrivalDirections = arrivalDirections;
        this.departureDirections = departureDirections;
    }
}
