/*
 * Copyright (c) 2004-2012, University of Oslo
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

package org.hisp.dhis.caseentry.action.report;

import static org.hisp.dhis.patientreport.PatientTabularReport.PREFIX_DATA_ELEMENT;
import static org.hisp.dhis.patientreport.PatientTabularReport.PREFIX_NUMBER_DATA_ELEMENT;
import static org.hisp.dhis.patientreport.PatientTabularReport.VALUE_TYPE_OPTION_SET;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.exception.SQLGrammarException;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.patientreport.TabularReportColumn;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.system.util.TextUtils;

/**
 * @author Chau Thu Tran
 * 
 * @version $GenerateTabularReportAction.java Feb 29, 2012 10:15:05 AM$
 */
public class GenerateTabularReportAction
    extends ActionPagingSupport<ProgramStageInstance>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Collection<Integer> orgunitIds;

    public void setOrgunitIds( Collection<Integer> orgunitIds )
    {
        this.orgunitIds = orgunitIds;
    }

    private Integer programStageId;

    public void setProgramStageId( Integer programStageId )
    {
        this.programStageId = programStageId;
    }

    private String startDate;

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    private String endDate;

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    private List<String> values = new ArrayList<String>();

    public List<String> getValues()
    {
        return values;
    }

    private List<String> searchingValues = new ArrayList<String>();

    public void setSearchingValues( List<String> searchingValues )
    {
        this.searchingValues = searchingValues;
    }

    private boolean orderByOrgunitAsc;

    public void setOrderByOrgunitAsc( boolean orderByOrgunitAsc )
    {
        this.orderByOrgunitAsc = orderByOrgunitAsc;
    }

    private Integer level;

    public void setLevel( Integer level )
    {
        this.level = level;
    }

    private Grid grid;

    public Grid getGrid()
    {
        return grid;
    }

    private int totalRecords;

    public int getTotalRecords()
    {
        return totalRecords;
    }

    public void setTotal( Integer total )
    {
        this.total = total;
    }

    private Integer total;

    public Integer getTotal()
    {
        return total;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private Boolean completed;

    public void setCompleted( Boolean completed )
    {
        this.completed = completed;
    }

    private List<DataElement> dataElements = new ArrayList<DataElement>();

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    private String type;

    public void setType( String type )
    {
        this.type = type;
    }

    private String facilityLB; // All, children, current

    public void setFacilityLB( String facilityLB )
    {
        this.facilityLB = facilityLB;
    }

    private List<String> valueTypes = new ArrayList<String>();

    public List<String> getValueTypes()
    {
        return valueTypes;
    }

    private Map<Integer, List<String>> mapSuggestedValues = new HashMap<Integer, List<String>>();

    public Map<Integer, List<String>> getMapSuggestedValues()
    {
        return mapSuggestedValues;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Get orgunitIds
        // ---------------------------------------------------------------------

        Set<Integer> organisationUnits = new HashSet<Integer>();

        if ( facilityLB.equals( "selected" ) )
        {
            organisationUnits.addAll( orgunitIds );
        }
        else if ( facilityLB.equals( "childrenOnly" ) )
        {
            for ( Integer orgunitId : orgunitIds )
            {
                OrganisationUnit selectedOrgunit = organisationUnitService.getOrganisationUnit( orgunitId );
                organisationUnits.addAll( organisationUnitService.getOrganisationUnitHierarchy()
                    .getChildren( orgunitId ) );
                organisationUnits.remove( selectedOrgunit );
            }
        }
        else
        {
            for ( Integer orgunitId : orgunitIds )
            {
                OrganisationUnit selectedOrgunit = organisationUnitService.getOrganisationUnit( orgunitId );

                if ( selectedOrgunit.getParent() == null )
                {
                    organisationUnits = null; // Ignore unit criteria when root
                }
                else
                {
                    organisationUnits.addAll( organisationUnitService.getOrganisationUnitHierarchy().getChildren(
                        orgunitId ) );
                }
            }
        }

        // ---------------------------------------------------------------------
        // Get program-stage, start-date, end-date
        // ---------------------------------------------------------------------

        if ( level == 0 )
        {
            level = organisationUnitService.getMaxOfOrganisationUnitLevels();
            for ( Integer orgunitId : orgunitIds )
            {
                int orgLevel = organisationUnitService.getLevelOfOrganisationUnit( orgunitId );
                if ( level > orgLevel )
                {
                    level = orgLevel;
                }
            }
        }

        // ---------------------------------------------------------------------
        // Get program-stage, start-date, end-date
        // ---------------------------------------------------------------------

        ProgramStage programStage = programStageService.getProgramStage( programStageId );
        Date startValue = format.parseDate( startDate );
        Date endValue = format.parseDate( endDate );
        List<TabularReportColumn> columns = getTableColumns();

        // ---------------------------------------------------------------------
        // Generate tabular report
        // ---------------------------------------------------------------------
        try
        {
            if ( type == null ) // Tabular report
            {
                totalRecords = programStageInstanceService.getTabularReportCount( programStage, columns,
                    organisationUnits, level, completed, startValue, endValue );

                total = getNumberOfPages( totalRecords );

                this.paging = createPaging( totalRecords );

                grid = programStageInstanceService.getTabularReport( programStage, columns, organisationUnits, level,
                    startValue, endValue, !orderByOrgunitAsc, completed, getStartPos(), paging.getPageSize(), i18n );
            }
            else
            // Download as Excel
            {
                grid = programStageInstanceService.getTabularReport( programStage, columns, organisationUnits, level,
                    startValue, endValue, !orderByOrgunitAsc, completed, null, null, i18n );
            }
        }
        catch ( SQLGrammarException ex )
        {
            message = i18n.getString( "failed_to_get_events" );
        }

        return type == null ? SUCCESS : type;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private int getNumberOfPages( int totalRecord )
    {
        int pageSize = this.getDefaultPageSize();
        return (totalRecord % pageSize == 0) ? (totalRecord / pageSize) : (totalRecord / pageSize + 1);
    }

    public int getStartPos()
    {
        if ( currentPage == null )
        {
            return paging.getStartPos();
        }
        int startPos = currentPage <= 0 ? 0 : (currentPage - 1) * paging.getPageSize();
        startPos = (startPos > total) ? total : startPos;
        return startPos;
    }

    private List<TabularReportColumn> getTableColumns()
    {
        List<TabularReportColumn> columns = new ArrayList<TabularReportColumn>();

        int index = 0;

        for ( String searchValue : searchingValues )
        {
            String[] values = searchValue.split( "_" );

            if ( values != null && values.length >= 3 )
            {
                String prefix = values[0];

                TabularReportColumn column = new TabularReportColumn();
                column.setPrefix( prefix );
                column.setIdentifier( values[1] );
                column.setHidden( Boolean.parseBoolean( values[2] ) );
                column.setQuery( values.length == 4 ? TextUtils.lower( values[3] ) : null );

                if ( PREFIX_DATA_ELEMENT.equals( prefix ) )
                {
                    int objectId = Integer.parseInt( values[1] );
                    DataElement dataElement = dataElementService.getDataElement( objectId );
                    if ( dataElement.getType().equals( DataElement.VALUE_TYPE_INT ) )
                    {
                        column.setPrefix( PREFIX_NUMBER_DATA_ELEMENT );
                    }
                    dataElements.add( dataElement );

                    String valueType = dataElement.getOptionSet() != null ? VALUE_TYPE_OPTION_SET : dataElement
                        .getType();
                    valueTypes.add( valueType );
                    mapSuggestedValues.put( index, getSuggestedDataElementValues( dataElement ) );

                    column.setName( dataElement.getFormNameFallback() );
                }

                columns.add( column );

                index++;
            }
        }

        return columns;
    }

    private List<String> getSuggestedDataElementValues( DataElement dataElement )
    {
        List<String> values = new ArrayList<String>();
        String valueType = dataElement.getType();

        if ( valueType.equals( DataElement.VALUE_TYPE_BOOL ) )
        {
            values.add( i18n.getString( "yes" ) );
            values.add( i18n.getString( "no" ) );
        }
        if ( valueType.equals( DataElement.VALUE_TYPE_TRUE_ONLY ) )
        {
            values.add( i18n.getString( "" ) );
            values.add( i18n.getString( "yes" ) );
        }

        return values;
    }
}
