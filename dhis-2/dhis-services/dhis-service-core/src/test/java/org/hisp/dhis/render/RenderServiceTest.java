package org.hisp.dhis.render;

/*
 * Copyright (c) 2004-2016, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dataelement.DataElement;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class RenderServiceTest
    extends DhisSpringTest
{
    @Autowired
    private RenderService renderService;

    @Test
    public void testParseJsonMetadata() throws IOException
    {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> map = renderService.fromMetadata(
            new ClassPathResource( "render/metadata.json" ).getInputStream(), RenderFormat.JSON );

        assertTrue( map.containsKey( DataElement.class ) );
        assertEquals( 3, map.get( DataElement.class ).size() );
        assertEquals( "DataElementA", map.get( DataElement.class ).get( 0 ).getName() );
        assertEquals( "DataElementB", map.get( DataElement.class ).get( 1 ).getName() );
        assertEquals( "DataElementC", map.get( DataElement.class ).get( 2 ).getName() );
        assertEquals( "deabcdefghA", map.get( DataElement.class ).get( 0 ).getUid() );
        assertEquals( "deabcdefghB", map.get( DataElement.class ).get( 1 ).getUid() );
        assertEquals( "deabcdefghC", map.get( DataElement.class ).get( 2 ).getUid() );
    }

    @Test
    @Ignore // ignoring since Jackson can't handle parsing of XML as trees
    public void testParseXmlMetadata() throws IOException
    {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> map = renderService.fromMetadata(
            new ClassPathResource( "render/metadata.xml" ).getInputStream(), RenderFormat.XML );

        assertTrue( map.containsKey( DataElement.class ) );
        assertEquals( 3, map.get( DataElement.class ).size() );
        assertEquals( "DataElementA", map.get( DataElement.class ).get( 0 ).getName() );
        assertEquals( "DataElementB", map.get( DataElement.class ).get( 1 ).getName() );
        assertEquals( "DataElementC", map.get( DataElement.class ).get( 2 ).getName() );
        assertEquals( "deabcdefghA", map.get( DataElement.class ).get( 0 ).getUid() );
        assertEquals( "deabcdefghB", map.get( DataElement.class ).get( 1 ).getUid() );
        assertEquals( "deabcdefghC", map.get( DataElement.class ).get( 2 ).getUid() );
    }
}
