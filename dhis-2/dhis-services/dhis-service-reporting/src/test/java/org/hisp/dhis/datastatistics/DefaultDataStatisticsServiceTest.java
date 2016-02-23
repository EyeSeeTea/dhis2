package org.hisp.dhis.datastatistics;

import org.hisp.dhis.DhisSpringTest;
//import org.hisp.dhis.datastatistics.DataStatisticsEvent;
//import org.hisp.dhis.datastatistics.DataStatisticsService;

//import org.hisp.dhis.datastatistics.DataStatisticsStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by yrjanaff on 22.02.2016.
 */
public class DefaultDataStatisticsServiceTest extends DhisSpringTest
{
    @Autowired
    DataStatisticsService dataStatisticsService;

    @Autowired
    DataStatisticsStore hibernateDataStatisticsStore;

    private Date now;
    private Date startDate;
    private DataStatisticsEvent dse1;
    private DataStatisticsEvent dse2;
    private DataStatistics ds;
    private int snapId1;
    private int snapId2;

    @Override
    public void setUpTest() throws Exception
    {
        now = new Date();
        startDate = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime( startDate );
        c.add( Calendar.DATE, -1 );
        startDate = c.getTime();

        dse1 = new DataStatisticsEvent(  );
        dse2 = new DataStatisticsEvent( EventType.EVENT_CHART, now, 1150503);
        ds = new DataStatistics( 10,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18 );
        snapId1 = hibernateDataStatisticsStore.addSnapshot( ds );

    }

    @After
    public void tearDown() throws Exception
    {

    }

    @Test
    public void testAddEvent() throws Exception
    {
        int id = dataStatisticsService.addEvent( dse1 );
        assertNotEquals( 0, id );

    }

    @Test
    public void testAddEventWithParams() throws Exception
    {
        int id = dataStatisticsService.addEvent( dse2 );
        assertNotEquals( 0, id );
    }

    @Test
    public void testGetReports() throws Exception
    {
        Date startDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime( startDate );
        c.add( Calendar.DATE, -1 );
        startDate = c.getTime();

        assertNotNull( dataStatisticsService.getReports( startDate, now ) );
    }

    /*@Test
    public void testGetNumberOfCharts() throws Exception
    {

    }

    @Test
    public void testGetNumberOfReportTables() throws Exception
    {

    }

    @Test
    public void testGetNumberOfFavoriteViews() throws Exception
    {

    }

    @Test
    public void testGetNumberOfMaps() throws Exception
    {

    }

    @Test
    public void testGetNumberOfEventReports() throws Exception
    {

    }

    @Test
    public void testGetNumberOfEventCharts() throws Exception
    {

    }

    @Test
    public void testGetNumberOfDashboards() throws Exception
    {

    }

    @Test
    public void testGetNumberOfIndicators() throws Exception
    {

    }

    @Test
    public void testGetNumberOfUsers() throws Exception
    {

    }*/

    @Test
    public void testSaveSnapshot() throws Exception
    {
        snapId2 = dataStatisticsService.saveSnapshot();

        assertTrue( dataStatisticsService.getReports( startDate, now ).size() >= 2 );
    }
}