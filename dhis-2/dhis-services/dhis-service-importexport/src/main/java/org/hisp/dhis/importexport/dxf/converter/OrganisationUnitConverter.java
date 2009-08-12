package org.hisp.dhis.importexport.dxf.converter;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.util.Collection;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.converter.AbstractOrganisationUnitConverter;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.source.Source;
import org.hisp.dhis.system.util.DateUtils;

/**
 * @author Lars Helge Overland
 * @version $Id: OrganisationUnitConverter.java 6455 2008-11-24 08:59:37Z larshelg $
 */
public class OrganisationUnitConverter
    extends AbstractOrganisationUnitConverter implements XMLConverter
{
    public static final String COLLECTION_NAME = "organisationUnits";
    public static final String ELEMENT_NAME = "organisationUnit";
    
    private static final String FIELD_ID = "id";
    private static final String FIELD_UUID = "uuid";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_SHORT_NAME = "shortName";
    private static final String FIELD_CODE = "code";
    private static final String FIELD_OPENING_DATE = "openingDate";
    private static final String FIELD_CLOSED_DATE = "closedDate";
    private static final String FIELD_ACTIVE = "active";
    private static final String FIELD_COMMENT = "comment";
    private static final String FIELD_GEO_CODE = "geoCode";
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public OrganisationUnitConverter( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    /**
     * Constructor for read operations.
     * 
     * @param batchHandler the batchHandler to use.
     * @param organisationUnitService the organisationUnitService to use.
     * @param importObjectService the importObjectService to use.
     */
    public OrganisationUnitConverter( BatchHandler<OrganisationUnit> batchHandler, 
        BatchHandler<Source> sourceBatchHandler, 
        ImportObjectService importObjectService, 
        OrganisationUnitService organisationUnitService )
    {
        this.batchHandler = batchHandler;
        this.sourceBatchHandler = sourceBatchHandler;
        this.importObjectService = importObjectService;
        this.organisationUnitService = organisationUnitService;
    }
    
    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<OrganisationUnit> units = organisationUnitService.getOrganisationUnits( params.getOrganisationUnits() );
        
        if ( units != null && units.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( OrganisationUnit unit : units )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( unit.getId() ) );
                writer.writeElement( FIELD_UUID, unit.getUuid() );
                writer.writeElement( FIELD_NAME, unit.getName() );
                writer.writeElement( FIELD_SHORT_NAME, unit.getShortName() );
                writer.writeElement( FIELD_CODE, unit.getCode() );
                writer.writeElement( FIELD_OPENING_DATE, DateUtils.getMediumDateString( unit.getOpeningDate() ) );
                writer.writeElement( FIELD_CLOSED_DATE, DateUtils.getMediumDateString( unit.getClosedDate() ) );
                writer.writeElement( FIELD_ACTIVE, String.valueOf( unit.isActive() ) );
                writer.writeElement( FIELD_COMMENT, unit.getComment() );
                writer.writeElement( FIELD_GEO_CODE, unit.getGeoCode() );
                
                writer.closeElement();
            }
            
            writer.closeElement();
        }
    }
    
    public void read( XMLReader reader, ImportParams params )
    {
        while ( reader.moveToStartElement( ELEMENT_NAME, COLLECTION_NAME ) )
        {
            final Map<String, String> values = reader.readElements( ELEMENT_NAME );
            
            final OrganisationUnit unit = new OrganisationUnit();

            unit.setId( Integer.parseInt( values.get( FIELD_ID ) ) );
            unit.setUuid( values.get( FIELD_UUID ) );
            unit.setName( values.get( FIELD_NAME ) );
            unit.setShortName( values.get( FIELD_SHORT_NAME ) );
            unit.setCode( values.get( FIELD_CODE ) );
            unit.setOpeningDate( DateUtils.getMediumDate( values.get( FIELD_OPENING_DATE ) ) );
            unit.setClosedDate( DateUtils.getMediumDate( values.get( FIELD_CLOSED_DATE ) ) );
            unit.setActive( Boolean.parseBoolean( values.get( FIELD_ACTIVE ) ) );
            unit.setComment( values.get( FIELD_COMMENT ) );
            unit.setGeoCode( values.get( FIELD_GEO_CODE ) );
            
            NameMappingUtil.addOrganisationUnitMapping( unit.getId(), unit.getName() );
            
            read( unit, GroupMemberType.NONE, params );
        }
    }
}
