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

import io.github.eckig.libavoid.scanline.Scanline;
import io.github.eckig.libavoid.scanline.ShiftSegment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Translates HyperedgeImprover from hyperedgeimprover.h/cpp.
 * Performs local improvement of hyperedge routing by building hyperedge trees,
 * removing zero-length edges, moving junctions to divergence points,The vis-graph related ones are less critical since Java doesn't use a full vis graph. Let me add the important ones:
 * building shift segments, nudging, and writing paths back to connectors.
 */
public class HyperedgeImprover {

    private Router m_router;
    private boolean m_can_make_major_changes;

    // Maps from JunctionRef to its HyperedgeTreeNode in the tree.
    // C++: JunctionHyperedgeTreeNodeMap m_hyperedge_tree_junctions;
    private final Map<JunctionRef, HyperedgeTreeNode> m_hyperedge_tree_junctions = new HashMap<>();

    // Roots of hyperedge trees (one root per connected tree).
    // C++: JunctionSet m_hyperedge_tree_roots;
    private final Set<JunctionRef> m_hyperedge_tree_roots = new LinkedHashSet<>();

    // Per-root shift segments for nudging.
    // C++: RootSegmentsMap m_root_shift_segments;
    private final Map<JunctionRef, List<ShiftSegment>> m_root_shift_segments = new LinkedHashMap<>();

    // All shift segments across all roots (for buildOrthogonalChannelInfo).
    // C++: ShiftSegmentList m_all_shift_segments;
    private final List<ShiftSegment> m_all_shift_segments = new ArrayList<>();

    // Tracking new/deleted/changed objects.
    List<JunctionRef> m_new_junctions = new ArrayList<>();
    List<ConnRef> m_new_connectors = new ArrayList<>();
    List<JunctionRef> m_deleted_junctions = new ArrayList<>();
    List<ConnRef> m_deleted_connectors = new ArrayList<>();
    List<ConnRef> m_changed_connectors = new ArrayList<>();

    public HyperedgeImprover() {
        clear();
    }

    public void clear() {
        m_hyperedge_tree_junctions.clear();
        m_hyperedge_tree_roots.clear();
        m_root_shift_segments.clear();
        m_all_shift_segments.clear();
        m_new_junctions.clear();
        m_new_connectors.clear();
        m_deleted_junctions.clear();
        m_deleted_connectors.clear();
        m_changed_connectors.clear();
    }

    public void setRouter(Router router) {
        m_router = router;
    }

    public HyperedgeNewAndDeletedObjectLists newAndDeletedObjectLists() {
        HyperedgeNewAndDeletedObjectLists result = new HyperedgeNewAndDeletedObjectLists();
        result.newJunctionList.addAll(m_new_junctions);
        result.newConnectorList.addAll(m_new_connectors);
        result.deletedJunctionList.addAll(m_deleted_junctions);
        result.deletedConnectorList.addAll(m_deleted_connectors);
        result.changedConnectorList.addAll(m_changed_connectors);
        return result;
    }

