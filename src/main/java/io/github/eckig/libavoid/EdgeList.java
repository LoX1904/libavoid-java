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
 * Translated from graph.h / graph.cpp — EdgeList class.
 * An intrusive doubly-linked list of EdgeInf objects.
 */
public class EdgeList {

    private final boolean m_orthogonal;
    private EdgeInf m_first_edge;
    private EdgeInf m_last_edge;
    private int m_count;

    public EdgeList() {
        this(false);
    }

    public EdgeList(boolean orthogonal) {
        this.m_orthogonal = orthogonal;
        this.m_first_edge = null;
        this.m_last_edge = null;
        this.m_count = 0;
    }

    // C++ clear
    public void clear() {
        while (m_first_edge != null) {
            // EdgeInf.remove() calls removeEdge(), updating m_first_edge
            m_first_edge.remove();
        }
        assert m_count == 0;
        m_last_edge = null;
    }

    // C++ size
    public int size() {
        return m_count;
    }

    // C++ begin
    public EdgeInf begin() {
        return m_first_edge;
    }

    // C++ end
    public EdgeInf end() {
        return null;
    }

    // C++ addEdge (package-private, called by EdgeInf.makeActive)
    void addEdge(EdgeInf edge) {
        // Dummy connections for ShapeConnectionPins won't be orthogonal,
        // even in the orthogonal visibility graph.
        assert !m_orthogonal || edge.isOrthogonal() || edge.isDummyConnection();
        if (m_first_edge == null) {
            assert m_last_edge == null;
            m_last_edge = edge;
            m_first_edge = edge;
            edge.lstPrev = null;
        } else {
            assert m_last_edge != null;
            m_last_edge.lstNext = edge;
            edge.lstPrev = m_last_edge;
            m_last_edge = edge;
        }
        edge.lstNext = null;
        m_count++;
    }

    // C++ removeEdge (package-private, called by EdgeInf.makeInactive)
    void removeEdge(EdgeInf edge) {
        if (edge.lstPrev != null) {
            edge.lstPrev.lstNext = edge.lstNext;
        }
        if (edge.lstNext != null) {
            edge.lstNext.lstPrev = edge.lstPrev;
        }
        if (edge == m_last_edge) {
            m_last_edge = edge.lstPrev;
            if (edge == m_first_edge) {
                m_first_edge = null;
            }
        } else if (edge == m_first_edge) {
            m_first_edge = edge.lstNext;
        }

        edge.lstPrev = null;
        edge.lstNext = null;
        m_count--;
    }
}
