package org.hisp.dhis.translation.hibernate;

/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.i18n.locale.I18nLocale;
import org.hisp.dhis.translation.Translation;
import org.hisp.dhis.translation.TranslationStore;

//import java.util.I18nLocale;

/**
 * @author Oyvind Brucker
 */
public class HibernateTranslationStore
    implements TranslationStore
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
    // Translation
    // -------------------------------------------------------------------------

    public void addTranslation( Translation translation )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( translation );
    }

    public void updateTranslation( Translation translation )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( translation );
    }

  
    public Translation getTranslation( String className, int id, I18nLocale locale, String property )
    {
        Collection<Translation> translations = getTranslations( className, id, locale, property, true );

        return (translations == null || translations.size() == 0) ? null : translations.iterator().next();
    }
    
    public Collection<Translation> getTranslations( String className, int id, I18nLocale locale )
    {
        return getTranslations( className, id, locale, null, true );
    }

    public Collection<Translation> getTranslations( String className, I18nLocale locale )
    {
        return getTranslations( className, null, locale, null, true );
    }

    public Translation getTranslationWithoutDefault( String className, int id, I18nLocale locale, String property )
    {
        Collection<Translation> translations = getTranslations( className, id, locale, property, false );

        return (translations == null || translations.size() == 0) ? null : translations.iterator().next();
    }

    public Collection<Translation> getTranslationsWithoutDefault( I18nLocale locale )
    {
        return getTranslations( null, null, locale, null, false );
    }

    public Collection<Translation> getTranslationsWithoutDefault( String className, int id, I18nLocale locale )
    {
        return getTranslations( className, id, locale, null, false );
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Translation> getTranslations( String className, Integer id, I18nLocale locale, String property, boolean useDefault )
    {
        Session session = sessionFactory.getCurrentSession();

        String hql = "from Translation t where t.className is not null";
        
        if ( className != null )
        {
            hql += " and t.className='" + className + "'";
        }

        if ( id != null )
        {
            hql += " and t.id=" + id;
        }

        if ( property != null )
        {
            hql += " and t.property='" + property + "'";
        }

        if ( locale.getCountry().equals( Translation.DEFAULT_COUNTRY ) )
        {
            hql += " and t.locale='" + locale.getLanguage() + "' and t.country = '" + Translation.DEFAULT_COUNTRY + "'";
        }
        else
        {
            hql += " and ( (t.locale='" + locale.getLanguage() + "' and t.country = '" + locale.getCountry() + "' ) ";

            if( useDefault )
            {                
                hql += " or ( t.locale='" + locale.getLanguage() + "' and t.country = '" + Translation.DEFAULT_COUNTRY + "'" 
                        + " and not exists ( from Translation t1 where t1.className=t.className and t1.property=t.property and t1.id=t.id and t1.locale=t.locale and t1.country = '" + locale.getCountry() + "' ) )";
            }
                
            hql += " )";                    
                        
        }

        Query query = session.createQuery( hql );

        return query.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Translation> getAllTranslations()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Translation.class );

        criteria.setCacheable( true );

        return criteria.list();
    }

    public void deleteTranslation( Translation translation )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( translation );
    }

    @SuppressWarnings( "unchecked" )
    public void deleteTranslations( String className, int id )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "from Translation t where t.className = :className and t.id = :id" );

        query.setString( "className", className );
        query.setInteger( "id", id );

        List<Object> objlist = query.list();

        for ( Object object : objlist )
        {
            session.delete( object );
        }
    }
}