    /**
     * Execute local improvement process.
     * Translated from HyperedgeImprover::execute() in hyperedgeimprover.cpp lines 787-1023.
     */
    public void execute(boolean canMakeMajorChanges) {
        m_can_make_major_changes = canMakeMajorChanges;

        // Build Hyperedge trees.
        // Translated from hyperedgeimprover.cpp lines 792-887.
        for (ConnRef connRef : new ArrayList<>(m_router.m_connectors)) {
            JunctionRef jFront = null;
            JunctionRef jBack = null;

            if (connRef.m_src_connend != null) {
                jFront = connRef.m_src_connend.junction();
            }
            if (connRef.m_dst_connend != null) {
                jBack = connRef.m_dst_connend.junction();
            }

            if (jFront == null && jBack == null) {
                continue;
            }

            boolean seenFront = m_hyperedge_tree_junctions.containsKey(jFront);
            boolean seenBack = m_hyperedge_tree_junctions.containsKey(jBack);

            HyperedgeTreeNode nodeFront;
            HyperedgeTreeNode nodeBack;

            if (jFront != null) {
                if (!seenFront) {
                    nodeFront = new HyperedgeTreeNode();
                    nodeFront.point = jFront.position();
                    nodeFront.junction = jFront;
                    m_hyperedge_tree_junctions.put(jFront, nodeFront);
                } else {
                    nodeFront = m_hyperedge_tree_junctions.get(jFront);
                }
            } else {
                nodeFront = new HyperedgeTreeNode();
            }

            if (jBack != null) {
                if (!seenBack) {
                    nodeBack = new HyperedgeTreeNode();
                    nodeBack.point = jBack.position();
                    nodeBack.junction = jBack;
                    m_hyperedge_tree_junctions.put(jBack, nodeBack);
                } else {
                    nodeBack = m_hyperedge_tree_junctions.get(jBack);
                }
            } else {
                nodeBack = new HyperedgeTreeNode();
            }

            // Build tree from display route points.
            Polygon route = connRef.displayRoute();
            HyperedgeTreeNode prev = null;
            for (int i = 1; i < route.size(); ++i) {
                HyperedgeTreeNode node;
                if (i + 1 == route.size()) {
                    node = nodeBack;
                } else {
                    node = new HyperedgeTreeNode();
                }
                node.point = route.ps.get(i);
                if (i == 1) {
                    prev = nodeFront;
                    nodeFront.point = route.ps.getFirst();
                    nodeFront.isConnectorSource = true;
                }
                new HyperedgeTreeEdge(prev, node, connRef);
                prev = node;
            }
        }

        // Make a list that contains a single junction from each tree.
        // C++ lines 890-896: add all junctions as potential roots.
        for (Map.Entry<JunctionRef, HyperedgeTreeNode> entry :
                m_hyperedge_tree_junctions.entrySet()) {
            m_hyperedge_tree_roots.add(entry.getValue().junction);
        }
        // C++ lines 897-918: remove non-roots and detect cycles.
        // In C++, std::set iteration is safe even when elements are erased
        // during traversal (other elements' iterators remain valid).
        // In Java, we use a snapshot but skip roots already removed by earlier iterations.
        List<JunctionRef> cyclicHyperedgeTreeRoots = new ArrayList<>();
        for (JunctionRef root : new ArrayList<>(m_hyperedge_tree_roots)) {
            if (!m_hyperedge_tree_roots.contains(root)) {
                // Already removed by a previous root's traversal — skip.
                continue;
            }
            HyperedgeTreeNode node = m_hyperedge_tree_junctions.get(root);
            boolean containsCycle = node.removeOtherJunctionsFrom(null,
                    m_hyperedge_tree_roots);
            if (containsCycle) {
                cyclicHyperedgeTreeRoots.add(node.junction);
            }
        }
        // Remove roots of cyclic hyperedges, we can't improve these.
        for (JunctionRef junction : cyclicHyperedgeTreeRoots) {
            m_hyperedge_tree_roots.remove(junction);
        }

        // Remove zero length edges.
        removeZeroLengthEdges();

        // Move junctions to divergence points.
        moveJunctionsAlongCommonEdges();

        // C++ lines 935-965: 4 iterations alternating dimensions.
        for (int count = 0; count < 4; ++count) {
            int dimension = count % 2;
            // Build shift segments.
            buildHyperedgeSegments(dimension);
            // Calculate channel information for this dimension.
            Scanline.buildOrthogonalChannelInfo(m_router, dimension, m_all_shift_segments);
            // Nudge hyperedge segments to locally improve the route.
            nudgeHyperedgeSegments();
            // Remove resulting zero length edges.
            removeZeroLengthEdges();
            // Move junctions to divergence points.
            moveJunctionsAlongCommonEdges();

            // Clean up shift segments.
            m_root_shift_segments.clear();
            m_all_shift_segments.clear();
        }

        // Rewrite updated connector attachments to junctions.
        if (m_can_make_major_changes) {
            for (JunctionRef root : new ArrayList<>(m_hyperedge_tree_roots)) {
                HyperedgeTreeNode treeRoot = m_hyperedge_tree_junctions.get(root);
                if (treeRoot != null) {
                    treeRoot.updateConnEnds(null, true, m_changed_connectors);
                    treeRoot.validateHyperedge(null, 0);
                }
            }
        }

        // Write back final recommended positions to junctions.
        for (Map.Entry<JunctionRef, HyperedgeTreeNode> entry :
                m_hyperedge_tree_junctions.entrySet()) {
            HyperedgeTreeNode node = entry.getValue();
            node.junction.setRecommendedPosition(node.point);
        }

        // Write paths from the hyperedge tree back into individual connector routes.
        writeHyperedgeSegmentsBackToConnPaths();

        // Free HyperedgeTree structure.
        // C++ hyperedgeimprover.cpp:997-1004
        for (JunctionRef root : m_hyperedge_tree_roots) {
            HyperedgeTreeNode node = m_hyperedge_tree_junctions.get(root);
            if (node != null) {
                node.deleteEdgesExcept(null);
            }
        }

        // Tell the router that we are deleting the objects used for the
        // previous path for the hyperedge.
        for (ConnRef conn : m_deleted_connectors) {
            conn.assignConnectionPinVisibility(false);
            m_router.deleteConnector(conn);
        }
        for (JunctionRef junction : m_deleted_junctions) {
            m_router.deleteJunction(junction);
        }
    }

