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

package com.splicemachine.db.iapi.error;

import java.sql.SQLException;

import com.splicemachine.db.impl.jdbc.EmbedSQLException;


/**
	Class that wraps StandardExceptions in a SQLException.
	This is used to make any public API methods always
	throw SQLException rather than a random collection.
	This wrapping is also special cased by TypeStatementException
	to avoid double wrapping of some errors.
	<P>
	This will get cleaned up in main.
 */
public class PublicAPI
{
	/**
		Generates a SQLException for signalling that the
		operation failed due to a database error.
	 */
	public static SQLException wrapStandardException(StandardException se) {
		return EmbedSQLException.wrapStandardException(se.getMessage(),
			se.getMessageId(), se.getSeverity(), se);
	}
}
