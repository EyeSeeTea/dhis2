package org.hisp.dhis.jdbc.batchhandler;

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

import java.util.HashMap;
import java.util.Map;

import org.amplecode.quick.JdbcConfiguration;
import org.amplecode.quick.batchhandler.AbstractBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Lars Helge Overland
 * @version $Id: OrganisationUnitBatchHandler.java 5062 2008-05-01 18:10:35Z larshelg $
 */
public class OrganisationUnitBatchHandler
    extends AbstractBatchHandler
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public OrganisationUnitBatchHandler( JdbcConfiguration configuration )
    {
        super( configuration );
        
        hasSinglePrimaryKey = false;
    }

    // -------------------------------------------------------------------------
    // AbstractBatchHandler implementation
    // -------------------------------------------------------------------------

    protected void setTableName()
    {
        this.tableName = "organisationunit";
    }
    
    @Override
    protected void setIdentifierColumns()
    {
        statementBuilder.setIdentifierColumn( "organisationunitid" );
    }
    
    @Override
    protected void setIdentifierColumnValues( Object object )
    {
        OrganisationUnit unit = (OrganisationUnit) object;
        
        statementBuilder.setIdentifierValue( unit.getId() );
    }
    
    protected void setColumns()
    {
        statementBuilder.setColumn( "organisationunitid" );
        statementBuilder.setColumn( "uuid" );
        statementBuilder.setColumn( "name" );
        statementBuilder.setColumn( "parentid" );
        statementBuilder.setColumn( "shortname" );
        statementBuilder.setColumn( "code" );
        statementBuilder.setColumn( "openingdate" );
        statementBuilder.setColumn( "closeddate" );
        statementBuilder.setColumn( "active" );
        statementBuilder.setColumn( "comment" );
        statementBuilder.setColumn( "geocode" );
        statementBuilder.setColumn( "latitude" );
        statementBuilder.setColumn( "longitude" );
    }
    
    protected void setValues( Object object )
    {
        OrganisationUnit unit = (OrganisationUnit) object;
        
        statementBuilder.setInt( unit.getId() );
        statementBuilder.setString( unit.getUuid() );
        statementBuilder.setString( unit.getName() );        
        statementBuilder.setInt( unit.getParent() != null ? unit.getParent().getId() : null );
        statementBuilder.setString( unit.getShortName() );
        statementBuilder.setString( unit.getCode() );
        statementBuilder.setDate( unit.getOpeningDate() );
        statementBuilder.setDate( unit.getClosedDate() );
        statementBuilder.setBoolean( unit.isActive() );
        statementBuilder.setString( unit.getComment() );
        statementBuilder.setString( unit.getGeoCode() );
        statementBuilder.setString( unit.getLatitude() );
        statementBuilder.setString( unit.getLongitude() );
    }
    
    protected String getIdentifierStatement( Object objectName )
    {
        return statementBuilder.getValueStatement( tableName, "organisationunitid", "name", String.valueOf( objectName ) );
    }

    protected String getUniquenessStatement( Object object )
    {
        OrganisationUnit unit = (OrganisationUnit) object;
        
        Map<String, String> fieldMap = new HashMap<String, String>();
        
        fieldMap.put( "name", unit.getName() );
        fieldMap.put( "shortname", unit.getShortName() );
        fieldMap.put( "code", unit.getCode() );
        
        return statementBuilder.getValueStatement( tableName, "organisationunitid", fieldMap, false );
    }
}