    /**
     * Removes zero-length edges from all trees.
     * C++: void HyperedgeImprover::removeZeroLengthEdges(void) lines 398-407
     */
    private void removeZeroLengthEdges() {
        for (JunctionRef root : new ArrayList<>(m_hyperedge_tree_roots)) {
            HyperedgeTreeNode node = m_hyperedge_tree_junctions.get(root);
            if (node != null) {
                removeZeroLengthEdges(node, null);
            }
        }
    }

    /**
     * Traverses the hyperedge tree removing zero length edges (edge overload).
     * Translated from HyperedgeImprover::removeZeroLengthEdges(HyperedgeTreeEdge*, HyperedgeTreeNode*)
     * in hyperedgeimprover.cpp lines 448-460.
     */
    private void removeZeroLengthEdges(HyperedgeTreeEdge self, HyperedgeTreeNode ignored) {
        if (self.ends[0] != ignored) {
            removeZeroLengthEdges(self.ends[0], self);
        }
        if (self.ends[1] != ignored) {
            removeZeroLengthEdges(self.ends[1], self);
        }
    }

    /**
     * Traverses the hyperedge tree removing zero length edges (node overload).
     * Translated from HyperedgeImprover::removeZeroLengthEdges(HyperedgeTreeNode*, HyperedgeTreeEdge*)
     * in hyperedgeimprover.cpp lines 465-547.
     */
    private void removeZeroLengthEdges(HyperedgeTreeNode self, HyperedgeTreeEdge ignored) {
        for (HyperedgeTreeEdge edge : new ArrayList<>(self.edges)) {
            if (edge == ignored) {
                continue;
            }

            if (!edge.hasFixedRoute && edge.zeroLength()) {
                HyperedgeTreeNode other = edge.followFrom(self);
                HyperedgeTreeNode target = null;
                HyperedgeTreeNode source = null;

                if (other.junction != null && self.junction == null) {
                    target = other;
                    source = self;
                } else if (self.junction != null && other.junction == null) {
                    target = self;
                    source = other;
                } else if (other.junction == null) {
                    target = self;
                    source = other;
                } else if (m_can_make_major_changes) {
                    // Only delete junctions if we can make major changes.

                    // Delete one of the junctions.
                    m_deleted_junctions.add(other.junction);
                    m_hyperedge_tree_junctions.remove(other.junction);
                    if (m_hyperedge_tree_roots.contains(other.junction)) {
                        // If 'other' was the root for the hyperedge, we need
                        // to make 'self' the new root.
                        m_hyperedge_tree_roots.remove(other.junction);
                        m_hyperedge_tree_roots.add(self.junction);
                        // It should already be in the junctions list.
                        assert m_hyperedge_tree_junctions.containsKey(self.junction);
                    }
                    other.junction = null;

                    // Delete the connector on the zero length edge.
                    m_deleted_connectors.add(edge.conn);
                    edge.conn = null;

                    target = self;
                    source = other;
                }

                if (target != null) {
                    edge.disconnectEdge();
                    target.spliceEdgesFrom(source);
                    removeZeroLengthEdges(target, ignored);
                    return;
                }
            }

            // Recursive call.
            removeZeroLengthEdges(edge, self);
        }
    }

