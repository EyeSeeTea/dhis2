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
package org.hisp.dhis.reportexcel.export.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportexcel.ReportExcelService;
import org.hisp.dhis.reportexcel.period.db.PeriodDatabaseService;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @author Dang Duy Hieu
 * @version $Id$
 * @since 2009-11-26
 */
public class SelectFormAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private ReportExcelService reportService;

    public void setReportService( ReportExcelService reportService )
    {
        this.reportService = reportService;
    } 

    private PeriodDatabaseService periodDatabaseService;

    public void setPeriodDatabaseService( PeriodDatabaseService periodDatabaseService )
    {
        this.periodDatabaseService = periodDatabaseService;
    }

    private OrganisationUnitSelectionManager organisationUnitSelectionManager;

    public void setOrganisationUnitSelectionManager( OrganisationUnitSelectionManager organisationUnitSelectionManager )
    {
        this.organisationUnitSelectionManager = organisationUnitSelectionManager;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<Period> periods;

    private OrganisationUnit organisationUnit;

    private List<String> groups;

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    public List<String> getGroups()
    {
        return groups;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    } 
    
    public List<Period> getPeriods()
    {
        return periods;
    }

    private File fileExcel;

    public File getFileExcel()
    {
        return fileExcel;
    }

    public void setFileExcel( File fileExcel )
    {
        this.fileExcel = fileExcel;
    }

    public String execute()
        throws Exception
    {
        organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();

        if ( organisationUnit == null )
        {
            return SUCCESS;
        }

        periodDatabaseService.setSelectedPeriodTypeName( MonthlyPeriodType.NAME );

        periods = periodDatabaseService.getPeriodList();

        groups = new ArrayList<String>( reportService.getReportExcelGroups() );

        Collections.sort( groups );
       
        return SUCCESS;
    }

}
