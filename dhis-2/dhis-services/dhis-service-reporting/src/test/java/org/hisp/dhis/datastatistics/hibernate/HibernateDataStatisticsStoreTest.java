package org.hisp.dhis.datastatistics.hibernate;

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

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.datastatistics.AggregatedStatistics;
import org.hisp.dhis.datastatistics.DataStatistics;
import org.hisp.dhis.datastatistics.DataStatisticsStore;

import org.hisp.dhis.datastatistics.EventInterval;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Yrjan A. F. Fraschetti
 * @author Julie Hill Roa
 */
public class HibernateDataStatisticsStoreTest extends DhisSpringTest
{
    @Autowired
    DataStatisticsStore dataStatisticsStore;

    DataStatistics ds1;
    DataStatistics ds2;
    DataStatistics ds3;
    DataStatistics ds4;
    DataStatistics ds5;
    DataStatistics ds6;

    Calendar c;
    Calendar d;

    int ds1Id;
    int ds2Id;

    String testBefore;
    String testAfter;

    @Override
    public void setUpTest() throws Exception
    {
        ds1 = new DataStatistics();
        ds2 = new DataStatistics( 10, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 );
        ds3 = new DataStatistics();
        ds4 = new DataStatistics( 10, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 );
        ds5 = new DataStatistics( 3, 2, 1, 6, 5, 4, 8, 7, 6, 3, 4, 4, 5, 9, 7, 6, 4, 2 );
        ds6 = new DataStatistics( 5, 6, 4, 3, 5, 7, 8, 5, 3, 2, 1, 5, 6, 8, 8, 9, 9, 9 );

        ds1Id = 0;
        ds2Id = 0;

        Date now = new Date();

        c = Calendar.getInstance();
        d = Calendar.getInstance();

        c.setTime( now );
        d.setTime( now );


    }

    @Test
    public void saveSnapshotTest() throws Exception
    {
        ds1Id = dataStatisticsStore.save( ds1 );
        ds2Id = dataStatisticsStore.save( ds2 );

        assertTrue( ds1Id != 0 );
        assertTrue( ds2Id != 0 );
    }


    @Test
    public void getSnapshotsInIntervalGetInDAYTest()
    {

        c.add( Calendar.YEAR, -1 );
        d.add( Calendar.YEAR, 1 );

        testBefore = c.get( Calendar.YEAR ) + "-" + (c.get( Calendar.MONTH ) + 1) + "-" + c.get( Calendar.DAY_OF_MONTH );
        testAfter = d.get( Calendar.YEAR ) + "-" + (d.get( Calendar.MONTH ) + 1) + "-" + d.get( Calendar.DAY_OF_MONTH );

        dataStatisticsStore.save( ds2 );
        dataStatisticsStore.save( ds4 );
        dataStatisticsStore.save( ds5 );
        dataStatisticsStore.save( ds6 );
        double expected = ds2.getSavedMaps() + ds4.getSavedMaps() + ds5.getSavedMaps() + ds6.getSavedMaps();

        String sql = "select extract(year from created) as yr, extract(day from created) as day, " +
            "max(active_users) as activeUsers," +
            "sum(mapviews) as mapViews," +
            "sum(chartviews) as chartViews," +
            "sum(reporttableviews) as reportTablesViews," +
            "sum(eventreportviews) as eventReportViews," +
            "sum(eventchartviews) as eventChartViews," +
            "sum(dashboardviews) as dashboardViews," +
            "sum(indicatorviews) as indicatorsViews," +
            "sum(totalviews) as totalViews," +
            "sum(average_views) as averageViews," +
            "sum(maps) as savedMaps," +
            "sum(charts) as savedCharts," +
            "sum(reporttables) as savedReportTables," +
            "sum(eventreports) as savedEventReports," +
            "sum(eventcharts) as savedEventCharts," +
            "sum(dashborards) as savedDashboards," +
            "sum(indicators) as savedIndicators," +
            "max(users) as users from datastatistics " +
            "where (created between '" + testBefore + "' and '" + testAfter + "') group by yr, day;";
        List<AggregatedStatistics> asList = dataStatisticsStore.getSnapshotsInInterval( sql, EventInterval.DAY );
        assertTrue( asList.get( 0 ).getSavedMaps() == expected );
        assertTrue( asList.size() == 1 );

    }

