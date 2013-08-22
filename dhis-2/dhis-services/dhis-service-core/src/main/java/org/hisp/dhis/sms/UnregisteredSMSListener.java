package org.hisp.dhis.sms;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.sms.incoming.IncomingSms;
import org.hisp.dhis.sms.incoming.IncomingSmsListener;
import org.hisp.dhis.sms.parse.ParserType;
import org.hisp.dhis.sms.parse.SMSParserException;
import org.hisp.dhis.smscommand.SMSCommand;
import org.hisp.dhis.smscommand.SMSCommandService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserService;
import org.springframework.transaction.annotation.Transactional;

public class UnregisteredSMSListener
    implements IncomingSmsListener
{
    private SMSCommandService smsCommandService;

    private UserService userService;

    private MessageService messageService;

    private SmsMessageSender smsMessageSender;

    @Transactional
    @Override
    public boolean accept( IncomingSms sms )
    {
        String message = sms.getText();
        String commandString = null;
        if ( message.indexOf( " " ) > 0 )
        {
            commandString = message.substring( 0, message.indexOf( " " ) );
            message = message.substring( commandString.length() );
        }
        else
        {
            commandString = message;
        }

        return smsCommandService.getSMSCommand( commandString, ParserType.UNREGISTERED_PARSER ) != null;
    }

    @Transactional
    @Override
    public void receive( IncomingSms sms )
    {
        String message = sms.getText();
        String commandString = null;
        if ( message.indexOf( " " ) > 0 )
        {
            commandString = message.substring( 0, message.indexOf( " " ) );
            message = message.substring( commandString.length() );
        }
        else
        {
            commandString = message;
        }

        SMSCommand smsCommand = smsCommandService.getSMSCommand( commandString, ParserType.UNREGISTERED_PARSER );

        UserGroup userGroup = smsCommand.getUserGroup();

        String senderPhoneNumber = StringUtils.replace( sms.getOriginator(), "+", "" );

        if ( userGroup != null )
        {
            Collection<User> users = userService.getUsersByPhoneNumber( senderPhoneNumber );

            if ( users != null && users.size() >= 1 )
            {
                String messageError = "This number is already registered for user: ";
                for ( Iterator<User> iterator = users.iterator(); iterator.hasNext(); )
                {
                    User user = iterator.next();
                    messageError += user.getName();
                    if ( iterator.hasNext() )
                    {
                        messageError += ", ";
                    }
                }
                throw new SMSParserException( messageError );
            }
            else
            {
                Set<User> receivers = new HashSet<User>( userGroup.getMembers() );

                UserCredentials anonymousUser = userService.getUserCredentialsByUsername( "system" );

                if ( anonymousUser == null )
                {
                    anonymousUser = userService.getUserCredentialsByUsername( "admin" );
                }
                /*MessageConversation conversation = new MessageConversation( smsCommand.getName(),
                    anonymousUser.getUser() );

                conversation.addMessage( new Message( message, null, anonymousUser.getUser() ) );

                for ( User receiver : receivers )
                {
                    boolean read = false;

                    conversation.addUserMessage( new UserMessage( receiver, read ) );
                }*/
                // forward to user group by SMS, E-mail, DHIS conversation

                messageService.sendMessage( smsCommand.getName(), message, null, receivers, anonymousUser.getUser(),
                    false, false );

                // confirm SMS was received and forwarded completely
                Set<User> feedbackList = new HashSet<User>();
                User sender = new User();
                sender.setPhoneNumber( senderPhoneNumber );
                feedbackList.add( sender );
                smsMessageSender.sendMessage( smsCommand.getName(), smsCommand.getReceivedMessage(), null,
                    feedbackList, true );
            }
        }
    }

    public void setSmsCommandService( SMSCommandService smsCommandService )
    {
        this.smsCommandService = smsCommandService;
    }

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    public void setMessageService( MessageService messageService )
    {
        this.messageService = messageService;
    }

    public void setSmsMessageSender( SmsMessageSender smsMessageSender )
    {
        this.smsMessageSender = smsMessageSender;
    }
    
}
