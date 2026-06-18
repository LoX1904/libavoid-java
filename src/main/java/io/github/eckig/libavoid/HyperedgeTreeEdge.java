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

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Represents an edge in the hyperedge tree structure.
 * Translated from HyperedgeTreeEdge in hyperedgetree.h/cpp.
 */
public class HyperedgeTreeEdge {

    public HyperedgeTreeNode[] ends = new HyperedgeTreeNode[2];
    public ConnRef conn;
    public boolean hasFixedRoute;

    public HyperedgeTreeEdge(HyperedgeTreeNode node1, HyperedgeTreeNode node2,
            ConnRef conn) {
        this.conn = conn;
        this.hasFixedRoute = false;
        if (conn != null) {
            this.hasFixedRoute = conn.hasFixedRoute();
        }
        ends[0] = node1;
        ends[1] = node2;
        node1.edges.add(this);
        node2.edges.add(this);
    }

    // Given one endpoint, returns the other.
    public HyperedgeTreeNode followFrom(HyperedgeTreeNode from) {
        return (ends[0] == from) ? ends[1] : ends[0];
    }

    // Returns true if both endpoints are at the same position.
    public boolean zeroLength() {
        return ends[0].point.equals(ends[1].point);
    }

    // Returns true if the edge is aligned in the given dimension.
    public boolean hasOrientation(int dimension) {
        return ends[0].point.get(dimension) == ends[1].point.get(dimension);
    }

    // Updates any endpoints attached to oldNode to attach to newNode instead.
    public void replaceNode(HyperedgeTreeNode oldNode, HyperedgeTreeNode newNode) {
        if (ends[0] == oldNode) {
            oldNode.disconnectEdge(this);
            newNode.edges.add(this);
            ends[0] = newNode;
        } else if (ends[1] == oldNode) {
            oldNode.disconnectEdge(this);
            newNode.edges.add(this);
            ends[1] = newNode;
        }
    }

    // Writes paths back to individual connectors as routes.
    public void writeEdgesToConns(HyperedgeTreeNode ignored, int pass) {
        assert ignored != null;
        assert ends[0] != null;
        assert ends[1] != null;

        HyperedgeTreeNode prevNode = (ignored == ends[0]) ? ends[0] : ends[1];
        HyperedgeTreeNode nextNode = (ignored == ends[0]) ? ends[1] : ends[0];

        if (pass == 0) {
            conn.m_display_route.clear();
        } else if (pass == 1) {
            if (conn.m_display_route.empty()) {
                conn.m_display_route.ps.add(prevNode.point);
            }
            conn.m_display_route.ps.add(nextNode.point);

            int nextNodeEdges = nextNode.edges.size();
            if (nextNodeEdges != 2) {
                // We have finished writing a connector.
                boolean shouldReverse = false;
                if (nextNodeEdges == 1) {
                    // This connector led to a terminal.
                    if (nextNode.isConnectorSource) {
                        shouldReverse = true;
                    }
                    if (nextNode.isPinDummyEndpoint) {
                        // Remove extra segments leading to centre dummy pin.
                        conn.m_display_route.ps.removeLast();
                        if (prevNode.point.equals(nextNode.point)) {
                            conn.m_display_route.ps.removeLast();
                        }
                    }
                } else { // nextNodeEdges > 2
                    // This connector was between two junctions.
                    ConnEnd dstConnEnd = conn.m_dst_connend;
                    if (dstConnEnd != null) {
                        JunctionRef correctEndJunction = dstConnEnd.junction();
                        if (nextNode.junction != correctEndJunction) {
                            shouldReverse = true;
                        }
                    }
                }

                if (shouldReverse) {
                    Collections.reverse(conn.m_display_route.ps);
                }
            }
        }

        nextNode.writeEdgesToConns(this, pass);
    }

    // Creates connectors for each segment bridging junctions/terminals.
    public void addConns(HyperedgeTreeNode ignored, Router router,
            List<ConnRef> oldConns) {
        assert conn != null;
        HyperedgeTreeNode endNode = null;
        if (ends[0] != null && ends[0] != ignored) {
            endNode = ends[0];
            ends[0].addConns(this, router, oldConns, conn);
        }
        if (ends[1] != null && ends[1] != ignored) {
            endNode = ends[1];
            ends[1].addConns(this, router, oldConns, conn);
        }

        if (endNode.finalVertex != null) {
            // Reached a terminal — find ConnEnd from original connectors.
            ConnEnd connend = null;
            for (ConnRef oldConn : oldConns) {
                connend = oldConn.getConnEndForEndpointVertex(endNode.finalVertex);
                if (connend != null) {
                    break;
                }
            }
            if (connend != null) {
                conn.updateEndPoint(VertID.tar, connend);
            }
        } else if (endNode.junction != null) {
            ConnEnd connend = new ConnEnd(endNode.junction);
            conn.updateEndPoint(VertID.tar, connend);
        }
    }