    @Test
    public void getSnapshotsInIntervalGetInDAY_DifferenDAyesSavedTest()
    {   c.add( Calendar.DATE, -1 );

        ds2.setCreated( c.getTime() );
        c.add( Calendar.YEAR, -1 );
        d.add( Calendar.YEAR, 1 );

        testBefore = c.get( Calendar.YEAR ) + "-" + (c.get( Calendar.MONTH ) + 1) + "-" + c.get( Calendar.DAY_OF_MONTH );
        testAfter = d.get( Calendar.YEAR ) + "-" + (d.get( Calendar.MONTH ) + 1) + "-" + d.get( Calendar.DAY_OF_MONTH );


        dataStatisticsStore.save( ds2 );
        dataStatisticsStore.save( ds4 );
        dataStatisticsStore.save( ds5 );
        dataStatisticsStore.save( ds6 );
        double expected = ds2.getSavedMaps() + ds4.getSavedMaps() + ds5.getSavedMaps() + ds6.getSavedMaps();

        String sql = "select extract(year from created) as yr, extract(day from created) as day, " +
            "max(active_users) as activeUsers," +
            "sum(mapviews) as mapViews," +
            "sum(chartviews) as chartViews," +
            "sum(reporttableviews) as reportTablesViews," +
            "sum(eventreportviews) as eventReportViews," +
            "sum(eventchartviews) as eventChartViews," +
            "sum(dashboardviews) as dashboardViews," +
            "sum(indicatorviews) as indicatorsViews," +
            "sum(totalviews) as totalViews," +
            "sum(average_views) as averageViews," +
            "sum(maps) as savedMaps," +
            "sum(charts) as savedCharts," +
            "sum(reporttables) as savedReportTables," +
            "sum(eventreports) as savedEventReports," +
            "sum(eventcharts) as savedEventCharts," +
            "sum(dashborards) as savedDashboards," +
            "sum(indicators) as savedIndicators," +
            "max(users) as users from datastatistics " +
            "where (created between '" + testBefore + "' and '" + testAfter + "') group by yr, day;";
        List<AggregatedStatistics> asList = dataStatisticsStore.getSnapshotsInInterval( sql, EventInterval.DAY );
        assertTrue( asList.size() == 2 );

    }

    @Test
    public void getSnapshotsInIntervalGetInDAY_GEDatesTest()
    {
        c.add( Calendar.YEAR, 1 );
        d.add( Calendar.YEAR, 1 );

        testBefore = c.get( Calendar.YEAR ) + "-" + (c.get( Calendar.MONTH ) + 1) + "-" + c.get( Calendar.DAY_OF_MONTH );
        testAfter = d.get( Calendar.YEAR ) + "-" + (d.get( Calendar.MONTH ) + 1) + "-" + d.get( Calendar.DAY_OF_MONTH );


        dataStatisticsStore.save( ds2 );
        dataStatisticsStore.save( ds4 );
        dataStatisticsStore.save( ds5 );
        dataStatisticsStore.save( ds6 );
        double expected = ds2.getSavedMaps() + ds4.getSavedMaps() + ds5.getSavedMaps() + ds6.getSavedMaps();

        String sql = "select extract(year from created) as yr, extract(day from created) as day, " +
            "max(active_users) as activeUsers," +
            "sum(mapviews) as mapViews," +
            "sum(chartviews) as chartViews," +
            "sum(reporttableviews) as reportTablesViews," +
            "sum(eventreportviews) as eventReportViews," +
            "sum(eventchartviews) as eventChartViews," +
            "sum(dashboardviews) as dashboardViews," +
            "sum(indicatorviews) as indicatorsViews," +
            "sum(totalviews) as totalViews," +
            "sum(average_views) as averageViews," +
            "sum(maps) as savedMaps," +
            "sum(charts) as savedCharts," +
            "sum(reporttables) as savedReportTables," +
            "sum(eventreports) as savedEventReports," +
            "sum(eventcharts) as savedEventCharts," +
            "sum(dashborards) as savedDashboards," +
            "sum(indicators) as savedIndicators," +
            "max(users) as users from datastatistics " +
            "where (created between '" + testBefore + "' and '" + testAfter + "') group by yr, day;";
        List<AggregatedStatistics> asList = dataStatisticsStore.getSnapshotsInInterval( sql, EventInterval.DAY );
        assertTrue( asList.size() == 0);

    }

    @Test
    public void getSnapshotsInIntervalGetInWEEKTest()
    {
        c.add( Calendar.YEAR, -1 );
        d.add( Calendar.YEAR, 1 );

        testBefore = c.get( Calendar.YEAR ) + "-" + (c.get( Calendar.MONTH ) + 1) + "-" + c.get( Calendar.DAY_OF_MONTH );
        testAfter = d.get( Calendar.YEAR ) + "-" + (d.get( Calendar.MONTH ) + 1) + "-" + d.get( Calendar.DAY_OF_MONTH );
        dataStatisticsStore.save( ds2 );
        dataStatisticsStore.save( ds4 );
        dataStatisticsStore.save( ds5 );
        dataStatisticsStore.save( ds6 );
        double expected = ds2.getSavedMaps() + ds4.getSavedMaps() + ds5.getSavedMaps() + ds6.getSavedMaps();

        String sql = "select extract(year from created) as yr, extract(week from created) as week, " +
            "max(active_users) as activeUsers," +
            "sum(mapviews) as mapViews," +
            "sum(chartviews) as chartViews," +
            "sum(reporttableviews) as reportTablesViews," +
            "sum(eventreportviews) as eventReportViews," +
            "sum(eventchartviews) as eventChartViews," +
            "sum(dashboardviews) as dashboardViews," +
            "sum(indicatorviews) as indicatorsViews," +
            "sum(totalviews) as totalViews," +
            "sum(average_views) as averageViews," +
            "sum(maps) as savedMaps," +
            "sum(charts) as savedCharts," +
            "sum(reporttables) as savedReportTables," +
            "sum(eventreports) as savedEventReports," +
            "sum(eventcharts) as savedEventCharts," +
            "sum(dashborards) as savedDashboards," +
            "sum(indicators) as savedIndicators," +
            "max(users) as users from datastatistics " +
            "where (created between '" + testBefore + "' and '" + testAfter + "') group by yr, week;";
        List<AggregatedStatistics> asList = dataStatisticsStore.getSnapshotsInInterval( sql, EventInterval.WEEK );
        assertTrue( asList.get( 0 ).getSavedMaps() == expected );
        assertTrue( asList.size() == 1 );
    }

