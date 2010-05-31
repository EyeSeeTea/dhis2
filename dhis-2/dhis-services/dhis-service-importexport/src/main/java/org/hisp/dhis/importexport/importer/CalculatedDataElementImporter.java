package org.hisp.dhis.importexport.importer;

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

import org.amplecode.quick.BatchHandler;
import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.Importer;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class CalculatedDataElementImporter
    extends AbstractImporter<CalculatedDataElement> implements Importer<CalculatedDataElement>
{
    protected DataElementService dataElementService;

    public CalculatedDataElementImporter()
    {
    }
    
    public CalculatedDataElementImporter( BatchHandler<CalculatedDataElement> batchHandler, DataElementService dataElementService )
    {
        this.batchHandler = batchHandler;
        this.dataElementService = dataElementService;
    }
    
    @Override
    public void importObject( CalculatedDataElement object, ImportParams params )
    {
        NameMappingUtil.addDataElementMapping( object.getId(), object.getName() );
        NameMappingUtil.addDataElementAggregationOperatorMapping( object.getId(), object.getAggregationOperator() );
        
        read( object, GroupMemberType.NONE, params );
    }

    @Override
    protected void importUnique( CalculatedDataElement object )
    {
        dataElementService.addDataElement( object );
    }

    @Override
    protected void importMatching( CalculatedDataElement object, CalculatedDataElement match )
    {
        match.setUuid( object.getUuid() );
        match.setName( object.getName() );
        match.setShortName( object.getShortName() );
        match.setAlternativeName( object.getAlternativeName() );
        match.setCode( object.getCode() );
        match.setDescription( object.getDescription() );
        match.setActive( object.isActive() );
        match.setType( object.getType() );
        match.setAggregationOperator( object.getAggregationOperator() );
        match.setLastUpdated( object.getLastUpdated() );
        match.setSaved( object.isSaved() );
        match.setExpression( object.getExpression() );
        
        dataElementService.updateDataElement( match );
    }

    @Override
    protected CalculatedDataElement getMatching( CalculatedDataElement object )
    {
        DataElement match = dataElementService.getDataElementByName( object.getName() );

        if ( match == null )
        {
            match = dataElementService.getDataElementByAlternativeName( object.getAlternativeName() );
        }
        if ( match == null )
        {
            match = dataElementService.getDataElementByShortName( object.getShortName() );
        }
        
        CalculatedDataElement calculated = (CalculatedDataElement) match;
        
        if ( calculated != null )
        {
            calculated.getExpression().getExpression(); // Load Expression in session
        }
        
        return calculated; 
    }

    @Override
    protected boolean isIdentical( CalculatedDataElement object, CalculatedDataElement existing )
    {
        if ( !object.getName().equals( existing.getName() ) )
        {
            return false;
        }        
        if ( !isSimiliar( object.getAlternativeName(), existing.getAlternativeName() ) || ( isNotNull( object.getAlternativeName(), existing.getAlternativeName() ) && !object.getAlternativeName().equals( existing.getAlternativeName() ) ) )
        {
            return false;
        }
        if ( !object.getShortName().equals( existing.getShortName() ) )
        {
            return false;
        }
        if ( !isSimiliar( object.getCode(), existing.getCode() ) || ( isNotNull( object.getCode(), existing.getCode() ) && !object.getCode().equals( existing.getCode() ) ) )
        {
            return false;
        }
        if ( !isSimiliar( object.getDescription(), existing.getDescription() ) || ( isNotNull( object.getDescription(), existing.getDescription() ) && !object.getDescription().equals( existing.getDescription() ) ) )
        {
            return false;
        }
        if ( object.isActive() != existing.isActive() )
        {
            return false;
        }
        if ( !object.getType().equals( existing.getType() ) )
        {
            return false;
        }
        if ( !object.getAggregationOperator().equals( existing.getAggregationOperator() ) )
        {
            return false;
        }
        if ( object.isSaved() != existing.isSaved() )
        {
            return false;
        }
        
        return true;
    }   
    
    @Override
    protected boolean ignore( CalculatedDataElement object, CalculatedDataElement match )
    {
        boolean ignore = !(object instanceof CalculatedDataElement) && match instanceof CalculatedDataElement;
        
        if ( ignore )
        {
            log.warn( "Data element ignored because it matches with a calculated data element: " + object );
        }
        
        return ignore;
    } 
}
