package org.hisp.dhis.reporting.dataset.action;

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

import java.util.Map;

import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datasetreport.DataSetReportService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reporting.dataset.state.SelectedStateManager;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class GenerateCustomDataSetReportAction
    extends AbstractAction
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private DataEntryFormService dataEntryFormService;

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }    
    
    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }
    
    private DataSetReportService dataSetReportService;

    public void setDataSetReportService( DataSetReportService dataSetReportService )
    {
        this.dataSetReportService = dataSetReportService;
    }

    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------      
    
    private String customDataEntryFormCode;

    public String getCustomDataEntryFormCode()
    {
        return this.customDataEntryFormCode;
    }
    
    private String reportingUnit;
    
    public String getReportingUnit()
    {
    	return this.reportingUnit;
    }
    
    private String reportingPeriod;
    
    public String getReportingPeriod()
    {
    	return this.reportingPeriod;
    }  
   
    private String selectedUnitOnly;
    
    public void setSelectedUnitOnly( String selectedUnitOnly )
    {
    	this.selectedUnitOnly = selectedUnitOnly;
    }
    
    public String getSelectedUnitOnly()
    {
    	return selectedUnitOnly;
    }
    
    // -----------------------------------------------------------------------
    // Action implementation
    // -----------------------------------------------------------------------
    
    public String execute()
        throws Exception
    {        
        OrganisationUnit unit = selectedStateManager.getSelectedOrganisationUnit();    
        
        DataSet dataSet = selectedStateManager.getSelectedDataSet();

        Period period = selectedStateManager.getSelectedPeriod();       
        
        if ( unit != null && dataSet != null && period != null )
        {
            Map<String, String> aggregatedDataValueMap = dataSetReportService.getAggregatedValueMap( dataSet, unit, period, selectedUnitOnly != null );
            
            DataEntryForm dataEntryForm = dataEntryFormService.getDataEntryFormByDataSet( dataSet );
            
            customDataEntryFormCode = dataSetReportService.prepareReportContent( dataEntryForm.getHtmlCode(), aggregatedDataValueMap );
            
            reportingUnit = unit.getName();
            
            reportingPeriod = format.formatPeriod( period );
           
            return SUCCESS;           	
        }
        
        return ERROR;
    }
}
