package org.hisp.dhis.message.hibernate;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import org.hibernate.Query;
import org.hisp.dhis.common.CodeGenerator;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.message.MessageConversationStore;
import org.hisp.dhis.message.UserMessage;
import org.hisp.dhis.system.util.SqlHelper;
import org.hisp.dhis.user.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Lars Helge Overland
 */
public class HibernateMessageConversationStore
    extends HibernateIdentifiableObjectStore<MessageConversation>
    implements MessageConversationStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementBuilder statementBuilder;

    public void setStatementBuilder( StatementBuilder statementBuilder )
    {
        this.statementBuilder = statementBuilder;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    public List<MessageConversation> getMessageConversations( User user, boolean followUpOnly, boolean unreadOnly,
        Integer first, Integer max )
    {
        SqlHelper sh = new SqlHelper();

        String sql = "select mc.messageconversationid, mc.uid, mc.subject, mc.lastmessage, ui.surname, ui.firstname, um.isread, um.isfollowup, (" +
            "select count(messageconversationid) from messageconversation_messages mcm where mcm.messageconversationid=mc.messageconversationid) as messagecount " +
            ", mc.created, mc.lastupdated from messageconversation mc " +
            "inner join messageconversation_usermessages mu on mc.messageconversationid=mu.messageconversationid " +
            "inner join usermessage um on mu.usermessageid=um.usermessageid " +
            "left join userinfo ui on mc.lastsenderid=ui.userinfoid ";

        if ( user != null )
        {
            sql += sh.whereAnd() + " um.userid=" + user.getId() + " ";
        }

        if ( followUpOnly )
        {
            sql += sh.whereAnd() + " um.isfollowup=true ";
        }

        if ( unreadOnly )
        {
            sql += sh.whereAnd() + " um.isread=false ";
        }

        sql += "order by mc.lastmessage desc ";

        if ( first != null && max != null )
        {
            sql += statementBuilder.limitRecord( first, max );
        }

        final List<MessageConversation> conversations = jdbcTemplate.query( sql, new RowMapper<MessageConversation>()
        {
            @Override
            public MessageConversation mapRow( ResultSet resultSet, int count )
                throws SQLException
            {
                MessageConversation conversation = new MessageConversation();

                conversation.setId( resultSet.getInt( 1 ) );
                conversation.setUid( resultSet.getString( 2 ) );
                conversation.setSubject( resultSet.getString( 3 ) );
                conversation.setLastMessage( resultSet.getDate( 4 ) );
                conversation.setLastSenderSurname( resultSet.getString( 5 ) );
                conversation.setLastSenderFirstname( resultSet.getString( 6 ) );
                conversation.setRead( resultSet.getBoolean( 7 ) );
                conversation.setFollowUp( resultSet.getBoolean( 8 ) );
                conversation.setMessageCount( resultSet.getInt( 9 ) );
                conversation.setCreated( resultSet.getTimestamp( 10 ) );
                conversation.setLastUpdated( resultSet.getTimestamp( 11 ) );

                return conversation;
            }
        } );

        return conversations;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<MessageConversation> getMessageConversations( String[] uids )
    {
        final String params = prepareArrayParameters( uids );

        if( params == null )
        {
            return new ArrayList<>();
        }

        final String sql =
            "SELECT mc.messageconversationid, mc.uid, mc.subject, mc.lastmessage, " +
            "ui.surname, ui.firstname, um.isread, um.isfollowup, " +
            "(SELECT count(messageconversationid) FROM messageconversation_messages mcm WHERE mcm.messageconversationid=mc.messageconversationid) " +
            "AS messagecount, mc.created, mc.lastupdated FROM messageconversation mc " +
            "INNER JOIN messageconversation_usermessages mu on mc.messageconversationid=mu.messageconversationid " +
            "INNER JOIN usermessage um on mu.usermessageid=um.usermessageid " +
            "LEFT JOIN userinfo ui on mc.lastsenderid=ui.userinfoid " +
            "WHERE mc.uid IN (" + params + ") ";

        return jdbcTemplate.query( sql, new RowMapper<MessageConversation>()
        {
            @Override
            public MessageConversation mapRow( ResultSet resultSet, int i )
                throws SQLException
            {
                MessageConversation conversation = new MessageConversation();

                conversation.setId( resultSet.getInt( "messageconversationid" ) );
                conversation.setUid( resultSet.getString( "uid" ) );
                conversation.setSubject( resultSet.getString( "subject" ) );
                conversation.setLastMessage( resultSet.getDate( "lastmessage" ) );
                conversation.setLastSenderSurname( resultSet.getString( "surname" ) );
                conversation.setLastSenderFirstname( resultSet.getString( "firstname" ) );
                conversation.setRead( resultSet.getBoolean( "isread" ) );
                conversation.setFollowUp( resultSet.getBoolean( "isfollowup" ) );
                conversation.setMessageCount( resultSet.getInt( "messagecount" ) );
                conversation.setCreated( resultSet.getTimestamp( "created" ) );
                conversation.setLastUpdated( resultSet.getTimestamp( "lastupdated" ) );

                return conversation;
            }
        } );
    }

    @Override
    public int getMessageConversationCount( User user, boolean followUpOnly, boolean unreadOnly )
    {
        String sql = "select count(*) from messageconversation mc "
            + "left join messageconversation_usermessages mu on mc.messageconversationid=mu.messageconversationid "
            + "left join usermessage um on mu.usermessageid=um.usermessageid " + "where um.userid=" + user.getId()
            + " ";

        if ( followUpOnly )
        {
            sql += "and um.isfollowup=true ";
        }

        if ( unreadOnly )
        {
            sql += "and um.isread=false ";
        }

        return jdbcTemplate.queryForObject( sql, Integer.class );
    }

    @Override
    public long getUnreadUserMessageConversationCount( User user )
    {
        if ( user == null )
        {
            return -1;
        }

        String hql = "select count(*) from MessageConversation m join m.userMessages u where u.user = :user and u.read = false";

        Query query = getQuery( hql );
        query.setEntity( "user", user );

        return (Long) query.uniqueResult();
    }

    @Override
    public int deleteMessages( User sender )
    {
        if ( sender == null )
        {
            return -1;
        }

        String sql = "delete from messageconversation_messages where messageid in ("
            + "select messageid from message where userid = " + sender.getId() + ")";

        getSqlQuery( sql ).executeUpdate();

        String hql = "delete Message m where m.sender = :sender";

        Query query = getQuery( hql );
        query.setEntity( "sender", sender );
        return query.executeUpdate();
    }

    @Override
    public int deleteUserMessages( User user )
    {
        if ( user == null )
        {
            return -1;
        }

        String sql = "delete from messageconversation_usermessages where usermessageid in ("
            + "select usermessageid from usermessage where userid = " + user.getId() + ")";

        getSqlQuery( sql ).executeUpdate();

        String hql = "delete UserMessage u where u.user = :user";

        Query query = getQuery( hql );
        query.setEntity( "user", user );
        return query.executeUpdate();
    }

    @Override
    public int removeUserFromMessageConversations( User lastSender )
    {
        String hql = "update MessageConversation m set m.lastSender = null where m.lastSender = :lastSender";

        Query query = getQuery( hql );
        query.setEntity( "lastSender", lastSender );
        return query.executeUpdate();
    }

    @Override
    public List<UserMessage> getLastRecipients( User user, Integer first, Integer max )
    {
        String sql = " select distinct userinfoid, surname, firstname from userinfo uf"
            + " join usermessage um on (uf.userinfoid = um.userid)"
            + " join messageconversation_usermessages mu on (um.usermessageid = mu.usermessageid)"
            + " join messageconversation mc on (mu.messageconversationid = mc.messageconversationid)"
            + " where mc.lastsenderid = " + user.getId();

        sql += " order by userinfoid desc";

        if ( first != null && max != null )
        {
            sql += " " + statementBuilder.limitRecord( first, max );
        }

        final List<UserMessage> recipients = jdbcTemplate.query( sql, new RowMapper<UserMessage>()
        {
            @Override
            public UserMessage mapRow( ResultSet resultSet, int count ) throws SQLException
            {
                UserMessage recipient = new UserMessage();

                recipient.setId( resultSet.getInt( 1 ) );
                recipient.setLastRecipientSurname( resultSet.getString( 2 ) );
                recipient.setLastRecipientFirstname( resultSet.getString( 3 ) );

                return recipient;
            }
        } );

        return recipients;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String prepareArrayParameters( String[] parameterArray )
    {
        if( parameterArray.length < 1 )
        {
            return null;
        }

        String params = parameterArray[0];

        for ( int i = 1 ; i < parameterArray.length; i++ )
        {
            if( !CodeGenerator.isValidCode( parameterArray[i] ) )
            {
                return null;
            }

            params += "," + "'" + parameterArray[i] + "'";
        }

        return params;
    }
}
