package org.hisp.dhis.jchart;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.AscendingPeriodComparator;
import org.hisp.dhis.period.comparator.PeriodComparator;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */

public class JChart
{

    public static final String LOAD_PERIOD_AUTO = "LOAD_PERIOD_AUTO";

    public static final String LOAD_PERIOD_SELECTED = "LOAD_PERIOD_SELECTED";

    private int id;

    private String title;
  
    private Set<JChartSeries> series = new HashSet<JChartSeries>();

    private Set<Period> periods = new HashSet<Period>();

    private PeriodType periodType;

    private String loadPeriodBy;

    /*
     * This string is json format
     */
    private String legend;

    // -------------------------------------------
    // Contructor
    // -------------------------------------------

    public JChart()
    {
    }

    // -------------------------------------------
    // Method
    // -------------------------------------------

    public boolean isLoadSelectedPeriods()
    {
        return this.loadPeriodBy.equals( LOAD_PERIOD_SELECTED );
    }

    public boolean isAutoLoadPeriods()
    {
        return this.loadPeriodBy.equals( LOAD_PERIOD_AUTO );
    }

    public void addSeries( JChartSeries jChartSeries )
    {
        this.series.add( jChartSeries );
    }

    public void clearAllSeries()
    {
        this.series.clear();
    }

    public void addPeriod( Period period )
    {
        this.periods.add( period );
    }

    public void clearAllPeriod()
    {
        this.periods.clear();
    }

    public List<Period> getSortedPeriods()
    {
        List<Period> ps = new ArrayList<Period>( this.periods );

        Collections.sort( ps, new PeriodComparator() );

        return ps;
    }
    
    public List<Period> getdAscendingPeriodSorted()
    {
        List<Period> ps = new ArrayList<Period>( this.periods );

        Collections.sort( ps, new AscendingPeriodComparator() );

        return ps;
    }

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }
  
    public String getLegend()
    {
        return legend;
    }

    public void setLegend( String legend )
    {
        this.legend = legend;
    }

    public Set<JChartSeries> getSeries()
    {
        return series;
    }

    public void setSeries( Set<JChartSeries> series )
    {
        this.series = series;
    }

    public Set<Period> getPeriods()
    {
        return periods;
    }

    public void setPeriods( Set<Period> periods )
    {
        this.periods = periods;
    }

    public PeriodType getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType( PeriodType periodType )
    {
        this.periodType = periodType;
    }

    public String getLoadPeriodBy()
    {
        return loadPeriodBy;
    }

    public void setLoadPeriodBy( String loadPeriodBy )
    {
        this.loadPeriodBy = loadPeriodBy;
    }

    // -------------------------------------------
    // Hash Code & Equal
    // -------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        JChart other = (JChart) obj;
        if ( id != other.id )
            return false;
        return true;
    }

}
