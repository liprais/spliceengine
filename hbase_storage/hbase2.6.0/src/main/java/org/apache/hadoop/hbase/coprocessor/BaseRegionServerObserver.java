/*
 * Copyright (c) 2012 - 2017 Splice Machine, Inc.
 *
 * This file is part of Splice Machine.
 * Splice Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3, or (at your option) any later version.
 * Splice Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with Splice Machine.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package org.apache.hadoop.hbase.coprocessor;

import java.util.Optional;

/**
 * Compatibility shim for HBase 2.x migration.
 * In HBase 1.x, BaseRegionServerObserver was a built-in abstract class providing
 * empty implementations of all RegionServerObserver methods. In HBase 2.x,
 * RegionServerObserver became a default interface and this class was removed.
 *
 * This shim restores backward compatibility.
 */
public abstract class BaseRegionServerObserver implements RegionServerObserver, RegionServerCoprocessor {

    @Override
    public Optional<RegionServerObserver> getRegionServerObserver() {
        return Optional.of(this);
    }
}
