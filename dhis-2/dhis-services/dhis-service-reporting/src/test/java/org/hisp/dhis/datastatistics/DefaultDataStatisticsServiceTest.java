package org.hisp.dhis.datastatistics;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.datastatistics.DataStatisticsEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by yrjanaff on 22.02.2016.
 */
public class DefaultDataStatisticsServiceTest extends DhisSpringTest
{
    @Autowired
    DefaultDataStatisticsService defaultDataStatisticsService;

    private DataStatisticsEvent dse1;

    @Override
    public void setUpTest() throws Exception
    {
        dse1 = new DataStatisticsEvent(  );
    }

    @After
    public void tearDown() throws Exception
    {

    }

    @Test
    public void testAddEvent() throws Exception
    {
        int id = defaultDataStatisticsService.addEvent( dse1 );
        assertNotEquals( 0, id );
    }

    @Test
    public void testAddEventWithParams() throws Exception
    {

    }

    @Test
    public void testGetReports() throws Exception
    {

    }

    @Test
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

    }

    @Test
    public void testSaveSnapshot() throws Exception
    {

    }
}