    // Rewrites connector ends that may have changed junctions.
    public void updateConnEnds(HyperedgeTreeNode ignored, boolean forward,
            List<ConnRef> changedConns) {
        HyperedgeTreeNode endNode = null;
        if (ends[0] != null && ends[0] != ignored) {
            endNode = ends[0];
            ends[0].updateConnEnds(this, forward, changedConns);
        }
        if (ends[1] != null && ends[1] != ignored) {
            endNode = ends[1];
            ends[1].updateConnEnds(this, forward, changedConns);
        }

        if (endNode != null && endNode.junction != null) {
            ConnEnd[] existingEnds = conn.endpointConnEndsArray();
            ConnEnd existingEnd = forward ? existingEnds[1] : existingEnds[0];
            if (existingEnd != null && existingEnd.junction() != endNode.junction) {
                ConnEnd connend = new ConnEnd(endNode.junction);
                int end = forward ? VertID.tar : VertID.src;
                conn.updateEndPoint(end, connend);
                if (changedConns.isEmpty() || changedConns.getLast() != conn) {
                    changedConns.add(conn);
                }
            }
        }
    }

    // Returns a list of junctions and connectors in the hyperedge.
    public void listJunctionsAndConnectors(HyperedgeTreeNode ignored,
            List<JunctionRef> junctions, List<ConnRef> connectors) {
        if (!connectors.contains(conn)) {
            connectors.add(conn);
        }
        if (ends[0] != ignored) {
            ends[0].listJunctionsAndConnectors(this, junctions, connectors);
        } else if (ends[1] != ignored) {
            ends[1].listJunctionsAndConnectors(this, junctions, connectors);
        }
    }

    public void validateHyperedge(HyperedgeTreeNode ignored, int dist) {
        if (ends[0] != ignored) {
            ends[0].validateHyperedge(this, dist);
        } else if (ends[1] != ignored) {
            ends[1].validateHyperedge(this, dist);
        }
    }

    // Splits the current edge, adding a node at the given point.
    public void splitFromNodeAtPoint(HyperedgeTreeNode source, Point point) {
        if (ends[1] == source) {
            HyperedgeTreeNode tmp = ends[0];
            ends[0] = ends[1];
            ends[1] = tmp;
        }
        assert ends[0] == source;

        HyperedgeTreeNode target = ends[1];

        HyperedgeTreeNode split = new HyperedgeTreeNode();
        split.point = point;

        new HyperedgeTreeEdge(split, target, conn);

        target.disconnectEdge(this);
        ends[1] = split;
        split.edges.add(this);
    }

    // Disconnects this edge from both its nodes.
    public void disconnectEdge() {
        assert ends[0] != null;
        assert ends[1] != null;
        ends[0].disconnectEdge(this);
        ends[1].disconnectEdge(this);
        ends[0] = null;
        ends[1] = null;
    }

    // Traverses the tree and removes junction nodes from treeRoots.
    public boolean removeOtherJunctionsFrom(HyperedgeTreeNode ignored,
            Set<JunctionRef> treeRoots) {
        boolean containsCycle = false;
        if (ends[0] != null && ends[0] != ignored) {
            containsCycle |= ends[0].removeOtherJunctionsFrom(this, treeRoots);
        }
        if (ends[1] != null && ends[1] != ignored) {
            containsCycle |= ends[1].removeOtherJunctionsFrom(this, treeRoots);
        }
        return containsCycle;
    }

    // Clears up tree objects and memory.
    public void deleteNodesExcept(HyperedgeTreeNode ignored) {
        if (ends[0] != null && ends[0] != ignored) {
            ends[0].deleteEdgesExcept(this);
        }
        ends[0] = null;
        if (ends[1] != null && ends[1] != ignored) {
            ends[1].deleteEdgesExcept(this);
        }
        ends[1] = null;
    }
}
