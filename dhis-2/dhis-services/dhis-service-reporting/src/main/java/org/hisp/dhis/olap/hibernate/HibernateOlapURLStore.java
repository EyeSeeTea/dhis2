package org.hisp.dhis.olap.hibernate;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateSessionManager;
import org.hisp.dhis.olap.OlapURL;
import org.hisp.dhis.olap.OlapURLStore;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class HibernateOlapURLStore
    implements OlapURLStore
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
    // OlapURLStore implementation
    // -------------------------------------------------------------------------

    public int saveOlapURL( OlapURL olapURL )
    {
        return (Integer) sessionManager.getCurrentSession().save( olapURL );
    }
    
    public void updateOlapURL( OlapURL olapURL )
    {
        sessionManager.getCurrentSession().update( olapURL );
    }
    
    public OlapURL getOlapURL( int id )
    {
        return (OlapURL) sessionManager.getCurrentSession().get( OlapURL.class, id );
    }
    
    public void deleteOlapURL( OlapURL olapURL )
    {
        sessionManager.getCurrentSession().delete( olapURL );
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<OlapURL> getAllOlapURLs()
    {
        return sessionManager.getCurrentSession().createCriteria( OlapURL.class ).list();
    }
    
    public OlapURL getOlapURLByName( String name )
    {
        Criteria criteria = sessionManager.getCurrentSession().createCriteria( OlapURL.class );
        
        criteria.add( Restrictions.eq( "name", name ) );
        
        return (OlapURL) criteria.uniqueResult();
    }
}
