package org.hisp.dhis.reporting.reportviewer.action;

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

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.hisp.dhis.system.util.TextUtils.getTrimmedValue;
import static org.hisp.dhis.util.ContextUtils.getBaseUrl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.report.ReportManager;
import org.hisp.dhis.report.ReportService;
import org.hisp.dhis.report.comparator.ReportComparator;
import org.hisp.dhis.report.manager.ReportConfiguration;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class GetAllReportsAction
    extends ActionPagingSupport<Report>
{
    private static final String SEPARATOR = "/";
    private static final String BASE_QUERY = "frameset?__report=";
    private static final String JASPER_BASE_URL = "renderReport.action?id=";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ReportManager reportManager;

    public void setReportManager( ReportManager reportManager )
    {
        this.reportManager = reportManager;
    }

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }
    
    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<Report> reports = new ArrayList<Report>();

    public List<Report> getReports()
    {
        return reports;
    }
    
    private String key;
    
    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = getTrimmedValue( key );
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() 
        throws Exception
    {
        String reportFramework = (String) systemSettingManager.getSystemSetting( SystemSettingManager.KEY_REPORT_FRAMEWORK, Report.TYPE_DEFAULT );
        
        if ( reportFramework.equals( Report.TYPE_JASPER ) )
        {
            for ( Report report : reportService.getAllReports() )
            {
                report.setUrl( JASPER_BASE_URL + report.getId() );
                
                reports.add( report );
            }
        }
        else // BIRT
        {
            ReportConfiguration config = reportManager.getConfiguration();
            
            HttpServletRequest request = ServletActionContext.getRequest();
            
            String birtURL = getBaseUrl( request ) + config.getDirectory() + SEPARATOR + BASE_QUERY;
            
            for ( Report report : reportService.getAllReports() )
            {
                report.setUrl( birtURL + report.getDesign() );
                
                reports.add( report );
            }
        }
        
        Collections.sort( reports, new ReportComparator() );
        
        if ( isNotBlank( key ) )
        {
            reports = searchByName( reports, key );
            
            this.paging = createPaging( reports.size() );
            
            reports = getBlockElement( reports, paging.getStartPos(), paging.getPageSize() );           
        }
        else 
        {
            this.paging = createPaging( reports.size() );
                
            reports = getBlockElement( reports, paging.getStartPos(), paging.getPageSize() );
        }
        
        return SUCCESS;
    }
    
    private List<Report> searchByName( List<Report> reports, String key )
    {
        List<Report> result = new ArrayList<Report>();

        for ( Report each : reports )
        {
            if ( each.getName().toLowerCase().contains( key.toLowerCase() ) )
            {
                result.add( each );
            }
        }
        
        return result;
    }
}
