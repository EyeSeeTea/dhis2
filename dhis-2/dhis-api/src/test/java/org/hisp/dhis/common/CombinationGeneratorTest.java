package org.hisp.dhis.common;

/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
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

import java.util.Arrays;

import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class CombinationGeneratorTest
{
    private IdentifiableObject a = new DataElementGroup( "A" );
    private IdentifiableObject b = new DataElementGroup( "B" );
    private IdentifiableObject c = new DataElementGroup( "C" );
    private IdentifiableObject d = new DataElementGroup( "D" );
    
    @Test
    public void testA()
    {
        IdentifiableObject[] a1 = {a,b,c,d};
        IdentifiableObject[] a2 = {a,b,c};
        
        CombinationGenerator generator = new CombinationGenerator( a1, a2 );
        
        assertTrue( equals( generator.getNext(), a, a ) );
        assertTrue( equals( generator.getNext(), a, b ) );
        assertTrue( equals( generator.getNext(), a, c ) );
        assertTrue( equals( generator.getNext(), b, a ) );
        assertTrue( equals( generator.getNext(), b, b ) );
        assertTrue( equals( generator.getNext(), b, c ) );
        assertTrue( equals( generator.getNext(), c, a ) );
        assertTrue( equals( generator.getNext(), c, b ) );
        assertTrue( equals( generator.getNext(), c, c ) );
        assertTrue( equals( generator.getNext(), d, a ) );
        assertTrue( equals( generator.getNext(), d, b ) );
        assertTrue( equals( generator.getNext(), d, c ) );       
    }
    
    //@Test
    public void testB()
    {
        IdentifiableObject[] a1 = {a,b};
        IdentifiableObject[] a2 = {a,b};
        IdentifiableObject[] a3 = {a,b,c};

        CombinationGenerator generator = new CombinationGenerator( a1, a2, a3 );
        
        assertTrue( equals( generator.getNext(), a, a, a ) );
        assertTrue( equals( generator.getNext(), a, a, b ) );
        assertTrue( equals( generator.getNext(), a, a, c ) );
        assertTrue( equals( generator.getNext(), a, b, a ) );
        assertTrue( equals( generator.getNext(), a, b, b ) );
        assertTrue( equals( generator.getNext(), a, b, c ) );
        assertTrue( equals( generator.getNext(), b, a, a ) );
        assertTrue( equals( generator.getNext(), b, a, b ) );
        assertTrue( equals( generator.getNext(), b, a, c ) );
        assertTrue( equals( generator.getNext(), b, b, a ) );
        assertTrue( equals( generator.getNext(), b, b, b ) );
        assertTrue( equals( generator.getNext(), b, b, c ) );
    }
    
    private static boolean equals( IdentifiableObject[] array, IdentifiableObject... integers )
    {
        return Arrays.asList( array ).equals( Arrays.asList( integers ) );
    }
}
