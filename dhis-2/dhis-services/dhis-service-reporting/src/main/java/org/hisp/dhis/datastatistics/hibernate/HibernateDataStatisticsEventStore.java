package org.hisp.dhis.datastatistics.hibernate;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;
import org.hisp.dhis.datastatistics.DataStatisticsEvent;
import org.hisp.dhis.datastatistics.DataStatisticsEventStore;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.query.Restriction;
import org.hisp.dhis.query.Restrictions;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Yrjan A. F. Fraschetti
 * @author Julie Hill Roa
 */
public class HibernateDataStatisticsEventStore extends HibernateGenericStore<DataStatisticsEvent> implements DataStatisticsEventStore
{

    @Override
    public int addDataStatisticsEvent( DataStatisticsEvent dataStatistics )
    {
        return save(dataStatistics);
    }

    @Override
    public int getNumberOfEvents(Date startDate, Date endDate){

        //TODO Find replacement for expressions!
        int count = ((Number) getSharingCriteria()
            .setProjection( Projections.countDistinct( "id" ) )
            .add( Expression.ge( "timestamp", startDate) )
            .add( Expression.le( "timestamp", endDate) )
            .uniqueResult()).intValue();

        System.out.println("\n\nDette er svaret fra getCount(): " + count);
        return count;
    }
}
