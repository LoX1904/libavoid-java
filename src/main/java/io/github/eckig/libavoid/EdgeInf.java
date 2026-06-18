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

import java.util.ArrayList;
import java.util.List;

/**
 * Translated from graph.h / graph.cpp — EdgeInf class.
 * An edge in the visibility graph connecting two VertInf vertices.
 */
public class EdgeInf {

    // Intrusive linked list pointers for EdgeList
    public EdgeInf lstPrev;
    public EdgeInf lstNext;

    private Router m_router;
    private int m_blocker;
    private boolean m_added;
    private boolean m_visible;
    private final boolean m_orthogonal;
    private boolean m_disabled;
    private final VertInf m_vert1;
    private final VertInf m_vert2;
    // C++ uses list iterators for O(1) removal. In Java we just use the list directly.
    private final List<boolean[]> m_conns;  // C++ FlagList — list of boolean* flags
    private double m_dist;
    private double m_mtst_dist;

    public EdgeInf(VertInf v1, VertInf v2) {
        this(v1, v2, false);
    }

    public EdgeInf(VertInf v1, VertInf v2, boolean orthogonal) {
        this.lstPrev = null;
        this.lstNext = null;
        this.m_router = null;
        this.m_blocker = 0;
        this.m_added = false;
        this.m_visible = false;
        this.m_orthogonal = orthogonal;
        this.m_disabled = false;
        this.m_vert1 = v1;
        this.m_vert2 = v2;
        this.m_dist = -1;
        this.m_mtst_dist = 0;

        assert v1 != null && v2 != null;
        assert v1._router == v2._router;
        m_router = v1._router;

        m_conns = new ArrayList<>();
    }

    // C++ getDist (inline)
    public double getDist() {
        return m_dist;
    }

    // C++ setDist
    public void setDist(double dist) {
        if (m_added && !m_visible) {
            makeInactive();
            assert !m_added;
        }
        if (!m_added) {
            m_visible = true;
            makeActive();
        }
        m_dist = dist;
        m_blocker = 0;
    }

    // C++ alertConns
    public void alertConns() {
        for (boolean[] flag : m_conns) {
            flag[0] = true;
        }
        m_conns.clear();
    }

    // C++ addConn
    public void addConn(boolean[] flag) {
        m_conns.add(flag);
    }

    // C++ addBlocker
    public void addBlocker(int b) {
        assert m_router.InvisibilityGrph;

        if (m_added && m_visible) {
            makeInactive();
            assert !m_added;
        }
        if (!m_added) {
            m_visible = false;
            makeActive();
        }
        m_dist = 0;
        m_blocker = b;
    }

    // C++ added
    public boolean added() {
        return m_added;
    }

    // C++ isOrthogonal
    public boolean isOrthogonal() {
        return (m_vert1.point.x == m_vert2.point.x) ||
               (m_vert1.point.y == m_vert2.point.y);
    }

    // C++ isDummyConnection
    public boolean isDummyConnection() {
        return (m_vert1.id.isConnectionPin() && m_vert2.id.isConnPt()) ||
               (m_vert2.id.isConnectionPin() && m_vert1.id.isConnPt());
    }

    // C++ isDisabled
    public boolean isDisabled() {
        return m_disabled;
    }

    // C++ setDisabled
    public void setDisabled(boolean disabled) {
        m_disabled = disabled;
    }

    // C++ blocker
    public int blocker() {
        return m_blocker;
    }

    // C++ mtstDist
    public double mtstDist() {
        return m_mtst_dist;
    }

    // C++ setMtstDist
    public void setMtstDist(double joinCost) {
        m_mtst_dist = joinCost;
    }

    // C++ otherVert
    public VertInf otherVert(VertInf vert) {
        assert vert == m_vert1 || vert == m_vert2;
        return (vert == m_vert1) ? m_vert2 : m_vert1;
    }

    // C++ vert1 / vert2 accessors (not in C++ public API but needed)
    public VertInf vert1() { return m_vert1; }
    public VertInf vert2() { return m_vert2; }

    // C++ ids
    public VertID[] ids() {
        return new VertID[]{new VertID(m_vert1.id), new VertID(m_vert2.id)};
    }

    // C++ points
    public Point[] points() {
        return new Point[]{new Point(m_vert1.point), new Point(m_vert2.point)};
    }

