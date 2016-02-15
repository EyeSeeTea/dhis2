package org.hisp.dhis.datastatistics;

import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.dashboard.DashboardService;
import org.hisp.dhis.eventreport.EventReportService;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.reporttable.ReportTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by yrjanaff on 08.02.2016.
 */

@Transactional
public class DefaultDataStatisticsService implements DataStatisticsService
{
    @Autowired
    DataStatisticsEventStore hibernateDataStatisticsEventStore;

    @Autowired
    ChartService chartService;

    @Autowired
    ReportTableService reportTableService;

    /*@Autowired
    MappingService mappingService;

    @Autowired
    EventReportService eventReportService;*/

    @Autowired
    DashboardService dashboardService;


    @Override
    public int addEvent(DataStatisticsEvent event)
    {
        return hibernateDataStatisticsEventStore.addDataStatisticsEvent( event );
    }

    @Override
    public DataStatistics createReport(Date startDate, Date endDate)
    {
        int favViewsCount = getNumberOfFavoriteViews( startDate, endDate );
        int chartsCount = getNumberOfCharts();
        int reportTableCount = getNumberOfReportTables();
        int favCount = chartsCount + reportTableCount;

        DataStatistics dataStatistics = new DataStatistics( favCount, reportTableCount, chartsCount, 0, 0, 0, favViewsCount );

        return dataStatistics;
    }

    @Override
    public int getNumberOfCharts(){
        return chartService.getChartCount();
    }

    @Override
    public int getNumberOfReportTables(){
        return reportTableService.getReportTableCount();
    }

    @Override
    public int getNumberOfFavoriteViews(Date startDate, Date endDate){
        return hibernateDataStatisticsEventStore.getNumberOfEvents( startDate, endDate );
    }

    @Override
    public int getNumberOfMaps()
    {
        return 0;
    }

    @Override
    public int getNumberOfEventReports()
    {
        return 0;
    }

    @Override
    public int getNumberOfEventCharts()
    {
        return 0;
    }

    @Override
    public int getNumberOfDashboards()
    {
        return 0;
    }

    @Override
    public int getNumberOfIndicators()
    {
        return 0;
    }

    @Override
    public int getNumberOfUsers(){
        return 0;
    }

    @Override public void saveSnapshot()
    {
        //TODO Lagre antall unike brukere, antall av hver enum, gjennomsnitt, total, når på døgnet folk er mest aktive
        List<DataStatisticsEvent> events = hibernateDataStatisticsEventStore.getDataStatisticsEventList();
        System.out.println("\n\nFirst place: " + events.get( 0 ));
    }
}