    /**
     * Iterates all junctions, repeatedly calling moveJunctionAlongCommonEdge
     * until no more moves are possible.
     * Translated from HyperedgeImprover::moveJunctionsAlongCommonEdges(void)
     * in hyperedgeimprover.cpp lines 413-444.
     */
    private void moveJunctionsAlongCommonEdges() {
        List<JunctionRef> keys = new ArrayList<>(m_hyperedge_tree_junctions.keySet());
        int idx = 0;
        while (idx < keys.size()) {
            JunctionRef junction = keys.get(idx);
            HyperedgeTreeNode node = m_hyperedge_tree_junctions.get(junction);
            if (node == null) {
                idx++;
                continue;
            }

            boolean[] nodeMapHasChanged = {false};
            // For each junction, try and move it.
            HyperedgeTreeNode result = moveJunctionAlongCommonEdge(node, nodeMapHasChanged);
            while (result != null) {
                // Junction has moved, rewrite the pointer in the map.
                m_hyperedge_tree_junctions.put(junction, result);
                node = result;
                result = moveJunctionAlongCommonEdge(node, nodeMapHasChanged);
            }

            if (nodeMapHasChanged[0]) {
                // Objects have been added to the map, restart.
                keys = new ArrayList<>(m_hyperedge_tree_junctions.keySet());
                idx = 0;
            } else {
                idx++;
            }
        }
    }

