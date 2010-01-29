package org.hisp.dhis.system.util;

/*
 * Copyright (c) 2004-2005, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.BufferedInputStream;

import junit.framework.TestCase;

/**
 *
 * @author bobj
 * @version created 19-Dec-2009
 */
public class StreamUtilsTest
        extends TestCase {

    public static BufferedInputStream zipStream;

    public static BufferedInputStream gzipStream;

    @Override
    public void setUp()
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        zipStream = new BufferedInputStream(classLoader.getResourceAsStream( "dxfA.zip" ));

        gzipStream = new BufferedInputStream(classLoader.getResourceAsStream( "Export.xml.gz" ));
    }

    @Override
    public void tearDown() throws Exception
    {
        zipStream.close();

        gzipStream.close();
    }

    public void testZip()
    {
        assertTrue(StreamUtils.isZip(zipStream));

        assertFalse(StreamUtils.isGZip(zipStream));
    }

    public void testGZip()
    {
        assertTrue(StreamUtils.isGZip(gzipStream));

        assertFalse(StreamUtils.isZip(gzipStream));
    }
}


