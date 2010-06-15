package org.hisp.dhis.reports;

import org.hisp.dhis.source.Source;
import org.hisp.dhis.system.deletion.DeletionHandler;

public class Report_inDeletionHandler
    extends DeletionHandler
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    // -------------------------------------------------------------------------
    // DeletionHandler implementation
    // -------------------------------------------------------------------------
    
    @Override
    public String getClassName()
    {
        return Report_in.class.getSimpleName();
    }

    @Override
    public void deleteSource( Source source )
    {
        for ( Report_in report : reportService.getAllReports() )
        {
            if ( report.getSources().remove( source ) )
            {
                reportService.updateReport( report );
            }
        }
    }

    public void deleteReport_in( Report_in report )
    {
    }

    public boolean allowDeleteReport_in( Report_in report )
    {
        return true;
    }
}
