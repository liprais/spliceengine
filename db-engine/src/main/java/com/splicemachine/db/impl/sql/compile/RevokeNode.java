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

package com.splicemachine.db.impl.sql.compile;

import com.splicemachine.db.iapi.sql.execute.ConstantAction;
import com.splicemachine.db.iapi.services.sanity.SanityManager;
import com.splicemachine.db.iapi.error.StandardException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a REVOKE statement.
 */
public class RevokeNode extends DDLStatementNode
{
    private PrivilegeNode privileges;
    private List grantees;

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
            StringBuffer sb = new StringBuffer();
            for( Iterator it = grantees.iterator(); it.hasNext();)
            {
                if( sb.length() > 0)
                    sb.append( ",");
                sb.append( it.next().toString());
            }
			return super.toString() +
                   privileges.toString() +
                   "TO: \n" + sb.toString() + "\n";
		}
		else
		{
			return "";
		}
    } // end of toString

	public String statementToString()
	{
        return "REVOKE";
    }

    
    /**
     * Initialize a RevokeNode.
     *
     * @param privileges PrivilegesNode
     * @param grantees List
     */
    public void init( Object privileges,
                      Object grantees)
    {
        this.privileges = (PrivilegeNode) privileges;
        this.grantees = (List) grantees;
    }

    /**
     * Bind this RevokeNode. Resolve all table, column, and routine references.
     *
     *
     * @exception StandardException	Standard error policy.
     */
	public void bindStatement() throws StandardException
	{
        privileges = (PrivilegeNode) privileges.bind( new HashMap(), grantees, false);
    } // end of bind


	/**
	 * Create the Constant information that will drive the guts of Execution.
	 *
	 * @exception StandardException	Standard error policy.
	 */
	public ConstantAction makeConstantAction() throws StandardException
	{
        return getGenericConstantActionFactory().getRevokeConstantAction( privileges.makePrivilegeInfo(),
                                                                          grantees);
    }
}
