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
 * A block structure defined over the variables such that each block contains
 * 1 or more variables, with the invariant that all constraints inside a block
 * are satisfied by keeping the variables fixed relative to one another.
 * Translated from vpsc.h/vpsc.cpp in libavoid C++.
 */
public class Blocks {
    private final List<Block> m_blocks;
    public long blockTimeCtr;

    public Blocks(List<Variable> vs) {
        this.blockTimeCtr = 0;
        this.m_blocks = new ArrayList<>();
        for (Variable v : vs) {
            Block b = new Block(this, v);
            m_blocks.add(b);
            b.timeStamp = blockTimeCtr;
        }
    }

    public int size() {
        return m_blocks.size();
    }

    public Block at(int index) {
        return m_blocks.get(index);
    }

    public void insert(Block block) {
        m_blocks.add(block);
    }

    public void mergeLeft(Block r) {
        r.timeStamp = ++blockTimeCtr;
        r.setUpInConstraints();
        Constraint c = r.findMinInConstraint();
        while (c != null && c.slack() < 0) {
            r.deleteMinInConstraint();
            Block l = c.left.block;
            if (l.in == null) l.setUpInConstraints();
            double dist = c.right.offset - c.left.offset - c.gap;
            if (r.vars.size() < l.vars.size()) {
                dist = -dist;
                // swap l and r
                Block tmp = l;
                l = r;
                r = tmp;
            }
            blockTimeCtr++;
            r.mergeBlock(l, c, dist);
            r.mergeIn(l);
            r.timeStamp = blockTimeCtr;
            removeBlock(l);
            c = r.findMinInConstraint();
        }
    }

    public void mergeRight(Block l) {
        l.setUpOutConstraints();
        Constraint c = l.findMinOutConstraint();
        while (c != null && c.slack() < 0) {
            l.deleteMinOutConstraint();
            Block r = c.right.block;
            r.setUpOutConstraints();
            double dist = c.left.offset + c.gap - c.right.offset;
            if (l.vars.size() > r.vars.size()) {
                dist = -dist;
                // swap l and r
                Block tmp = l;
                l = r;
                r = tmp;
            }
            l.mergeBlock(r, c, dist);
            l.mergeOut(r);
            removeBlock(r);
            c = l.findMinOutConstraint();
        }
    }

    /**
     * Split block b on constraint c, then merge the resulting halves with
     * their neighbours.
     * Translated from vpsc.cpp:651-673 (Blocks::split).
     */
    public void split(Block b, Block[] result, Constraint c) {
        b.split(result, c);
        Block l = result[0];
        Block r = result[1];
        m_blocks.add(l);
        m_blocks.add(r);
        r.posn = b.posn;
        mergeLeft(l);
        // r may have been merged!
        r = c.right.block;
        r.updateWeightedPosition();
        mergeRight(r);
        removeBlock(b);
        assert !Double.isNaN(l.posn);
        assert !Double.isNaN(r.posn);
    }

    /**
     * Clears up deleted blocks from the blocks list.
     * Handles removal in-place with a single linear pass.
     */
    public void cleanup() {
        int i = 0;
        int length = m_blocks.size();
        for (int j = 0; j < length; j++) {
            if (!m_blocks.get(j).deleted) {
                if (j > i) {
                    m_blocks.set(i, m_blocks.get(j));
                }
                i++;
            }
        }
        // Trim the list
        while (m_blocks.size() > i) {
            m_blocks.removeLast();
        }
    }

    public double cost() {
        double c = 0;
        for (Block b : m_blocks) {
            c += b.cost();
        }
        return c;
    }

    private void removeBlock(Block doomed) {
        doomed.deleted = true;
    }
}
