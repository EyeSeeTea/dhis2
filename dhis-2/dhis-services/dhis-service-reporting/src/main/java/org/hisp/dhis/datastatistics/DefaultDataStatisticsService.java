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
        return hibernateDataStatisticsEventStore.addDataStatisticsEvent( event );
    }

    /**
     * gets number of saved Reports from a start date too a end date
     * @param startDate - From date
     * @param endDate - Too date
     * @return number of Reports saved in db
     */
    @Override
    public List<DataStatistics> getReports(Date startDate, Date endDate)
    {
        return hibernateDataStatisticsStore.getSnapshotsInInterval( startDate, endDate );
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
     * @param date
     * @return number og active users
     */
    @Override
    public int getNumberOfUsers(Date date)
    {
        //DO WE NEED A DATE PARAM ???????
        // change this to 1 when its time to deploy
        return userService.getActiveUsersCount( 1000 );
    }


    /**
     * Gets all important information and creates a Datastatistics object and saves it in db
     */
    @Override public int saveSnapshot()
    {
        Date startDate = new Date();
        Date endDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime( startDate );
        c.add(Calendar.DATE, -1);
        startDate = c.getTime();

        System.out.println("\nStartdate: " + startDate.toString());
        System.out.println("\nEndDate: " + endDate.toString());

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

        int averageNumberOfSavedMaps = getNumberOfMaps( startDate );
        int averageNumberOfSavedCharts = getNumberOfCharts( startDate );
        int averageNumberOfSavedReportTables = getNumberOfReportTables( startDate );
        int averageNumberOfSavedEventReports = getNumberOfEventReports( startDate );
        int averageNumberOfSavedEventCharts = getNumberOfEventCharts( startDate );
        int averageNumberOfSavedDashboards = getNumberOfDashboards( startDate );
        int averageNumberOfSavedIndicators = getNumberOfIndicators( startDate );

        if( totalNumberOfUsers != 0){
            averageNumberOfSavedCharts /= totalNumberOfUsers;
            averageNumberOfSavedDashboards /= totalNumberOfUsers;
            averageNumberOfSavedEventCharts /= totalNumberOfUsers;
            averageNumberOfSavedEventReports /= totalNumberOfUsers;
            averageNumberOfSavedIndicators /= totalNumberOfUsers;
            averageNumberOfSavedMaps /= totalNumberOfUsers;
            averageNumberOfSavedReportTables /= totalNumberOfUsers;
        }

        numberOfUsers = uniqueUsers.size();

        if(numberOfUsers != 0)
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
            System.out.println("\nCould not save snapshot");
        }
        else{
            System.out.println("\nSnapshot was saved with id: " + id);
        }
        return id;
    }
}
