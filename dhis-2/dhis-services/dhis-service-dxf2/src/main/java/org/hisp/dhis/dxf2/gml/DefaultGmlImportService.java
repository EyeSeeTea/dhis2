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

import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.render.RenderService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Halvdan Hoem Grelland
 */
public class DefaultGmlImportService
    implements GmlImportService
{
    private static final String GML_TO_DXF_TRANSFORM = "gml/gml2metadata.xsl";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private RenderService renderService;

    public void setRenderService( RenderService renderService )
    {
        this.renderService = renderService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // GmlImportService implementation
    // -------------------------------------------------------------------------

    @Override
    public MetaData fromGml( InputStream input )
        throws Exception
    {
        MetaData rawMetaData = renderService.fromXml( transformGml( input ), MetaData.class );
        MetaData resultMetaData = new MetaData();

        Map<String, OrganisationUnit> orgUnitMap = new HashMap<>();

        for ( OrganisationUnit orgUnit : rawMetaData.getOrganisationUnits() )
        {
            orgUnitMap.put( orgUnit.getName(), orgUnit );
        }

        Collection<OrganisationUnit> fetchedUnits = organisationUnitService.getOrganisationUnitsByNames( orgUnitMap.keySet() );

        for( OrganisationUnit o : fetchedUnits )
        {
            // TODO Fix this.
            System.out.println( o.getName() + " " + o.getUid() );
        }

        for ( OrganisationUnit unit : fetchedUnits )
        {
            OrganisationUnit importedUnit = orgUnitMap.get( unit.getName() );
            importedUnit.mergeWith( unit );

            resultMetaData.getOrganisationUnits().add( importedUnit );
        }

        return resultMetaData;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private InputStream transformGml( InputStream input )
        throws IOException, TransformerException
    {
        InputStream s = Thread.currentThread().getContextClassLoader().getResourceAsStream( GML_TO_DXF_TRANSFORM );

        StreamSource gml = new StreamSource( input ), xsl = new StreamSource( s );

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        TransformerFactory.newInstance().newTransformer( xsl ).transform( gml, new StreamResult( output ) );

        return new ByteArrayInputStream( output.toByteArray() );
    }
}
