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

/**
 * Contains lists of junctions and connectors created and deleted during
 * hyperedge improvement.
 * Corresponds to HyperedgeNewAndDeletedObjectLists in hyperedge.h.
 */
public class HyperedgeNewAndDeletedObjectLists {

    /** Junctions created during hyperedge improvement. */
    public final List<JunctionRef> newJunctionList = new ArrayList<>();

    /** Connectors created during hyperedge improvement. */
    public final List<ConnRef> newConnectorList = new ArrayList<>();

    /** Junctions deleted during hyperedge improvement. */
    public final List<JunctionRef> deletedJunctionList = new ArrayList<>();

    /** Connectors deleted during hyperedge improvement. */
    public final List<ConnRef> deletedConnectorList = new ArrayList<>();

    /** Connectors whose routes changed during hyperedge improvement. */
    public final List<ConnRef> changedConnectorList = new ArrayList<>();
}
