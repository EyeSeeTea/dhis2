package org.hisp.dhis.datastatistics.hibernate;

import org.hibernate.Session;
import org.hisp.dhis.datastatistics.DataStatistics;
import org.hisp.dhis.datastatistics.DataStatisticsStore;
import org.hisp.dhis.hibernate.HibernateGenericStore;



import java.util.Date;

/**
 * @author Yrjan A. F. Fraschetti
 * @author Julie Hill Roa
 */
public class HibernateDataStatisticsStore extends HibernateGenericStore<DataStatistics> implements DataStatisticsStore
{

    @Override
    public void addDataStatistics( DataStatistics dataStatistics )
    {
        Session session = sessionFactory.openSession();
        session.save(dataStatistics);
        session.flush();
    }

    @Override
    public void updateDataStatistics( DataStatistics dataStatistics )
    {
        Session session = sessionFactory.openSession();
        session.update(dataStatistics);
        session.flush();
    }
    @Override
    public void updateDataStatisticsTest( String text )
    {
        Session session = sessionFactory.openSession();
        DataStatistics ds = (DataStatistics)session.get(DataStatistics.class, 1);
        ds.setText(text);
        session.update(ds);
        session.flush();
    }

    @Override
    public void deleteDataStatistics( DataStatistics dataStatistics )
    {
        Session session = sessionFactory.openSession();
        session.delete(dataStatistics);
        session.flush();
    }

    @Override
    public void getDataStatisticsInterval( Date startIterval, Date endInterval )
    {

    }

    @Override
    public DataStatistics getDataStatisticsById( int id )
    {
       return (DataStatistics) sessionFactory.getCurrentSession().get(DataStatistics.class,id);
    }

    @Override
    public void getAllDataStatistics(){


    }
}
