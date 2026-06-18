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
import java.util.Iterator;

/**
 * A JunctionRef represents a junction point where multiple connectors meet.
 * Corresponds to junction.h/junction.cpp in C++.
 */
public class JunctionRef extends Obstacle {

    private Point m_position;
    private Point m_recommended_position;
    private boolean m_position_fixed;

    public JunctionRef(Router router, Point position, int id) {
        super(router, makeRectangle(router, position), id);
        m_position = new Point(position);
        m_recommended_position = new Point(position);
        m_position_fixed = false;

        // For Junctions we use a single non-exclusive pin.
        // Note: ShapeConnectionPin constructor calls addConnectionPin(this) internally.
        ShapeConnectionPin pin = new ShapeConnectionPin(this,
                ShapeConnectionPin.CONNECTIONPIN_CENTRE, ConnDirFlag.ConnDirAll);
        pin.setExclusive(false);

        m_router.addJunction(this);
    }

    public JunctionRef(Router router, Point position) {
        this(router, position, 0);
    }

    static Rectangle makeRectangle(Router router, Point position) {
        // A suitably small value.
        double nudgeDist = router.routingParameter(Router.RoutingParameter.idealNudgingDistance);
        nudgeDist = Math.min(1.0, nudgeDist);

        Point low = new Point(position);
        low.x -= nudgeDist;
        low.y -= nudgeDist;

        Point high = new Point(position);
        high.x += nudgeDist;
        high.y += nudgeDist;

        return new Rectangle(low, high);
    }

    @Override
    public Point position() {
        return new Point(m_position);
    }

    public void setPositionFixed(boolean fixed) {
        m_position_fixed = fixed;
        m_router.registerSettingsChange();
    }

    public boolean positionFixed() {
        return m_position_fixed;
    }

    public void preferOrthogonalDimension(int dim) {
        final double smallPenalty = 1.0;
        for (ShapeConnectionPin pin : m_connection_pins) {
            if (dim == Point.YDIM) {
                if ((pin.directions() & (ConnDirFlag.ConnDirLeft | ConnDirFlag.ConnDirRight)) != 0) {
                    pin.setConnectionCost(smallPenalty);
                }
            } else if (dim == Point.XDIM) {
                if ((pin.directions() & (ConnDirFlag.ConnDirUp | ConnDirFlag.ConnDirDown)) != 0) {
                    pin.setConnectionCost(smallPenalty);
                }
            }
        }
    }

    public Point recommendedPosition() {
        return new Point(m_recommended_position);
    }

    public void setRecommendedPosition(Point position) {
        m_recommended_position = new Point(position);
    }

    public void setPosition(Point position) {
        m_position = new Point(position);
        m_recommended_position = new Point(position);
        m_polygon = makeRectangle(m_router, m_position);
        setNewPoly(m_polygon);
    }

    /**
     * Removes a junction that has only two connectors attached to it and
     * merges them into a single connector.
     * Corresponds to JunctionRef::removeJunctionAndMergeConnectors() in C++.
     *
     * @return The merged connector, or null if the junction was not removed.
     */
    public ConnRef removeJunctionAndMergeConnectors() {
        if (m_following_conn_ends.size() != 2) {
            return null;
        }

        Iterator<ConnEnd> iter = m_following_conn_ends.iterator();
        ConnEnd connEnd1 = iter.next();
        ConnEnd connEnd2 = iter.next();
        assert connEnd2.m_conn_ref != null;
        assert connEnd1.m_conn_ref != null;

        // The second conn will be the one we will delete.
        ConnRef conn2 = connEnd2.m_conn_ref;
        // Determine its endpoint that is not attached to the junction.
        ConnEnd connEnd2Other = (conn2.m_src_connend == connEnd2) ?
                conn2.m_dst_connend : conn2.m_src_connend;
        if (connEnd2Other == null) {
            // If it doesn't have a valid other endpoint, then ignore.
            return null;
        }
        // Modify the first connector's junction endpoint to connect to the
        // other end of the second connector.
        m_router.modifyConnector(connEnd1.m_conn_ref,
                connEnd1.endpointType(), connEnd2Other, false);

        // Delete the second connector.
        m_router.deleteConnector(conn2);

        // Remove the junction from the router scene. It should get deleted later.
        m_router.deleteJunction(this);

        // Return the first (i.e. merged) connector.
        return connEnd1.m_conn_ref;
    }

    /**
     * Moves all attached connectors when the junction position changes.
     * Corresponds to JunctionRef::moveAttachedConns(const Point&) in C++.
     */
    void moveAttachedConns(Point newPosition) {
        // Update positions of attached connector ends.
        for (ConnEnd connEnd : new ArrayList<>(m_following_conn_ends)) {
            assert connEnd.m_conn_ref != null;
            m_router.modifyConnector(connEnd.m_conn_ref, connEnd.endpointType(),
                    connEnd, false);
        }
        for (ShapeConnectionPin pin : m_connection_pins) {
            pin.updatePosition(newPosition);
        }
    }
}
