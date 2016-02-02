package org.hisp.dhis.datastatistics;

import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.common.AnalyticalObjectStore;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableService;

import java.util.List;

/**
 * @author Yrjan A. F. Fraschetti
 * @author Julie Hill Roa
 */
public class DefaultDataStatisticsEvent implements DataStatisticsEvent
{
    private AnalyticalObjectStore<Chart> chartStore;
    private AnalyticalObjectStore<ReportTable> reportStore;

    private ReportTableService defaultReportTableService;

    public void getReportTable()
    {
        System.out.println("\n\nInne i getReportTable\n\n");
        List<ReportTable> reports;
        try
        {
            reports = defaultReportTableService.getAllReportTables();
            //int id = 241411;
            //reports = reportStore.get;
            System.out.println(reports);
        }
        catch(Exception e){
            System.out.println("\n\nreports er tom!!!! " + e.toString());
         }

       /*Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria( ReportTable.class );
        Collection<ReportTable> table = (Collection<ReportTable>) criteria.list();

        System.out.println(table.toString());*/

    }

}
