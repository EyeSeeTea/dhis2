package org.hisp.dhis.datastatistics.hibernate;

import org.hisp.dhis.datastatistics.DataStatistics;
import org.hisp.dhis.datastatistics.DataStatisticsStore;
import org.hisp.dhis.hibernate.HibernateGenericStore;

import java.util.Date;
import java.util.List;

/**
 * Created by yrjanaff on 16.02.2016.
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
        return null;
    }
}
