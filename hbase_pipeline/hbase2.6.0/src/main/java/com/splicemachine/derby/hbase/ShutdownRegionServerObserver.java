/*
 * Copyright (c) 2012 - 2018 Splice Machine, Inc.
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

package com.splicemachine.derby.hbase;

import com.splicemachine.lifecycle.DatabaseLifecycleManager;
import com.splicemachine.utils.SpliceLogUtils;
import org.apache.hadoop.hbase.CoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionServerCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionServerCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionServerObserver;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.util.Optional;

/**
 * HBase 2.x compatible version of ShutdownRegionServerObserver.
 *
 * Key changes from HBase 1.x to HBase 2.x:
 * - RegionServerObserver became a default interface (all methods have defaults)
 * - preMerge/postMerge methods were removed from RegionServerObserver (moved to MasterObserver)
 * - preReplicateLogEntries/postReplicateLogEntries removed
 * - AdminProtos.WALEntry moved to different package
 * - RegionServerCoprocessor interface required for coprocessor registration
 *
 * This coprocessor must be listed in hbase.coprocessor.regionserver.classes.
 */
public class ShutdownRegionServerObserver implements RegionServerObserver, RegionServerCoprocessor {

    private static final Logger LOG = Logger.getLogger(ShutdownRegionServerObserver.class);

    @Override
    public Optional<RegionServerObserver> getRegionServerObserver() {
        return Optional.of(this);
    }

    @Override
    public void preStopRegionServer(ObserverContext<RegionServerCoprocessorEnvironment> env) throws IOException {
        LOG.warn("shutting down splice on this node/JVM");
        try {
            DatabaseLifecycleManager.manager().shutdown();
        } catch (Exception e) {
            SpliceLogUtils.warn(LOG, "splice machine shut down with error", e);
        }
    }

    // Coprocessor lifecycle

    @Override
    public void start(CoprocessorEnvironment env) throws IOException {
    }

    @Override
    public void stop(CoprocessorEnvironment env) throws IOException {
    }
}
