package org.hisp.dhis.datastatistics.hibernate;

import org.hisp.dhis.datastatistics.DataStatistics;
import org.hisp.dhis.datastatistics.DataStatisticsStore;
import org.hisp.dhis.hibernate.HibernateGenericStore;

import java.util.Date;

/**
 * Created by yrjanaff on 19.01.2016.
 */
public class HibernateDataStatisticsStore extends HibernateGenericStore<DataStatistics> implements DataStatisticsStore
{

    @Override public void addDataStatistics( DataStatistics dataStatistics )
    {

    }

    @Override public void updateDataStatistics( DataStatistics dataStatistics )
    {

    }

    @Override public void deleteDataStatistics( DataStatistics dataStatistics )
    {

    }

    @Override public void getDataStatisticsInterval( Date startIterval, Date endInterval )
    {

    }

    @Override public void getDataSatisticsById( int id )
    {

    }
}
