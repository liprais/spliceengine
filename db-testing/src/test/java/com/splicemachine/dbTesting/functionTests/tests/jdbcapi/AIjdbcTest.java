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
package com.splicemachine.dbTesting.functionTests.tests.jdbcapi;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.Test;

import com.splicemachine.dbTesting.junit.BaseJDBCTestCase;
import com.splicemachine.dbTesting.junit.CleanDatabaseTestSetup;
import com.splicemachine.dbTesting.junit.TestConfiguration;

/**
 * Test execution of JDBC method, isAutoincrement.
 */
public class AIjdbcTest extends BaseJDBCTestCase {

	/**
	 * Basic constructor.
	 */
	public AIjdbcTest(String name) {
		super(name);
	}
	
	/**
	 * Returns the implemented tests.
	 * 
	 * @return An instance of <code>Test</code> with the implemented tests to
	 *         run.
	 */
	public static Test suite() {
		return new CleanDatabaseTestSetup(TestConfiguration.defaultSuite(AIjdbcTest.class, false)) {
			protected void decorateSQL(Statement stmt) throws SQLException {
				stmt.execute("create table tab1 (x int, y int generated always as identity,z char(2))");
				stmt.execute("create view tab1_view (a,b) as select y,y+1 from tab1");
			}
		};
	}
	
	/**
	 * Sets the auto commit to false.
	 */
	protected void initializeConnection(Connection conn) throws SQLException {
		conn.setAutoCommit(false);
	}
	
	/**
	 * Select from base table.
	 * 
	 * @throws SQLException
	 */
	public void testSelect() throws SQLException {
		Statement s = createStatement();
		ResultSet rs;
		ResultSetMetaData rsmd;
		
		rs = s.executeQuery("select x,z from tab1");
		rsmd = rs.getMetaData();
		
		assertFalse("Column count doesn't match.", rsmd.getColumnCount() != 2);
		assertFalse("Column 1 is NOT ai.", rsmd.isAutoIncrement(1));
		assertFalse("Column 2 is NOT ai.", rsmd.isAutoIncrement(2));
		
		rs.close();
		
		rs = s.executeQuery("select y, x,z from tab1");
		rsmd = rs.getMetaData();
		
		assertFalse("Column count doesn't match.", rsmd.getColumnCount() != 3);
		assertFalse("Column 1 IS ai.", !rsmd.isAutoIncrement(1));
		assertFalse("Column 2 is NOT ai.", rsmd.isAutoIncrement(2));
		assertFalse("Column 3 is NOT ai.", rsmd.isAutoIncrement(3));
		
		rs.close();
		s.close();
	}

	/**
	 * Select from view.
	 * 
	 * @throws SQLException
	 */
	public void testSelectView() throws SQLException {
		Statement s = createStatement();
		ResultSet rs;
		ResultSetMetaData rsmd;
		
		rs = s.executeQuery("select * from tab1_view");
		rsmd = rs.getMetaData();
		
		assertFalse("Column count doesn't match.", rsmd.getColumnCount() != 2);
		assertFalse("Column 1 IS ai.", !rsmd.isAutoIncrement(1));
		assertFalse("Column 1 is NOT ai.", rsmd.isAutoIncrement(2));
		
		rs.close();
		s.close();
	}
}
