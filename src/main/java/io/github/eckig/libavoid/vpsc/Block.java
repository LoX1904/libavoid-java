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

package io.github.eckig.libavoid.vpsc;

import java.util.ArrayList;
import java.util.List;

/**
 * A Block of variables in the VPSC solver. A block contains one or more
 * variables with the invariant that all constraints inside a block are
 * satisfied by keeping the variables fixed relative to one another.
 * Translated from vpsc.h/vpsc.cpp in libavoid C++.
 */
public class Block {
    public List<Variable> vars;
    public double posn;
    public PositionStats ps;
    public boolean deleted;
    public long timeStamp;

    // Parent container, that holds the blockTimeCtr.
    Blocks blocks;

    public Block(Blocks blocks, Variable v) {
        this.vars = new ArrayList<>();
        this.posn = 0;
        this.deleted = false;
        this.timeStamp = 0;
        this.blocks = blocks;
        this.ps = new PositionStats();
        if (v != null) {
            v.offset = 0;
            addVariable(v);
        }
    }

    public Block(Blocks blocks) {
        this(blocks, null);
    }

    public void addVariable(Variable v) {
        v.block = this;
        vars.add(v);
        if (ps.A2 == 0) ps.scale = v.scale;
        ps.addVariable(v);
        posn = (ps.AD - ps.AB) / ps.A2;
        assert !Double.isNaN(posn);
    }

    public void updateWeightedPosition() {
        ps.AB = ps.AD = ps.A2 = 0;
        for (Variable v : vars) {
            ps.addVariable(v);
        }
        posn = (ps.AD - ps.AB) / ps.A2;
        assert !Double.isNaN(posn);
    }

    public Block merge(Block b, Constraint c) {
        double dist = c.right.offset - c.left.offset - c.gap;
        Block l = c.left.block;
        Block r = c.right.block;
        if (l.vars.size() < r.vars.size()) {
            r.mergeBlock(l, c, dist);
        } else {
            l.mergeBlock(r, c, -dist);
        }
        return b.deleted ? this : b;
    }

    /**
     * Merges b into this block across c.
     * Can be either a right merge or a left merge.
     * @param b block to merge into this
     * @param c constraint being merged
     * @param dist separation required to satisfy c
     */
    public void mergeBlock(Block b, Constraint c, double dist) {
        c.activate();
        for (Variable v : b.vars) {
            v.offset += dist;
            addVariable(v);
        }
        posn = (ps.AD - ps.AB) / ps.A2;
        assert !Double.isNaN(posn);
        b.deleted = true;
    }

    /**
     * Computes the derivative of v and the lagrange multipliers
     * of v's out constraints (as the recursive sum of those below).
     * Does not backtrack over u.
     * Also records the constraint with minimum lagrange multiplier in min_lm[0].
     *
     * Optimized: iterates only activeOut/activeIn (active constraints) instead
     * of scanning all out/in constraints and filtering via canFollow.
     */
    double compute_dfdv(Variable v, Variable u, Constraint[] min_lm) {
        double dfdv = v.dfdv();
        for (int i = 0, n = v.activeOut.size(); i < n; i++) {
            Constraint c = v.activeOut.get(i);
            if (c.right != u) {
                c.lm = compute_dfdv(c.right, v, min_lm);
                dfdv += c.lm * c.left.scale;
                if (!c.equality && (min_lm[0] == null || c.lm < min_lm[0].lm)) {
                    min_lm[0] = c;
                }
            }
        }
        for (int i = 0, n = v.activeIn.size(); i < n; i++) {
            Constraint c = v.activeIn.get(i);
            if (c.left != u) {
                c.lm = -compute_dfdv(c.left, v, min_lm);
                dfdv -= c.lm * c.right.scale;
                if (!c.equality && (min_lm[0] == null || c.lm < min_lm[0].lm)) {
                    min_lm[0] = c;
                }
            }
        }
        return dfdv / v.scale;
    }

    double compute_dfdv(Variable v, Variable u) {
        double dfdv = v.dfdv();
        for (int i = 0, n = v.activeOut.size(); i < n; i++) {
            Constraint c = v.activeOut.get(i);
            if (c.right != u) {
                c.lm = compute_dfdv(c.right, v);
                dfdv += c.lm * c.left.scale;
            }
        }
        for (int i = 0, n = v.activeIn.size(); i < n; i++) {
            Constraint c = v.activeIn.get(i);
            if (c.left != u) {
                c.lm = -compute_dfdv(c.left, v);
                dfdv -= c.lm * c.right.scale;
            }
        }
        return dfdv / v.scale;
    }

    private static final Constraint TRUE = new Constraint(null,null,0);

