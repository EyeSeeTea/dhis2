package org.hisp.dhis.dxf2.gml;

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

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.iterators.CollatingIterator;
import org.apache.commons.lang3.StringUtils;
import org.hisp.dhis.common.MergeStrategy;
import org.hisp.dhis.dxf2.common.ImportOptions;
import org.hisp.dhis.dxf2.metadata.ImportService;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.render.RenderService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.scheduling.TaskId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Halvdan Hoem Grelland
 */
public class DefaultGmlImportService
    implements GmlImportService
{
    private static final String GML_TO_DXF_STYLESHEET = "gml/gml2dxf2.xsl";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private ImportService importService;

    @Autowired
    private RenderService renderService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    // -------------------------------------------------------------------------
    // GmlImportService implementation
    // -------------------------------------------------------------------------

    @Override
    public MetaData fromGml( InputStream inputStream )
        throws IOException, TransformerException
    {
        InputStream dxfStream = transformGml( inputStream );

        MetaData metaData = renderService.fromXml( dxfStream, MetaData.class );

        dxfStream.close();

        List<OrganisationUnit> gmlOrgUnits = metaData.getOrganisationUnits();

        Map<String, OrganisationUnit> uidMap  = Maps.newHashMap(),
                                      codeMap = Maps.newHashMap(),
                                      nameMap = Maps.newHashMap();

        for ( OrganisationUnit orgUnit : gmlOrgUnits ) // Identifier Matching priority: uid, code, name
        {
            if ( !Strings.isNullOrEmpty( orgUnit.getUid() ) )
            {
                uidMap.put( orgUnit.getUid(), orgUnit );
            }
            else if ( !Strings.isNullOrEmpty( orgUnit.getCode() ) )
            {
                codeMap.put( orgUnit.getCode(), orgUnit );
            }
            else if ( !Strings.isNullOrEmpty( orgUnit.getName() ) )
            {
                nameMap.put( orgUnit.getName(), orgUnit );
            }
        }

        Map<String, OrganisationUnit> persistedUidMap = Maps.uniqueIndex( organisationUnitService.getOrganisationUnitsByUid( uidMap.keySet() ),
            new Function<OrganisationUnit, String>()
            {
                @Override public String apply( OrganisationUnit organisationUnit )
                {
                    return organisationUnit.getUid();
                }
            }
        );

        Map<String, OrganisationUnit> persistedCodeMap = Maps.uniqueIndex( organisationUnitService.getOrganisationUnitsByCodes( codeMap.keySet() ),
            new Function<OrganisationUnit, String>()
            {
                @Override public String apply( OrganisationUnit organisationUnit )
                {
                    return organisationUnit.getCode();
                }
            }
        );

        Map<String, OrganisationUnit> persistedNameMap = Maps.uniqueIndex( organisationUnitService.getOrganisationUnitsByNames( nameMap.keySet() ),
            new Function<OrganisationUnit, String>()
            {
                @Override public String apply( OrganisationUnit organisationUnit )
                {
                    return organisationUnit.getName();
                }
            }
        );

        Iterator<OrganisationUnit> allPersistedOrgUnits = Iterators.concat(
            persistedUidMap.values().iterator(), persistedCodeMap.values().iterator(), persistedNameMap.values().iterator() );

        System.out.println( "Iterator size: " + Iterators.size( allPersistedOrgUnits ) );

        while( allPersistedOrgUnits.hasNext() )
        {
            OrganisationUnit persisted = allPersistedOrgUnits.next(), unit = null;

            System.out.println( "Persisted: " + persisted.getName() + " uid: " + persisted.getUid() );

            if ( !Strings.isNullOrEmpty( persisted.getUid() ) && uidMap.containsKey( persisted.getUid() ) )
            {
                unit = uidMap.get( persisted.getUid() );
            }
            else if ( !Strings.isNullOrEmpty( persisted.getCode() ) && codeMap.containsKey( persisted.getCode() ) )
            {
                unit = codeMap.get( persisted.getCode() );
            }
            else if ( !Strings.isNullOrEmpty( persisted.getName() ) && nameMap.containsKey( persisted.getName() ) )
            {
                unit = nameMap.get( persisted.getName() );
                System.out.println( "    unit: " + unit.getName() + " unit feature" + unit.getFeatureType() );
            }

            if ( unit == null || unit.getCoordinates() == null || unit.getFeatureType() == null )
            {
                continue; // Failed to dereference a persisted entity for this org unit or geo data incomplete/missing, therefore ignore
            }

            String coordinates = unit.getCoordinates(),
                   featureType = unit.getFeatureType();

            unit.setShortName( StringUtils.abbreviate( persisted.getName(), 50 ) );

            unit.mergeWith( persisted, MergeStrategy.MERGE );

            unit.setCoordinates( coordinates );
            unit.setFeatureType( featureType );

            if ( persisted.getParent() != null )
            {
                OrganisationUnit parent = new OrganisationUnit();
                parent.setUid( persisted.getParent().getUid() );
                unit.setParent( parent );
            }
        }

        return metaData;
    }

    @Transactional
    @Override
    public void importGml( InputStream inputStream, String userUid, ImportOptions importOptions, TaskId taskId )
        throws IOException, TransformerException
    {
        importService.importMetaData( userUid, fromGml( inputStream ), importOptions, taskId );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private InputStream transformGml( InputStream input )
        throws IOException, TransformerException
    {
        StreamSource gml = new StreamSource( input );
        StreamSource xsl = new StreamSource( new ClassPathResource( GML_TO_DXF_STYLESHEET ).getInputStream() );

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        TransformerFactory.newInstance().newTransformer( xsl ).transform( gml, new StreamResult( output ) );

        xsl.getInputStream().close();
        gml.getInputStream().close();

        return new ByteArrayInputStream( output.toByteArray() );
    }
}
