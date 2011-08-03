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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 */
public class MessageServiceTest
    extends DhisSpringTest
{
    private User sender;
    private User userA;
    private User userB;

    private Set<User> users;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        userService = (UserService) getBean( UserService.ID );
        messageService = (MessageService) getBean( MessageService.ID );
        
        sender = createUser( 'S' );
        userA = createUser( 'A' );
        userB = createUser( 'B' );

        userService.addUser( sender );
        userService.addUser( userA );
        userService.addUser( userB );
        
        users = new HashSet<User>();
        users.add( userA );
        users.add( userB );        
    }

    @Test
    public void testSaveMessageA()
    {
        Message messageA = new Message( "SubjectA" );
        Message messageB = new Message( "SubjectB" );
        
        int idA = messageService.saveMessage( messageA );
        int idB = messageService.saveMessage( messageB );
        
        messageA = messageService.getMessage( idA );
        messageB = messageService.getMessage( idB );
        
        assertNotNull( messageA );
        assertEquals( "SubjectA", messageA.getSubject() );

        assertNotNull( messageB );
        assertEquals( "SubjectB", messageB.getSubject() );
    }

    @Test
    public void testSaveMessageB()
    {
        Message message = new Message( "Subject" );
        
        UserMessage userMessageA = new UserMessage( userA );
        UserMessage userMessageB = new UserMessage( userB );
        
        message.addUserMessage( userMessageA );
        message.addUserMessage( userMessageB );
        
        MessageContent contentA = new MessageContent( "TextA", sender );
        MessageContent contentB = new MessageContent( "TextB", sender );
        
        message.addMessageContent( contentA );
        message.addMessageContent( contentB );
        
        int id = messageService.saveMessage( message );
        
        message = messageService.getMessage( id );
        
        assertNotNull( message );
        assertEquals( "Subject", message.getSubject() );
        assertEquals( 2, message.getUserMessages().size() );
        assertTrue( message.getUserMessages().contains( userMessageA ) );
        assertTrue( message.getUserMessages().contains( userMessageB ) );
        assertEquals( 2, message.getContents().size() );
        assertTrue( message.getContents().contains( contentA ) );
        assertTrue( message.getContents().contains( contentB ) );
    }

    @Test
    public void testSendMessage()
    {
        int id = messageService.sendMessage( "Subject", "Text", users );
        
        Message message = messageService.getMessage( id );
        
        assertNotNull( message );
        assertEquals( "Subject", message.getSubject() );
        assertEquals( 2, message.getUserMessages().size() );
        assertEquals( 1, message.getContents().size() );
        assertTrue( message.getContents().iterator().next().getText().equals( "Text" ) );
    }
    
    @Test
    public void testSendFeedback()
    {
        int id = messageService.sendFeedback( "Subject", "Text" );
        
        Message message = messageService.getMessage( id );
        
        assertNotNull( message );
        assertEquals( "Subject", message.getSubject() );
        assertEquals( 1, message.getContents().size() );
        assertTrue( message.getContents().iterator().next().getText().equals( "Text" ) );
    }
    
    @Test
    public void testSendReply()
    {
        Message message = new Message( "Subject" );
        message.addMessageContent( new MessageContent( "TextA", sender ) );
        int id = messageService.saveMessage( message );
        
        messageService.sendReply( message, "TextB" );
        
        message = messageService.getMessage( id );
        
        assertNotNull( message );
        assertEquals( "Subject", message.getSubject() );
        assertEquals( 2, message.getContents().size() );       
    }
}
