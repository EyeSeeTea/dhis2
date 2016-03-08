package org.hisp.dhis.datastatistics;

/*
 * Copyright (c) 2004-2016, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.dashboard.DashboardService;
import org.hisp.dhis.eventchart.EventChartService;
import org.hisp.dhis.eventreport.EventReportService;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.user.UserService;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Weeks;
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


    /**
     * Adds an datastatistics event in the database
     * @param event - object to be saved
     * @return id of the object in the database
     */
    @Override
    public int addEvent(DataStatisticsEvent event)
    {
        return hibernateDataStatisticsEventStore.save( event );
    }

    /**
     * gets number of saved Reports from a start date too a end date
     * @param startDate - From date
     * @param endDate - Too date
     * @param eventInterval - Enum EventInterval (YEAR, MONTH, WEEK, DAY)
     * @return list of reports
     */
    @Override
    public List<AggregatedStatistics> getReports(Date startDate, Date endDate, EventInterval eventInterval )
    {
        Calendar start = Calendar.getInstance();
        start.setTime( startDate );

        Calendar end = Calendar.getInstance();
        end.setTime( endDate );

        DateTime startDateTime = new DateTime(start.getTime());
        DateTime endDateTime = new DateTime(end.getTime());

        switch ( eventInterval ){
            case DAY: return hibernateDataStatisticsStore.getSnapshotsInIntervalDay( startDate, endDate);

            case WEEK: return hibernateDataStatisticsStore.getSnapshotsInInterval(start, end, Calendar.WEEK_OF_YEAR, Weeks.weeksBetween(startDateTime,endDateTime).getWeeks());

            case MONTH: return hibernateDataStatisticsStore.getSnapshotsInInterval(start, end, Calendar.MONTH, Months.monthsBetween( startDateTime, endDateTime ).getMonths());

            case YEAR: return hibernateDataStatisticsStore.getSnapshotsInInterval(start, end, Calendar.YEAR, end.get(Calendar.YEAR) - start.get(Calendar.YEAR));

            default: return hibernateDataStatisticsStore.getSnapshotsInIntervalDay( startDate, endDate );
        }
    }

    /**
     * gets number of saved Charts from a date till now
     * @param date - From date
     * @return number of Charts saved in db
     */
    @Override
    public int getNumberOfCharts(Date date){
        return chartService.countChartGeCreated( date );
    }
    /**
     * gets number of saved Report tables from a date till now
     * @param date - From date
     * @return number of Report tables saved in db
     */
    @Override
    public int getNumberOfReportTables(Date date){

       return reportTableService.getCountGeCreated( date );
    }

    /**
     * gets number of saved favorite views from a start date too a end date
     * @param startDate - From date
     * @param endDate - Too date
     * @return number of favorite views saved in db
     */
    @Override
    public int getNumberOfFavoriteViews(Date startDate, Date endDate){
        return hibernateDataStatisticsEventStore.getNumberOfEvents(startDate, endDate);
    }

    /**
     * gets number of saved Maps from a date till now
     * @param date - From date
     * @return number of Maps saved in db
     */
    @Override
    public int getNumberOfMaps(Date date)
    {
        return mappingService.getCountGeCreated( date );
    }

    /**
     * gets number of saved Event Reports from a date till now
     * @param date - From date
     * @return number of Event Reports saved in db
     */
    @Override
    public int getNumberOfEventReports(Date date)
    {
        return eventReportService.getCountGeCreated(date);
    }

    /**
     * gets number of saved Event Charts from a date till now
     * @param date - From date
     * @return number of Event Charts saved in db
     */
    @Override
    public int getNumberOfEventCharts(Date date)
    {
        return eventChartService.countGeCreated(date);
    }

    /**
     * gets number of saved dashboards from a date till now
     * @param date - From date
     * @return number of dashboards saved in db
     */

    @Override
    public int getNumberOfDashboards(Date date)
    {
        return dashboardService.countGeCreated(date);
    }

    /**
     * Gets the number of indicators saved from a date till now
     * @param date - From date
     * @return number of indicators saved in db
     */
    @Override
    public int getNumberOfIndicators(Date date)
    {
        return indicatorService.getCountGeCreated(date);
    }

    /**
     * Gets a number of active users that day.
     * @return number og active users
     */
    @Override
    public int getNumberOfUsers()
    {
        return userService.getUserCount();
    }


    /**
     * Gets all important information and creates a Datastatistics object and saves it in db
     */
    @Override public int saveSnapshot( )
    {
        Date startDate = new Date( );
        Calendar c = Calendar.getInstance( );
        c.setTime( startDate );
        c.add( Calendar.DATE, -1);
        startDate = c.getTime( );

        List<DataStatisticsEvent> events = hibernateDataStatisticsEventStore.getDataStatisticsEventCount( startDate );
        List<String> uniqueUsers = new ArrayList<>( );
        int numberOfActiveUsers = 0;
        double numberOfMapViews = 0;
        double numberOfChartViews = 0;
        double numberOfReportTablesViews = 0;
        double numberOfEventReportViews = 0;
        double numberOfEventChartViews = 0;
        double totalNumberOfViews = events.size( );
        double averageNumberofViews = 0;
        double numberOfDashboardViews = 0;
        double numberOfIndicatorsViews = 0;

        int totalNumberOfUsers = getNumberOfUsers( );

        for( DataStatisticsEvent e : events ){
            switch ( e.getType( ) ) {
                case CHART_VIEWS: numberOfChartViews++;
                    break;
                case MAP_VIEWS: numberOfMapViews++;
                    break;
                case DASHBOARD_VIEWS: numberOfDashboardViews++;
                    break;
                case REPORT_TABLE_VIEWS: numberOfReportTablesViews++;
                    break;
                case EVENT_REPORT_VIEWS: numberOfEventReportViews++;
                    break;
                case EVENT_CHART_VIEWS: numberOfEventChartViews++;
                    break;
                case INDICATOR_VIEWS: numberOfIndicatorsViews++;
                    break;
            }

            if(!uniqueUsers.contains( e.getUserName( ) ) ){
                uniqueUsers.add( e.getUserName( ) );
            }
        }

        double numberOfSavedMaps = getNumberOfMaps( startDate );
        double numberOfSavedCharts = getNumberOfCharts( startDate );
        double numberOfSavedReportTables = getNumberOfReportTables( startDate );
        double numberOfSavedEventReports = getNumberOfEventReports( startDate );
        double numberOfSavedEventCharts = getNumberOfEventCharts( startDate );
        double numberOfSavedDashboards = getNumberOfDashboards( startDate );
        double numberOfSavedIndicators = getNumberOfIndicators( startDate );

        numberOfActiveUsers = uniqueUsers.size( );

        if( numberOfActiveUsers != 0 )
            averageNumberofViews = totalNumberOfViews / numberOfActiveUsers;

        DataStatistics dataStatistics = new DataStatistics( numberOfActiveUsers, numberOfMapViews, numberOfChartViews,
            numberOfReportTablesViews, numberOfEventReportViews, numberOfEventChartViews, numberOfDashboardViews,
            numberOfIndicatorsViews, totalNumberOfViews, averageNumberofViews, numberOfSavedMaps,
            numberOfSavedCharts, numberOfSavedReportTables, numberOfSavedEventReports,
            numberOfSavedEventCharts, numberOfSavedDashboards ,numberOfSavedIndicators ,
            totalNumberOfUsers );
        System.out.println( dataStatistics.toString( ) );

        try
        {
            int id = hibernateDataStatisticsStore.addSnapshot( dataStatistics );
            System.out.println( "\nSnapshot was saved with id: " + id );
            return id;
        }
        catch ( Exception e )
        {
            System.out.println( "Could not save snapshot. Exception: " + e.toString() );
            return 0;
        }
    }
}
