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
 * A common interface used by the Polygon classes.
 * Corresponds to C++ PolygonInterface.
 */
public abstract class AbstractPolygon {

    public abstract void clear();
    public abstract boolean empty();
    public abstract int size();
    public abstract int id();
    public abstract Point at(int index);

    /** Returns the bounding rectangle that contains this polygon
     *  with optionally some buffer space around it for routing. */
    public Box offsetBoundingBox(double offset) {
        Box bBox = new Box();
        bBox.min.x = Double.MAX_VALUE;
        bBox.min.y = Double.MAX_VALUE;
        bBox.max.x = -Double.MAX_VALUE;
        bBox.max.y = -Double.MAX_VALUE;

        for (int i = 0; i < size(); ++i) {
            bBox.min.x = Math.min(bBox.min.x, at(i).x);
            bBox.min.y = Math.min(bBox.min.y, at(i).y);
            bBox.max.x = Math.max(bBox.max.x, at(i).x);
            bBox.max.y = Math.max(bBox.max.y, at(i).y);
        }

        bBox.min.x -= offset;
        bBox.min.y -= offset;
        bBox.max.x += offset;
        bBox.max.y += offset;

        return bBox;
    }

    private static Point unitNormalForEdge(Point pt1, Point pt2) {
        if (pt1.equals(pt2)) {
            return new Point(0, 0);
        }
        double dx = pt2.x - pt1.x;
        double dy = pt2.y - pt1.y;
        double f = 1.0 / Math.sqrt((dx * dx) + (dy * dy));
        dx *= f;
        dy *= f;
        return new Point(dy, -dx);
    }

    public Polygon offsetPolygon(double offset) {
        Polygon newPoly = new Polygon();
        newPoly._id = id();
        if (offset == 0) {
            for (int i = 0; i < size(); ++i) {
                newPoly.ps.add(new Point(at(i)));
            }
            return newPoly;
        }

        int numOfEdges = size();
        Point[] normals = new Point[numOfEdges];
        for (int i = 0; i < numOfEdges; ++i) {
            normals[i] = unitNormalForEdge(at(i), at((i + 1) % numOfEdges));
        }

        int j = numOfEdges - 1;
        for (int i = 0; i < numOfEdges; ++i) {
            double R = 1 + ((normals[i].x * normals[j].x) +
                    (normals[i].y * normals[j].y));
            if (((normals[j].x * normals[i].y) - (normals[i].x * normals[j].y)) *
                    offset >= 0) {
                double q = offset / R;
                Point pt = new Point(at(i).x + (normals[j].x + normals[i].x) * q,
                        at(i).y + (normals[j].y + normals[i].y) * q);
                pt.id = id();
                pt.vn = (short) newPoly.size();
                newPoly.ps.add(pt);
            } else {
                Point pt1 = new Point(at(i).x + normals[j].x * offset,
                        at(i).y + normals[j].y * offset);
                Point pt2 = new Point(at(i));
                Point pt3 = new Point(at(i).x + normals[i].x * offset,
                        at(i).y + normals[i].y * offset);

                pt1.id = id();
                pt1.vn = (short) newPoly.size();
                newPoly.ps.add(pt1);

                pt2.id = id();
                pt2.vn = (short) newPoly.size();
                newPoly.ps.add(pt2);

                pt3.id = id();
                pt3.vn = (short) newPoly.size();
                newPoly.ps.add(pt3);
            }
            j = i;
        }

        return newPoly;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj instanceof AbstractPolygon r)
        {
            if (r.size() != this.size())
            {
                return false;
            }
            for (int i = 0; i < r.size(); i++)
            {
                if (!at(i).equals(r.at(i)))
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
