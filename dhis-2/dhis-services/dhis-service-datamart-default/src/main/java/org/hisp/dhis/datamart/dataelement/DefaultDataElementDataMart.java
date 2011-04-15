package org.hisp.dhis.datamart.dataelement;

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

import static org.hisp.dhis.system.util.MathUtils.getRounded;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datamart.aggregation.cache.AggregationCache;
import org.hisp.dhis.datamart.aggregation.dataelement.DataElementAggregator;
import org.hisp.dhis.jdbc.batchhandler.AggregatedDataValueBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitHierarchy;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;

/**
 * @author Lars Helge Overland
 */
public class DefaultDataElementDataMart
    implements DataElementDataMart
{
    private static final int DECIMALS = 1;
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }
        
    private AggregationCache aggregationCache;

    public void setAggregationCache( AggregationCache aggregationCache )
    {
        this.aggregationCache = aggregationCache;
    }

    // -------------------------------------------------------------------------
    // DataMart functionality
    // -------------------------------------------------------------------------
    
    public int exportDataValues( final Collection<DataElementOperand> operands, final Collection<Period> periods, 
        final Collection<OrganisationUnit> organisationUnits, final DataElementAggregator dataElementAggregator, String key )
    {
        final BatchHandler<AggregatedDataValue> batchHandler = batchHandlerFactory.createBatchHandler( AggregatedDataValueBatchHandler.class ).init();
        
        final OrganisationUnitHierarchy hierarchy = organisationUnitService.getOrganisationUnitHierarchy().prepareChildren( organisationUnits );
        
        int count = 0;
        
        final AggregatedDataValue value = new AggregatedDataValue();
        
        for ( final Period period : periods )
        {
            final Collection<DataElementOperand> currentOperands = dataElementAggregator.filterOperands( operands, period.getPeriodType() );
            
            if ( currentOperands != null && currentOperands.size() > 0 )
            {
                for ( final OrganisationUnit unit : organisationUnits )
                {
                    final int level = aggregationCache.getLevelOfOrganisationUnit( unit.getId() );
                    
                    final Map<DataElementOperand, Double> valueMap = dataElementAggregator.getAggregatedValues( currentOperands, period, unit, level, hierarchy, key );
                    
                    for ( Entry<DataElementOperand, Double> entry : valueMap.entrySet() )
                    {
                        value.clear();
                        
                        value.setDataElementId( entry.getKey().getDataElementId() );
                        value.setCategoryOptionComboId( entry.getKey().getOptionComboId() );
                        value.setPeriodId( period.getId() );
                        value.setPeriodTypeId( period.getPeriodType().getId() );
                        value.setOrganisationUnitId( unit.getId() );
                        value.setLevel( level );
                        value.setValue( getRounded( entry.getValue(), DECIMALS ) );
                        
                        batchHandler.addObject( value );
                        
                        count++;
                    }
                }
            }
        }
        
        batchHandler.flush();
        
        return count;
    }
}
