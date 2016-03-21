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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
     *
     * @param event - object to be saved
     * @return id of the object in the database
     */
    public int addEvent( DataStatisticsEvent event )
    {
        return hibernateDataStatisticsEventStore.save( event );
    }

    /**
     * gets number of saved Reports from a start date too a end date
     *
     * @param startDate     - From date
     * @param endDate       - Too date
     * @param eventInterval - Enum EventInterval (YEAR, MONTH, WEEK, DAY)
     * @return list of reports
     */
    @Override
    public List<AggregatedStatistics> getReports( Date startDate, Date endDate, EventInterval eventInterval )
    {
        String sql = "";
        switch ( eventInterval )
        {
            case DAY:
                sql = getDaySql( startDate, endDate );
                break;
            case WEEK:
                sql = getWeekSql( startDate, endDate );
                break;
            case MONTH:
                sql = getMonthSql( startDate, endDate );
                break;
            case YEAR:
                sql = getYearSql( startDate, endDate );
                break;
            default:
                sql = getDaySql( startDate, endDate );
        }
        return hibernateDataStatisticsStore.getSnapshotsInInterval( sql, eventInterval );
    }

    /**
     * private method: creating a sql for retriving aggregated data with grpup by YEAR
     *
     * @param start - start date
     * @param end   - end date
     * @return - sql string
     */
    private String getYearSql( Date start, Date end )
    {

        return "select extract(year from created) as yr, " +
            commonSql( start, end ) +
            " order by yr;";

    }

    /**
     * private method: creating a sql for retriving aggregated data with grpup by YEAR, MONTH
     *
     * @param start - start date
     * @param end   - end date
     * @return - sql string
     */
    private String getMonthSql( Date start, Date end )
    {

        return "select extract(year from created) as yr, " +
            "extract(month from created) as mnt, " +
            commonSql( start, end ) +
            ", mnt order by yr, mnt;";

    }

    /**
     * private method: creating a sql for retriving aggregated data with grpup by YEAR, WEEK
     *
     * @param start - start date
     * @param end   - end date
     * @return - sql string
     */
    private String getWeekSql( Date start, Date end )
    {

        return "select extract(year from created) as yr, " +
            "extract(week from created) as week, " +
            commonSql( start, end ) +
            ", week order by yr, week;";

    }

    /**
     * private method: creating a sql for retriving aggregated data with grpup by YEAR, DAY
     *
     * @param start - start date
     * @param end   - end date
     * @return - sql string
     */
    private String getDaySql( Date start, Date end )
    {
        return "select extract(year from created) as yr, " +
            "extract(month from created) as mnt,"+
            "extract(day from created) as day, " +
            commonSql( start, end ) +
            ", mnt, day order by yr, mnt, day;";

    }

    /**
     * private method: part of sql witch is always the same in the different intervall YEAR, MONTH, WEEK and DAY
     *
     * @param start - start date
     * @param end   - end date
     * @return - sql string
     */
    private String commonSql( Date start, Date end )
    {
        return "max(active_users) as activeUsers," +
            "sum(mapviews) as mapViews," +
            "sum(chartviews) as chartViews," +
            "sum(reporttableviews) as reportTablesViews, " +
            "sum(eventreportviews) as eventReportViews, " +
            "sum(eventchartviews) as eventChartViews," +
            "sum(dashboardviews) as dashboardViews, " +
            "sum(indicatorviews) as indicatorsViews, " +
            "sum(totalviews) as totalViews," +
            "sum(average_views) as averageViews, " +
            "sum(maps) as savedMaps," +
            "sum(charts) as savedCharts," +
            "sum(reporttables) as savedReportTables," +
            "sum(eventreports) as savedEventReports," +
            "sum(eventcharts) as savedEventCharts," +
            "sum(dashborards) as savedDashboards, " +
            "sum(indicators) as savedIndicators," +
            "max(users) as users from datastatistics " +
            "where (created between '" + start + "'and '" + end + "') group by yr";
    }

    /**
     * private method to get number of saved Charts from a date till now
     *
     * @param date - From date
     * @return number of Charts saved in db
     */
    private int getCharts( Date date )
    {
        return chartService.countChartGeCreated( date );
    }

    /**
     * private method to get number of saved Report tables from a date till now
     *
     * @param date - From date
     * @return number of Report tables saved in db
     */
    private int getReportTables( Date date )
    {
        return reportTableService.getCountGeCreated( date );
    }


    /**
     * private method to get number of saved Maps from a date till now
     *
     * @param date - From date
     * @return number of Maps saved in db
     */
    private int getMaps( Date date )
    {
        return mappingService.getCountGeCreated( date );
    }

    /**
     * private method to get number of saved Event Reports from a date till now
     *
     * @param date - From date
     * @return number of Event Reports saved in db
     */
    private int getEventReports( Date date )
    {
        return eventReportService.getCountGeCreated( date );
    }

    /**
     * private method to get number of saved Event Charts from a date till now
     *
     * @param date - From date
     * @return number of Event Charts saved in db
     */
    private int getEventCharts( Date date )
    {
        return eventChartService.countGeCreated( date );
    }

    /**
     * private method to get number of saved dashboards from a date till now
     *
     * @param date - From date
     * @return number of dashboards saved in db
     */
    private int getDashboards( Date date )
    {
        return dashboardService.countGeCreated( date );
    }

    /**
     * private method to get the number of indicators saved from a date till now
     *
     * @param date - From date
     * @return number of indicators saved in db
     */
    private int getIndicators( Date date )
    {
        return indicatorService.getCountGeCreated( date );
    }

    /**
     * private method to get a number of users in db
     *
     * @return number og active users
     */
    private int getUsers( )
    {
        return userService.getUserCount();
    }

    /**
     * private method to get a number of active/logged in users that day.
     *
     * @return number og active users
     */
    private int getActiveUsers( ){
        return userService.getActiveUsersCount( 1 );
    }


    /**
     * Gets all important information and creates a Datastatistics object and saves it in db
     */
    @Override public int saveSnapshot( )
    {
        Date now = new Date(  );
        Date startDate = new Date( );
        Calendar c = Calendar.getInstance();
        c.setTime( startDate );
        c.add( Calendar.DATE, -1 );
        startDate = c.getTime( );

        int totalUsers = getUsers();
        double savedMaps = getMaps( startDate );
        double savedCharts = getCharts( startDate );
        double savedReportTables = getReportTables( startDate );
        double savedEventReports = getEventReports( startDate );
        double savedEventCharts = getEventCharts( startDate );
        double savedDashboards = getDashboards( startDate );
        double savedIndicators = getIndicators( startDate );
        int activeUsers = getActiveUsers();

        double chartViews = 0;
        double mapViews = 0;
        double dashboardViews = 0;
        double reportTablesViews = 0;
        double eventReportViews = 0;
        double eventChartViews = 0;
        double indicatorsViews = 0;
        double totalNumberOfViews = 0;
        double averageNumberofViews = 0;

        String sql = "select eventtype as eventtype, count(eventtype) as numberofviews from datastatisticsevent where (timestamp between '"+startDate+"' and '"+now+"') group by eventtype;";

        List list = hibernateDataStatisticsEventStore.getDataStatisticsEventCount( sql );

        for(int i = 0; i<list.size(); i++){
            int[] temp = (int[])list.get(i);
            switch (temp[0]){
                case 0: chartViews = temp[1];
                    totalNumberOfViews += chartViews;
                    break;
                case 1: mapViews = temp[1];
                    totalNumberOfViews += mapViews;
                    break;
                case 2: dashboardViews = temp[1];
                    totalNumberOfViews += dashboardViews;
                    break;
                case 3: reportTablesViews = temp[1];
                    totalNumberOfViews += reportTablesViews;
                    break;
                case 4: eventReportViews = temp[1];
                    totalNumberOfViews += eventReportViews;
                    break;
                case 5: eventChartViews = temp[1];
                    totalNumberOfViews += eventChartViews;
                    break;
                case 6: indicatorsViews = temp[1];
                    totalNumberOfViews += indicatorsViews;
                    break;

            }

        }
        if(activeUsers != 0)
            averageNumberofViews = totalNumberOfViews/activeUsers;
        else averageNumberofViews = totalNumberOfViews;

        DataStatistics dataStatistics = new DataStatistics( activeUsers, mapViews, chartViews,
            reportTablesViews, eventReportViews, eventChartViews, dashboardViews,
            indicatorsViews, totalNumberOfViews, averageNumberofViews, savedMaps,
            savedCharts, savedReportTables, savedEventReports,
            savedEventCharts, savedDashboards, savedIndicators,
            totalUsers );

        int id = hibernateDataStatisticsStore.save( dataStatistics );
        return id;


    }
}
