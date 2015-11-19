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
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.dxf2.webmessage.WebMessageStatus;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.sms.outbound.OutboundSmsStatus;
import org.hisp.dhis.sms.outbound.OutboundSmsTransportService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.webapi.service.WebMessageService;
import org.hisp.dhis.webapi.utils.WebMessageUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SmsController
{

    public static final String SENDSMS = "/sendSMSMessage";

    public static final String SENDSMSTOALL = "/sendSMSToAll";

    public static final String LISTALLSMSINBOX = "/listAllSMSInbox";

    public static final String LISTALLSMSSENT = "/listAllSMSSent";

    public static final String RECEIVESMS = "/feedSMS";

    public static final String GETSMSSTATUS = "/getSMSStatus";

    public static final String DELETESMS = "/deleteSMS";

    public static final String DELETEALLSMS = "/deleteAllSMS";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private OutboundSmsTransportService transportService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private WebMessageService webMessageService;

    // -------------------------------------------------------------------------
    // POST
    // -------------------------------------------------------------------------

    @RequestMapping( value = SENDSMS, method = RequestMethod.POST )
    public void sendSMSMessage( @RequestParam String recipient, @RequestParam String textMessage,
        HttpServletResponse response, HttpServletRequest request )
    {

        int sms_id = 0;

        OutboundSms sms = createSMS( recipient, textMessage );

        String gateWayId = transportService.getDefaultGateway();

        boolean isServiceEnabled = transportService.isEnabled();

        if ( !isServiceEnabled || gateWayId == null || gateWayId.trim().length() <= 0 )
        {
            webMessageService.send( WebMessageUtils.badRequest( "Incorrect Gateway" ), response, request );
            return;
        }

        String result = transportService.sendMessage( sms, gateWayId );

        if ( result.equals( "success" ) )
        {
            webMessageService.send( WebMessageUtils.ok( result + " SMS Id :" + sms_id ), response, request );
        }
        else
        {
            webMessageService.send( WebMessageUtils.createWebMessage( "Failed to send SMS", WebMessageStatus.ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR ), response, request );
        }

    }

    private OutboundSms createSMS( String recipient, String textMessage )
    {

        Set<String> recipients = new HashSet<String>();
        recipients.add( recipient );

        OutboundSms sms = new OutboundSms();
        sms.setRecipients( recipients );
        sms.setMessage( textMessage );
        sms.setStatus( OutboundSmsStatus.OUTBOUND );
        sms.setUser( currentUserService.getCurrentUser() );

        return sms;
    }

}
