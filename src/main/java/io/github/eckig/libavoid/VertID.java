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
 * Translated from vertices.h / vertices.cpp — VertID class.
 * Identifies a vertex by its object ID and vertex number within that object.
 */
public class VertID implements Comparable<VertID> {

    public int objID;       // unsigned int in C++
    public short vn;        // unsigned short in C++
    public short props;     // VertIDProps (unsigned short in C++)

    // Constants matching C++ VertID::src / VertID::tar
    public static final short src = 1;
    public static final short tar = 2;

    // Property flags matching C++ PROP_* constants
    public static final short PROP_ConnPoint      = 1;
    public static final short PROP_OrthShapeEdge  = 2;
    public static final short PROP_ConnectionPin  = 4;
    public static final short PROP_ConnCheckpoint = 8;
    public static final short PROP_DummyPinHelper = 16;

    public VertID() {
    }

    public VertID(int id, short n) {
        this(id, n, (short) 0);
    }

    public VertID(int id, short n, short p) {
        this.objID = id;
        this.vn = n;
        this.props = p;
    }

    // Copy constructor equivalent
    public VertID(VertID other) {
        this.objID = other.objID;
        this.vn = other.vn;
        this.props = other.props;
    }

    public void set(VertID rhs) {
        this.objID = rhs.objID;
        this.vn = rhs.vn;
        this.props = rhs.props;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof VertID rhs)) return false;
        // C++ operator== compares only objID and vn (not props)
        return (objID == rhs.objID) && (vn == rhs.vn);
    }

    @Override
    public int hashCode() {
        return 31 * objID + vn;
    }

    // C++ operator<
    @Override
    public int compareTo(VertID rhs) {
        if (objID < rhs.objID) return -1;
        if (objID > rhs.objID) return 1;
        return Short.compare(vn, rhs.vn);
    }

    // C++ operator+(int)
    public VertID plus(int rhs) {
        return new VertID(objID, (short) (vn + rhs), props);
    }

    // C++ operator++(int) — post-increment
    public void postIncrement() {
        vn += 1;
    }

    // Property tests — matching C++ inline methods
    public boolean isOrthShapeEdge() {
        return (props & PROP_OrthShapeEdge) != 0;
    }

    public boolean isConnPt() {
        return (props & PROP_ConnPoint) != 0;
    }

    public boolean isConnectionPin() {
        return (props & PROP_ConnectionPin) != 0;
    }

    public boolean isConnCheckpoint() {
        return (props & PROP_ConnCheckpoint) != 0;
    }

    public boolean isDummyPinHelper() {
        return (props & PROP_DummyPinHelper) != 0;
    }

    @Override
    public String toString() {
        return "[" + objID + "," + vn + ", p=" + (int) props + "]";
    }
}
