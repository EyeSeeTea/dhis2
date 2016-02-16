package org.hisp.dhis.datastatistics;


import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.dashboard.DashboardService;
import org.hisp.dhis.eventchart.EventChartService;
import org.hisp.dhis.eventreport.EventReportService;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yrjanaff on 08.02.2016.
 */

@Transactional
public class DefaultDataStatisticsService implements DataStatisticsService
{
    @Autowired
    DataStatisticsStore hibernateDataStatisticsStore;

    @Autowired
    DataStatisticsEventStore hibernateDataStatisticsEventStore;

    @Autowired
    ChartService chartService;

    @Autowired
    ReportTableService reportTableService;

    @Autowired
    MappingService mappingService;

    @Autowired
    EventReportService eventReportService;

    @Autowired
    UserService userService;

    @Autowired
    IndicatorService indicatorService;

    @Autowired
    EventChartService eventChartService;

    @Autowired
    DashboardService dashboardService;

    @Autowired
    PeriodService periodService;


    @Override
    public int addEvent(DataStatisticsEvent event)
    {
        return hibernateDataStatisticsEventStore.addDataStatisticsEvent( event );
    }

    @Override
    public DataStatistics createReport(Date startDate, Date endDate)
    {
        int favViewsCount = getNumberOfFavoriteViews( startDate, endDate );
        //int chartsCount = getNumberOfCharts();
        //int reportTableCount = getNumberOfReportTables();
        //int favCount = chartsCount + reportTableCount;

        //DataStatistics dataStatistics = new DataStatistics( favCount, reportTableCount, chartsCount, 0, 0, 0, favViewsCount );

        return null;
    }

    @Override
    public int getNumberOfCharts(Date startDate){
        return chartService.countChartGeCreated( startDate );
    }

    @Override
    public int getNumberOfReportTables(Date startDate, Date endDate){
        DailyPeriodType dailyPeriodType = new DailyPeriodType();
        //Period period = new Period(  );
        //period.setStartDate( startDate ); period.setEndDate( endDate ); period.setPeriodType( dailyPeriodType );
        return reportTableService.countAnalyticalObjects(periodService.getPeriod( startDate, endDate, dailyPeriodType ));
    }

    @Override
    public int getNumberOfFavoriteViews(Date startDate, Date endDate){
        return hibernateDataStatisticsEventStore.getNumberOfEvents(startDate, endDate);
    }

    @Override
    public int getNumberOfMaps(Date startDate, Date endDate)
    {
        DailyPeriodType dailyPeriodType = new DailyPeriodType();
        //Period period = new Period(  );
        //period.setStartDate( startDate ); period.setEndDate( endDate ); period.setPeriodType( dailyPeriodType );
        return mappingService.countAnalyticalObjects( periodService.getPeriod( startDate, endDate, dailyPeriodType ) );
    }

    @Override
    public int getNumberOfEventReports(Date date)
    {
        return eventReportService.getCountGeCreated(date);
    }

    @Override
    public int getNumberOfEventCharts(Date date)
    {
        return eventChartService.countGeCreated(date);
    }

    @Override
    public int getNumberOfDashboards(Date date)
    {
        return dashboardService.countGeCreated(date);
    }

    @Override
    public int getNumberOfIndicators(Date date)
    {
        return indicatorService.getCountGeCreated(date);
    }

    @Override
    public int getNumberOfUsers(Date date)
    {
        return userService.getActiveUsersCount( 1 );
    }


    @Override public void saveSnapshot()
    {
        //TODO Lagre antall unike brukere, antall av hver enum, gjennomsnitt, total, når på døgnet folk er mest aktive
        List<DataStatisticsEvent> events = hibernateDataStatisticsEventStore.getDataStatisticsEventList();
        List<Integer> uniqueUsers = new ArrayList<Integer>();
        int numberOfUsers = 0;
        int numberOfMapViews = 0;
        int numberOfChartViews = 0;
        int numberOfReportTablesViews = 0;
        int numberOfEventReportViews = 0;
        int numberOfEventChartViews = 0;
        int totalNumberOfViews = events.size();
        int averageNumberofViews = 0;
        int numberOfDashboardViews = 0;
        int numberOfIndicatorsViews = 0;


        for(DataStatisticsEvent e : events){
            switch ( e.getType() ) {
                case CHART: numberOfChartViews++;
                    break;
                case MAP: numberOfMapViews++;
                    break;
                case DASHBOARD: numberOfDashboardViews++;
                    break;
                case REPORT_TABLE: numberOfReportTablesViews++;
                    break;
                case EVENT_REPORT: numberOfEventReportViews++;
                    break;
                case EVENT_CHART: numberOfEventChartViews++;
                    break;
                case INDICATOR: numberOfIndicatorsViews++;
                    break;
            }
            if(!uniqueUsers.contains( e.getUserId())){
                uniqueUsers.add( e.getUserId() );
            }
        }

        numberOfUsers = uniqueUsers.size();
        averageNumberofViews = totalNumberOfViews / numberOfUsers;

        System.out.println("\nNumberofUsers: " + numberOfUsers +
        "\nNumberOfMapViews: " + numberOfMapViews +
        "\nNumberofChartViews: " + numberOfChartViews +
        "\nnumberOfReportTableViews: " + numberOfReportTablesViews +
        "\nNumberOfEventReportViews: " + numberOfEventReportViews +
        "\nNumberOfEventChartViews: " + numberOfEventChartViews +
        "\ntotalNumberofViews: " + totalNumberOfViews +
        "\naverageNumberOfViews: " + averageNumberofViews +
        "\nNumberOfDashboardViews: " + numberOfDashboardViews +
        "\nnumberOfIndicatorViews: " + numberOfIndicatorsViews);

        DataStatistics dataStatistics = new DataStatistics( numberOfUsers, numberOfMapViews, numberOfChartViews,
            numberOfReportTablesViews, numberOfEventReportViews, numberOfEventChartViews, numberOfDashboardViews,
            numberOfIndicatorsViews, totalNumberOfViews, averageNumberofViews, 0, 0, 0, 0, 0, 0 ,0 ,0 );

        int id = hibernateDataStatisticsStore.addSnapshot( dataStatistics );

        if(id == 0){
           // Logger logger = Logger.getLogger(DefaultDataStatisticsService.class);
            System.out.println("\n\nCould not save snapshot");
        }
        else{
            System.out.println("\n\nSnapshot was saved with id: " + id);
        }

    }
}