    // C++ rotationLessThan
    public boolean rotationLessThan(VertInf lastV, EdgeInf rhs) {
        if (m_vert1 == rhs.m_vert1 && m_vert2 == rhs.m_vert2) {
            return false;
        }
        VertInf lhsV = null, rhsV = null, commonV = null;

        if (m_vert1 == rhs.m_vert1) {
            commonV = m_vert1; lhsV = m_vert2; rhsV = rhs.m_vert2;
        } else if (m_vert1 == rhs.m_vert2) {
            commonV = m_vert1; lhsV = m_vert2; rhsV = rhs.m_vert1;
        } else if (m_vert2 == rhs.m_vert1) {
            commonV = m_vert2; lhsV = m_vert1; rhsV = rhs.m_vert2;
        } else if (m_vert2 == rhs.m_vert2) {
            commonV = m_vert2; lhsV = m_vert1; rhsV = rhs.m_vert1;
        }

        assert lhsV != null;
        Point lhsPt = lhsV.point;
        Point rhsPt = rhsV.point;
        Point commonPt = commonV.point;

        Point lastPt = (lastV != null) ? lastV.point : new Point(commonPt.x - 10, commonPt.y);

        int lhsVal = orthogTurnOrder(lastPt, commonPt, lhsPt);
        int rhsVal = orthogTurnOrder(lastPt, commonPt, rhsPt);

        return lhsVal < rhsVal;
    }

    // C++ isBetween (package-private)
    boolean isBetween(VertInf i, VertInf j) {
        return ((i == m_vert1) && (j == m_vert2)) ||
               ((i == m_vert2) && (j == m_vert1));
    }

    // C++ makeActive
    void makeActive() {
        assert !m_added;

        if (m_orthogonal) {
            assert m_visible;
            m_router.visOrthogGraph.addEdge(this);
            m_vert1.orthogVisList.addFirst(this);
            m_vert1.orthogVisListSize++;
            m_vert2.orthogVisList.addFirst(this);
            m_vert2.orthogVisListSize++;
        } else {
            if (m_visible) {
                m_router.visGraph.addEdge(this);
                m_vert1.visList.addFirst(this);
                m_vert1.visListSize++;
                m_vert2.visList.addFirst(this);
                m_vert2.visListSize++;
            } else {
                m_router.invisGraph.addEdge(this);
                m_vert1.invisList.addFirst(this);
                m_vert1.invisListSize++;
                m_vert2.invisList.addFirst(this);
                m_vert2.invisListSize++;
            }
        }
        m_added = true;
    }

    // C++ makeInactive
    void makeInactive() {
        assert m_added;

        if (m_orthogonal) {
            assert m_visible;
            m_router.visOrthogGraph.removeEdge(this);
            m_vert1.orthogVisList.remove(this);
            m_vert1.orthogVisListSize--;
            m_vert2.orthogVisList.remove(this);
            m_vert2.orthogVisListSize--;
        } else {
            if (m_visible) {
                m_router.visGraph.removeEdge(this);
                m_vert1.visList.remove(this);
                m_vert1.visListSize--;
                m_vert2.visList.remove(this);
                m_vert2.visListSize--;
            } else {
                m_router.invisGraph.removeEdge(this);
                m_vert1.invisList.remove(this);
                m_vert1.invisListSize--;
                m_vert2.invisList.remove(this);
                m_vert2.invisListSize--;
            }
        }
        m_blocker = 0;
        m_conns.clear();
        m_added = false;
    }

    /**
     * Remove this edge from the graph entirely.
     * In C++ this is the destructor (~EdgeInf). In Java we call it explicitly.
     */
    public void remove() {
        if (m_added) {
            makeInactive();
        }
    }

    // C++ checkVis — translated from graph.cpp line 383
    public void checkVis() {
        int blocker = 0;
        boolean cone1 = true;
        boolean cone2 = true;

        VertInf i = m_vert1;
        VertInf j = m_vert2;
        VertID iID = i.id;
        VertID jID = j.id;
        Point iPoint = i.point;
        Point jPoint = j.point;

        m_router.st_checked_edges++;

        if (!iID.isConnPt()) {
            cone1 = Geometry.inValidRegion(m_router.IgnoreRegions, i.shPrev.point,
                    iPoint, i.shNext.point, jPoint);
        } else if (!m_router.IgnoreRegions) {
            // If Ignoring regions then this case is already caught by
            // the invalid regions, so only check it when not ignoring regions.
            java.util.Set<Integer> ss = m_router.contains.get(iID);
            if (ss != null && !jID.isConnPt() && ss.contains(jID.objID)) {
                // Don't even check this edge, it should be zero,
                // since a point in a shape can't see it's corners
                cone1 = false;
            }
        }

        if (cone1) {
            // If outside the first cone, don't even bother checking.
            if (!jID.isConnPt()) {
                cone2 = Geometry.inValidRegion(m_router.IgnoreRegions, j.shPrev.point,
                        jPoint, j.shNext.point, iPoint);
            } else if (!m_router.IgnoreRegions) {
                // If Ignoring regions then this case is already caught by
                // the invalid regions, so only check it when not ignoring regions.
                java.util.Set<Integer> ss = m_router.contains.get(jID);
                if (ss != null && !iID.isConnPt() && ss.contains(iID.objID)) {
                    // Don't even check this edge, it should be zero,
                    // since a point in a shape can't see it's corners
                    cone2 = false;
                }
            }
        }

        if (cone1 && cone2 && ((blocker = firstBlocker()) == 0)) {
            // if i and j see each other, add edge
            double d = Geometry.euclideanDist(iPoint, jPoint);
            setDist(d);
        } else if (m_router.InvisibilityGrph) {
            // if i and j can't see each other, add blank edge
            addBlocker(blocker);
        }
    }

