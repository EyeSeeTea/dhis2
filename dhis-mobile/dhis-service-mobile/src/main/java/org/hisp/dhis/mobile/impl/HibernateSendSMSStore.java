package org.hisp.dhis.mobile.impl;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hisp.dhis.mobile.api.SendSMS;
import org.hisp.dhis.mobile.api.SendSMSStore;
import org.hisp.dhis.user.User;

public class HibernateSendSMSStore implements SendSMSStore
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    // -------------------------------------------------------------------------
    // SendSMS
    // -------------------------------------------------------------------------

    public void addSendSMS( SendSMS sendSMS )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( sendSMS );
    }
    
    public void updateSendSMS( SendSMS sendSMS )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( sendSMS );
    }
    
    public void deleteSendSMS( SendSMS sendSMS )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( sendSMS );
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<SendSMS> getSendSMS( int start, int end )
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery( "from SendSMS" ).list().subList( start, end );
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<SendSMS> getAllSendSMS( )
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery( "from SendSMS" ).list();
    }
    
    @SuppressWarnings( "unchecked" )
    public int getRowCount()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( SendSMS.class );
        criteria.setProjection( Projections.rowCount() );
        Integer count = (Integer) criteria.uniqueResult();
        return count != null ? count.intValue() : 0;
    }

}
