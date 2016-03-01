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
import org.hisp.dhis.datastatistics.DataStatistics;
import org.hisp.dhis.datastatistics.DataStatisticsStore;

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

    int ds1Id;
    int ds2Id;

    Date startDate;
    Date endDate;
    Date testDate;

    @Override
    public void setUpTest() throws Exception
    {
        ds1 = new DataStatistics(  );
        ds2 = new DataStatistics( 10,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18 );
        ds3 = new DataStatistics(  );
        ds4 = new DataStatistics( 10,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19 );
        ds5 = new DataStatistics(  );
        ds6 = new DataStatistics(  );

        ds1Id = 0;
        ds2Id = 0;

        startDate = new Date(  );
        endDate = new Date(  );
        testDate = new Date(  );

        Calendar c = Calendar.getInstance();
        c.setTime( startDate );
        c.add( Calendar.DATE, -1 );
        startDate = c.getTime();

        c.setTime( testDate );
        c.add( Calendar.DATE, -2 );
        testDate = c.getTime();

    }

    @Test
    public void testAddSnapshot() throws Exception
    {
        ds1Id = dataStatisticsStore.addSnapshot( ds1 );
        ds2Id = dataStatisticsStore.addSnapshot( ds2 );

        assertTrue( ds1Id != 0 );
        assertTrue( ds2Id != 0 );
    }

   /* @Test
    public void testGetSnapshotsInInterval() throws Exception
    {
        dataStatisticsStore.addSnapshot( ds3 );
        dataStatisticsStore.addSnapshot( ds4 );

        List<DataStatistics> list = dataStatisticsStore.getSnapshotsInInterval( startDate, endDate );

        assertTrue( list.size() == 2 );
        assertTrue( list.contains( ds3 ) );
        assertTrue( list.contains( ds4 ) );

        assertFalse( list.contains( ds5 ) );

    }

    @Test
    public void testGetSnapshotInIntervalNotInInterval(){
        ds6.setCreated( testDate );
        dataStatisticsStore.addSnapshot( ds3 );
        dataStatisticsStore.addSnapshot( ds6 );

        List<DataStatistics> list = dataStatisticsStore.getSnapshotsInInterval( startDate, endDate );

        assertFalse( list.contains( ds6 ) );
        assertTrue( list.size() == 1);
    }*/
}