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
 * Geometry utility functions. Corresponds to geometry.h/geometry.cpp in C++.
 */
public final class Geometry {

    public enum Intersection
    {
        DONT_INTERSECT,
        DO_INTERSECT,
        PARALLEL;
    }

    private static final Pair<Intersection, Point> DONT_INTERSECT = new Pair<Intersection, Point>(Intersection.DONT_INTERSECT, new Point(0, 0));
    private static final Pair<Intersection, Point> PARALLEL = new Pair<Intersection, Point>(Intersection.PARALLEL, new Point(0, 0));

    private Geometry() {}

    /**
     * Direction from vector.
     * Looks at the position of point c from the directed segment ab.
     * Returns: 1 counterclockwise, 0 collinear, -1 clockwise.
     */
    public static int vecDir(Point a, Point b, Point c, double maybeZero) {
        double area2 = ((b.x - a.x) * (c.y - a.y)) -
                ((c.x - a.x) * (b.y - a.y));

        if (area2 < (-maybeZero)) {
            return -1;
        } else if (area2 > maybeZero) {
            return 1;
        }
        return 0;
    }

    public static int vecDir(Point a, Point b, Point c) {
        return vecDir(a, b, c, 0.0);
    }

    public static double euclideanDist(Point a, Point b) {
        double xdiff = a.x - b.x;
        double ydiff = a.y - b.y;
        return Math.sqrt((xdiff * xdiff) + (ydiff * ydiff));
    }

