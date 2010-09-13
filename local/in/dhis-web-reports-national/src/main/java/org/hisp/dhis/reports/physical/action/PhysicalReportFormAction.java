package org.hisp.dhis.reports.physical.action;

/**
 * @author devratan
 *
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.ReportType;
import org.hisp.dhis.reports.Report_in;

import com.opensymphony.xwork2.Action;

public class PhysicalReportFormAction
    implements Action

{
   // private final String ALL = "null";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private String reportTypeName;

    public String getReportTypeName()
    {
        return reportTypeName;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private List<String> periodNameList;

    public List<String> getPeriodNameList()
    {
        return periodNameList;
    }

    private SimpleDateFormat simpleDateFormat1;

   // private String id;

   // public void setId( String id )
   // {
  //      this.id = id;
   // }

    private List<Period> periods;

    public List<Period> getPeriods()
    {
        return periods;
    }

    private List<String> reportIds;

    public List<String> getReportIds()
    {
        return reportIds;
    }

    private List<String> reportNames;

    public List<String> getReportNames()
    {
        return reportNames;
    }

    private List<Report_in> reportList;

    public List<Report_in> getReportList()
    {
        return reportList;
    }

  //  private String raFolderName;

    int count;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        // reportTypeName = ReportType.RT_PHYSICAL_OUTPUT;

        periodNameList = new ArrayList<String>();

        periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( new YearlyPeriodType() ) );

        Iterator<Period> periodIterator = periods.iterator();
        while ( periodIterator.hasNext() )
        {
            Period p1 = periodIterator.next();

            if ( p1.getStartDate().compareTo( new Date() ) > 0 )
            {
                periodIterator.remove();
            }
        }
        Collections.sort( periods, new PeriodComparator() );

        simpleDateFormat1 = new SimpleDateFormat( "yyyy" );

        for ( Period p1 : periods )
        {
            int year = Integer.parseInt( simpleDateFormat1.format( p1.getStartDate() ) ) + 1;
            periodNameList.add( simpleDateFormat1.format( p1.getStartDate() ) + "-" + year );
        }
        System.out.println( periods );
        reportList = new ArrayList<Report_in>( reportService.getReportsByReportType( ReportType.RT_PHYSICAL_OUTPUT ) );

        return SUCCESS;

    }

}
