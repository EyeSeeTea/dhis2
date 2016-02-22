package org.hisp.dhis.datastatistics.hibernate;

import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.datastatistics.DataStatistics;
import org.hisp.dhis.datastatistics.DataStatisticsStore;
import org.hisp.dhis.hibernate.HibernateGenericStore;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Yrjan A. F. Fraschetti
 * @author Julie Hill Roa
 */

public class HibernateDataStatisticsStore extends HibernateGenericStore<DataStatistics> implements DataStatisticsStore
{
    @Override
    public int addSnapshot( DataStatistics dataStatistics )
    {
        return save( dataStatistics );
    }

    @Override
    public List<DataStatistics> getSnapshotsInInterval( Date startDate, Date endDate )
    {
        Calendar c = Calendar.getInstance();
        c.setTime( endDate );
        c.add( Calendar.DATE, 1 );
        endDate = c.getTime();

        return ((List<DataStatistics>) getSharingCriteria()
        .add( Restrictions.ge( "created", startDate ) )
            .add( Restrictions.le( "created", endDate ) ).list());
    }
}
