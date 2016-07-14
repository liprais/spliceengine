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
package com.splicemachine.db.iapi.services.io;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Test case for InputStreamUtil.
 */
public class InputStreamUtilTest extends TestCase {


    public void testNullStream() throws IOException{
        try{
            InputStreamUtil.skipUntilEOF(null);
            fail("Null InputStream is accepted!");
        }catch (NullPointerException e) {
            assertTrue(true);
        }

        try{
            InputStreamUtil.skipFully(null, 0);
            fail("Null InputStream is accepted!");
        }catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    public void testSkipUtilEOFWithOddLength() throws IOException{
        int[] lengths = {0, 1};

        for(int i = 0; i < lengths.length; i++){
            int length = lengths[i];
            InputStream is = new ByteArrayInputStream(new byte[length]);
            assertEquals(length, InputStreamUtil.skipUntilEOF(is));
        }
    }

    public void testSkipUtilEOF() throws IOException{
        int[] lengths = {1024, 1024 * 1024};

        for(int i = 0; i < lengths.length; i++){
            int length = lengths[i];
            InputStream is = new ByteArrayInputStream(new byte[length]);
            assertEquals(length, InputStreamUtil.skipUntilEOF(is));
        }
    }

    public void testSkipFully() throws IOException{
        int length = 1024;

        InputStream is = new ByteArrayInputStream(new byte[length]);
        InputStreamUtil.skipFully(is, length);
        assertEquals(0, InputStreamUtil.skipUntilEOF(is));

        is = new ByteArrayInputStream(new byte[length]);
        InputStreamUtil.skipFully(is, length - 1);
        assertEquals(1, InputStreamUtil.skipUntilEOF(is));

        is = new ByteArrayInputStream(new byte[length]);
        try {
            InputStreamUtil.skipFully(is, length + 1);
            fail("Should have Meet EOF!");
        } catch (EOFException e) {
            assertTrue(true);
        }
        assertEquals(0, InputStreamUtil.skipUntilEOF(is));
    }

    /**
     * Returns a suite of tests.
     */
    public static Test suite() {
        return new TestSuite(InputStreamUtilTest.class, "InputStreamUtil tests");
    }
}
