package org.hisp.dhis.reportexcel.period.generic;

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

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.AscendingPeriodComparator;
import org.hisp.dhis.period.comparator.PeriodComparator;

import com.opensymphony.xwork2.ActionContext;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */
public class DefaultPeriodGenericManager
    implements PeriodGenericManager
{

    private static final Log log = LogFactory.getLog( DefaultPeriodGenericManager.class );

    public static final String SESSION_KEY_SELECTED_PERIOD_GENERIC_INDEX = "SESSION_KEY_SELECTED_PERIOD_GENERIC_INDEX";

    public static final String SESSION_KEY_BASE_PERIOD_GENERIC = "SESSION_KEY_BASE_PERIOD_GENERIC";

    public static final String SESSION_KEY_BASE_PERIOD_TYPE_GENERIC = "SESSION_KEY_BASE_PERIOD_TYPE_GENERIC";

    public void setSelectedPeriodIndex( Integer index )
    {

        getSession().put( SESSION_KEY_SELECTED_PERIOD_GENERIC_INDEX, index );
    }

    public Integer getSelectedPeriodIndex()
    {

        return (Integer) getSession().get( SESSION_KEY_SELECTED_PERIOD_GENERIC_INDEX );
    }

    public Period getSelectedPeriod()
    {

        Integer index = getSelectedPeriodIndex();

        if ( index == null )
        {
            return null;
        }

        List<Period> periods = getPeriodList();

        if ( index >= 0 && index < periods.size() )
        {
            return periods.get( index );
        }

        return null;
    }

    public void clearSelectedPeriod()
    {
        getSession().remove( SESSION_KEY_SELECTED_PERIOD_GENERIC_INDEX );
    }

    public List<Period> getPeriodList()
    {

        Period basePeriod = getBasePeriod();

        CalendarPeriodType periodType = (CalendarPeriodType) getPeriodType();

        List<Period> periods = periodType.generatePeriods( basePeriod );

        Date now = new Date();

        Iterator<Period> iterator = periods.iterator();

        while ( iterator.hasNext() )
        {

            if ( iterator.next().getStartDate().after( now ) )
            {

                iterator.remove();
            }
        }

        Collections.sort( periods, new PeriodComparator() );

        return periods;
    }
    

    @Override
    public Period getSelectedPeriod( Integer index )
    {        
        return getPeriodList().get( index );
    }

    public void nextPeriodSpan()
    {

        List<Period> periods = getPeriodList();

        CalendarPeriodType periodType = (CalendarPeriodType) getPeriodType();

        Period basePeriod = periods.get( periods.size() - 1 );

        Period newBasePeriod = periodType.getNextPeriod( basePeriod );

        if ( newBasePeriod.getStartDate().before( new Date() ) )
        {
            getSession().put( SESSION_KEY_BASE_PERIOD_GENERIC, newBasePeriod );
        }
    }

    public void previousPeriodSpan()
    {

        List<Period> periods = getPeriodList();

        CalendarPeriodType periodType = (CalendarPeriodType) getPeriodType();

        Period basePeriod = periods.get( 0 );

        Period newBasePeriod = periodType.getPreviousPeriod( basePeriod );

        getSession().put( SESSION_KEY_BASE_PERIOD_GENERIC, newBasePeriod );
    }

    // -------------------------------------------------------------------------
    // Support methods
    // -------------------------------------------------------------------------

    private PeriodType getPeriodType()
    {

        String periodTypeName = (String) getSession().get( SESSION_KEY_BASE_PERIOD_TYPE_GENERIC );

        if ( periodTypeName == null )
        {
            throw new IllegalStateException( "Cannot ask for Period Type when no Period Type Name is setted" );
        }

        return PeriodType.getPeriodTypeByName( periodTypeName );
    }

    private Period getBasePeriod()
    {

        Period basePeriod = (Period) getSession().get( SESSION_KEY_BASE_PERIOD_GENERIC );

        PeriodType periodType = getPeriodType();

        if ( basePeriod == null )
        {
            log.debug( "Base period is null, creating new" );

            basePeriod = periodType.createPeriod();

            getSession().put( SESSION_KEY_BASE_PERIOD_GENERIC, basePeriod );

        }
        else if ( !basePeriod.getPeriodType().equals( periodType ) )
        {

            log.debug( "Wrong type of base period, transforming" );

            basePeriod = periodType.createPeriod( basePeriod.getStartDate() );

            getSession().put( SESSION_KEY_BASE_PERIOD_GENERIC, basePeriod );
        }

        return basePeriod;
    }

    private static final Map<String, Object> getSession()
    {

        return ActionContext.getContext().getSession();
    }

    @Override
    public void setPeriodType( String periodTypeNam )
    {
        getSession().put( SESSION_KEY_BASE_PERIOD_TYPE_GENERIC, periodTypeNam );
    }

}
