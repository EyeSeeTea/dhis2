package org.hisp.dhis.datastatistics.hibernate;

import org.hisp.dhis.datastatistics.DataStatisticsEvent;
import org.hisp.dhis.datastatistics.DataStatisticsStore;
import org.hisp.dhis.hibernate.HibernateGenericStore;

/**
 * @author Yrjan A. F. Fraschetti
 * @author Julie Hill Roa
 */
public class HibernateDataStatisticsStore extends HibernateGenericStore<DataStatisticsEvent> implements DataStatisticsStore
{

    @Override
    public int addDataStatisticsEvent( DataStatisticsEvent dataStatistics )
    {
        return save(dataStatistics);
    }
}
