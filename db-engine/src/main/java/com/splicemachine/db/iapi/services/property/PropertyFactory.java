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

package com.splicemachine.db.iapi.services.property;

import com.splicemachine.db.iapi.error.StandardException;

import com.splicemachine.db.iapi.store.access.TransactionController;

import java.util.Properties;
import java.io.Serializable;
import java.util.Dictionary;

/**
  Module interface for an Property validation.  

  <p>
  An PropertyFactory is typically obtained from the Monitor:
  <p>
  <blockquote><pre>
	// Get the current validation factory.
	PropertyFactory af;
	af = (PropertyFactory) Monitor.findServiceModule(this, com.splicemachine.db.iapi.reference.Module.PropertyFactory);
  </pre></blockquote>
**/

public interface PropertyFactory
{
    /**************************************************************************
     * methods that are Property related.
     **************************************************************************
     */

    /**
     * Add a callback for a change in any property value.
	 * <BR>
     * The callback is made in the context of the transaction making the change.
     *
     * @param who   which object is called
     **/
	public void addPropertySetNotification(
    PropertySetCallback     who);

    /**
     * Validate a Property set.
     * <p>
     * Validate a Property set by calling all the registered property set
     * notification functions with .
     *
	 * @param p Properties to validate.
	 * @param ignore Properties to not validate in p. Usefull for properties
	 *        that may not be set after boot. 
     *
	 * @exception StandardException Throws if p fails a check.
     **/
	public void verifyPropertySet(
    Properties p, 
    Properties ignore) 
        throws StandardException;

	/**
	 * validation a single property
	 */
	public void validateSingleProperty(String key,
						  Serializable value,
						  Dictionary set)
		throws StandardException;

	/**
	   
	 */
	public Serializable doValidateApplyAndMap(TransactionController tc,
											 String key, Serializable value,
											 Dictionary d, boolean dbOnlyProperty)
		throws StandardException;


	/**
	  Call the property set callbacks to map a proposed property value
	  to a value to save.
	  <P>
	  The caller must run this in a block synchronized on this
	  to serialize validations with changes to the set of
	  property callbacks
	  */
	public Serializable doMap(String key,
							 Serializable value,
							 Dictionary set)
		throws StandardException;
}
