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

import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.ByteArrayComparable;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.regionserver.*;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.hbase.wal.WALEdit;

import java.io.IOException;
import java.util.List;
import java.util.NavigableSet;
import java.util.Optional;

/**
 * Compatibility shim for HBase 2.x migration.
 * In HBase 1.x, BaseRegionObserver was a built-in abstract class providing
 * empty implementations of all RegionObserver methods. In HBase 2.x, RegionObserver
 * became a default interface and BaseRegionObserver was removed.
 *
 * This shim restores backward compatibility by:
 * 1. Implementing RegionCoprocessor (required in HBase 2.x)
 * 2. Implementing RegionObserver (now a default interface in HBase 2.x)
 * 3. Providing stub methods for HBase 1.x method signatures that were
 *    removed or changed in HBase 2.x
 */
public abstract class BaseRegionObserver implements RegionObserver, RegionCoprocessor {

    @Override
    public Optional<RegionObserver> getRegionObserver() {
        return Optional.of(this);
    }

    // HBase 1.x-only methods (removed from RegionObserver in HBase 2.x)
    // Subclasses may override these; they won't be invoked by HBase 2.x directly
    // but the @Override annotations in subclasses will compile correctly.

    public void preGetClosestRowBefore(ObserverContext<RegionCoprocessorEnvironment> e,
                                       byte[] row, byte[] family, Result result)
            throws IOException {
    }

    // HBase 1.x signature for preCheckAndDelete (uses CompareFilter.CompareOp)
    public boolean preCheckAndDelete(ObserverContext<RegionCoprocessorEnvironment> e,
                                     byte[] row, byte[] family, byte[] qualifier,
                                     CompareFilter.CompareOp compareOp,
                                     ByteArrayComparable comparator,
                                     Delete delete, boolean result)
            throws IOException {
        return result;
    }

    // HBase 1.x signature for preCheckAndPut (uses CompareFilter.CompareOp)
    public boolean preCheckAndPut(ObserverContext<RegionCoprocessorEnvironment> e,
                                  byte[] row, byte[] family, byte[] qualifier,
                                  CompareFilter.CompareOp compareOp,
                                  ByteArrayComparable comparator,
                                  Put put, boolean result)
            throws IOException {
        return result;
    }

    // HBase 1.x signature for postCompact (3-parameter version)
    public void postCompact(ObserverContext<RegionCoprocessorEnvironment> e,
                            Store store, StoreFile resultFile)
            throws IOException {
    }

    // HBase 1.x+ signature for postCompact (4-parameter version with CompactionRequest)
    public void postCompact(ObserverContext<RegionCoprocessorEnvironment> e,
                            Store store, StoreFile resultFile,
                            CompactionRequest request)
            throws IOException {
    }

    // HBase 1.x signature for preFlush (3-parameter, returns InternalScanner)
    public InternalScanner preFlush(ObserverContext<RegionCoprocessorEnvironment> e,
                                    Store store, InternalScanner scanner)
            throws IOException {
        return scanner;
    }

    // HBase 1.x signature for preCompact (returns InternalScanner)
    public InternalScanner preCompact(ObserverContext<RegionCoprocessorEnvironment> e,
                                      Store store, InternalScanner scanner,
                                      ScanType scanType)
            throws IOException {
        return scanner;
    }

    // HBase 1.x signature for preScannerOpen (returns RegionScanner)
    public RegionScanner preScannerOpen(ObserverContext<RegionCoprocessorEnvironment> e,
                                        Scan scan, RegionScanner s)
            throws IOException {
        return s;
    }

    // HBase 1.x preStoreScannerOpen (returns KeyValueScanner)
    public KeyValueScanner preStoreScannerOpen(ObserverContext<RegionCoprocessorEnvironment> c,
                                               Store store, Scan scan,
                                               NavigableSet<byte[]> targetCols,
                                               KeyValueScanner s)
            throws IOException {
        return s;
    }

    // HBase 1.x preBulkLoadHFile signature
    public void preBulkLoadHFile(ObserverContext<RegionCoprocessorEnvironment> ctx,
                                 List<Pair<byte[], String>> familyPaths)
            throws IOException {
    }

    // HBase 1.x preDelete with WALEdit and Durability
    public void preDelete(ObserverContext<RegionCoprocessorEnvironment> e,
                          Delete delete, WALEdit edit,
                          org.apache.hadoop.hbase.client.Durability durability)
            throws IOException {
    }

    // HBase 1.x prePut with WALEdit and Durability
    public void prePut(ObserverContext<RegionCoprocessorEnvironment> e,
                       Put put, WALEdit edit,
                       org.apache.hadoop.hbase.client.Durability writeToWAL)
            throws IOException {
    }

    // HBase 1.x preAppend (returns Result)
    public Result preAppend(ObserverContext<RegionCoprocessorEnvironment> e,
                            Append append)
            throws IOException {
        return null;
    }

    // HBase 1.x preIncrement (returns Result)
    public Result preIncrement(ObserverContext<RegionCoprocessorEnvironment> e,
                               Increment increment)
            throws IOException {
        return null;
    }

    // HBase 1.x preExists
    public boolean preExists(ObserverContext<RegionCoprocessorEnvironment> e,
                             Get get, boolean exists)
            throws IOException {
        return exists;
    }

    // HBase 1.x postFlush with StoreFile
    public void postFlush(ObserverContext<RegionCoprocessorEnvironment> e,
                          Store store, StoreFile resultFile)
            throws IOException {
    }

    // HBase 1.x preSplit
    public void preSplit(ObserverContext<RegionCoprocessorEnvironment> c,
                         byte[] splitRow)
            throws IOException {
    }

    // HBase 1.x postCompleteSplit
    public void postCompleteSplit(ObserverContext<RegionCoprocessorEnvironment> e)
            throws IOException {
    }

    // HBase 1.x postRollBackSplit
    public void postRollBackSplit(ObserverContext<RegionCoprocessorEnvironment> ctx)
            throws IOException {
    }

    // HBase 1.x preClose
    public void preClose(ObserverContext<RegionCoprocessorEnvironment> c,
                         boolean abortRequested)
            throws IOException {
    }

    // HBase 1.x postClose
    public void postClose(ObserverContext<RegionCoprocessorEnvironment> e,
                          boolean abortRequested) {
    }

    // HBase 1.x preBatchMutate
    public void preBatchMutate(ObserverContext<RegionCoprocessorEnvironment> c,
                               MiniBatchOperationInProgress<Mutation> miniBatchOp)
            throws IOException {
    }
}
