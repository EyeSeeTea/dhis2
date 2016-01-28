package org.hisp.dhis.datastatistics.hibernate;

import org.hibernate.SessionFactory;
import org.hisp.dhis.datastatistics.DataStatistics;
import org.hisp.dhis.datastatistics.DataStatisticsStore;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.springframework.beans.factory.annotation.Autowired;


import javax.mail.Session;
import java.util.Date;

/**
 * Created by yrjanaff on 19.01.2016.
 */
public class HibernateDataStatisticsStore extends HibernateGenericStore<DataStatistics> implements DataStatisticsStore
{

    @Override
    public void addDataStatistics( DataStatistics dataStatistics )
    {
        sessionFactory.getCurrentSession().beginTransaction();
        System.out.println("\n\n\n\n i hibernatestore: " + dataStatistics.getText());
        System.out.println("Er det no SessionFactory?:" + sessionFactory);
        System.out.println("Er det no currentSession?:" + sessionFactory.getCurrentSession());
        int id = (Integer)sessionFactory.getCurrentSession().save(dataStatistics);
        sessionFactory.getCurrentSession().getTransaction().commit();

        System.out.println("Etter save: " + id);

    }

    @Override
    public void updateDataStatistics( DataStatistics dataStatistics )
    {

    }

    @Override
    public void deleteDataStatistics( DataStatistics dataStatistics )
    {

    }

    @Override
    public void getDataStatisticsInterval( Date startIterval, Date endInterval )
    {

    }

    @Override
    public DataStatistics getDataStatisticsById( int id )
    {
        System.out.println("\n\n\n\n i hibernatestore for å hente ut: " + id);
       return (DataStatistics) sessionFactory.getCurrentSession().get(DataStatistics.class,id);
    }

    @Override
    public void getAllDataStatistics(){


    }
}
