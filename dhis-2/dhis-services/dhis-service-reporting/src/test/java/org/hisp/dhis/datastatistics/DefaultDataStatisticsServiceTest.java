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

import org.hisp.dhis.DhisSpringTest;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Yrjan A. F. Fraschetti
 * @author Julie Hill Roa
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
        c.add( Calendar.DATE, -2 );
        startDate = c.getTime();

       /* SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        now = dateFormat.parse( now.toString() );
        startDate = dateFormat.parse( startDate.toString() );*/



        dse1 = new DataStatisticsEvent();
        dse2 = new DataStatisticsEvent( EventType.EVENT_CHART_VIEW, now, "TestUser" );
        ds = new DataStatistics( 10, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 );

        snapId1 = hibernateDataStatisticsStore.save( ds );
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

    @Ignore
    @Test
    public void testSaveSnapshot() throws Exception
    {
        dataStatisticsService.addEvent( dse1);
        dataStatisticsService.addEvent( dse2 );
        snapId2 = dataStatisticsService.saveSnapshot();

        assertTrue( dataStatisticsService.getReports( startDate, now, EventInterval.DAY ).size() == 2 );
    }

    @Ignore
    @Test
    public void testSaveSnapshotWithInvalidInterval() throws Exception
    {
        assertTrue( dataStatisticsService.getReports( now, startDate, EventInterval.DAY ).size() == 0 );
    }

    @Ignore
    @Test
    public void testGetReportsDayInterval() throws Exception
    {
        dataStatisticsService.addEvent( dse1);
        dataStatisticsService.addEvent( dse2 );
        Date startDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime( startDate );
        c.add( Calendar.DATE, -1 );
        startDate = c.getTime();

        List<AggregatedStatistics> asList = dataStatisticsService.getReports( startDate, now, EventInterval.DAY );

        assertNotNull( asList );
        assertTrue( asList.size() == 2 );
    }



    @Ignore
    @Test
    public void testGetReportsWEEKInterval() throws Exception
    {
        dataStatisticsService.addEvent( dse1 );
        dataStatisticsService.addEvent( dse2 );
        Date startDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime( startDate );
        c.add( Calendar.DATE, -1 );
        startDate = c.getTime();

        List<AggregatedStatistics> asList = dataStatisticsService.getReports( now, new Date(  ), EventInterval.WEEK );

        assertNotNull( asList );
        assertTrue( asList.size() == 1 );
    }

    @Ignore
    @Test
    public void testGetReportsMONTHInterval() throws Exception
    {
        dataStatisticsService.addEvent( dse1);
        dataStatisticsService.addEvent( dse2 );
        Date startDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime( startDate );
        c.add( Calendar.DATE, -1 );
        startDate = c.getTime();

        List<AggregatedStatistics> asList = dataStatisticsService.getReports( startDate, now, EventInterval.MONTH );

        assertNotNull( asList );
        assertTrue( asList.size() == 1 );    }

    @Ignore
    @Test
    public void testGetReportsYEAHInterval() throws Exception
    {
        dataStatisticsService.addEvent( dse1);
        dataStatisticsService.addEvent( dse2 );
        Date startDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime( startDate );
        c.add( Calendar.DATE, -1 );
        startDate = c.getTime();

        List<AggregatedStatistics> asList = dataStatisticsService.getReports( startDate, now, EventInterval.YEAR );

        assertNotNull( asList );
        assertTrue( asList.size() == 1 );    }


}