    /**
     * Attempts to move a single junction node along a common edge.
     * Returns the new node position if moved, null if no move possible.
     * Translated from HyperedgeImprover::moveJunctionAlongCommonEdge()
     * in hyperedgeimprover.cpp lines 1046-1227.
     */
    private HyperedgeTreeNode moveJunctionAlongCommonEdge(
            HyperedgeTreeNode self, boolean[] nodeMapHasChanged) {
        assert self.junction != null;

        HyperedgeTreeNode newSelf = null;
        List<HyperedgeTreeEdge> commonEdges = new ArrayList<>();
        List<HyperedgeTreeEdge> otherEdges = new ArrayList<>();

        // Consider each edge from this node in turn.
        for (HyperedgeTreeEdge currEdge : new ArrayList<>(self.edges)) {
            HyperedgeTreeNode currNode = currEdge.followFrom(self);
            commonEdges.clear();
            otherEdges.clear();

            if (currNode.junction != null) {
                // Don't shift junctions onto other junctions.
                continue;
            }
            if (currEdge.hasFixedRoute) {
                // Don't move junctions along fixed edges.
                continue;
            }

            // The current edge is a common edge we are looking to shift along.
            commonEdges.add(currEdge);

            // Consider each of the other edges.
            for (HyperedgeTreeEdge otherEdge : new ArrayList<>(self.edges)) {
                if (otherEdge == currEdge) {
                    continue;
                }

                if (otherEdge.hasFixedRoute) {
                    otherEdges.add(otherEdge);
                    continue;
                }

                HyperedgeTreeNode otherNode = otherEdge.followFrom(self);
                if (otherNode.point.equals(currNode.point)) {
                    // A common edge can be at the same point, but can't have
                    // a junction at it.
                    if (otherNode.junction != null) {
                        otherEdges.add(otherEdge);
                    } else {
                        commonEdges.add(otherEdge);
                    }
                } else if (Geometry.pointOnLine(self.point, otherNode.point,
                        currNode.point)) {
                    // A common edge can be a (longer) collinear line, but we
                    // need to split the longer line at the other end of curr.
                    otherEdge.splitFromNodeAtPoint(self, currNode.point);
                    commonEdges.add(otherEdge);
                } else {
                    // If the edge goes in another direction it is not common.
                    otherEdges.add(otherEdge);
                }
            }

            // For the purpose of these changes a junction is considered fixed
            // only when not performing major improvements.
            boolean selfFixed = self.junction.positionFixed() &&
                    !m_can_make_major_changes;

            if ((commonEdges.size() > 1) && (otherEdges.size() <= 1) && !selfFixed) {
                // One of the common nodes becomes the target node, we move
                // all connections from the other common nodes to this node.
                HyperedgeTreeNode targetNode = commonEdges.getFirst().followFrom(self);
                for (int i = 1; i < commonEdges.size(); i++) {
                    HyperedgeTreeNode thisNode = commonEdges.get(i).followFrom(self);
                    commonEdges.get(i).disconnectEdge();
                    targetNode.spliceEdgesFrom(thisNode);
                }
                targetNode.junction = self.junction;
                self.junction = null;

                if (otherEdges.isEmpty()) {
                    // Nothing else connected to this node, so remove the node
                    // and the edge to the target node.
                    commonEdges.getFirst().disconnectEdge();
                } else {
                    // We need to mark commonEdges[0] as being from the connector
                    // of the otherEdges[0].
                    commonEdges.getFirst().conn = otherEdges.getFirst().conn;
                }
                newSelf = targetNode;
                break;
            } else if (m_can_make_major_changes && commonEdges.size() > 1) {
                // Case where this is one junction we need to split into two.
                HyperedgeTreeNode targetNode = commonEdges.getFirst().followFrom(self);
                for (int i = 1; i < commonEdges.size(); i++) {
                    HyperedgeTreeNode thisNode = commonEdges.get(i).followFrom(self);
                    commonEdges.get(i).disconnectEdge();
                    targetNode.spliceEdgesFrom(thisNode);
                }

                // Create the additional junction at the target node.
                targetNode.junction = new JunctionRef(m_router, targetNode.point);
                m_router.removeObjectFromQueuedActions(targetNode.junction);
                targetNode.junction.makeActive();
                m_hyperedge_tree_junctions.put(targetNode.junction, targetNode);
                nodeMapHasChanged[0] = true;
                m_new_junctions.add(targetNode.junction);

                // Create a new connector between the original junction and new junction.
                ConnRef conn = new ConnRef(m_router);
                m_router.removeObjectFromQueuedActions(conn);
                conn.makeActive();
                conn.m_initialised = true;
                ConnEnd srcConnend = new ConnEnd(targetNode.junction);
                conn.updateEndPoint(VertID.src, srcConnend);
                ConnEnd tarConnend = new ConnEnd(self.junction);
                conn.updateEndPoint(VertID.tar, tarConnend);
                commonEdges.getFirst().conn = conn;
                m_new_connectors.add(conn);

                newSelf = self;
                break;
            }
        }

        return newSelf;
    }

    /**
     * Helper method for buildHyperedgeSegments() for hyperedge tree nodes.
     * Translated from HyperedgeImprover::createShiftSegmentsForDimensionExcluding(HyperedgeTreeNode*, ...)
     * in hyperedgeimprover.cpp lines 293-307.
     */
    private void createShiftSegmentsForDimensionExcluding(
            HyperedgeTreeNode node, int dim, HyperedgeTreeEdge ignore,
            List<ShiftSegment> segments) {
        for (HyperedgeTreeEdge edge : new ArrayList<>(node.edges)) {
            if (edge != ignore) {
                createShiftSegmentsForDimensionExcluding(edge, dim, node, segments);
            }
        }
    }

