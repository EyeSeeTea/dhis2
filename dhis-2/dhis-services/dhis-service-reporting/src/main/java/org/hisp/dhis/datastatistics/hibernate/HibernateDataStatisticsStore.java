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

import org.h2.store.Data;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.datastatistics.DataStatistics;
import org.hisp.dhis.datastatistics.DataStatisticsStore;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.period.Cal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;

/**
 * @author Yrjan A. F. Fraschetti
 * @author Julie Hill Roa
 *
 * Class for database logic for datastatistics
 */

public class HibernateDataStatisticsStore extends HibernateGenericStore<DataStatistics> implements DataStatisticsStore
{

    /**
     * Saves a snapshot object in the database
     * @param dataStatistics - object to be saved in the db
     * @return id of saved object
     */
    @Override
    public int addSnapshot( DataStatistics dataStatistics )
    {
        return save( dataStatistics );
    }

    /**
     * Creates a list of snapshots in interval (day)
     *
     * @param startDate of interval
     * @param endDate   of interval
     * @return List of DataStatistics (snapshot)
     */
    @Override public List<DataStatistics> getSnapshotsInIntervalDay( Date startDate, Date endDate )
    {
        return ((List<DataStatistics>) getSharingCriteria()
            .add( Restrictions.ge( "created", startDate ) )
            .add( Restrictions.le( "created", endDate ) ).list());
    }

    /**
     * Creates an aggregated list of snapshots in interval (week)
     *
     * @param start of interval
     * @param end   of interval
     * @return List of DataStatistics (snapshot)
     */
    @Override public List<DataStatistics> getSnapshotsInIntervalWeek( Calendar start, Calendar end )
    {
        return null;
    }

    /**
     * Creates an aggregated list of snapshots in interval (month)
     *
     * @param start of interval
     * @param end   of interval
     * @return List of DataStatistics (snapshot)
     */
    @Override public List<DataStatistics> getSnapshotsInIntervalMonth( Calendar start, Calendar end )
    {
        return null;
    }

    /**
     * Creates an aggregated list of snapshots in interval (year)
     *
     * @param start of interval
     * @param end   of interval
     * @return List of DataStatistics (snapshot)
     */
    @Override public List<DataStatistics> getSnapshotsInIntervalYear( Calendar start, Calendar end )
    {
        int stop = end.get(Calendar.YEAR) - start.get(Calendar.YEAR);
        List<DataStatistics> aggregatedSnapshotList = new ArrayList<>();

        Calendar startDate = Calendar.getInstance();
        Calendar nextDate = Calendar.getInstance();

        System.out.println("\n\nStop: " + stop);
        for(int i = 0; i < stop; i++ ){

            startDate.setTime( start.getTime() );
            startDate.add(Calendar.YEAR, i);

            nextDate.setTime( startDate.getTime() );
            nextDate.add( Calendar.YEAR,  1 );

            System.out.println("\n\n\n for: i = " + i + " startDate: " + startDate.get( Calendar.YEAR ) + " nextDate: " + nextDate.get( Calendar.YEAR ) + "\n\n");


            List<DataStatistics> tmpList = ((List<DataStatistics>) getSharingCriteria()
                .add( Restrictions.ge( "created", startDate.getTime() ) )
                .add( Restrictions.le( "created", nextDate.getTime() ) ).list());

            DataStatistics dataStatistics = new DataStatistics(  );

           /* for ( DataStatistics ds : tmpList )
            {
                dataStatistics.accumulate(ds);
            }*/

        }
        return null;
    }
}
