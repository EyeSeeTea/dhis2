package org.hisp.dhis.datamart.action;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataelement.comparator.DataElementGroupNameComparator;
import org.hisp.dhis.datamart.DataMartExport;
import org.hisp.dhis.datamart.DataMartService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.comparator.IndicatorGroupNameComparator;
import org.hisp.dhis.options.displayproperty.DisplayPropertyHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;
import org.hisp.dhis.system.filter.AggregatableDataElementFilter;
import org.hisp.dhis.system.util.FilterUtils;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Lars Helge Overland
 * @version $Id: GetOptionsAction.java 6256 2008-11-10 17:10:30Z larshelg $
 */
public class GetOptionsAction
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataMartService dataMartService;

    public void setDataMartService( DataMartService dataMartService )
    {
        this.dataMartService = dataMartService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    private final static int ALL = 0;

    public int getALL()
    {
        return ALL;
    }

    private final static int DATAVALUE = 1;

    public int getDATAVALUE()
    {
        return DATAVALUE;
    }

    private final static int INDICATORVALUE = 2;

    public int getINDICATORVALUE()
    {
        return INDICATORVALUE;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public Integer getId()
    {
        return id;
    }
    
    public void setId( Integer id )
    {
        this.id = id;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<DataElement> dataElements;

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    private List<DataElementGroup> dataElementGroups;

    public List<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }

    private List<Indicator> indicators;

    public List<Indicator> getIndicators()
    {
        return indicators;
    }

    private List<IndicatorGroup> indicatorGroups;

    public List<IndicatorGroup> getIndicatorGroups()
    {
        return indicatorGroups;
    }

    private List<OrganisationUnit> organisationUnits;

    public List<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }

    private List<OrganisationUnitLevel> levels;

    public List<OrganisationUnitLevel> getLevels()
    {
        return levels;
    }
    
    private List<Period> periods = new ArrayList<Period>();

    public List<Period> getPeriods()
    {
        return periods;
    }

    private Collection<PeriodType> periodTypes;

    public Collection<PeriodType> getPeriodTypes()
    {
        return periodTypes;
    }
    
    private List<DataElement> selectedDataElements;

    public List<DataElement> getSelectedDataElements()
    {
        return selectedDataElements;
    }

    private List<Indicator> selectedIndicators;

    public List<Indicator> getSelectedIndicators()
    {
        return selectedIndicators;
    }

    private List<OrganisationUnit> selectedOrganisationUnits;

    public List<OrganisationUnit> getSelectedOrganisationUnits()
    {
        return selectedOrganisationUnits;
    }

    private List<Period> selectedPeriods;

    public List<Period> getSelectedPeriods()
    {
        return selectedPeriods;
    }
    
    private String name;

    public String getName()
    {
        return name;
    }
    
    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------

    private Comparator<DataElement> dataElementComparator;

    public void setDataElementComparator( Comparator<DataElement> dataElementComparator )
    {
        this.dataElementComparator = dataElementComparator;
    }

    private Comparator<Indicator> indicatorComparator;

    public void setIndicatorComparator( Comparator<Indicator> indicatorComparator )
    {
        this.indicatorComparator = indicatorComparator;
    }
    
    private Comparator<OrganisationUnit> organisationUnitComparator;

    public void setOrganisationUnitComparator( Comparator<OrganisationUnit> organisationUnitComparator )
    {
        this.organisationUnitComparator = organisationUnitComparator;
    }
    
    // -------------------------------------------------------------------------
    // DisplayPropertyHandler
    // -------------------------------------------------------------------------

    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Data element
        // ---------------------------------------------------------------------

        dataElements = new ArrayList<DataElement>( dataElementService.getAllDataElements() );

        Collections.sort( dataElements, dataElementComparator );

        displayPropertyHandler.handle( dataElements );
        
        FilterUtils.filter( dataElements, new AggregatableDataElementFilter() );
        
        // ---------------------------------------------------------------------
        // Data element group
        // ---------------------------------------------------------------------

        dataElementGroups = new ArrayList<DataElementGroup>( dataElementService.getAllDataElementGroups() );
        
        Collections.sort( dataElementGroups, new DataElementGroupNameComparator() );
        
        // ---------------------------------------------------------------------
        // Indicator
        // ---------------------------------------------------------------------

        indicators = new ArrayList<Indicator>( indicatorService.getAllIndicators() );

        Collections.sort( indicators, indicatorComparator );

        displayPropertyHandler.handle( indicators );

        // ---------------------------------------------------------------------
        // Indicator group
        // ---------------------------------------------------------------------

        indicatorGroups = new ArrayList<IndicatorGroup>(indicatorService.getAllIndicatorGroups());
        
        Collections.sort( indicatorGroups, new IndicatorGroupNameComparator() );

        // ---------------------------------------------------------------------
        // Organisation unit
        // ---------------------------------------------------------------------

        organisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitsAtLevel( 1 ) );

        Collections.sort( organisationUnits, organisationUnitComparator );
        
        displayPropertyHandler.handle( organisationUnits );
        
        // ---------------------------------------------------------------------
        // Level
        // ---------------------------------------------------------------------

        levels = organisationUnitService.getOrganisationUnitLevels();
        
        // ---------------------------------------------------------------------
        // Period type
        // ---------------------------------------------------------------------

        periodTypes = periodService.getAllPeriodTypes();

        // ---------------------------------------------------------------------
        // Period
        // ---------------------------------------------------------------------

        periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( new MonthlyPeriodType() ) );
        
        Collections.sort( periods, new PeriodComparator() );

        for ( Period period : periods )
        {
            period.setName( format.formatPeriod( period ) );
        }
        
        if ( id != null )
        {
            DataMartExport export = dataMartService.getDataMartExport( id );
            
            // -----------------------------------------------------------------
            // Data element
            // -----------------------------------------------------------------

            selectedDataElements = new ArrayList<DataElement>( export.getDataElements() );
            displayPropertyHandler.handle( selectedDataElements );
            Collections.sort( selectedDataElements, dataElementComparator );
            dataElements.removeAll( selectedDataElements );
            
            // ---------------------------------------------------------------------
            // Indicator
            // ---------------------------------------------------------------------

            selectedIndicators = new ArrayList<Indicator>( export.getIndicators() );
            displayPropertyHandler.handle( selectedIndicators );
            Collections.sort( selectedIndicators, indicatorComparator );
            indicators.removeAll( selectedIndicators );
            
            // ---------------------------------------------------------------------
            // Organisation unit
            // ---------------------------------------------------------------------

            selectedOrganisationUnits = new ArrayList<OrganisationUnit>( export.getOrganisationUnits() );
            displayPropertyHandler.handle( selectedOrganisationUnits );
            Collections.sort( selectedOrganisationUnits, organisationUnitComparator );
            organisationUnits.removeAll( selectedOrganisationUnits );
            
            // ---------------------------------------------------------------------
            // Period
            // ---------------------------------------------------------------------

            selectedPeriods = new ArrayList<Period>( export.getPeriods() );
            Collections.sort( selectedPeriods, new PeriodComparator() );
            periods.removeAll( selectedPeriods );
            
            for ( Period period : periods )
            {
                period.setName( format.formatPeriod( period ) );
            }
            
            // ---------------------------------------------------------------------
            // Name
            // ---------------------------------------------------------------------

            name = export.getName();
        }
        
        return SUCCESS;
    }
}
