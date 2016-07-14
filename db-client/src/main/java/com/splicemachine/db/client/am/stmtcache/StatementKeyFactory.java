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

package com.splicemachine.db.client.am.stmtcache;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * A factory for creating JDBC statement keys for use with the JDBC statement
 * cache.
 *
 * @see JDBCStatementCache
 */
//@ThreadSafe
public final class StatementKeyFactory {

    private static final boolean CALLABLE = true;
    private static final boolean PREPARED = false;

    /** Instantiation not allowed. */
    private StatementKeyFactory() {};

    /**
     * Creates a key for a query with default settings.
     * <p>
     * Defaults are according to the JDBC standard; result set type will be
     * <code>ResultSet.TYPE_FORWARD_ONLY</code>, concurrency will be
     * <code>ResultSet.CONCUR_READ_ONLY</code> and the statement will not
     * return auto-generated keys.
     *
     * @param sql SQL query string
     * @param schema current compilation schema
     * @param holdability result set holdability
     * @return A statement key.
     */
    public static StatementKey newPrepared(
            String sql, String schema, int holdability) {
        return new StatementKey(PREPARED, sql, schema,
                ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY,
                holdability, Statement.NO_GENERATED_KEYS);
    }

    /**
     * Creates a key for a query specifying whether auto-generated keys
     * shall be returned.
     * <p>
     * Unspecified settings will be according to the JDBC standard; result set
     * type will be <code>ResultSet.TYPE_FORWARD_ONLY</code>, concurrency will
     * be <code>ResultSet.CONCUR_READ_ONLY</code>.
     *
     * @param sql SQL query string
     * @param schema current compilation schema
     * @param holdability result set holdability
     * @param autogeneratedKeys tells whether or not to reutrn auto-generated
     *      keys
     * @return A statement key.
     */
    public static StatementKey newPrepared(
            String sql, String schema, int holdability, int autogeneratedKeys) {
        return new StatementKey(PREPARED, sql, schema,
                                ResultSet.TYPE_FORWARD_ONLY,
                                ResultSet.CONCUR_READ_ONLY,
                                holdability, autogeneratedKeys);
    }

    /**
     * Creates a key for a query specifying result set type and concurrency.
     * <p>
     * The returned key is for a statement not returning auto-generated keys.
     *
     * @param sql SQL query string
     * @param schema current compilation schema
     * @param rst result set type
     * @param rsc result set concurrency level
     * @param rsh result set holdability
     * @return A statement key.
     */
    public static StatementKey newPrepared(
            String sql, String schema, int rst, int rsc, int rsh) {
        return new StatementKey(PREPARED, sql, schema, rst, rsc, rsh,
                                Statement.NO_GENERATED_KEYS);
    }

    /**
     * Creates a key for a callable statement.
     * <p>
     * Unspecified settings will be according to the JDBC standard; result set
     * type will be <code>ResultSet.TYPE_FORWARD_ONLY</code>, concurrency will
     * be <code>ResultSet.CONCUR_READ_ONLY</code>.
     *
     * @param sql SQL query string
     * @param schema current compilation schema
     * @param holdability result set holdability
     * @return A statement key.
     */
    public static StatementKey newCallable(
            String sql, String schema, int holdability) {
        return newCallable(sql, schema, ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY, holdability);
    }

    /**
     * Creates a key for a callable statement specifying result set type and
     * concurrency.
     * <p>
     * The returned key is for a statement not returning auto-generated keys.
     *
     * @param sql SQL query string
     * @param schema current compilation schema
     * @param rst result set type
     * @param rsc result set concurrency level
     * @param rsh result set holdability
     * @return A statement key.
     */
    public static StatementKey newCallable(
            String sql, String schema, int rst, int rsc, int rsh) {
        return new StatementKey(CALLABLE, sql, schema, rst, rsc, rsh,
                                Statement.NO_GENERATED_KEYS);
    }
}
