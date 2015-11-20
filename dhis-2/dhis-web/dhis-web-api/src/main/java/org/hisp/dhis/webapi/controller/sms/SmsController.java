package org.hisp.dhis.webapi.controller.sms;
/*
 * Copyright (c) 2004-2015, University of Oslo
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

/**
 * Zubair <rajazubair.asghar@gmail.com>
 */

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.sms.SmsSender;
import org.hisp.dhis.sms.incoming.IncomingSms;
import org.hisp.dhis.sms.incoming.IncomingSmsService;
import org.hisp.dhis.sms.incoming.SmsMessageEncoding;
import org.hisp.dhis.sms.incoming.SmsMessageStatus;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.sms.outbound.OutboundSmsStatus;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.webapi.service.WebMessageService;
import org.hisp.dhis.webapi.utils.WebMessageUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SmsController
{

    public static final String SENDSMS = "/sendSMS";

    public static final String SENDSMSTOALL = "/sendSMSToAll";

    public static final String LISTALLSMSINBOX = "/listAllSMSInbox";

    public static final String LISTALLSMSSENT = "/listAllSMSSent";

    public static final String RECEIVESMS = "/receiveSMS";

    public static final String GETSMSSTATUS = "/getSMSStatus";

    public static final String DELETESMS = "/deleteSMS";

    public static final String DELETEALLSMS = "/deleteAllSMS";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private SmsSender smsSender;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private WebMessageService webMessageService;

    @Autowired
    private IncomingSmsService incomingSMSService;

    // -------------------------------------------------------------------------
    // POST
    // -------------------------------------------------------------------------

    @RequestMapping( value = SENDSMS, method = RequestMethod.POST )
    public void sendSMSMessage( @RequestParam String recipient, @RequestParam String message,
        HttpServletResponse response, HttpServletRequest request )
            throws WebMessageException
    {
        if ( recipient == null || recipient.length() <= 0 )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Recipient must be specified" ) );
        }

        if ( message == null || message.length() <= 0 )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Message must be specified" ) );
        }

        OutboundSms sms = createOutBoundSMS( recipient, message );

        String result = smsSender.sendMessage( sms );

        if ( result.equals( "success" ) )
        {
            webMessageService.send( WebMessageUtils.ok( result ), response, request );
        }
        else
        {
            throw new WebMessageException( WebMessageUtils.error( "Message seding failed" ) );
        }
    }

    @RequestMapping( value = RECEIVESMS, method = RequestMethod.POST )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_MOBILE_SETTINGS')" )
    public void receiveSMSMessage( @RequestParam String sender, @RequestParam( required = false ) String received_time,
        @RequestParam String message, @RequestParam( defaultValue = "Unknown", required = false ) String gateway,
        HttpServletRequest request, HttpServletResponse response)
            throws WebMessageException
    {
        if ( sender == null || sender.length() <= 0 )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Sender must be specified" ) );
        }

        if ( message == null || message.length() <= 0 )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Message must be specified" ) );
        }

        IncomingSms sms = createIncomingSMS( message, sender, gateway );
        incomingSMSService.save( sms );

        webMessageService.send( WebMessageUtils.ok( "Received" ), response, request );
    }

    @RequestMapping( value = RECEIVESMS, method = RequestMethod.POST, consumes = "application/json" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_MOBILE_SETTINGS')" )
    public void receiveSMSMessage( @RequestBody Map<String, Object> jsonMassage, HttpServletRequest request,
        HttpServletResponse response )
            throws WebMessageException
    {
        if ( jsonMassage == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "RequestBody must not be empty" ) );
        }

        IncomingSms sms = createIncomingSMS( jsonMassage.get( "message" ), jsonMassage.get( "originator" ),
            jsonMassage.get( "gateway" ) );
        incomingSMSService.save( sms );

        webMessageService.send( WebMessageUtils.ok( "Received Json" ), response, request );
    }

    // -------------------------------------------------------------------------
    // Utility Methods
    // -------------------------------------------------------------------------

    private IncomingSms createIncomingSMS( Object text, Object originator, Object gateway )
    {
        IncomingSms sms = new IncomingSms();
        sms.setText( text.toString() );
        sms.setOriginator( originator.toString() );
        sms.setGatewayId( gateway.toString() );
        sms.setSentDate( new Date() );
        sms.setReceivedDate( new Date() );
        sms.setEncoding( SmsMessageEncoding.ENC7BIT );
        sms.setStatus( SmsMessageStatus.INCOMING );
        return sms;
    }

    private OutboundSms createOutBoundSMS( Object recipient, Object textMessage )
    {
        Set<String> recipients = new HashSet<String>();
        recipients.add( recipient.toString() );

        OutboundSms sms = new OutboundSms();
        sms.setRecipients( recipients );
        sms.setMessage( textMessage.toString() );
        sms.setStatus( OutboundSmsStatus.OUTBOUND );
        sms.setUser( currentUserService.getCurrentUser() );
        return sms;
    }
}
