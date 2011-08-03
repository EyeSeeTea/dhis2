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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hisp.dhis.user.User;

/**
 * @author Lars Helge Overland
 */
public class Message
{
    private int id;
    
    private String key;
    
    private String subject;

    private Date lastUpdated;
    
    private Set<UserMessage> userMessages = new HashSet<UserMessage>();
    
    private List<MessageContent> contents = new ArrayList<MessageContent>();
    
    public Message()
    {
        this.key = UUID.randomUUID().toString();
        this.lastUpdated = new Date();
    }
    
    public Message( String subject )
    {
        this.key = UUID.randomUUID().toString();
        this.subject = subject;
        this.lastUpdated = new Date();
    }
    
    public void addUserMessage( UserMessage userMessage )
    {
        this.userMessages.add( userMessage );
    }
    
    public void addMessageContent( MessageContent content )
    {
        this.contents.add( content );
    }
    
    public void markUnread( User sender )
    {
        for ( UserMessage userMessage : userMessages )
        {
            if ( userMessage.getUser() != null && !userMessage.getUser().equals( sender ) )
            {
                userMessage.setRead( false );
            }
        }
    }
        
    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject( String subject )
    {
        this.subject = subject;
    }

    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated( Date lastUpdated )
    {
        this.lastUpdated = lastUpdated;
    }

    public Set<UserMessage> getUserMessages()
    {
        return userMessages;
    }

    public void setUserMessages( Set<UserMessage> userMessages )
    {
        this.userMessages = userMessages;
    }

    public List<MessageContent> getContents()
    {
        return contents;
    }

    public void setContents( List<MessageContent> contents )
    {
        this.contents = contents;
    }

    @Override
    public int hashCode()
    {
        return key.hashCode();
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }
        
        if ( object == null )
        {
            return false;
        }
        
        if ( getClass() != object.getClass() )
        {
            return false;
        }
        
        final Message other = (Message) object;
        
        return key.equals( other.key );
    }
    
    @Override
    public String toString()
    {
        return "[" + subject + "]";
    }
}