    public static double manhattanDist(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    public static double dist(Point a, Point b) {
        return euclideanDist(a, b);
    }

    public static double angle(Point a, Point b, Point c) {
        double ux = b.x - a.x;
        double uy = b.y - a.y;
        double vx = c.x - b.x;
        double vy = c.y - b.y;
        double lu = Math.sqrt(ux * ux + uy * uy);
        double lv = Math.sqrt(vx * vx + vy * vy);
        double udotv = ux * vx + uy * vy;
        double costheta = udotv / (lu * lv);
        return Math.acos(costheta);
    }

    public static boolean inBetween(Point a, Point b, Point c) {
        double epsilon = Math.ulp(1.0); // std::numeric_limits<double>::epsilon()
        if (Math.abs(a.x - b.x) > epsilon) {
            return (((a.x < c.x) && (c.x < b.x)) ||
                    ((b.x < c.x) && (c.x < a.x)));
        } else {
            return (((a.y < c.y) && (c.y < b.y)) ||
                    ((b.y < c.y) && (c.y < a.y)));
        }
    }

    public static boolean pointOnLine(Point a, Point b, Point c, double tolerance) {
        if (a.x == b.x) {
            return (a.x == c.x) &&
                    (((a.y < c.y) && (c.y < b.y)) ||
                            ((b.y < c.y) && (c.y < a.y)));
        } else if (a.y == b.y) {
            return (a.y == c.y) &&
                    (((a.x < c.x) && (c.x < b.x)) ||
                            ((b.x < c.x) && (c.x < a.x)));
        }
        return (vecDir(a, b, c, tolerance) == 0) && inBetween(a, b, c);
    }

    public static boolean pointOnLine(Point a, Point b, Point c) {
        return pointOnLine(a, b, c, 0.0);
    }

    public static boolean segmentIntersect(Point a, Point b, Point c, Point d) {
        int ab_c = vecDir(a, b, c);
        if (ab_c == 0) return false;
        int ab_d = vecDir(a, b, d);
        if (ab_d == 0) return false;
        int cd_a = vecDir(c, d, a);
        int cd_b = vecDir(c, d, b);
        return (((ab_c * ab_d) < 0) && ((cd_a * cd_b) < 0));
    }

    public static boolean segmentShapeIntersect(Point e1, Point e2, Point s1, Point s2,
                                                 boolean[] seenIntersectionAtEndpoint) {
        if (segmentIntersect(e1, e2, s1, s2)) {
            return true;
        } else if ((((s2.equals(e1)) || pointOnLine(s1, s2, e1)) &&
                (vecDir(s1, s2, e2) != 0))
                ||
                (((s2.equals(e2)) || pointOnLine(s1, s2, e2)) &&
                        (vecDir(s1, s2, e1) != 0))) {
            if (seenIntersectionAtEndpoint[0]) {
                return true;
            }
            seenIntersectionAtEndpoint[0] = true;
        }
        return false;
    }

    public static boolean inValidRegion(boolean ignoreRegions, Point a0, Point a1,
                                         Point a2, Point b) {
        int rSide = vecDir(b, a0, a1);
        int sSide = vecDir(b, a1, a2);

        boolean rOutOn = (rSide <= 0);
        boolean sOutOn = (sSide <= 0);
        boolean rOut = (rSide < 0);
        boolean sOut = (sSide < 0);

        if (vecDir(a0, a1, a2) > 0) {
            if (ignoreRegions) {
                return (rOutOn && !sOut) || (!rOut && sOutOn);
            }
            return (rOutOn || sOutOn);
        } else {
            return (!ignoreRegions && (rOutOn && sOutOn));
        }
    }

    public static int cornerSide(Point c1, Point c2, Point c3, Point p) {
        int s123 = vecDir(c1, c2, c3);
        int s12p = vecDir(c1, c2, p);
        int s23p = vecDir(c2, c3, p);

        if (s123 == 1) {
            if ((s12p >= 0) && (s23p >= 0)) return 1;
            return -1;
        } else if (s123 == -1) {
            if ((s12p <= 0) && (s23p <= 0)) return -1;
            return 1;
        }
        return s12p;
    }

    public static boolean inPoly(Polygon poly, Point q, boolean countBorder) {
        int n = poly.size();
        boolean onBorder = false;
        for (int i = 0; i < n; i++) {
            int prev = (i + n - 1) % n;
            int dir = vecDir(poly.ps.get(prev), poly.ps.get(i), q);
            if (dir == -1) return false;
            onBorder |= (dir == 0);
        }
        return countBorder || !onBorder;
    }

    /**
     * Segment intersection point calculation.
     * Returns {intersectionType, x, y}.
     */
    public static Pair<Intersection, Point> segmentIntersectPoint(Point a1, Point a2, Point b1, Point b2) {
        double Ax = a2.x - a1.x;
        double Bx = b1.x - b2.x;
        double x1lo, x1hi;

        if (Ax < 0) { x1lo = a2.x; x1hi = a1.x; }
        else { x1hi = a2.x; x1lo = a1.x; }
        if (Bx > 0) {
            if (x1hi < b2.x || b1.x < x1lo) return DONT_INTERSECT;
        } else {
            if (x1hi < b1.x || b2.x < x1lo) return DONT_INTERSECT;
        }

        double Ay = a2.y - a1.y;
        double By = b1.y - b2.y;
        double y1lo, y1hi;

        if (Ay < 0) { y1lo = a2.y; y1hi = a1.y; }
        else { y1hi = a2.y; y1lo = a1.y; }
        if (By > 0) {
            if (y1hi < b2.y || b1.y < y1lo) return DONT_INTERSECT;
        } else {
            if (y1hi < b1.y || b2.y < y1lo) return DONT_INTERSECT;
        }

        double Cx = a1.x - b1.x;
        double Cy = a1.y - b1.y;
        double d = By * Cx - Bx * Cy;
        double f = Ay * Bx - Ax * By;

        if (f > 0) {
            if (d < 0 || d > f) return DONT_INTERSECT;
        } else {
            if (d > 0 || d < f) return DONT_INTERSECT;
        }

        double e = Ax * Cy - Ay * Cx;
        if (f > 0) {
            if (e < 0 || e > f) return DONT_INTERSECT;
        } else {
            if (e > 0 || e < f) return DONT_INTERSECT;
        }

        if (f == 0) {
            return PARALLEL;
        }

        double num = d * Ax;
        double x = a1.x + num / f;
        num = d * Ay;
        double y = a1.y + num / f;

        return new Pair<>(Intersection.DO_INTERSECT, new Point(x, y));
    }

    public static Pair<Intersection, Point> rayIntersectPoint(Point a1, Point a2, Point b1, Point b2) {
        double Ay = a2.y - a1.y;
        double By = b1.y - b2.y;
        double Ax = a2.x - a1.x;
        double Bx = b1.x - b2.x;

        double Cx = a1.x - b1.x;
        double Cy = a1.y - b1.y;
        double d = By * Cx - Bx * Cy;
        double f = Ay * Bx - Ax * By;

        if (f == 0) return PARALLEL;

        double num = d * Ax;
        double x = a1.x + num / f;
        num = d * Ay;
        double y = a1.y + num / f;

        return new Pair<>(Intersection.DO_INTERSECT, new Point(x, y));
    }

    public static double rotationalAngle(Point p) {
        if (p.y == 0) {
            return (p.x < 0) ? 180 : 0;
        } else if (p.x == 0) {
            return (p.y < 0) ? 270 : 90;
        }

        double ang = Math.atan(p.y / p.x);
        ang = (ang * 180) / Math.PI;

        if (p.x < 0) {
            ang += 180;
        } else if (p.y < 0) {
            ang += 360;
        }

        return ang;
    }
}
