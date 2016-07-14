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

import java.sql.*;
import java.io.*;
import java.util.*;

/**
 *  Test that the two new encryption properties
 *	DATA_ENCRYPT_ALGORITHM_VERSION="data_encrypt_algorithm_version"
 *  LOG_ENCRYPT_ALGORITHM_VERSION="log_encrypt_algorithm_version"
 *	exist and verify the version. Note, these values start off with 1.
 */
public class Beetle6038  {

	public static void main(String[] args)
		throws Exception
	{
		String driver = "com.splicemachine.db.jdbc.EmbeddedDriver";
		Class.forName(driver).newInstance();
		String dburl = "jdbc:splice:Beetle6038Db;create=true;dataEncryption=true;bootPassword=Thursday;encryptionAlgorithm=DES/CBC/NoPadding";

		Connection conn = DriverManager.getConnection(dburl);
		conn.close();
		conn = DriverManager.getConnection(dburl);
		conn.close();

		// read in the properties in the service.properties file of the db
		Properties serviceProperties = new Properties();
		String systemhome =  System.getProperty("derby.system.home");
		File f = new File(systemhome + File.separatorChar + "Beetle6038Db" + File.separatorChar + "service.properties");
		serviceProperties.load(new FileInputStream(f.getCanonicalPath()));

		// check if the properties are set
		checkProperty("data_encrypt_algorithm_version",serviceProperties);
		checkProperty("log_encrypt_algorithm_version",serviceProperties);
	}

	public static void checkProperty(String name,Properties props)
	{
		String value = props.getProperty(name);

		if( value == null )
			System.out.println("Test failed!! - "+name + " not set in service.properties as expected");
		else
			System.out.println(name+"="+value);
	}


}
