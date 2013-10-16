package org.hisp.dhis.analytics.event.data;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import static org.hisp.dhis.common.DimensionalObject.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.analytics.IllegalQueryException;
import org.hisp.dhis.analytics.Partitions;
import org.hisp.dhis.analytics.QueryPlanner;
import org.hisp.dhis.analytics.event.EventQueryParams;
import org.hisp.dhis.analytics.event.EventQueryPlanner;
import org.hisp.dhis.period.Cal;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.program.Program;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class DefaultEventQueryPlanner
    implements EventQueryPlanner
{
    private static final Log log = LogFactory.getLog( DefaultEventQueryPlanner.class );
    
    private static final String TABLE_BASE_NAME = "analytics_event_";
    
    @Autowired
    private QueryPlanner queryPlanner;
    
    public void validate( EventQueryParams params )
        throws IllegalQueryException
    {
        String violation = null;

        if ( params == null )
        {
            throw new IllegalQueryException( "Params cannot be null" );
        }
        
        if ( !params.hasOrganisationUnits() )
        {
            violation = "At least one organisation unit must be specified";
        }
        
        if ( !params.hasPeriods() && ( params.getStartDate() == null || params.getEndDate() == null ) )
        {
            violation = "Start and end date or at least one period must be specified";
        }
        
        if ( params.getStartDate() != null && params.getEndDate() != null )
        {
            if ( params.getStartDate().after( params.getEndDate() ) )
            {
                violation = "Start date is after end date: " + params.getStartDate() + " - " + params.getEndDate();
            }            
        }

        if ( params.getPage() != null && params.getPage() <= 0 )
        {
            violation = "Page number must be positive: " + params.getPage();
        }
        
        if ( params.getPageSize() != null && params.getPageSize() < 0 )
        {
            violation = "Page size must be zero or positive: " + params.getPageSize();
        }
        
        if ( violation != null )
        {
            log.warn( "Validation failed: " + violation );
            
            throw new IllegalQueryException( violation );
        }
    }
    
    public List<EventQueryParams> planQuery( EventQueryParams params )
    {
        List<EventQueryParams> queries = new ArrayList<EventQueryParams>();
        
        List<EventQueryParams> groupedByPartition = groupByPartition( params );
        
        for ( EventQueryParams byPartition : groupedByPartition )
        {
            List<EventQueryParams> groupedByOrgUnitLevel = convert( queryPlanner.groupByOrgUnitLevel( byPartition ) );
            
            for ( EventQueryParams byOrgUnitLevel : groupedByOrgUnitLevel )
            {
                queries.addAll( convert( queryPlanner.groupByPeriodType( byOrgUnitLevel ) ) );
            }
        }
        
        return queries;
    }
    
    private List<EventQueryParams> groupByPartition( EventQueryParams params )
    {
        List<EventQueryParams> list = new ArrayList<EventQueryParams>();
        
        Program program = params.getProgram();
        
        if ( params.hasStartEndDate() )
        {
            Date startDate = params.getStartDate();
            Date endDate = params.getEndDate();
            
            Date currentStartDate = startDate;
            Date currentEndDate = endDate;
            
            while ( true )
            {
                if ( year( currentStartDate ) < year( endDate ) ) // Spans multiple
                {
                    // Set end date to max of current year
                    
                    currentEndDate = maxOfYear( currentStartDate ); 
                    
                    list.add( getQuery( params, currentStartDate, currentEndDate, program ) );
                    
                    // Set start date to start of next year
                    
                    currentStartDate = new Cal( ( year( currentStartDate ) + 1 ), 1, 1 ).time();                 
                }
                else
                {
                    list.add( getQuery( params, currentStartDate, endDate, program ) );
                    
                    break;
                }
            }
        }
        else
        {
            //TODO implement properly 
            
            Period period = (Period) params.getDimensionOrFilter( PERIOD_DIM_ID ).get( 0 );
            String tableName = TABLE_BASE_NAME + year( period.getStartDate() ) + "_" + program.getUid();
            params.setPartitions( new Partitions().add( tableName ) );
            params.setPeriodType( period.getPeriodType().getName() );
            list.add( params );
        }
        
        return list;
    }
    
    private EventQueryParams getQuery( EventQueryParams params, Date startDate, Date endDate, Program program )
    {
        EventQueryParams query = params.instance();
        query.setStartDate( startDate );
        query.setEndDate( endDate );
        String tableName = TABLE_BASE_NAME + year( startDate ) + "_" + program.getUid();
        query.setPartitions( new Partitions().add( tableName ) );
        return query;
    }
    
    private static int year( Date date )
    {
        return new Cal( date ).getYear();
    }
    
    private static Date maxOfYear( Date date )
    {
        return new Cal( year( date ), 12, 31 ).time();
    }

    private static List<EventQueryParams> convert( List<DataQueryParams> params )
    {
        List<EventQueryParams> eventParams = new ArrayList<EventQueryParams>();
        
        for ( DataQueryParams param : params )
        {
            eventParams.add( (EventQueryParams) param );
        }
        
        return eventParams;
    }    
}
