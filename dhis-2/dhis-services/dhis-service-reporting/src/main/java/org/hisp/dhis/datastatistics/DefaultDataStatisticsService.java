package org.hisp.dhis.datastatistics;

import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.dashboard.DashboardService;
import org.hisp.dhis.eventchart.EventChartService;
import org.hisp.dhis.eventreport.EventReportService;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Yrjan A. F. Fraschetti
 * @author Julie Hill Roa
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



    @Override
    public int addEvent(DataStatisticsEvent event)
    {
        return hibernateDataStatisticsEventStore.addDataStatisticsEvent( event );
    }

    @Override
    public List<DataStatistics> getReports(Date startDate, Date endDate)
    {
        return hibernateDataStatisticsStore.getSnapshotsInInterval( startDate, endDate );
    }

    @Override
    public int getNumberOfCharts(Date startDate){
        return chartService.countChartGeCreated( startDate );
    }

    @Override
    public int getNumberOfReportTables(Date date){

       return reportTableService.getCountGeCreated( date );
    }

    @Override
    public int getNumberOfFavoriteViews(Date startDate, Date endDate){
        return hibernateDataStatisticsEventStore.getNumberOfEvents(startDate, endDate);
    }

    @Override
    public int getNumberOfMaps(Date date)
    {
        return mappingService.getCountGeCreated( date );
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
        return userService.getActiveUsersCount( 1000 );
    }


    @Override public void saveSnapshot()
    {
        Date startDate = new Date();
        Date endDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime( startDate );
        //c.add( Calendar.DATE, -1 );
        c.add( Calendar.YEAR, -3 );
        startDate = c.getTime();

        System.out.println("\n\nstartDate.toString: " + startDate.toString());
        System.out.println("\n\nendDate.toString: " + endDate.toString());

        //TODO Lagre antall unike brukere, antall av hver enum, gjennomsnitt, total, når på døgnet folk er mest aktive
        List<DataStatisticsEvent> events = hibernateDataStatisticsEventStore.getDataStatisticsEventList(startDate);
        List<Integer> uniqueUsers = new ArrayList<>();
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

        int totalNumberOfUsers = getNumberOfUsers( startDate );
        System.out.println("\n\ntotalNumersOfUsers: " + totalNumberOfUsers);


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

        int averageNumberOfSavedMaps =getNumberOfMaps( startDate ) / totalNumberOfUsers;
        int averageNumberOfSavedCharts = getNumberOfCharts( startDate );
        int averageNumberOfSavedReportTables = getNumberOfReportTables( startDate ) / totalNumberOfUsers;
        int averageNumberOfSavedEventReports = getNumberOfEventReports( startDate ) ;
        int averageNumberOfSavedEventCharts = getNumberOfEventCharts( startDate );
        int averageNumberOfSavedDashboards = getNumberOfDashboards( startDate ) ;
        int averageNumberOfSavedIndicators = getNumberOfIndicators( startDate ) ;

        numberOfUsers = uniqueUsers.size();
        averageNumberofViews = totalNumberOfViews / numberOfUsers;

        DataStatistics dataStatistics = new DataStatistics( numberOfUsers, numberOfMapViews, numberOfChartViews,
            numberOfReportTablesViews, numberOfEventReportViews, numberOfEventChartViews, numberOfDashboardViews,
            numberOfIndicatorsViews, totalNumberOfViews, averageNumberofViews, averageNumberOfSavedMaps,
            averageNumberOfSavedCharts, averageNumberOfSavedReportTables, averageNumberOfSavedEventReports,
            averageNumberOfSavedEventCharts, averageNumberOfSavedDashboards ,averageNumberOfSavedIndicators ,
            totalNumberOfUsers );
        System.out.println(dataStatistics.toString());
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
