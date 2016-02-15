package org.hisp.dhis.datastatistics.hibernate;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hisp.dhis.datastatistics.DataStatisticsEvent;
import org.hisp.dhis.datastatistics.DataStatisticsEventStore;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.query.Restriction;
import org.hisp.dhis.query.Restrictions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

        Calendar c = Calendar.getInstance();
        c.setTime( endDate );
        c.add( Calendar.DATE, 1 );
        endDate = c.getTime();

        System.out.println("\n\nendDate + 1: " + endDate);
        //TODO Find replacement for expressions!
        int count = ((Number) getSharingCriteria()
            .setProjection( Projections.countDistinct( "id" ) )
            .add( Expression.ge( "timestamp", startDate) )
            .add( Expression.lt( "timestamp", endDate) )
            .uniqueResult()).intValue();

        System.out.println("\n\nDette er svaret fra getCount(): " + count);
        return count;
    }

    public List<DataStatisticsEvent> getDataStatisticsEventList(){
        Date now = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime( now );
        //c.add( Calendar.DATE, -1 );
        c.add( Calendar.DATE, -6 );
        now = c.getTime();

        System.out.println("\n\nDate.toString: " + now.toString());
        return getSharingCriteria()
            .add( Expression.like( "timestamp", now) )
            .list();


    }
}
