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

package com.splicemachine.dbTesting.functionTests.tests.store;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.splicemachine.db.tools.ij;

/**
 * This class will do the setup for testing recovery after backup.
 * This test will insert some records into a table, do a backup and
 * end without shutting down the database.  The succeeding test,
 * RecoveryAfterBackup, will then do recovery of the database.
 * 
 * @see RecoveryAfterBackup
 */

public class RecoveryAfterBackupSetup
{
    
    public static void main(String[] argv) throws Throwable 
    {
        try {
            ij.getPropertyArg(argv); 
            Connection conn = ij.startJBMS();
            conn.setAutoCommit(true);

            System.out.println("Connection has been opened.");
            Statement s = conn.createStatement();
            try { // Drop table if it exists
                s.execute("DROP TABLE t1");
            } catch (SQLException e) {
                if (e.getSQLState().equals("42Y55")) {
                    // IGNORE. Table did not exist. That is our target.
                } else {
                    throw e;
                }
            }

            System.out.println("Creating table and inserting two records.");
            s.execute("CREATE TABLE t1(a INT)");
            s.execute("INSERT INTO t1 VALUES(0)");
            s.execute("INSERT INTO t1 VALUES(1)");

            System.out.println("Performing backup...");
            s.execute("CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE_AND_ENABLE_LOG_ARCHIVE_MODE('extinout/mybackup', 0)");
            System.out.println("Backup completed.  Test finished.");
        } catch (SQLException sqle) {
            com.splicemachine.db.tools.JDBCDisplayUtil.ShowSQLException(System.out,
                                                                    sqle);
            sqle.printStackTrace(System.out);
        }
    }
}
