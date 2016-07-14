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

package com.splicemachine.dbTesting.functionTests.harness;

import java.util.Enumeration;
import java.util.Properties;

/*
 **
 **	Keeps a copy of the system properties saved at a critical early
 **	point during the running of the test harness.  Uses this copy
 **	to create new copies which can then be mussed up and thrown
 **	away, as needed.
 */

public class ManageSysProps
{

	private static Properties savedSysProps = null;

	public static void saveSysProps() {
		Properties sp = System.getProperties();
		savedSysProps = new Properties();
		String key = null;
		for (Enumeration e = sp.propertyNames(); e.hasMoreElements();) {
			key = (String)e.nextElement();
			savedSysProps.put(key, sp.getProperty(key));
		}
	}

	// reset the system properties to prevent confusion
	// when running with java threads
	public static void resetSysProps() {
		String key = null;
		Properties nup = new Properties();
		for (Enumeration e = savedSysProps.propertyNames(); e.hasMoreElements();) {
			key = (String)e.nextElement();
			nup.put(key, savedSysProps.getProperty(key));
		}
		System.setProperties(nup);
	}
}
