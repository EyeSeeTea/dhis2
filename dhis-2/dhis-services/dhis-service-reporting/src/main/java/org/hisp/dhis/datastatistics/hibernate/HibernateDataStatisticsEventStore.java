package org.hisp.dhis.datastatistics.hibernate;

import org.hibernate.criterion.Projections;
import org.hisp.dhis.datastatistics.DataStatisticsEvent;
import org.hisp.dhis.datastatistics.DataStatisticsEventStore;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hibernate.criterion.Restrictions;

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
        int count = ((Number) getSharingCriteria()
            .setProjection( Projections.countDistinct( "id" ) )
            .add( Restrictions.ge( "timestamp", startDate) )
            .add( Restrictions.lt( "timestamp", endDate) )
            .uniqueResult()).intValue();

        System.out.println("\n\nDette er svaret fra getCount(): " + count);
        return count;
    }

    public List<DataStatisticsEvent> getDataStatisticsEventList(Date date){
        return getSharingCriteria()
            .add( Restrictions.ge( "timestamp", date) )
            .list();


    }
}
