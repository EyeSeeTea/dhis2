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

import org.geotools.GML;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollectionIteration;
import org.geotools.gml.GMLFilterFeature;
import org.geotools.gml3.GMLConfiguration;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.PullParser;
import org.geotools.xml.StreamingParser;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.organisationunit.CoordinatesTuple;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.PropertyDescriptor;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Halvdan Hoem Grelland
 */
public class DefaultGmlImportService
    implements GmlImportService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // GmlImportService implementation
    // -------------------------------------------------------------------------

    @Override
    public MetaData fromGml( InputStream input, Class<?> clazz ) throws Exception
    {
        MetaData metaData = new MetaData();

        GMLConfiguration gmlCfg = new GMLConfiguration();
        PullParser parser = new PullParser( gmlCfg, input, SimpleFeature.class );

        int nfeatures = 0;
        SimpleFeature f = null;

        while( ( f = (SimpleFeature) parser.parse() ) != null )
        {
            nfeatures++;
            String name = f.getAttribute( "Name" ).toString();

            System.out.println( f.getID() + " name: " + name );
        }

        System.out.println( "features: " + nfeatures );

        return metaData;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private List<OrganisationUnit> organisationUnitsFromGml( SimpleFeature feature )
    {
        List<OrganisationUnit> organisationUnits = new ArrayList<>();

        return organisationUnits;
    }
}
