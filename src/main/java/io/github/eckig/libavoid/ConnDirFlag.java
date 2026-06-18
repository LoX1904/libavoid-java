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
 * Flags specifying which sides of a shape a point should have visibility to.
 */
public final class ConnDirFlag {
    public static final int ConnDirNone = 0;
    public static final int ConnDirUp = 1;
    public static final int ConnDirDown = 2;
    public static final int ConnDirLeft = 4;
    public static final int ConnDirRight = 8;
    public static final int ConnDirAll = 15;

    private ConnDirFlag() {}
}