    /**
     * Helper method for buildHyperedgeSegments() for hyperedge tree edges.
     * Translated from HyperedgeImprover::createShiftSegmentsForDimensionExcluding(HyperedgeTreeEdge*, ...)
     * in hyperedgeimprover.cpp lines 310-336.
     */
    private void createShiftSegmentsForDimensionExcluding(
            HyperedgeTreeEdge edge, int dim, HyperedgeTreeNode ignore,
            List<ShiftSegment> segments) {
        if (edge.hasOrientation(dim) && !edge.zeroLength()) {
            boolean immovable = (edge.ends[0].isImmovable() ||
                    edge.ends[1].isImmovable());

            HyperedgeShiftSegment newSegment =
                    new HyperedgeShiftSegment(edge.ends[0],
                            edge.ends[1], dim, immovable);
            segments.add(newSegment);
        }

        if (edge.ends[0] != null && edge.ends[0] != ignore) {
            createShiftSegmentsForDimensionExcluding(edge.ends[0], dim,
                    edge, segments);
        }

        if (edge.ends[1] != null && edge.ends[1] != ignore) {
            createShiftSegmentsForDimensionExcluding(edge.ends[1], dim,
                    edge, segments);
        }
    }

    /**
     * Given a hyperedge tree and a dimension, creates shift segments for all
     * edges in that orientation. Per-root segment tracking.
     * Translated from HyperedgeImprover::buildHyperedgeSegments(const size_t dim)
     * in hyperedgeimprover.cpp lines 376-392.
     */
    private void buildHyperedgeSegments(int dim) {
        for (JunctionRef root : new ArrayList<>(m_hyperedge_tree_roots)) {
            List<ShiftSegment> segments = m_root_shift_segments
                    .computeIfAbsent(root, _ -> new ArrayList<>());

            HyperedgeTreeNode node = m_hyperedge_tree_junctions.get(root);
            createShiftSegmentsForDimensionExcluding(node, dim, null, segments);

            // Merge overlapping segments.
            mergeOverlappingSegments(segments);

            m_all_shift_segments.addAll(0, segments);
        }
    }

    /**
     * During creation and nudging of shift segments it is often necessary
     * to merge collinear or overlapping segments.
     * Translated from HyperedgeImprover::mergeOverlappingSegments(ShiftSegmentList& segments)
     * in hyperedgeimprover.cpp lines 342-370.
     */
    private void mergeOverlappingSegments(List<ShiftSegment> segments) {
        for (int i = 0; i < segments.size(); i++) {
            HyperedgeShiftSegment edge1 = (HyperedgeShiftSegment) segments.get(i);
            for (int j = 0; j < segments.size(); ) {
                if (j == i) {
                    j++;
                    continue;
                }
                HyperedgeShiftSegment edge2 = (HyperedgeShiftSegment) segments.get(j);
                if (edge1.mergesWith(edge2)) {
                    segments.remove(j);
                    if (j < i) {
                        i--;
                    }
                } else {
                    j++;
                }
            }
        }
    }

    /**
     * Given a set of hyperedge shift segments in a particular dimension,
     * with limits and balance values precomputed, this method shifts and
     * merges segments to improve the overall cost for the hyperedge.
     * Translated from HyperedgeImprover::nudgeHyperedgeSegments(size_t, unsigned int&)
     * in hyperedgeimprover.cpp lines 555-615.
     */
    private void nudgeHyperedgeSegments() {
        // For each hyperedge...
        for (JunctionRef root : new ArrayList<>(m_hyperedge_tree_roots)) {
            // Calculate the balance for each shift segment.
            List<ShiftSegment> segmentList = m_root_shift_segments.get(root);
            if (segmentList == null) continue;

            for (ShiftSegment seg : segmentList) {
                ((HyperedgeShiftSegment) seg).setBalanceCount();
            }

            boolean change = false;
            int idx = 0;
            while (idx < segmentList.size()) {
                // While we haven't considered every segment...
                HyperedgeShiftSegment segment =
                        (HyperedgeShiftSegment) segmentList.get(idx);

                if (!segment.settled()) {
                    // The segment is not settled, so move it to the next
                    // ideal position and then merge it with overlapping
                    // segments.
                    segment.adjustPosition();
                    mergeOverlappingSegments(segmentList);
                    change = true;
                }

                if (change) {
                    // We made a change, so start again from the beginning
                    // of the list of segments.
                    change = false;
                    idx = 0;
                } else {
                    // Consider the next segment.
                    idx++;
                }
            }
        }
    }

