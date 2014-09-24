package org.hisp.dhis.dxf2.gml;

/*
 * Copyright (c) 2004-2014, University of Oslo
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

import org.hisp.dhis.DhisTest;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Halvdan Hoem Grelland
 */
public class GmlImportServiceTest
    extends DhisTest
{
    private GmlImportService gmlImportService;

    private InputStream inputStream;

    @Override
    public void setUpTest()
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        gmlImportService = (GmlImportService) getBean( "org.hisp.dhis.dxf2.gml.GmlImportService" );

        inputStream = classLoader.getResourceAsStream( "gmlOrgUnits.gml" );
    }

    @Test
    public void fromGmlTest()
        throws Exception
    {
        MetaData metaData = gmlImportService.fromGml( inputStream );

        assertNotNull( metaData );

        List<OrganisationUnit> orgUnits = metaData.getOrganisationUnits();

        assertEquals( 14, orgUnits.size() );

        HashMap<String, OrganisationUnit> units = new HashMap<>();

        for( OrganisationUnit orgUnit : orgUnits )
        {
            units.put( orgUnit.getName(), orgUnit );
        }

        assertEquals( 1, units.get( "Bo" ).getCoordinatesAsList().size() );
        assertEquals( 18, units.get( "Bonthe" ).getCoordinatesAsList().size() );
        assertEquals( 1, units.get( "Moyamba" ).getCoordinatesAsList().size() );
        assertEquals( 3, units.get( "Pujehun" ).getCoordinatesAsList().size() );
        assertEquals( 1, units.get( "Kailahun" ).getCoordinatesAsList().size() );
        assertEquals( 1, units.get( "Kenema" ).getCoordinatesAsList().size() );
        assertEquals( 1, units.get( "Kono" ).getCoordinatesAsList().size() );
        assertEquals( 1, units.get( "Bombali" ).getCoordinatesAsList().size() );
        assertEquals( 3, units.get( "Kambia" ).getCoordinatesAsList().size() );
        assertEquals( 1, units.get( "Koinadugu" ).getCoordinatesAsList().size() );
        assertEquals( 9, units.get( "Port Loko" ).getCoordinatesAsList().size() );
        assertEquals( 1, units.get( "Tonkolili" ).getCoordinatesAsList().size() );
        assertEquals( 2, units.get( "Western Area" ).getCoordinatesAsList().size() );
        assertEquals( 1, units.get( "Ole Johan Dahls Hus" ).getCoordinatesAsList().size() );

        assertEquals( 76, units.get( "Bo" ).getCoordinatesAsList().get( 0 ).getNumberOfCoordinates() );
        assertEquals( 7, units.get( "Pujehun" ).getCoordinatesAsList().get( 0 ).getNumberOfCoordinates() );
        assertEquals( 7, units.get( "Pujehun" ).getCoordinatesAsList().get( 1 ).getNumberOfCoordinates() );
        assertEquals( 159, units.get( "Pujehun" ).getCoordinatesAsList().get( 2 ).getNumberOfCoordinates() );
        assertEquals( 189, units.get( "Bonthe" ).getCoordinatesAsList().get( 1 ).getNumberOfCoordinates() );
        assertEquals( 1, units.get( "Ole Johan Dahls Hus" ).getCoordinatesAsList().get( 0 ).getNumberOfCoordinates() );
    }
}
