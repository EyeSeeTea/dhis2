package org.hisp.dhis.message;

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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserGroup;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 */
@Transactional
public class DefaultMessageService
    implements MessageService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MessageStore messageStore;

    public void setMessageStore( MessageStore messageStore )
    {
        this.messageStore = messageStore;
    }

    private CurrentUserService currentUserService;
    
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }

    // -------------------------------------------------------------------------
    // MessageService implementation
    // -------------------------------------------------------------------------

    public int sendMessage( String subject, String text, Set<User> users )
    {
        // ---------------------------------------------------------------------
        // Add feedback recipients to users if they are not there
        // ---------------------------------------------------------------------

        UserGroup userGroup = configurationService.getConfiguration().getFeedbackRecipients();

        if ( userGroup != null && userGroup.getMembers().size() > 0 )
        {
            users.addAll( userGroup.getMembers() );
        }

        // ---------------------------------------------------------------------
        // Instantiate message, content and user messages
        // ---------------------------------------------------------------------

        Message message = new Message( subject );
        
        message.addMessageContent( new MessageContent( text, currentUserService.getCurrentUser() ) );
        
        for ( User user : users )
        {
            message.addUserMessage( new UserMessage( user ) );        
        }
        
        return saveMessage( message );
    }

    public int sendFeedback( String subject, String text )
    {
        return sendMessage( subject, text, new HashSet<User>() );
    }
    
    public void sendReply( Message message, String text )
    {
        User sender = currentUserService.getCurrentUser();
        
        MessageContent content = new MessageContent( text, sender );
        
        message.addMessageContent( content );
        message.markUnread( sender );
        message.setLastUpdated( new Date() );
        
        updateMessage( message );        
    }
        
    public int saveMessage( Message message )
    {
        return messageStore.save( message );
    }
    
    public void updateMessage( Message message )
    {
        messageStore.update( message );
    }
    
    public Message getMessage( int id )
    {
        return messageStore.get( id );
    }
        
    public long getUnreadMessageCount()
    {
        return messageStore.getUnreadUserMessageCount( currentUserService.getCurrentUser() );
    }
    
    public long getUnreadMessageCount( User user )
    {
        return messageStore.getUnreadUserMessageCount( user );
    }
    
    public List<Message> getMessages( int first, int max )
    {
        return messageStore.getMessages( currentUserService.getCurrentUser(), first, max );
    }
}
