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

package com.splicemachine.db.iapi.types;

import com.splicemachine.db.iapi.error.StandardException;

/**
 * The ConcatableDataValue interface corresponds to the
 * SQL 92 string value data type.  It is implemented by
 * datatypes that have a length, and can be concatenated.
 * It is implemented by the character datatypes and the
 * bit datatypes.  
 *
 * The following methods are defined herein:
 *		charLength()
 *
 * The following is defined by the sub classes (bit and char)
 *		concatenate()
 * 
 */
public interface ConcatableDataValue extends DataValueDescriptor, VariableSizeDataValue
{

	/**
	 * The SQL char_length() function.
	 *
	 * @param result	The result of a previous call to this method,
	 *					null if not called yet.
	 *
	 * @return	A NumberDataValue containing the result of the char_length
	 *
	 * @exception StandardException		Thrown on error
	 */
	public NumberDataValue charLength(NumberDataValue result)
							throws StandardException;

	/**
	 * substr() function matchs DB2 syntax and behaviour.
	 *
	 * @param start		Start of substr
	 * @param length	Length of substr
	 * @param result	The result of a previous call to this method,
	 *					null if not called yet.
	 * @param maxLen	Maximum length of the result string
	 *
	 * @return	A ConcatableDataValue containing the result of the substr()
	 *
	 * @exception StandardException		Thrown on error
	 */
	public ConcatableDataValue substring(
				NumberDataValue start,
				NumberDataValue length,
				ConcatableDataValue result,
				int maxLen)
		throws StandardException;

	/**
	 * The SQL replace() function.
	 *
	 * @param fromStr the substring to be found and replaced
	 *        within this containing string
	 * @param toStr the string to replace the provided substring
	 *        <code>fromStr</code> within this containing string
	 * @param result the result of a previous call to this method,
	 *        or null if not called yet.
	 *
	 * @return ConcatableDataValue containing the result of the replace()
	 *
	 * @exception StandardException if an error occurs
	 */
	public ConcatableDataValue replace(
            StringDataValue fromStr,
            StringDataValue toStr,
            ConcatableDataValue result)
            throws StandardException;
}
