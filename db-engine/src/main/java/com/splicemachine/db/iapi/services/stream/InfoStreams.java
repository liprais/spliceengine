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

package com.splicemachine.db.iapi.services.stream;

/**
 *
 * The Basic Services provide InfoStreams for reporting
 * information.
 * <p>
 * When creating a message for a stream,
 * you can create an initial entry with header information
 * and then append to it as many times as desired.
 * <p>
 * 
 * @see HeaderPrintWriter
 */
public interface InfoStreams {

	/**
	 Return the default stream. If the default stream could not be set up as requested then
	 it points indirectly to System.err.
	 * 
	 * @return the default stream.
	 */
	HeaderPrintWriter stream();
}
