package org.hisp.dhis.mobile.action;

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
import java.util.Set;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.sms.MessageSender;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class ProcessingSendSMSAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private SelectionTreeManager selectionTreeManager;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private MessageSender messageSender;

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String gatewayId;

    public void setGatewayId( String gatewayId )
    {
        this.gatewayId = gatewayId;
    }

    private String smsSubject;

    public void setSmsSubject( String smsSubject )
    {
        this.smsSubject = smsSubject;
    }

    private String smsMessage;

    public void setSmsMessage( String smsMessage )
    {
        this.smsMessage = smsMessage;
    }

    private Set<String> recipients = new HashSet<String>();

    public void setRecipients( Set<String> recipients )
    {
        this.recipients = recipients;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // I18n
    // -------------------------------------------------------------------------

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        if ( gatewayId == null || gatewayId.isEmpty() )
        {
            message = i18n.getString( "please_select_a_gateway_type_to_send_sms" );

            return ERROR;
        }

        if ( smsMessage != null && !smsMessage.isEmpty() )
        {
            Collection<OrganisationUnit> units = selectionTreeManager.getReloadedSelectedOrganisationUnits();

            if ( units != null && !units.isEmpty() )
            {
                Set<User> users = new HashSet<User>();

                for ( OrganisationUnit unit : units )
                {
                    if ( unit.getUsers() == null || unit.getUsers().isEmpty() )
                    {
                        if ( unit.getPhoneNumber() != null && !unit.getPhoneNumber().isEmpty() )
                        {
                            recipients.add( unit.getPhoneNumber() );
                        }
                    }
                    else
                    {
                        users.addAll( unit.getUsers() );
                    }
                }

                message = messageSender.sendMessage( smsSubject, smsMessage, currentUserService.getCurrentUser(),
                    false, users, gatewayId );

                if ( message != null && !message.equals( "success" ) )
                {
                    message = i18n.getString( message );

                    return ERROR;
                }
            }
            
            if ( recipients != null && !recipients.isEmpty() )
            {
                message = messageSender.sendMessage( smsSubject, smsMessage, currentUserService.getCurrentUser(), true,
                    recipients, gatewayId );

                if ( message != null && !message.equals( "success" ) )
                {
                    message = i18n.getString( message );

                    return ERROR;
                }
            }
        }

        return SUCCESS;
    }
}
