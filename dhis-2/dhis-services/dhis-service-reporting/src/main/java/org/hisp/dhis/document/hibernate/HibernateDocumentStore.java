package org.hisp.dhis.document.hibernate;

import java.util.Collection;

import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.document.DocumentStore;
import org.hisp.dhis.hibernate.HibernateSessionManager;

public class HibernateDocumentStore
    implements DocumentStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private HibernateSessionManager sessionManager;

    public void setSessionManager( HibernateSessionManager sessionManager )
    {
        this.sessionManager = sessionManager;
    }

    // -------------------------------------------------------------------------
    // DocumentStore implementation
    // -------------------------------------------------------------------------

    public int saveDocument( Document document )
    {
        return (Integer) sessionManager.getCurrentSession().save( document );
    }
    
    public Document getDocument( int id )
    {
        return (Document) sessionManager.getCurrentSession().get( Document.class, id );
    }
    
    public void deleteDocument( Document document )
    {
        sessionManager.getCurrentSession().delete( document );
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Document> getAllDocuments()
    {
        return sessionManager.getCurrentSession().createCriteria( Document.class ).list();
    }
    
    public Document getDocumentByName( String name )
    {
        return (Document) sessionManager.getCurrentSession().
            createCriteria( Document.class ).add( Restrictions.eq( "name", name ) ).uniqueResult();
    } 
}
