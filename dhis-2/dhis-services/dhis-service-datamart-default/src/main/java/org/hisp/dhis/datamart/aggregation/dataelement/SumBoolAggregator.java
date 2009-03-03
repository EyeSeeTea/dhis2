package org.hisp.dhis.datamart.aggregation.dataelement;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.hisp.dhis.dataelement.Operand;
import org.hisp.dhis.datamart.CrossTabDataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitHierarchy;
import org.hisp.dhis.period.Period;

import static org.hisp.dhis.system.util.MathUtils.getDays;
import static org.hisp.dhis.system.util.MathUtils.getFloor;

/**
 * @author Lars Helge Overland
 * @version $Id: SumBoolAggregator.java 6049 2008-10-28 09:36:17Z larshelg $
 */
public class SumBoolAggregator
    extends DataElementAggregator
{
    public Map<Operand, Double> getAggregatedValues( final Map<Operand, Integer> operandIndexMap, final Period period, final OrganisationUnit unit )
    {
        final OrganisationUnitHierarchy hierarchy = aggregationCache.getLatestOrganisationUnitHierarchy();
        
        final Collection<CrossTabDataValue> crossTabValues = 
            getCrossTabDataValues( operandIndexMap, period.getStartDate(), period.getEndDate(), unit.getId(), hierarchy );
        
        final Map<Operand, Double[]> entries = getAggregate( crossTabValues, period.getStartDate(), 
            period.getEndDate(), period.getStartDate(), period.getEndDate() ); // <data element id, [total value, total relevant days]>

        final Map<Operand, Double> values = new HashMap<Operand, Double>( entries.size() ); // <Operand, total value>
        
        for ( final Entry<Operand, Double[]> entry : entries.entrySet() )
        {
            if ( entry.getValue() != null && entry.getValue()[ 1 ] > 0 )
            {
                values.put( entry.getKey(), getFloor( entry.getValue()[ 0 ] ) );
            }
        }
        
        return values;
    }

    protected Collection<CrossTabDataValue> getCrossTabDataValues( final Map<Operand, Integer> operandIndexMap, 
        final Date startDate, final Date endDate, final int parentId, final OrganisationUnitHierarchy hierarchy )
    {
        final Collection<Integer> sourceIds = aggregationCache.getChildren( hierarchy, parentId );
        
        final Collection<Period> periods = aggregationCache.getIntersectingPeriods( startDate, endDate );
        
        final Collection<Integer> periodIds = new ArrayList<Integer>( periods.size() );
        
        for ( final Period period : periods )
        {
            periodIds.add( period.getId() );
        }
        
        return dataMartStore.getCrossTabDataValues( operandIndexMap, periodIds, sourceIds );
    }
    
    protected Map<Operand, Double[]> getAggregate( final Collection<CrossTabDataValue> crossTabValues, 
        final Date startDate, final Date endDate, final Date aggregationStartDate, final Date aggregationEndDate )
    {
        final Map<Operand, Double[]> totalSums = new HashMap<Operand, Double[]>(); // <Operand, [total value, total relevant days]>

        Period period = null;
        Date currentStartDate = null;
        Date currentEndDate = null;
        
        double duration = 0.0;
        double value = 0.0;
        double relevantDays = 0.0;
        double factor = 0.0;
        double existingValue = 0.0;
        double existingRelevantDays = 0.0;
        
        for ( final CrossTabDataValue crossTabValue : crossTabValues )
        {
            period = aggregationCache.getPeriod( crossTabValue.getPeriodId() );
            
            currentStartDate = period.getStartDate();
            currentEndDate = period.getEndDate();
            
            duration = getDays( currentEndDate ) - getDays( currentStartDate );
            
            if ( duration > 0 )
            {
                for ( final Entry<Operand, String> entry : crossTabValue.getValueMap().entrySet() ) // <Operand, value>
                {
                    if ( entry.getValue() != null )
                    {
                        value = 0;
                        
                        relevantDays = 0;
                        factor = 0;

                        if ( currentStartDate.compareTo( startDate ) >= 0 && currentEndDate.compareTo( endDate ) <= 0 )
                        {
                            relevantDays = ( getDays( endDate ) - getDays( startDate ) );
                            factor = 1;
                        }
                        else if ( currentStartDate.compareTo( startDate ) <= 0 && currentEndDate.compareTo( endDate ) >= 0 )
                        {
                            relevantDays = ( getDays( endDate ) - getDays( startDate ) );
                            factor = relevantDays / duration;
                        }
                        else if ( currentStartDate.compareTo( startDate ) <= 0 && currentEndDate.compareTo( startDate ) >= 0
                            && currentEndDate.compareTo( endDate ) <= 0 )
                        {
                            relevantDays = ( getDays( currentEndDate ) - getDays( startDate ) );
                            factor = relevantDays / duration;
                        }
                        else if ( currentStartDate.compareTo( startDate ) >= 0 && currentStartDate.compareTo( endDate ) <= 0
                            && currentEndDate.compareTo( endDate ) >= 0 )
                        {
                            relevantDays = ( getDays( endDate ) - getDays( currentStartDate ) );
                            factor = relevantDays / duration;
                        }

                        if ( entry.getValue().toLowerCase().equals( TRUE ) )
                        {
                            value = 1;
                        }
                        
                        value = value * factor;
                        
                        existingValue = totalSums.containsKey( entry.getKey() ) ? totalSums.get( entry.getKey() )[ 0 ] : 0;
                        existingRelevantDays = totalSums.containsKey( entry.getKey() ) ? totalSums.get( entry.getKey() )[ 1 ] : 0;

                        final Double[] values = { ( value + existingValue ), ( relevantDays + existingRelevantDays ) };
                        
                        totalSums.put( entry.getKey(), values );
                    }
                }
            }
        }
        
        return totalSums;
    }
}




