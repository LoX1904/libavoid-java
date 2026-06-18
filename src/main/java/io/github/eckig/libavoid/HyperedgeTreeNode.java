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
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a node in the hyperedge tree structure.
 * Translated from HyperedgeTreeNode in hyperedgetree.h/cpp.
 */
public class HyperedgeTreeNode {

    // Stable unique ID for use as a deterministic tiebreaker in TreeSet comparators.
    private static int s_next_id = 0;
    final int nodeId = s_next_id++;

    public List<HyperedgeTreeEdge> edges = new ArrayList<>();
    public JunctionRef junction;
    public Point point = new Point();
    public TreeSet<HyperedgeTreeNode> shiftSegmentNodeSet;
    public VertInf finalVertex;
    public boolean isConnectorSource;
    public boolean isPinDummyEndpoint;
    public boolean visited;

    public HyperedgeTreeNode() {
        this.junction = null;
        this.shiftSegmentNodeSet = null;
        this.finalVertex = null;
        this.isConnectorSource = false;
        this.isPinDummyEndpoint = false;
        this.visited = false;
    }

    // Traverses the tree and removes from treeRoots any junction nodes
    // (other than this one).
    public boolean removeOtherJunctionsFrom(HyperedgeTreeEdge ignored,
            Set<JunctionRef> treeRoots) {
        boolean containsCycle = false;
        if (visited) {
            containsCycle = true;
            return containsCycle;
        }
        if (junction != null && ignored != null) {
            treeRoots.remove(junction);
        }
        visited = true;
        for (HyperedgeTreeEdge edge : new ArrayList<>(edges)) {
            if (edge != ignored) {
                containsCycle |= edge.removeOtherJunctionsFrom(this, treeRoots);
            }
        }
        return containsCycle;
    }

    // Traverses the tree and writes each of the paths back to
    // the individual connectors as routes.
    public void writeEdgesToConns(HyperedgeTreeEdge ignored, int pass) {
        for (HyperedgeTreeEdge edge : new ArrayList<>(edges)) {
            if (edge != ignored) {
                edge.writeEdgesToConns(this, pass);
            }
        }
    }

    // Traverses the tree and creates connectors for each segment
    // bridging junctions/terminals. Also sets appropriate ConnEnds.
    public void addConns(HyperedgeTreeEdge ignored, Router router,
            List<ConnRef> oldConns, ConnRef conn) {
        assert conn != null || junction != null;

        for (HyperedgeTreeEdge edge : new ArrayList<>(edges)) {
            if (edge != ignored) {
                if (junction != null) {
                    conn = new ConnRef(router);
                    router.removeObjectFromQueuedActions(conn);
                    conn.makeActive();
                    conn.m_initialised = true;
                    ConnEnd connend = new ConnEnd(junction);
                    conn.updateEndPoint(VertID.src, connend);
                }
                edge.conn = conn;
                edge.addConns(this, router, oldConns);
            }
        }
    }

    // Traverses the tree and rewrites connector ends that may have changed
    // junctions due to major hyperedge improvement.
    public void updateConnEnds(HyperedgeTreeEdge ignored, boolean forward,
            List<ConnRef> changedConns) {
        for (HyperedgeTreeEdge edge : new ArrayList<>(edges)) {
            if (edge != ignored) {
                if (junction != null) {
                    forward = travellingForwardOnConnector(edge.conn, junction);

                    ConnEnd[] existingEnds = edge.conn.endpointConnEndsArray();
                    ConnEnd existingEnd = forward ? existingEnds[0] : existingEnds[1];
                    if (existingEnd.junction() != junction) {
                        int end = forward ? VertID.src : VertID.tar;
                        ConnEnd connend = new ConnEnd(junction);
                        edge.conn.updateEndPoint(end, connend);
                        changedConns.add(edge.conn);
                    }
                }
                edge.updateConnEnds(this, forward, changedConns);
            }
        }
    }

    static boolean travellingForwardOnConnector(ConnRef conn, JunctionRef junction) {
        ConnEnd[] connEnds = conn.endpointConnEndsArray();
        if (connEnds[0] != null && connEnds[0].junction() == junction) {
            return true;
        }
        if (connEnds[1] != null && connEnds[1].junction() == junction) {
            return false;
        }
        if (connEnds[0] != null && connEnds[0].type() != ConnEndType.ConnEndJunction &&
                connEnds[0].type() != ConnEndType.ConnEndEmpty) {
            return false;
        }
        if (connEnds[1] != null && connEnds[1].type() != ConnEndType.ConnEndJunction &&
                connEnds[1].type() != ConnEndType.ConnEndEmpty) {
            return true;
        }
        return true;
    }

    // Returns a list of junctions and connectors that make up the hyperedge.
    public void listJunctionsAndConnectors(HyperedgeTreeEdge ignored,
            List<JunctionRef> junctions, List<ConnRef> connectors) {
        if (junction != null) {
            junctions.add(junction);
        }
        for (HyperedgeTreeEdge edge : new ArrayList<>(edges)) {
            if (edge != ignored) {
                edge.listJunctionsAndConnectors(this, junctions, connectors);
            }
        }
    }

    public void validateHyperedge(HyperedgeTreeEdge ignored, int dist) {
        // Debug validation — no-op in Java
        for (HyperedgeTreeEdge edge : new ArrayList<>(edges)) {
            if (edge != ignored) {
                edge.validateHyperedge(this, dist);
            }
        }
    }

    // Clears up the tree.
    public void deleteEdgesExcept(HyperedgeTreeEdge ignored) {
        for (HyperedgeTreeEdge edge : new ArrayList<>(edges)) {
            if (edge != ignored) {
                edge.deleteNodesExcept(this);
            }
        }
        edges.clear();
    }

    // Disconnects a specific edge from this node.
    public void disconnectEdge(HyperedgeTreeEdge edge) {
        edges.removeIf(e -> e == edge);
    }

    // Moves all edges attached to oldNode to be attached to this node.
    public void spliceEdgesFrom(HyperedgeTreeNode oldNode) {
        assert oldNode != this;
        while (!oldNode.edges.isEmpty()) {
            HyperedgeTreeEdge edge = oldNode.edges.getFirst();
            edge.replaceNode(oldNode, this);
        }
    }

    public boolean isImmovable() {
        if (edges.size() == 1 || (junction != null && junction.positionFixed())) {
            return true;
        }
        for (HyperedgeTreeEdge edge : edges) {
            if (edge.hasFixedRoute) {
                return true;
            }
        }
        return false;
    }
}
