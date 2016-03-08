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

import org.hibernate.criterion.Projections;
import org.hisp.dhis.datastatistics.DataStatisticsEvent;
import org.hisp.dhis.datastatistics.DataStatisticsEventStore;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hibernate.criterion.Restrictions;

import java.util.Date;
import java.util.List;

/**
 * @author Yrjan A. F. Fraschetti
 * @author Julie Hill Roa
 *
 * Class for database logic for datastatisticevent
 */
public class HibernateDataStatisticsEventStore extends HibernateGenericStore<DataStatisticsEvent> implements DataStatisticsEventStore
{
    /**
     * Get number of events between start- and enddate
     * @param startDate - from this date
     * @param endDate - to this date
     * @return number of events
     */
    @Override
    public int getNumberOfEvents( Date startDate, Date endDate )
    {
        int count = ( ( Number ) getSharingCriteria( )
            .setProjection( Projections.countDistinct( "id" ) )
            .add( Restrictions.ge( "timestamp", startDate) )
            .add( Restrictions.le( "timestamp", endDate) )
            .uniqueResult( ) ).intValue( );
        return count;
    }

    /**
     * Method for getting a list of all events saved from a specific date
     * @param date - get all saved from this date
     * @return List of datastatisticsevents
     */
    public List<DataStatisticsEvent> getDataStatisticsEventCount( Date date )
    {
        return getSharingCriteria( )
            .add( Restrictions.ge( "timestamp", date) )
            .list( );


    }
}
