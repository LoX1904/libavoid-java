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

/**
 * A ShapeRef represents an obstacle shape in the routing scene.
 * Corresponds to shape.h/shape.cpp in C++.
 */
public class ShapeRef extends Obstacle {

    /**
     * Shape transformation types for transformConnectionPinPositions().
     * Corresponds to ShapeTransformationType enum in C++.
     */
    public enum ShapeTransformationType {
        TransformationType_CW90,
        TransformationType_CW180,
        TransformationType_CW270,
        TransformationType_FlipX,
        TransformationType_FlipY
    }

    public ShapeRef(Router router, Polygon poly, int id) {
        super(router, poly, id);
        router.addShape(this);
    }

    public ShapeRef(Router router, Polygon poly) {
        this(router, poly, 0);
    }

    /**
     * Returns the position of this shape (the centre of its bounding box).
     * Corresponds to ShapeRef::position() in C++.
     */
    @Override
    public Point position() {
        Box bb = m_polygon.offsetBoundingBox(0);
        return new Point((bb.min.x + bb.max.x) / 2.0, (bb.min.y + bb.max.y) / 2.0);
    }

    /**
     * Moves all attached connectors when the shape polygon changes.
     * Corresponds to ShapeRef::moveAttachedConns(const Polygon&) in C++.
     */
    void moveAttachedConns(Polygon newPoly) {
        // Update positions of attached connector ends.
        // C++ shape.cpp:57-75
        for (ConnEnd connEnd : new ArrayList<>(m_following_conn_ends)) {
            assert connEnd.m_conn_ref != null;
            boolean connPinUpdate = true;
            m_router.modifyConnector(connEnd.m_conn_ref, connEnd.endpointType(),
                    connEnd, connPinUpdate);
        }
        for (ShapeConnectionPin pin : m_connection_pins) {
            pin.updatePosition(newPoly);
        }
    }

    /**
     * Translates the polygon so that its centre is at newCentre.
     * Corresponds to ShapeRef::setCentrePos(const Point&) in C++.
     */
    void setCentrePos(Point newCentre) {
        Point diff = new Point(newCentre.x - position().x, newCentre.y - position().y);
        m_polygon.translate(diff.x, diff.y);
    }

    /**
     * Transforms the positions and visibility directions of all connection pins
     * according to the given transformation.
     * Corresponds to ShapeRef::transformConnectionPinPositions() in C++.
     */
    public void transformConnectionPinPositions(ShapeTransformationType transform) {
        for (ShapeConnectionPin pin : m_connection_pins) {
            boolean usingProportionalOffsets = pin.m_proportional;
            double xOffset = pin.m_x_portion_offset;
            double yOffset = pin.m_y_portion_offset;
            int visDirs = pin.m_visibility_directions;
            double tmpOffset;

            // Number of clockwise 90 degree rotations
            int rotationN = 0;

            if (usingProportionalOffsets) {
                // Translate to Origin.
                xOffset -= 0.5;
                yOffset -= 0.5;

                switch (transform) {
                    case TransformationType_CW90:
                        rotationN = 3;
                        tmpOffset = yOffset;
                        yOffset = xOffset;
                        xOffset = -tmpOffset;
                        break;
                    case TransformationType_CW180:
                        rotationN = 2;
                        yOffset = -yOffset;
                        xOffset = -xOffset;
                        break;
                    case TransformationType_CW270:
                        rotationN = 1;
                        tmpOffset = yOffset;
                        yOffset = -xOffset;
                        xOffset = tmpOffset;
                        break;
                    case TransformationType_FlipX:
                        xOffset = -xOffset;
                        break;
                    case TransformationType_FlipY:
                        yOffset = -yOffset;
                        break;
                }
                // Translate back.
                xOffset += 0.5;
                yOffset += 0.5;
            } else {
                // Using absolute offsets for pin.
                Box shapeBox = pin.m_shape.polygon().offsetBoundingBox(0.0);
                switch (transform) {
                    case TransformationType_CW90:
                        rotationN = 3;
                        tmpOffset = yOffset;
                        yOffset = xOffset;
                        xOffset = absoluteOffsetInverse(tmpOffset, shapeBox, Point.XDIM);
                        break;
                    case TransformationType_CW180:
                        rotationN = 2;
                        yOffset = absoluteOffsetInverse(yOffset, shapeBox, Point.YDIM);
                        xOffset = absoluteOffsetInverse(xOffset, shapeBox, Point.XDIM);
                        break;
                    case TransformationType_CW270:
                        rotationN = 1;
                        tmpOffset = yOffset;
                        yOffset = absoluteOffsetInverse(xOffset, shapeBox, Point.YDIM);
                        xOffset = tmpOffset;
                        break;
                    case TransformationType_FlipX:
                        xOffset = absoluteOffsetInverse(xOffset, shapeBox, Point.XDIM);
                        break;
                    case TransformationType_FlipY:
                        yOffset = absoluteOffsetInverse(yOffset, shapeBox, Point.YDIM);
                        break;
                }
            }

            pin.m_x_portion_offset = xOffset;
            pin.m_y_portion_offset = yOffset;

            if ((visDirs & ConnDirFlag.ConnDirAll) != 0 && visDirs != ConnDirFlag.ConnDirAll) {
                // Visibility is set, but not in all directions.
                final int dirU = 0;
                final int dirR = 1;
                final int dirD = 2;
                final int dirL = 3;

                boolean[] visInDir = new boolean[4];
                if ((visDirs & ConnDirFlag.ConnDirUp) != 0) visInDir[dirU] = true;
                if ((visDirs & ConnDirFlag.ConnDirRight) != 0) visInDir[dirR] = true;
                if ((visDirs & ConnDirFlag.ConnDirDown) != 0) visInDir[dirD] = true;
                if ((visDirs & ConnDirFlag.ConnDirLeft) != 0) visInDir[dirL] = true;

                if (transform == ShapeTransformationType.TransformationType_FlipY) {
                    boolean tmpBool = visInDir[dirU];
                    visInDir[dirU] = visInDir[dirD];
                    visInDir[dirD] = tmpBool;
                } else if (transform == ShapeTransformationType.TransformationType_FlipX) {
                    boolean tmpBool = visInDir[dirL];
                    visInDir[dirL] = visInDir[dirR];
                    visInDir[dirR] = tmpBool;
                }

                visDirs = ConnDirFlag.ConnDirNone;
                if (visInDir[(rotationN + dirU) % 4]) visDirs |= ConnDirFlag.ConnDirUp;
                if (visInDir[(rotationN + dirR) % 4]) visDirs |= ConnDirFlag.ConnDirRight;
                if (visInDir[(rotationN + dirD) % 4]) visDirs |= ConnDirFlag.ConnDirDown;
                if (visInDir[(rotationN + dirL) % 4]) visDirs |= ConnDirFlag.ConnDirLeft;
            }

            pin.m_visibility_directions = visDirs;
            pin.updatePositionAndVisibility();
            m_router.modifyConnectionPin(pin);
        }
    }

    private static double absoluteOffsetInverse(double offset, Box shapeBox, int toDim) {
        if (offset == ShapeConnectionPin.ATTACH_POS_MIN_OFFSET) {
            return ShapeConnectionPin.ATTACH_POS_MAX_OFFSET;
        }
        if (offset == ShapeConnectionPin.ATTACH_POS_MAX_OFFSET) {
            return ShapeConnectionPin.ATTACH_POS_MIN_OFFSET;
        }
        return shapeBox.length(toDim) - offset;
    }
}