    @Test
    public void getSnapshotsInIntervalGetInMONTHTest()
    {
        c.add( Calendar.YEAR, -1 );
        d.add( Calendar.YEAR, 1 );

        testBefore = c.get( Calendar.YEAR ) + "-" + (c.get( Calendar.MONTH ) + 1) + "-" + c.get( Calendar.DAY_OF_MONTH );
        testAfter = d.get( Calendar.YEAR ) + "-" + (d.get( Calendar.MONTH ) + 1) + "-" + d.get( Calendar.DAY_OF_MONTH );
        dataStatisticsStore.save( ds2 );
        dataStatisticsStore.save( ds4 );
        dataStatisticsStore.save( ds5 );
        dataStatisticsStore.save( ds6 );
        double expected = ds2.getSavedMaps() + ds4.getSavedMaps() + ds5.getSavedMaps() + ds6.getSavedMaps();

        String sql = "select extract(year from created) as yr, extract(month from created) as mnt, " +
            "max(active_users) as activeUsers," +
            "sum(mapviews) as mapViews," +
            "sum(chartviews) as chartViews," +
            "sum(reporttableviews) as reportTablesViews," +
            "sum(eventreportviews) as eventReportViews," +
            "sum(eventchartviews) as eventChartViews," +
            "sum(dashboardviews) as dashboardViews," +
            "sum(indicatorviews) as indicatorsViews," +
            "sum(totalviews) as totalViews," +
            "sum(average_views) as averageViews," +
            "sum(maps) as savedMaps," +
            "sum(charts) as savedCharts," +
            "sum(reporttables) as savedReportTables," +
            "sum(eventreports) as savedEventReports," +
            "sum(eventcharts) as savedEventCharts," +
            "sum(dashborards) as savedDashboards," +
            "sum(indicators) as savedIndicators," +
            "max(users) as users from datastatistics " +
            "where (created between '" + testBefore + "' and '" + testAfter + "') group by yr, mnt;";
        List<AggregatedStatistics> asList = dataStatisticsStore.getSnapshotsInInterval( sql, EventInterval.MONTH );
        assertTrue( asList.get( 0 ).getSavedMaps() == expected );
        assertTrue( asList.size() == 1 );
    }

    @Test
    public void getSnapshotsInIntervalGetInYEARTest()
    {
        c.add( Calendar.YEAR, -1 );
        d.add( Calendar.YEAR, 1 );

        testBefore = c.get( Calendar.YEAR ) + "-" + (c.get( Calendar.MONTH ) + 1) + "-" + c.get( Calendar.DAY_OF_MONTH );
        testAfter = d.get( Calendar.YEAR ) + "-" + (d.get( Calendar.MONTH ) + 1) + "-" + d.get( Calendar.DAY_OF_MONTH );
        dataStatisticsStore.save( ds2 );
        dataStatisticsStore.save( ds4 );
        dataStatisticsStore.save( ds5 );
        dataStatisticsStore.save( ds6 );
        double expected = ds2.getSavedMaps() + ds4.getSavedMaps() + ds5.getSavedMaps() + ds6.getSavedMaps();

        String sql = "select extract(year from created) as yr, " +
            "max(active_users) as activeUsers," +
            "sum(mapviews) as mapViews," +
            "sum(chartviews) as chartViews," +
            "sum(reporttableviews) as reportTablesViews," +
            "sum(eventreportviews) as eventReportViews," +
            "sum(eventchartviews) as eventChartViews," +
            "sum(dashboardviews) as dashboardViews," +
            "sum(indicatorviews) as indicatorsViews," +
            "sum(totalviews) as totalViews," +
            "sum(average_views) as averageViews," +
            "sum(maps) as savedMaps," +
            "sum(charts) as savedCharts," +
            "sum(reporttables) as savedReportTables," +
            "sum(eventreports) as savedEventReports," +
            "sum(eventcharts) as savedEventCharts," +
            "sum(dashborards) as savedDashboards," +
            "sum(indicators) as savedIndicators," +
            "max(users) as users from datastatistics " +
            "where (created between '" + testBefore + "' and '" + testAfter + "') group by yr;";
        List<AggregatedStatistics> asList = dataStatisticsStore.getSnapshotsInInterval( sql, EventInterval.YEAR );
        assertTrue( asList.get( 0 ).getSavedMaps() == expected );
        assertTrue( asList.size() == 1 );
    }


}