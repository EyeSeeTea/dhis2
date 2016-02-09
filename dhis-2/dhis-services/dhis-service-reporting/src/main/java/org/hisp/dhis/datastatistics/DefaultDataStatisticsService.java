package org.hisp.dhis.datastatistics;

import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.reporttable.ReportTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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
    public  int getNumberOfUsers(){
        return 0;
    }
}
