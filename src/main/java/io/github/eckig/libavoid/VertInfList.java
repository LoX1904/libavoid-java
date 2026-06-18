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
 * Translated from vertices.h / vertices.cpp — VertInfList class.
 * An intrusive linked list of all vertices in the router instance.
 * Connector endpoints are listed first, then shape vertices.
 * Dummy vertices for orthogonal routing are classed as shape vertices
 * but have VertID(0, 0).
 */
public class VertInfList {

    private VertInf _firstShapeVert;
    private VertInf _firstConnVert;
    private VertInf _lastShapeVert;
    private VertInf _lastConnVert;
    private int _connVertices;

    public VertInfList() {
        _firstShapeVert = null;
        _firstConnVert = null;
        _lastShapeVert = null;
        _lastConnVert = null;
        _connVertices = 0;
    }

    // C++ addVertex
    public void addVertex(VertInf vert) {
        assert vert.lstPrev == null;
        assert vert.lstNext == null;

        if (vert.id.isConnPt()) {
            // A Connector vertex
            if (_firstConnVert != null) {
                // Join with previous front
                vert.lstNext = _firstConnVert;
                _firstConnVert.lstPrev = vert;

                // Make front
                _firstConnVert = vert;
            } else {
                // Make front and back
                _firstConnVert = vert;
                _lastConnVert = vert;

                // Link to front of shapes list
                vert.lstNext = _firstShapeVert;
            }
            _connVertices++;
        } else {
            // A Shape vertex
            if (_lastShapeVert != null) {
                // Join with previous back
                vert.lstPrev = _lastShapeVert;
                _lastShapeVert.lstNext = vert;

                // Make back
                _lastShapeVert = vert;
            } else {
                // Make first and last
                _firstShapeVert = vert;
                _lastShapeVert = vert;

                // Join with conns list
                if (_lastConnVert != null) {
                    assert _lastConnVert.lstNext == null;
                    _lastConnVert.lstNext = vert;
                }
            }
        }
    }

    // C++ removeVertex — removes vert and returns the following vertex
    public VertInf removeVertex(VertInf vert) {
        if (vert == null) {
            return null;
        }

        VertInf following = vert.lstNext;

        if (vert.id.isConnPt()) {
            // A Connector vertex
            if (vert == _firstConnVert) {
                if (vert == _lastConnVert) {
                    _firstConnVert = null;
                    _lastConnVert = null;
                } else {
                    // Set new first
                    _firstConnVert = _firstConnVert.lstNext;
                    if (_firstConnVert != null) {
                        _firstConnVert.lstPrev = null;
                    }
                }
            } else if (vert == _lastConnVert) {
                // Set new last
                _lastConnVert = _lastConnVert.lstPrev;
                // Make last point to shapes list
                _lastConnVert.lstNext = _firstShapeVert;
            } else {
                vert.lstNext.lstPrev = vert.lstPrev;
                vert.lstPrev.lstNext = vert.lstNext;
            }
            _connVertices--;
        } else {
            // A Shape vertex
            if (vert == _lastShapeVert) {
                // Set new last
                _lastShapeVert = _lastShapeVert.lstPrev;

                if (vert == _firstShapeVert) {
                    _firstShapeVert = null;
                    if (_lastConnVert != null) {
                        _lastConnVert.lstNext = null;
                    }
                }

                if (_lastShapeVert != null) {
                    _lastShapeVert.lstNext = null;
                }
            } else if (vert == _firstShapeVert) {
                // Set new first
                _firstShapeVert = _firstShapeVert.lstNext;

                // Correct the last conn vertex
                if (_lastConnVert != null) {
                    _lastConnVert.lstNext = _firstShapeVert;
                }

                if (_firstShapeVert != null) {
                    _firstShapeVert.lstPrev = null;
                }
            } else {
                vert.lstNext.lstPrev = vert.lstPrev;
                vert.lstPrev.lstNext = vert.lstNext;
            }
        }
        vert.lstPrev = null;
        vert.lstNext = null;

        return following;
    }

    // C++ getVertexByID
    public VertInf getVertexByID(VertID id) {
        VertID searchID = new VertID(id);
        if (searchID.vn == Point.kUnassignedVertexNumber) {
            int topbit = 1 << 31;
            if ((searchID.objID & topbit) != 0) {
                searchID.objID = searchID.objID & ~topbit;
                searchID.vn = VertID.src;
            } else {
                searchID.vn = VertID.tar;
            }
        }
        for (VertInf curr = connsBegin(); curr != end(); curr = curr.lstNext) {
            if (curr.id.equals(searchID)) {
                return curr;
            }
        }
        return null;
    }

    // C++ shapesBegin — first shape vertex (may be null)
    public VertInf shapesBegin() {
        return _firstShapeVert;
    }

    // C++ connsBegin — first conn vertex, or first shape if no conns
    public VertInf connsBegin() {
        if (_firstConnVert != null) {
            return _firstConnVert;
        }
        return _firstShapeVert;
    }

    // C++ end — always null (sentinel)
    public VertInf end() {
        return null;
    }

    // C++ connsSize
    public int connsSize() {
        return _connVertices;
    }
}
