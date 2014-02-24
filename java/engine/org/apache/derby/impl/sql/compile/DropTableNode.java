/*

   Derby - Class org.apache.derby.impl.sql.compile.DropTableNode

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to you under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */

package	org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.ExceptionSeverity;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.reference.SQLState;
import org.apache.derby.iapi.services.sanity.SanityManager;
import org.apache.derby.iapi.sql.StatementType;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.execute.ConstantAction;

/**
 * A DropTableNode is the root of a QueryTree that represents a DROP TABLE
 * statement.
 *
 */

public class DropTableNode extends DDLStatementNode
{
	private long		conglomerateNumber;
	private int			dropBehavior;
	private	TableDescriptor	td;

	/**
	 * Intializer for a DropTableNode
	 *
	 * @param dropObjectName	The name of the object being dropped
	 * @param dropBehavior		Drop behavior (RESTRICT | CASCADE)
	 *
	 */

	public void init(Object dropObjectName, Object dropBehavior)
		throws StandardException
	{
		initAndCheck(dropObjectName);
		this.dropBehavior = ((Integer) dropBehavior).intValue();
	}

	/**
	 * Convert this object to a String.  See comments in QueryTreeNode.java
	 * for how this should be done for tree printing.
	 *
	 * @return	This object as a String
	 */

	public String toString()
	{
		if (SanityManager.DEBUG)
		{
			return super.toString() +
				"conglomerateNumber: " + conglomerateNumber + "\n" +
				"td: " + ((td == null) ? "null" : td.toString()) + "\n" +
				"dropBehavior: " + "\n" + dropBehavior + "\n";
		}
		else
		{
			return "";
		}
	}

	public String statementToString()
	{
		return "DROP TABLE";
	}

	/**
	 * Bind this LockTableNode.  This means looking up the table,
	 * verifying it exists and getting the heap conglomerate number.
	 *
	 *
	 * @exception StandardException		Thrown on error
	 */

	public void bindStatement() throws StandardException
	{
		CompilerContext			cc = getCompilerContext();

		try {
			td = getTableDescriptor();
		} catch (StandardException e) {
	        if (e.getMessageId().equals(SQLState.LANG_OBJECT_DOES_NOT_EXIST) && this.dropBehavior == StatementType.DROP_IF_EXISTS) {
	        	// this case is only a warning - no exception
	            e.setSeverity(ExceptionSeverity.WARNING_SEVERITY);
	            throw e;
	        }
		}

		conglomerateNumber = td.getHeapConglomerateId();

		/* Get the base conglomerate descriptor */
		ConglomerateDescriptor cd = td.getConglomerateDescriptor(conglomerateNumber);

		/* Statement is dependent on the TableDescriptor and ConglomerateDescriptor */
		cc.createDependency(td);
		cc.createDependency(cd);
	}

	/**
	 * Return true if the node references SESSION schema tables (temporary or permanent)
	 *
	 * @return	true if references SESSION schema tables, else false
	 *
	 * @exception StandardException		Thrown on error
	 */
	public boolean referencesSessionSchema()
		throws StandardException
	{
		//If table being dropped is in SESSION schema, then return true. 
		return isSessionSchema(td.getSchemaDescriptor());
	}

	// inherit generate() method from DDLStatementNode


	/**
	 * Create the Constant information that will drive the guts of Execution.
	 *
	 * @exception StandardException		Thrown on failure
	 */
	public ConstantAction	makeConstantAction() throws StandardException
	{
		return	getGenericConstantActionFactory().getDropTableConstantAction(
			getFullName(),
			getRelativeName(),
			getSchemaDescriptor(td.getTableType() !=
								TableDescriptor.GLOBAL_TEMPORARY_TABLE_TYPE,
								true),
			conglomerateNumber,
			td.getUUID(),
			dropBehavior);
	}
}
