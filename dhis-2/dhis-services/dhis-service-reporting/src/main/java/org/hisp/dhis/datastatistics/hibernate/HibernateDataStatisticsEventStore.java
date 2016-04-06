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

import org.hisp.dhis.datastatistics.DataStatisticsEvent;
import org.hisp.dhis.datastatistics.DataStatisticsEventStore;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.system.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;
import java.util.List;

/**
 * Class for database logic for datastatisticevent
 * 
 * @author Yrjan A. F. Fraschetti
 * @author Julie Hill Roa        
 */
public class HibernateDataStatisticsEventStore 
    extends HibernateGenericStore<DataStatisticsEvent> 
    implements DataStatisticsEventStore
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<int[]> getDataStatisticsEventCount( Date startDate, Date endDate )
    {
        final String sql = "select eventtype as eventtype, count(eventtype) as numberofviews from datastatisticsevent where (timestamp between '" + 
            DateUtils.getMediumDateString( startDate ) + "' and '" + 
            DateUtils.getMediumDateString( endDate ) + "') group by eventtype;";

        return jdbcTemplate.query( sql, ( resultSet, i ) -> {
            int[] l = new int[2];
            l[0] = resultSet.getInt( "eventtype" );
            l[1] = resultSet.getInt( "numberofviews");
            return l;
        } );
    }
}
