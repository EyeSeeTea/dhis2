package org.hisp.dhis.visualizer.action;

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
import java.util.Collection;

import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.RelativePeriods;

import com.opensymphony.xwork2.Action;

/**
 * @author Jan Henrik Overland
 */
public class AddOrUpdateChartAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ChartService chartService;

    public void setChartService( ChartService chartService )
    {
        this.chartService = chartService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String uid;

    public void setUid( String uid )
    {
        this.uid = uid;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String type;

    public void setType( String type )
    {
        this.type = type;
    }

    private String series;

    public void setSeries( String series )
    {
        this.series = series;
    }

    private String category;

    public void setCategory( String category )
    {
        this.category = category;
    }

    private String filter;

    public void setFilter( String filter )
    {
        this.filter = filter;
    }

    private Collection<Integer> indicatorIds;

    public void setIndicatorIds( Collection<Integer> indicatorIds )
    {
        this.indicatorIds = indicatorIds;
    }

    private Collection<Integer> dataElementIds;

    public void setDataElementIds( Collection<Integer> dataElementIds )
    {
        this.dataElementIds = dataElementIds;
    }

    private boolean lastMonth;

    public boolean getLastMonth()
    {
        return lastMonth;
    }

    private boolean monthsThisYear;

    public boolean getMonthsThisYear()
    {
        return monthsThisYear;
    }

    private boolean monthsLastYear;

    public boolean getMonthsLastYear()
    {
        return monthsLastYear;
    }

    private boolean lastQuarter;

    public boolean getLastQuarter()
    {
        return lastQuarter;
    }

    private boolean quartersThisYear;

    public boolean getQuartersThisYear()
    {
        return quartersThisYear;
    }

    private boolean quartersLastYear;

    public boolean getQuartersLastYear()
    {
        return quartersLastYear;
    }

    private boolean thisYear;

    public boolean getThisYear()
    {
        return thisYear;
    }

    private boolean lastYear;

    public boolean getLastYear()
    {
        return lastYear;
    }

    private boolean lastFiveYears;

    public boolean getLastFiveYears()
    {
        return lastFiveYears;
    }

    private Collection<Integer> organisationUnitIds;

    public void setOrganisationUnitIds( Collection<Integer> organisationUnitIds )
    {
        this.organisationUnitIds = organisationUnitIds;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Chart chart = null;

        if ( uid != null )
        {
            chart = chartService.getChart( uid );
        }
        else
        {
            chart = new Chart();
        }

        if ( name != null )
        {
            chart.setName( name );
        }

        if ( type != null )
        {
            chart.setType( type );
        }

        if ( series != null )
        {
            chart.setSeries( series );
        }

        if ( category != null )
        {
            chart.setCategory( category );
        }

        if ( filter != null )
        {
            chart.setFilter( filter );
        }

        if ( indicatorIds != null )
        {
            chart.setIndicators( new ArrayList<Indicator>( indicatorService.getIndicators( indicatorIds ) ) );
        }

        if ( dataElementIds != null )
        {
            chart.setDataElements( new ArrayList<DataElement>( dataElementService.getDataElements( dataElementIds ) ) );
        }

        if ( lastMonth || monthsThisYear || monthsLastYear || lastQuarter || quartersThisYear || quartersLastYear
            || thisYear || lastYear || lastFiveYears )
        {
            RelativePeriods rp = new RelativePeriods();
            rp.setReportingMonth( lastMonth );
            rp.setMonthsThisYear( monthsThisYear );
            rp.setMonthsLastYear( monthsLastYear );
            rp.setReportingQuarter( lastQuarter );
            rp.setQuartersThisYear( quartersThisYear );
            rp.setQuartersLastYear( quartersLastYear );
            rp.setThisYear( thisYear );
            rp.setLastYear( lastYear );
            rp.setLast5Years( lastFiveYears );

            chart.setRelatives( rp );
        }

        if ( organisationUnitIds != null )
        {
            chart.setOrganisationUnits( new ArrayList<OrganisationUnit>( organisationUnitService
                .getOrganisationUnits( organisationUnitIds ) ) );
        }

        chartService.saveOrUpdate( chart );

        return SUCCESS;
    }
}
