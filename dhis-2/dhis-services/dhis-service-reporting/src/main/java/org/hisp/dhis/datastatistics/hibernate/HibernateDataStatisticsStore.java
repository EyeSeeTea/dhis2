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

import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hisp.dhis.datastatistics.AggregatedStatistics;
import org.hisp.dhis.datastatistics.DataStatistics;
import org.hisp.dhis.datastatistics.DataStatisticsStore;
import org.hisp.dhis.hibernate.HibernateGenericStore;

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
    @Override public List<AggregatedStatistics> getSnapshotsInIntervalDay( Date startDate, Date endDate )
    {
        String hql = "select data.created as startInterval, data.numberOfActiveUsers as maxNumberOfActiveUsers, data.numberOfMapViews as aggregatedMapViews, " +
        "data.numberOfChartViews as aggregatedChartViews, data.numberOfReportTablesViews as aggregatedReportTablesViews, " +
            "data.numberOfEventReportViews as aggregatedEventReportViews, data.numberOfEventChartViews as aggregatedEventChartViews, " +
            "data.numberOfDashboardViews as aggregatedDashboardViews, data.numberOfIndicatorsViews as aggregatedIndicatorsViews, " +
            "data.totalNumberOfViews as maxTotalNumberOfViews, data.averageNumberOfViews as averageNumberOfViews, " +
            "data.numberOfSavedMaps as aggregatedSavedMaps, data.numberOfSavedCharts as aggregatedSavedCharts, " +
            "data.numberOfSavedReportTables as aggregatedSavedReportTables, data.numberOfSavedEventReports as aggregatedSavedEventReports, " +
            "data.numberOfSavedEventCharts as aggregatedSavedEventCharts, data.numberOfSavedDashboards as aggregatedSavedDashboards, " +
            "data.numberOfSavedIndicators as aggregatedSavedIndicators, data.totalNumberOfUsers as maxTotalNumberOfUsers " +
            "from " + getClazz().getSimpleName() + " data where (created between '" + startDate + "' and '" + endDate + "')";

        return getQuery( hql )
            .setResultTransformer(
                new AliasToBeanResultTransformer( AggregatedStatistics.class ) ).list();
    }


    /**
     * Creates an aggregated list of snapshots in interval (year, month or week)
     * @param start of interval
     * @param end of interval
     * @param interval Type of interval (Calendar enum)
     * @param number of aggregations
     * @return
     */
    @Override public List<AggregatedStatistics> getSnapshotsInInterval( Calendar start, Calendar end , int interval, int number )
    {
        List<AggregatedStatistics> aggregatedSnapshotList = new ArrayList<>( );

        if(number == 0)
        {
            getAggregatedData( start.getTime( ), end.getTime( ), aggregatedSnapshotList );
        }
        else
        {

            Calendar startDate = Calendar.getInstance( );
            Calendar nextDate = Calendar.getInstance( );

            for( int i = 0; i < number; i++ )
            {
                startDate.setTime( start.getTime( ) );
                startDate.add( interval, i );

                nextDate.setTime( startDate.getTime( ) );
                nextDate.add( interval, 1 );

                if( !nextDate.before( end ) )
                    nextDate = end;

                System.out.println("\n\n\n for: i = " + i + " startDate: " + startDate.get(interval)+ " nextDate: " + nextDate.get(interval) + "\n\n");
                getAggregatedData( startDate.getTime( ), nextDate.getTime( ), aggregatedSnapshotList );
            }
        }
        return aggregatedSnapshotList;
    }


    /**
     * Retrives and aggregates data
     * @param start of interval
     * @param end of interval
     * @param list to put the aggregated data in
     */
    private void getAggregatedData(Date start, Date end, List<AggregatedStatistics> list){

        String hql = "select max(data.numberOfActiveUsers) as maxNumberOfActiveUsers, avg(data.numberOfMapViews) as aggregatedMapViews, " +
                "avg(data.numberOfChartViews) as aggregatedChartViews, avg(data.numberOfReportTablesViews) as aggregatedReportTablesViews, " +
                "avg(data.numberOfEventReportViews) as aggregatedEventReportViews, avg(data.numberOfEventChartViews) as aggregatedEventChartViews, " +
                "avg(data.numberOfDashboardViews) as aggregatedDashboardViews, avg(data.numberOfIndicatorsViews) as aggregatedIndicatorsViews, " +
                "max(data.totalNumberOfViews) as maxTotalNumberOfViews, avg(data.averageNumberOfViews) as averageNumberOfViews, " +
                "avg(data.numberOfSavedMaps) as aggregatedSavedMaps, avg(data.numberOfSavedCharts) as aggregatedSavedCharts, " +
                "avg(data.numberOfSavedReportTables) as aggregatedSavedReportTables, avg(data.numberOfSavedEventReports) as aggregatedSavedEventReports, " +
                "avg(data.numberOfSavedEventCharts) as aggregatedSavedEventCharts, avg(data.numberOfSavedDashboards) as aggregatedSavedDashboards, " +
                "avg(data.numberOfSavedIndicators) as aggregatedSavedIndicators, max(data.totalNumberOfUsers) as maxTotalNumberOfUsers " +
                "from " + getClazz().getSimpleName() + " data where (created between '" + start + "' and '" + end + "')";


        List<AggregatedStatistics> result;
        result = getQuery( hql )
                    .setResultTransformer(
                            new AliasToBeanResultTransformer(AggregatedStatistics.class)).list();

        AggregatedStatistics as = result.get( 0 );
        as.setStartInterval( start );
        as.setEndInterval( end );
        list.add( as );


    }
}
