package org.hisp.dhis.importexport.action.dxf2;

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

import com.opensymphony.xwork2.Action;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.dxf2.metadata.ExportService;
import org.hisp.dhis.dxf2.metadata.Filter;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.lang.System;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Rosu Ovidiu <rosu.ovi@gmail.com>
 */
public class MetaDataSubSetExportFormAction
    implements Action
{

    @Autowired
    private ExportService exportService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private HttpServletRequest request;

    public HttpServletRequest getRequest()
    {
        return request;
    }

    private Set<String> selectedAttributeTypes;

    public void setSelectedAttributeTypes( Set<String> selectedAttributeTypes )
    {
        this.selectedAttributeTypes = selectedAttributeTypes;
    }

    private Set<String> selectedDimensions;

    public void setSelectedDimensions( Set<String> selectedDimensions )
    {
        this.selectedDimensions = selectedDimensions;
    }

    private Set<String> selectedCharts;

    public void setSelectedCharts( Set<String> selectedCharts )
    {
        this.selectedCharts = selectedCharts;
    }

    private Set<String> selectedConcepts;

    public void setSelectedConcepts( Set<String> selectedConcepts )
    {
        this.selectedConcepts = selectedConcepts;
    }

    private Set<String> selectedConstants;

    public void setSelectedConstants( Set<String> selectedConstants )
    {
        this.selectedConstants = selectedConstants;
    }

    private Set<String> selectedDataDictionaries;

    public void setSelectedDataDictionaries( Set<String> selectedDataDictionaries )
    {
        this.selectedDataDictionaries = selectedDataDictionaries;
    }

    private Set<String> selectedDataElementGroupSets;

    public void setSelectedDataElementGroupSets( Set<String> selectedDataElementGroupSets )
    {
        this.selectedDataElementGroupSets = selectedDataElementGroupSets;
    }

    private Set<String> selectedDataElementGroups;

    public void setSelectedDataElementGroups( Set<String> selectedDataElementGroups )
    {
        this.selectedDataElementGroups = selectedDataElementGroups;
    }

    private Set<String> selectedDataElements;

    public void setSelectedDataElements( Set<String> selectedDataElements )
    {
        this.selectedDataElements = selectedDataElements;
    }

    private Set<String> selectedDataSets;

    public void setSelectedDataSets( Set<String> selectedDataSets )
    {
        this.selectedDataSets = selectedDataSets;
    }

    private Set<String> selectedDocuments;

    public void setSelectedDocuments( Set<String> selectedDocuments )
    {
        this.selectedDocuments = selectedDocuments;
    }

    private Set<String> selectedIndicatorGroupSets;

    public void setSelectedIndicatorGroupSets( Set<String> selectedIndicatorGroupSets )
    {
        this.selectedIndicatorGroupSets = selectedIndicatorGroupSets;
    }

    private Set<String> selectedIndicatorGroups;

    public void setSelectedIndicatorGroups( Set<String> selectedIndicatorGroups )
    {
        this.selectedIndicatorGroups = selectedIndicatorGroups;
    }

    private Set<String> selectedIndicatorTypes;

    public void setSelectedIndicatorTypes( Set<String> selectedIndicatorTypes )
    {
        this.selectedIndicatorTypes = selectedIndicatorTypes;
    }

    private Set<String> selectedIndicators;

    public void setSelectedIndicators( Set<String> selectedIndicators )
    {
        this.selectedIndicators = selectedIndicators;
    }

    private Set<String> selectedMapLegendSets;

    public void setSelectedMapLegendSets( Set<String> selectedMapLegendSets )
    {
        this.selectedMapLegendSets = selectedMapLegendSets;
    }

    private Set<String> selectedMaps;

    public void setSelectedMaps( Set<String> selectedMaps )
    {
        this.selectedMaps = selectedMaps;
    }

    private Set<String> selectOptionSets;

    public void setSelectOptionSets( Set<String> selectOptionSets )
    {
        this.selectOptionSets = selectOptionSets;
    }

    private Set<String> selectOrganisationUnitGroupSets;

    public void setSelectOrganisationUnitGroupSets( Set<String> selectOrganisationUnitGroupSets )
    {
        this.selectOrganisationUnitGroupSets = selectOrganisationUnitGroupSets;
    }

    private Set<String> selectOrganisationUnitGroups;

    public void setSelectOrganisationUnitGroups( Set<String> selectOrganisationUnitGroups )
    {
        this.selectOrganisationUnitGroups = selectOrganisationUnitGroups;
    }

    private Set<String> selectedOrganisationUnitLevels;

    public void setSelectedOrganisationUnitLevels( Set<String> selectedOrganisationUnitLevels )
    {
        this.selectedOrganisationUnitLevels = selectedOrganisationUnitLevels;
    }

    private Set<String> selectedOrganisationUnits;

    public void setSelectedOrganisationUnits( Set<String> selectedOrganisationUnits )
    {
        this.selectedOrganisationUnits = selectedOrganisationUnits;
    }

    private Set<String> selectedReportTables;

    public void setSelectedReportTables( Set<String> selectedReportTables )
    {
        this.selectedReportTables = selectedReportTables;
    }

    private Set<String> selectedReports;

    public void setSelectedReports( Set<String> selectedReports )
    {
        this.selectedReports = selectedReports;
    }

    private Set<String> selectedSqlViews;

    public void setSelectedSqlViews( Set<String> selectedSqlViews )
    {
        this.selectedSqlViews = selectedSqlViews;
    }

    private Set<String> selectedUserGroups;

    public void setSelectedUserGroups( Set<String> selectedUserGroups )
    {
        this.selectedUserGroups = selectedUserGroups;
    }

    private Set<String> selectedUserRoles;

    public void setSelectedUserRoles( Set<String> selectedUserRoles )
    {
        this.selectedUserRoles = selectedUserRoles;
    }

    private Set<String> selectedUsers;

    public void setSelectedUsers( Set<String> selectedUsers )
    {
        this.selectedUsers = selectedUsers;
    }

    private Set<String> selectedValidationRuleGroups;

    public void setSelectedValidationRuleGroups( Set<String> selectedValidationRuleGroups )
    {
        this.selectedValidationRuleGroups = selectedValidationRuleGroups;
    }

    private Set<String> selectedValidationRules;

    public void setSelectedValidationRules( Set<String> selectedValidationRules )
    {
        this.selectedValidationRules = selectedValidationRules;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute() throws Exception
    {
        Filter filter = new Filter();

        Map requestMap = ServletActionContext.getRequest().getParameterMap();


//        MetaData metaData = exportService.getFilteredMetaData( filter );



        return SUCCESS;
    }
}
