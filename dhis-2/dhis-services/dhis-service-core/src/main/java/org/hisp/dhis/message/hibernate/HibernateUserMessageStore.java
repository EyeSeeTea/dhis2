package org.hisp.dhis.message.hibernate;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.message.UserMessage;
import org.hisp.dhis.message.UserMessageStore;
import org.hisp.dhis.user.User;

/**
 * @author Lars Helge Overland
 */
public class HibernateUserMessageStore
    extends HibernateGenericStore<UserMessage> implements UserMessageStore 
{
    @SuppressWarnings("unchecked")
    public List<UserMessage> getUserMessages( User user, int first, int max )
    {
        Criteria criteria = getCriteria( Restrictions.eq( "user", user ) );
        
        criteria.setFirstResult( first );
        criteria.setMaxResults( max );
        criteria.addOrder( Order.desc( "messageDate" ) );
        //TODO eager-fetch message
        
        return criteria.list();
    }
    
    public long getUnreadUserMessageCount( User user )
    {
        String hql = "select count(*) from UserMessage where user = :user and read = false";
        
        Query query = getQuery( hql );
        query.setEntity( "user", user );
        query.setCacheable( true );
        
        return (Long) query.uniqueResult();
    }
}
