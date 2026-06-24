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

import io.github.eckig.libavoid.Box;
import io.github.eckig.libavoid.Obstacle;
import io.github.eckig.libavoid.Point;
import io.github.eckig.libavoid.VertInf;

/**
 * A node in the scanline, representing either an obstacle, a connector point,
 * or a shift segment.
 * Translated from scanline.h/scanline.cpp in libavoid C++.
 */
public class Node {

    public Obstacle v;      // obstacle (shape/junction)
    public VertInf c;        // connector point VertInf (for ConnPoint events)
    public ShiftSegment ss;  // shift segment (for nudging)
    public double pos;
    public double[] min = new double[2];
    public double[] max = new double[2];
    public Node firstAbove;
    public Node firstBelow;

    // Unique ID for tie-breaking in comparisons (replaces pointer comparison in C++)
    private static int nextId = 0;
    public final int nodeId;

    public Node(Obstacle v, double p) {
        this.nodeId = nextId++;
        this.v = v;
        this.ss = null;
        this.pos = p;
        this.firstAbove = null;
        this.firstBelow = null;

        Box bBox = v.routingBox();
        min[Point.XDIM] = bBox.min.x;
        min[Point.YDIM] = bBox.min.y;
        max[Point.XDIM] = bBox.max.x;
        max[Point.YDIM] = bBox.max.y;
    }

    /** Constructor for ConnPoint events (VertInf-based). */
    public Node(VertInf c, double p) {
        this.nodeId = nextId++;
        this.v = null;
        this.c = c;
        this.ss = null;
        this.pos = p;
        this.firstAbove = null;
        this.firstBelow = null;
        min[Point.XDIM] = max[Point.XDIM] = c.point.x;
        min[Point.YDIM] = max[Point.YDIM] = c.point.y;
    }

    public Node(ShiftSegment ss, double p) {
        this.nodeId = nextId++;
        this.v = null;
        this.ss = ss;
        this.pos = p;
        this.firstAbove = null;
        this.firstBelow = null;
        // These values shouldn't ever be used, so they don't matter.
        min[Point.XDIM] = max[Point.XDIM] = min[Point.YDIM] = max[Point.YDIM] = 0;
    }

    /**
     * Find the first Node above in the scanline that is a shape edge,
     * and does not have an open or close event at this position.
     */
    public double firstObstacleAbove(int dim) {
        Node curr = firstAbove;
        while (curr != null && (curr.ss != null || (curr.max[dim] > pos))) {
            curr = curr.firstAbove;
        }
        if (curr != null) {
            return curr.max[dim];
        }
        return -Double.MAX_VALUE;
    }

    /**
     * Find the first Node below in the scanline that is a shape edge,
     * and does not have an open or close event at this position.
     */
    public double firstObstacleBelow(int dim) {
        Node curr = firstBelow;
        while (curr != null && (curr.ss != null || (curr.min[dim] < pos))) {
            curr = curr.firstBelow;
        }
        if (curr != null) {
            return curr.min[dim];
        }
        return Double.MAX_VALUE;
    }

    /**
     * Mark all connector segments above in the scanline as being able
     * to see to this shape edge.
     */
    public void markShiftSegmentsAbove(int dim) {
        Node curr = firstAbove;
        while (curr != null && (curr.ss != null || (curr.pos > min[dim]))) {
            if (curr.ss != null && (curr.pos <= min[dim])) {
                curr.ss.maxSpaceLimit = Math.min(min[dim], curr.ss.maxSpaceLimit);
            }
            curr = curr.firstAbove;
        }
    }

    /**
     * Mark all connector segments below in the scanline as being able
     * to see to this shape edge.
     */
    public void markShiftSegmentsBelow(int dim) {
        Node curr = firstBelow;
        while (curr != null && (curr.ss != null || (curr.pos < max[dim]))) {
            if (curr.ss != null && (curr.pos >= max[dim])) {
                curr.ss.minSpaceLimit = Math.max(max[dim], curr.ss.minSpaceLimit);
            }
            curr = curr.firstBelow;
        }
    }

    /**
     * Overload that writes into a 4-element array:
     * [0]=minLimit, [1]=maxLimit, [2]=minLimitMax, [3]=maxLimitMin.
     */
    public void findFirstPointAboveAndBelow(int dim, double linePos, double[] limits) {
        double[] fa = new double[1], fb = new double[1], la = new double[1], lb = new double[1];
        findFirstPointAboveAndBelow(dim, linePos, fa, fb, la, lb);
        limits[0] = fa[0];
        limits[1] = fb[0];
        limits[2] = la[0];
        limits[3] = lb[0];
    }

    /**
     * Find the first point above and below this node in the alt dimension.
     */
    public void findFirstPointAboveAndBelow(int dim, double linePos,
            double[] firstAbovePos, double[] firstBelowPos,
            double[] lastAbovePos, double[] lastBelowPos) {
        firstAbovePos[0] = -Double.MAX_VALUE;
        firstBelowPos[0] = Double.MAX_VALUE;
        lastAbovePos[0] = max[dim];
        lastBelowPos[0] = min[dim];

        int notDim = (dim == 0) ? 1 : 0;
        for (int direction = 0; direction < 2; ++direction) {
            Node curr = (direction == 0) ? firstAbove : firstBelow;
            while (curr != null) {
                boolean eventsAtSamePos =
                    ((linePos == max[notDim]) && (linePos == curr.max[notDim])) ||
                    ((linePos == min[notDim]) && (linePos == curr.min[notDim]));

                if (curr.max[dim] <= min[dim]) {
                    firstAbovePos[0] = Math.max(curr.max[dim], firstAbovePos[0]);
                } else if (curr.min[dim] >= max[dim]) {
                    firstBelowPos[0] = Math.min(curr.min[dim], firstBelowPos[0]);
                } else if (!eventsAtSamePos) {
                    lastAbovePos[0] = Math.min(curr.min[dim], lastAbovePos[0]);
                    lastBelowPos[0] = Math.max(curr.max[dim], lastBelowPos[0]);
                }
                curr = (direction == 0) ? curr.firstAbove : curr.firstBelow;
            }
        }
    }

    public double firstPointAbove(int dim) {
        int altDim = (dim + 1) % 2;
        double result = -Double.MAX_VALUE;
        Node curr = firstAbove;
        while (curr != null) {
            boolean inLineWithEdge = (min[altDim] == curr.min[altDim]) ||
                    (min[altDim] == curr.max[altDim]);
            if (!inLineWithEdge && (curr.max[dim] <= pos)) {
                result = Math.max(curr.max[dim], result);
            }
            curr = curr.firstAbove;
        }
        return result;
    }

    public double firstPointBelow(int dim) {
        int altDim = (dim + 1) % 2;
        double result = Double.MAX_VALUE;
        Node curr = firstBelow;
        while (curr != null) {
            boolean inLineWithEdge = (min[altDim] == curr.min[altDim]) ||
                    (min[altDim] == curr.max[altDim]);
            if (!inLineWithEdge && (curr.min[dim] >= pos)) {
                result = Math.min(curr.min[dim], result);
            }
            curr = curr.firstBelow;
        }
        return result;
    }

    public boolean isInsideShape(int dimension) {
        for (Node curr = firstBelow; curr != null; curr = curr.firstBelow) {
            if ((curr.min[dimension] < pos) && (pos < curr.max[dimension])) {
                return true;
            }
        }
        for (Node curr = firstAbove; curr != null; curr = curr.firstAbove) {
            if ((curr.min[dimension] < pos) && (pos < curr.max[dimension])) {
                return true;
            }
        }
        return false;
    }
}
