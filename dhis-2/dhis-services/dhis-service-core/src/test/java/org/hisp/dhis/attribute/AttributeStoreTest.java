package org.hisp.dhis.attribute;

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

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.common.ValueType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class AttributeStoreTest
    extends DhisSpringTest
{
    @Autowired
    private AttributeStore attributeStore;

    private Attribute atA;
    private Attribute atB;

    @Override
    protected void setUpTest()
    {
        atA = new Attribute();
        atA.setName( "attribute_simple" );
        atA.setValueType( ValueType.TEXT );
        atA.setIndicatorAttribute( true );
        atA.setDataElementAttribute( true );

        atB = new Attribute();
        atB.setName( "attribute_with_options" );
        atB.setValueType( ValueType.TEXT );
        atB.setOrganisationUnitAttribute( true );
        atB.setDataElementAttribute( true );

        attributeStore.save( atA );
        attributeStore.save( atB );
    }

    @Test
    public void testGetDataElementAttributes()
    {
        assertEquals( 2, attributeStore.getDataElementAttributes().size() );
    }

    @Test
    public void testGetIndicatorAttributes()
    {
        assertEquals( 1, attributeStore.getIndicatorAttributes().size() );
    }

    @Test
    public void testGetOrganisationUnitAttributes()
    {
        assertEquals( 1, attributeStore.getOrganisationUnitAttributes().size() );
    }
}
