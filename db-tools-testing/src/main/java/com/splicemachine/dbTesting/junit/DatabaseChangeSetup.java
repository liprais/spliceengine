/*
 * Apache Derby is a subproject of the Apache DB project, and is licensed under
 * the Apache License, Version 2.0 (the "License"); you may not use these files
 * except in compliance with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Splice Machine, Inc. has modified this file.
 *
 * All Splice Machine modifications are Copyright 2012 - 2016 Splice Machine, Inc.,
 * and are licensed to you under the License; you may not use this file except in
 * compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */
package com.splicemachine.dbTesting.junit;

import junit.framework.Test;

/**
 * Change the current configuration's database name at setup.
 * Previous configuration is restored on tearDown.
 *
 */
public  final class DatabaseChangeSetup extends ChangeConfigurationSetup {

    private final String logicalDbName;
    private final String dbName;
    private final boolean defaultDb;

    private TestConfiguration   _myTestConfiguration;
    
    public DatabaseChangeSetup(Test test, String logicalDbName, String dbName, boolean defaultDb) {
        super(test);
        this.logicalDbName = logicalDbName;
        this.dbName = dbName;
        this.defaultDb = defaultDb;
   }

    TestConfiguration getNewConfiguration(TestConfiguration old) {
        _myTestConfiguration = new TestConfiguration(old, logicalDbName, dbName, defaultDb);
        return _myTestConfiguration;
    }

    public  TestConfiguration   getTestConfiguration()  { return _myTestConfiguration; }
    
    public  String  physicalDatabaseName() { return dbName; }
    
}
