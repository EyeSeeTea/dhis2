package org.hisp.dhis.util;

/*
 * Copyright (c) 2004-2015, University of Oslo
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

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.hisp.dhis.util.ReflectionUtils.getClassName;
import static org.hisp.dhis.util.ReflectionUtils.getId;
import static org.hisp.dhis.util.ReflectionUtils.getProperty;
import static org.hisp.dhis.util.ReflectionUtils.isCollection;
import static org.hisp.dhis.util.ReflectionUtils.setProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Lars Helge Overland
 */
public class ReflectionUtilsTest
{
    private MockObject mockObject;
    
    @Before
    public void before()
    {
        mockObject = new MockObject( 8, "NameA" );
    }
    
    
    @Test
    public void testGetId()
    {
        assertEquals( 8, getId( mockObject ) );
    }
    
    @Test
    public void testGetProperty()
    {
        assertEquals( "NameA", getProperty( mockObject, "name" ) );
        assertNull( getProperty( mockObject, "color" ) );
    }
    
    @Test
    public void testSetProperty()
    {
        setProperty( mockObject, "shortName", "ShortNameA" );
        assertEquals( "ShortNameA", mockObject.getShortName() );
    }
    
    @Test( expected=UnsupportedOperationException.class )
    public void testSetPropertyException()
    {
        setProperty( mockObject, "color", "Blue" );
    }
    
    @Test
    public void testGetClassName()
    {
        assertEquals( MockObject.class.getSimpleName(), getClassName( mockObject ) );
    }
    
    @Test
    public void testIsCollection()
    {
        List<Object> colA = new ArrayList<>();
        Collection<MockObject> colB = new HashSet<>();
        Collection<MockObject> colC = new ArrayList<>();

        assertTrue( isCollection( colA ) );
        assertTrue( isCollection( colB ) );
        assertTrue( isCollection( colC ) );
        assertFalse( isCollection( mockObject ) );
    }

    private class MockObject
    {
        private int id;
        private String name, shortName;

        public MockObject( int id, String name )
        {
            this.id = id;
            this.name = name;
        }

        public int getId()
        {
            return id;
        }

        public void setId( int id )
        {
            this.id = id;
        }

        public String getName()
        {
            return name;
        }

        public void setName( String name )
        {
            this.name = name;
        }

        public String getShortName()
        {
            return shortName;
        }

        public void setShortName( String shortName )
        {
            this.shortName = shortName;
        }
    }
}