    /**
     * Search for the constraint with the smallest lm on the path from lv to rv.
     */
    Constraint split_path(Variable r, Variable v, Variable u, boolean desperation) {
        for (int i = 0, n = v.activeIn.size(); i < n; i++) {
            Constraint c = v.activeIn.get(i);
            if (c.left != u) {
                if (c.left == r) {
                    if (desperation && !c.equality) return c;
                    else return TRUE;
                } else {
                    var m = split_path(r, c.left, v, desperation);
                    if (m != null) {
                        if (desperation && !c.equality && (m == TRUE || c.lm < m.lm)) {
                            m = c;
                        }
                        return m;
                    }
                }
            }
        }
        for (int i = 0, n = v.activeOut.size(); i < n; i++) {
            Constraint c = v.activeOut.get(i);
            if (c.right != u) {
                if (c.right == r) {
                    if (!c.equality) return c;
                    else return TRUE;
                } else {
                    var m = split_path(r, c.right, v, desperation);
                    if (m != null) {
                        if (!c.equality && (m == TRUE || c.lm < m.lm)) {
                            m = c;
                        }
                        return m;
                    }
                }
            }
        }
        return null;
    }

    Constraint split_path(Variable r, Variable v) {
        return split_path(r, v, null, false);
    }

    void reset_active_lm(Variable v, Variable u) {
        for (int i = 0, n = v.activeOut.size(); i < n; i++) {
            Constraint c = v.activeOut.get(i);
            if (c.right != u) {
                c.lm = 0;
                reset_active_lm(c.right, v);
            }
        }
        for (int i = 0, n = v.activeIn.size(); i < n; i++) {
            Constraint c = v.activeIn.get(i);
            if (c.left != u) {
                c.lm = 0;
                reset_active_lm(c.left, v);
            }
        }
    }

    /**
     * Finds the constraint with the minimum lagrange multiplier.
     */
    public Constraint findMinLM() {
        Constraint[] min_lm = {null};
        reset_active_lm(vars.getFirst(), null);
        compute_dfdv(vars.getFirst(), null, min_lm);
        return min_lm[0];
    }

    public Constraint findMinLMBetween(Variable lv, Variable rv) {
        reset_active_lm(vars.getFirst(), null);
        compute_dfdv(vars.getFirst(), null);
        final var min_lm = split_path(rv, lv);
        if (min_lm == null || min_lm == TRUE) {
            return null;
        }
        return min_lm;
    }

    /**
     * Populates block b by traversing the active constraint tree adding variables.
     */
    void populateSplitBlock(Block b, Variable v, Variable u) {
        b.addVariable(v);
        for (int i = 0, n = v.activeIn.size(); i < n; i++) {
            Constraint c = v.activeIn.get(i);
            if (c.left != u) {
                populateSplitBlock(b, c.left, v);
            }
        }
        for (int i = 0, n = v.activeOut.size(); i < n; i++) {
            Constraint c = v.activeOut.get(i);
            if (c.right != u) {
                populateSplitBlock(b, c.right, v);
            }
        }
    }


    public boolean isActiveDirectedPathBetween(Variable u, Variable v) {
        if (u == v) return true;
        for (int i = 0, n = u.activeOut.size(); i < n; i++) {
            Constraint c = u.activeOut.get(i);
            if (isActiveDirectedPathBetween(c.right, v)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Split this block because of a violated constraint between vl and vr.
     * Returns the split constraint.
     */
    public Constraint splitBetween(Variable vl, Variable vr, Block[] result) {
        Constraint c = findMinLMBetween(vl, vr);
        if (c != null) {
            Block[] lr = new Block[2];
            split(lr, c);
            result[0] = lr[0]; // lb
            result[1] = lr[1]; // rb
            deleted = true;
        }
        return c;
    }

    /**
     * Creates two new blocks, l and r, and splits this block across constraint c.
     */
    public void split(Block[] result, Constraint c) {
        c.deactivate();
        Block l = new Block(blocks);
        populateSplitBlock(l, c.left, c.right);
        Block r = new Block(blocks);
        populateSplitBlock(r, c.right, c.left);
        result[0] = l;
        result[1] = r;
    }

    /**
     * Computes the cost (squared euclidean distance from desired positions).
     */
    public double cost() {
        double c = 0;
        for (Variable v : vars) {
            double diff = v.position() - v.desiredPosition;
            c += v.weight.weight * diff * diff;
        }
        return c;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Block(posn=").append(posn).append("):");
        for (Variable v : vars) {
            sb.append(" ").append(v);
        }
        if (deleted) {
            sb.append(" Deleted!");
        }
        return sb.toString();
    }
}