    /**
     * Write the paths from an improved hyperedgetree object back as routes
     * to the component connectors that form the hyperedge.
     * Translated from HyperedgeImprover::writeHyperedgeSegmentsBackToConnPaths(void)
     * in hyperedgeimprover.cpp lines 619-633.
     */
    private void writeHyperedgeSegmentsBackToConnPaths() {
        // Write segments in two passes.
        for (int pass = 0; pass < 2; ++pass) {
            for (JunctionRef root : new ArrayList<>(m_hyperedge_tree_roots)) {
                HyperedgeTreeNode node = m_hyperedge_tree_junctions.get(root);
                if (node != null) {
                    node.writeEdgesToConns(null, pass);
                }
            }
        }
    }

    // resetVisitedFlags() removed — invented method, no C++ equivalent.
    // The visited flag is only used for cycle detection in removeOtherJunctionsFrom()
    // and is never read again afterward, so resetting is unnecessary.

    // ---- Inner class HyperedgeShiftSegment ----

    /**
     * Translated from class HyperedgeShiftSegment in hyperedgeimprover.cpp lines 41-249.
     */
    static class HyperedgeShiftSegment extends ShiftSegment {
        TreeSet<HyperedgeTreeNode> nodes;
        boolean isImmovable;
        private int m_balance_count;
        private boolean m_balance_count_set;
        private double m_next_pos_lower;
        private double m_next_pos_upper;
        private boolean m_at_limit;

        HyperedgeShiftSegment(HyperedgeTreeNode n1, HyperedgeTreeNode n2,
                int dim, boolean immovable) {
            super(dim);
            int altDim = (dim + 1) % 2;
            this.nodes = new TreeSet<>(Comparator.comparingDouble(
                    (HyperedgeTreeNode n) -> n.point.get(altDim))
                    .thenComparingInt(n -> n.nodeId));
            this.isImmovable = immovable;
            this.m_balance_count = 0;
            this.m_balance_count_set = false;
            this.m_at_limit = false;

            nodes.add(n1);
            nodes.add(n2);
            n1.shiftSegmentNodeSet = nodes;
            n2.shiftSegmentNodeSet = nodes;

            minSpaceLimit = -CHANNEL_MAX;
            maxSpaceLimit = CHANNEL_MAX;
        }

        @Override
        public Point lowPoint() {
            return nodes.first().point;
        }

        @Override
        public Point highPoint() {
            return nodes.last().point;
        }

        /**
         * Counts the number of segments diverging on each side and returns
         * a count: negative if more on the lower side, positive if more on upper.
         * Translated from HyperedgeShiftSegment::setBalanceCount() lines 90-129.
         */
        void setBalanceCount() {
            int altDim = (dimension + 1) % 2;
            m_next_pos_lower = minSpaceLimit;
            m_next_pos_upper = maxSpaceLimit;
            m_balance_count = 0;
            if (isImmovable) {
                m_balance_count_set = true;
                return;
            }
            for (HyperedgeTreeNode curr : nodes) {
                Point currPoint = curr.point;
                for (HyperedgeTreeEdge currEdge : curr.edges) {
                    HyperedgeTreeNode other = currEdge.followFrom(curr);
                    Point otherPoint = other.point;
                    if (currPoint.get(altDim) == otherPoint.get(altDim)) {
                        if (otherPoint.get(dimension) < currPoint.get(dimension)) {
                            m_next_pos_lower = Math.max(m_next_pos_lower,
                                    otherPoint.get(dimension));
                            --m_balance_count;
                        } else if (otherPoint.get(dimension) > currPoint.get(dimension)) {
                            m_next_pos_upper = Math.min(m_next_pos_upper,
                                    otherPoint.get(dimension));
                            ++m_balance_count;
                        }
                    }
                }
            }
            m_balance_count_set = true;
        }