    // C++ firstBlocker — translated from graph.cpp line 486
    int firstBlocker() {
        java.util.Set<Integer> ss = new java.util.HashSet<>();

        Point pti = m_vert1.point;
        Point ptj = m_vert2.point;
        VertID iID = m_vert1.id;
        VertID jID = m_vert2.id;

        java.util.Map<VertID, java.util.Set<Integer>> contains = m_router.contains;
        if (iID.isConnPt()) {
            java.util.Set<Integer> iContains = contains.get(iID);
            if (iContains != null) ss.addAll(iContains);
        }
        if (jID.isConnPt()) {
            java.util.Set<Integer> jContains = contains.get(jID);
            if (jContains != null) ss.addAll(jContains);
        }

        VertInf last = m_router.vertices.end();
        int lastId = 0;
        boolean[] seenIntersectionAtEndpoint = {false};
        for (VertInf k = m_router.vertices.shapesBegin(); k != last; ) {
            VertID kID = k.id;
            if (kID.equals(VertInf.dummyOrthogID)) {
                // Don't include orthogonal dummy vertices.
                k = k.lstNext;
                continue;
            }
            if (kID.objID != lastId) {
                if (ss.contains(kID.objID)) {
                    int shapeID = kID.objID;
                    // One of the endpoints is inside this shape so ignore it.
                    while (k != last && k.id.objID == shapeID) {
                        k = k.lstNext;
                    }
                    continue;
                }
                seenIntersectionAtEndpoint[0] = false;
                lastId = kID.objID;
            }
            Point kPoint = k.point;
            Point kPrevPoint = k.shPrev.point;
            if (Geometry.segmentShapeIntersect(pti, ptj, kPrevPoint, kPoint,
                    seenIntersectionAtEndpoint)) {
                ss.clear();
                return kID.objID;
            }
            k = k.lstNext;
        }
        ss.clear();
        return 0;
    }

    // C++ checkEdgeVisibility (static)
    public static EdgeInf checkEdgeVisibility(VertInf i, VertInf j, boolean knownNew) {
        assert !i.id.equals(VertInf.dummyOrthogID);
        assert !j.id.equals(VertInf.dummyOrthogID);

        Router router = i._router;
        EdgeInf edge;

        if (knownNew) {
            assert existingEdge(i, j) == null;
            edge = new EdgeInf(i, j);
        } else {
            edge = existingEdge(i, j);
            if (edge == null) {
                edge = new EdgeInf(i, j);
            }
        }
        edge.checkVis();
        if (!edge.m_added && !router.InvisibilityGrph) {
            edge = null;
        }

        return edge;
    }

    // C++ existingEdge (static)
    public static EdgeInf existingEdge(VertInf i, VertInf j) {
        // Look through poly-line visibility edges
        VertInf selected = (i.visListSize <= j.visListSize) ? i : j;
        for (EdgeInf edge : selected.visList) {
            if (edge.isBetween(i, j)) {
                return edge;
            }
        }

        // Look through orthogonal visibility edges
        selected = (i.orthogVisListSize <= j.orthogVisListSize) ? i : j;
        for (EdgeInf edge : selected.orthogVisList) {
            if (edge.isBetween(i, j)) {
                return edge;
            }
        }

        // Look through poly-line invisibility edges
        selected = (i.invisListSize <= j.invisListSize) ? i : j;
        for (EdgeInf edge : selected.invisList) {
            if (edge.isBetween(i, j)) {
                return edge;
            }
        }

        return null;
    }

    // --- Static helper matching C++ orthogTurnOrder ---
    private static int orthogTurnOrder(Point a, Point b, Point c) {
        if ((c.x != b.x && c.y != b.y) || (a.x != b.x && a.y != b.y)) {
            return 4; // Not orthogonally positioned
        }

        int direction = Geometry.vecDir(a, b, c);

        if (direction > 0) {
            return 1; // Counterclockwise (left)
        } else if (direction < 0) {
            return 2; // Clockwise (right)
        }

        if (b.x == c.x) {
            if ((a.y < b.y && c.y < b.y) || (a.y > b.y && c.y > b.y)) {
                return 0; // Behind
            }
        } else {
            if ((a.x < b.x && c.x < b.x) || (a.x > b.x && c.x > b.x)) {
                return 0; // Behind
            }
        }

        return 3; // Ahead
    }

    @Override
    public String toString() {
        return "EdgeInf(" + m_vert1.id + ", " + m_vert2.id + ")";
    }
}
