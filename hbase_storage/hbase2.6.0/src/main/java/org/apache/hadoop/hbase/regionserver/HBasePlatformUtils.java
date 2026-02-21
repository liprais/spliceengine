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

package org.apache.hadoop.hbase.regionserver;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.hbase.zookeeper.ZKConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HBase 2.x compatible version of HBasePlatformUtils.
 *
 * Key API changes from HBase 1.x to HBase 2.x:
 * - Counter class removed; writeRequestsCount/readRequestsCount are now LongAdder fields
 * - region.getMemstoreSize() renamed to region.getMemStoreDataSize()
 * - region.flushcache(boolean, boolean) renamed to region.flush(boolean)
 * - region.bulkLoadHFiles() parameter type changed
 * - ScannerContext.incrementSizeProgress() takes two args (dataSize, heapSize)
 * - ScannerContext.setScannerState() removed
 */
public class HBasePlatformUtils {

    public static void updateWriteRequests(HRegion region, long numWrites) {
        // In HBase 2.x, writeRequestsCount is a LongAdder (not Counter)
        if (region.writeRequestsCount != null) {
            region.writeRequestsCount.add(numWrites);
        }
    }

    public static void updateReadRequests(HRegion region, long numReads) {
        // In HBase 2.x, readRequestsCount is a LongAdder (not Counter)
        if (region.readRequestsCount != null) {
            region.readRequestsCount.add(numReads);
        }
    }

    public static Map<byte[], Store> getStores(HRegion region) {
        List<Store> stores = region.getStores();
        HashMap<byte[], Store> storesMap = new HashMap<>();
        for (Store store : stores) {
            storesMap.put(store.getColumnFamilyDescriptor().getName(), store);
        }
        return storesMap;
    }

    public static void flush(HRegion region) throws IOException {
        // In HBase 2.x: region.flush(boolean) replaces region.flushcache(boolean, boolean)
        region.flush(false);
    }

    public static void bulkLoadHFiles(HRegion region, List<Pair<byte[], String>> copyPaths) throws IOException {
        // In HBase 2.x, bulkLoadHFiles takes Map<byte[], List<Path>> instead of List<Pair<byte[], String>>
        Map<byte[], List<Path>> familyPaths = new java.util.HashMap<>();
        for (Pair<byte[], String> pair : copyPaths) {
            byte[] family = pair.getFirst();
            Path path = new Path(pair.getSecond());
            familyPaths.computeIfAbsent(family, k -> new java.util.ArrayList<>()).add(path);
        }
        region.bulkLoadHFiles(familyPaths, true, null);
    }

    public static long getMemstoreSize(HRegion region) {
        // In HBase 2.x: getMemstoreSize() renamed to getMemStoreDataSize()
        return region.getMemStoreDataSize();
    }

    public static long getReadpoint(HRegion region) {
        return region.getMVCC().getReadPoint();
    }

    public static void validateClusterKey(String quorumAddress) throws IOException {
        ZKConfig.validateClusterKey(quorumAddress);
    }

    public static boolean scannerEndReached(ScannerContext scannerContext) {
        // In HBase 2.x, incrementSizeProgress takes two arguments (dataSize, heapSize)
        // setScannerState()/NextState was removed; signalling is done differently
        scannerContext.setSizeLimitScope(ScannerContext.LimitScope.BETWEEN_ROWS);
        scannerContext.incrementBatchProgress(1);
        scannerContext.incrementSizeProgress(100L, 100L);
        // In HBase 2.x: return true to indicate more values exist (scanner continues)
        return true;
    }
}
