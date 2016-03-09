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
import org.hisp.dhis.datastatistics.DataStatisticsEvent;
import org.hisp.dhis.datastatistics.DataStatisticsEventStore;
import org.hisp.dhis.datastatistics.EventType;

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
public class HibernateDataStatisticsEventStoreTest extends DhisSpringTest
{

    @Autowired
    DataStatisticsEventStore dataStatisticsEventStore;

    DataStatisticsEvent dse1;
    DataStatisticsEvent dse2;
    DataStatisticsEvent dse3;
    DataStatisticsEvent dse4;
    DataStatisticsEvent dse5;


    int dse1Id;
    int dse2Id;

    Date startDate;
    Date endDate;
    Date testDate;

    @Override
    public void setUpTest() throws Exception
    {
        startDate = new Date();
        endDate = new Date();
        testDate = new Date();

        Calendar c = Calendar.getInstance();
        endDate = c.getTime();
        c.setTime( startDate );
        c.add( Calendar.DATE, -1 );
        startDate = c.getTime();


        c.setTime( testDate );
        c.add( Calendar.DATE, -5 );
        testDate = c.getTime();


        dse1 = new DataStatisticsEvent( EventType.REPORT_TABLE_VIEW, endDate, "Testuser" );
        dse2 = new DataStatisticsEvent( EventType.EVENT_CHART_VIEW, endDate, "TestUser" );
        dse3 = new DataStatisticsEvent( EventType.CHART_VIEW, endDate, "Testuser" );
        dse4 = new DataStatisticsEvent( EventType.DASHBOARD_VIEW, endDate, "TestUser" );
        dse5 = new DataStatisticsEvent( EventType.INDICATOR_VIEW, testDate, "Testuser" );


        dse1Id = 0;
        dse2Id = 0;
    }

    @Test
    public void testAddDataStatisticsEvent() throws Exception
    {
        dse1Id = dataStatisticsEventStore.save( dse1 );
        dse2Id = dataStatisticsEventStore.save( dse2 );

        assertTrue( dse1Id != 0 );
        assertTrue( dse2Id != 0 );
    }

    @Test
    public void testGetNumberOfEvents() throws Exception
    {
        dataStatisticsEventStore.save( dse3 );
        dataStatisticsEventStore.save( dse4 );

        int numberOfEvents = dataStatisticsEventStore.getNumberOfEvents( startDate, endDate );

        assertTrue( numberOfEvents == 2 );
    }

    @Test
    public void testGetNumberOfEventsNotInInterval() throws Exception
    {
        dataStatisticsEventStore.save( dse5 );

        int numberOfEvents = dataStatisticsEventStore.getNumberOfEvents( startDate, endDate );

        assertTrue( numberOfEvents == 0 );
    }

    @Test
    public void testGetDataStatisticsEventList() throws Exception
    {
        dataStatisticsEventStore.save( dse1 );
        dataStatisticsEventStore.save( dse2 );
        dataStatisticsEventStore.save( dse3 );
        dataStatisticsEventStore.save( dse4 );
        dataStatisticsEventStore.save( dse5 );

        List<DataStatisticsEvent> eventList = dataStatisticsEventStore.getDataStatisticsEventCount( startDate );

        assertTrue( eventList.size() == 4 );
        assertTrue( eventList.contains( dse1 ) );
        assertTrue( eventList.contains( dse2 ) );
        assertTrue( eventList.contains( dse3 ) );
        assertTrue( eventList.contains( dse4 ) );
        assertFalse( eventList.contains( dse5 ) );
    }
}