        int balanceCount() {
            assert m_balance_count_set;
            return m_balance_count;
        }

        /**
         * Translated from HyperedgeShiftSegment::adjustPosition() lines 135-184.
         */
        void adjustPosition() {
            assert m_balance_count_set;
            assert m_balance_count != 0;

            double newPos = (m_balance_count < 0) ?
                    m_next_pos_lower : m_next_pos_upper;
            double limit = (m_balance_count < 0) ?
                    minSpaceLimit : maxSpaceLimit;

            if (lowPoint().get(dimension) == newPos) {
                // If aren't actually moving this segment, then mark it as at-limit.
                m_at_limit = true;
            }

            for (HyperedgeTreeNode curr : nodes) {
                curr.point.set(dimension, newPos);
            }

            if (newPos == limit) {
                m_at_limit = true;
            }

            // Add nodes from collapsed segments, in case they are not part of
            // a segment that will be merged.
            for (HyperedgeTreeNode curr : new ArrayList<>(nodes)) {
                Point currPoint = curr.point;
                for (HyperedgeTreeEdge currEdge : curr.edges) {
                    HyperedgeTreeNode other = currEdge.followFrom(curr);
                    Point otherPoint = other.point;
                    if (currPoint.equals(otherPoint)) {
                        nodes.add(other);
                        other.shiftSegmentNodeSet = nodes;
                    }
                }
            }
        }

        /**
         * Translated from HyperedgeShiftSegment::overlapsWith() lines 185-203.
         */
        @Override
        public boolean overlapsWith(ShiftSegment rhs, int dim) {
            int altDim = (dim + 1) % 2;
            Point lowPt = lowPoint();
            Point highPt = highPoint();
            Point rhsLowPt = rhs.lowPoint();
            Point rhsHighPt = rhs.highPoint();
            if ((lowPt.get(altDim) <= rhsHighPt.get(altDim)) &&
                    (rhsLowPt.get(altDim) <= highPt.get(altDim))) {
                // The segments overlap.
                return (minSpaceLimit <= rhs.maxSpaceLimit) &&
                    (rhs.minSpaceLimit <= maxSpaceLimit);
            }
            return false;
        }

        @Override
        public boolean immovable() {
            return isImmovable;
        }

        /**
         * Translated from HyperedgeShiftSegment::settled() lines 208-211.
         */
        boolean settled() {
            return isImmovable || m_at_limit || (balanceCount() == 0);
        }

        /**
         * Translated from HyperedgeShiftSegment::mergesWith() lines 212-239.
         */
        boolean mergesWith(HyperedgeShiftSegment other) {
            int altDim = (dimension + 1) % 2;
            Point lowPt = lowPoint();
            Point highPt = highPoint();
            Point otherLowPt = other.lowPoint();
            Point otherHighPt = other.highPoint();
            if ((lowPt.get(dimension) == otherLowPt.get(dimension)) &&
                    (lowPt.get(altDim) <= otherHighPt.get(altDim)) &&
                    (otherLowPt.get(altDim) <= highPt.get(altDim))) {
                isImmovable |= other.isImmovable;
                m_at_limit |= other.m_at_limit;
                minSpaceLimit = Math.max(minSpaceLimit, other.minSpaceLimit);
                maxSpaceLimit = Math.min(maxSpaceLimit, other.maxSpaceLimit);
                nodes.addAll(other.nodes);
                for (HyperedgeTreeNode n : other.nodes) {
                    n.shiftSegmentNodeSet = nodes;
                }
                other.nodes.clear();
                setBalanceCount();
                return true;
            }
            setBalanceCount();
            return false;
        }
    }
}
