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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * PtOrder maintains ordering information for connector points at a given location.
 * Used during nudging to determine the correct relative order of overlapping segments.
 *
 * Translated from connector.h/connector.cpp PtOrder class in libavoid C++.
 * typedef std::pair<Point *, ConnRef *> PtConnPtrPair;
 * typedef std::map<Avoid::Point, PtOrder> PtOrderMap;
 */
public class PtOrder {

    // One for each dimension.
    private final boolean[] sorted = new boolean[2];
    private final List<Pair<Point,ConnRef>>[] nodes;
    private final List<int[]>[] links; // pairs of (from, to) indices
    private final List<Pair<Point,ConnRef>>[] sortedConnVector;

    @SuppressWarnings("unchecked")
    public PtOrder() {
        nodes = new List[2];
        links = new List[2];
        sortedConnVector = new List[2];
        for (int dim = 0; dim < 2; ++dim) {
            nodes[dim] = new ArrayList<>();
            links[dim] = new ArrayList<>();
            sortedConnVector[dim] = new ArrayList<>();
        }
    }

    /**
     * Returns the position index of the given connector in the sorted order
     * for the given dimension, or -1 if not found.
     */
    public int positionFor(int dim, ConnRef conn) {
        if (!sorted[dim]) {
            sort(dim);
        }
        for (int i = 0; i < sortedConnVector[dim].size(); ++i) {
            if (sortedConnVector[dim].get(i).second == conn) {
                return i;
            }
        }
        return -1;
    }

    private int insertPoint(int dim, Pair<Point, ConnRef> pointPair) {
        for (int i = 0; i < nodes[dim].size(); ++i) {
            if (nodes[dim].get(i).second == pointPair.second) {
                return i;
            }
        }
        nodes[dim].add(pointPair);
        return nodes[dim].size() - 1;
    }

    public void addOrderedPoints(int dim, Pair<Point, ConnRef> innerArg,
            Pair<Point, ConnRef> outerArg, boolean swapped) {
        var inner = swapped ? outerArg : innerArg;
        var outer = swapped ? innerArg : outerArg;

        int innerIndex = insertPoint(dim, inner);
        int outerIndex = insertPoint(dim, outer);

        // Edge: outer -> inner (outer comes before inner in the order)
        links[dim].add(new int[]{outerIndex, innerIndex});
    }

    /**
     * Topological sort of the points using the edge ordering info.
     */
    private void sort(int dim) {
        sorted[dim] = true;
        sortedConnVector[dim].clear();

        int n = nodes[dim].size();
        if (n == 0) return;

        // Build adjacency matrix
        boolean[][] adjacencyMatrix = new boolean[n][n];
        for (int[] link : links[dim]) {
            adjacencyMatrix[link[0]][link[1]] = true;
        }

        // Build incoming degree and add nodes with no incoming edges to queue
        int[] incomingDegree = new int[n];
        Queue<Integer> queue = new LinkedList<>();

        for (int i = 0; i < n; ++i) {
            int degree = 0;
            for (int j = 0; j < n; ++j) {
                if (adjacencyMatrix[j][i]) {
                    degree++;
                }
            }
            incomingDegree[i] = degree;
            if (degree == 0) {
                queue.add(i);
            }
        }

        while (!queue.isEmpty()) {
            int k = queue.poll();
            sortedConnVector[dim].add(nodes[dim].get(k));

            // Remove all edges leaving node k
            for (int i = 0; i < n; ++i) {
                if (adjacencyMatrix[k][i]) {
                    adjacencyMatrix[k][i] = false;
                    incomingDegree[i]--;
                    if (incomingDegree[i] == 0) {
                        queue.add(i);
                    }
                }
            }
        }
    }
}
