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


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.dxf2.webmessage.WebMessageStatus;
import org.hisp.dhis.sms.config.SMSGatewayStatus;
import org.hisp.dhis.sms.config.SmsConfigurationManager;
import org.hisp.dhis.sms.outbound.OutboundSmsTransportService;
import org.hisp.dhis.sms.outbound.SMSServiceStatus;
import org.hisp.dhis.webapi.service.WebMessageService;
import org.hisp.dhis.webapi.utils.WebMessageUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SmsConfigController
{

    public static final String GETSMSSERICESTATUS = "/getSMSServiceStatus";

    public static final String STARTSMSSERVICE = "/startSMSService";

    public static final String STOPSMSSERVICE = "/stopSMSService";

    public static final String ADDSMSGATEWAY = "/addSMSGateway";

    public static final String REMOVESMSGATEWAY = "/removeSMSGateway";

    public static final String REMOVEALLSMSGATEWAY = "/removeAllSMSGateway";

    public static final String SETDEFAULTSMSGATEWAY = "/setDefaultSMSGateway";

    public static final String GETSMSGATEWAYSTATUS = "/getSMSGatewayStatus";

    public static final String GETDEFAULTSMSGATEWAY = "/getDefaultSMSGateway";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private OutboundSmsTransportService transportService;

    @Autowired
    private SmsConfigurationManager smsConfigManager;

    @Autowired
    private WebMessageService webMessageService;

    // -------------------------------------------------------------------------
    // GET
    // -------------------------------------------------------------------------

    @RequestMapping( value = GETSMSSERICESTATUS, method = RequestMethod.GET )
    public void getSMSSServiceStatus( HttpServletRequest request, HttpServletResponse response )
    {

        if ( isTransportServiceAvailable( request, response ) )
        {
            return;
        }

        String smsServiceStatus = transportService.getServiceStatus();
        webMessageService.send( WebMessageUtils.ok( smsServiceStatus ), response, request );

    }

    @RequestMapping( value = GETSMSGATEWAYSTATUS, method = RequestMethod.GET )
    public void getSMSGatewayStatus( HttpServletRequest request, HttpServletResponse response )

    {
        if ( isTransportServiceAvailable( request, response ) )
        {
            return;
        }

        if ( transportService.getGatewayStatus() == SMSGatewayStatus.UNDEFINED )
        {
            webMessageService.send( WebMessageUtils.serviceUnavailable( "Could not find the gateway status",
                "Gateway Status is UNDEFINED" ), response, request );
            return;
        }

        webMessageService.send( WebMessageUtils.ok( transportService.getGatewayStatus().toString() ), response,
            request );
    }

    @RequestMapping( value = STARTSMSSERVICE, method = RequestMethod.GET )
    public void startSMSService( HttpServletRequest request, HttpServletResponse response )

    {

        if ( isTransportServiceAvailable( request, response ) )
        {
            return;
        }

        if ( transportService.getServiceStatusEnum() == SMSServiceStatus.STARTED
            || transportService.getServiceStatusEnum() == SMSServiceStatus.STARTING )
        {
            webMessageService.send(
                WebMessageUtils.ok( "Service already started ", "SMSServiceStatus is STARTING OR STARTED" ), response,
                request );
            return;
        }

        transportService.startService();
        webMessageService.send( WebMessageUtils.ok( "Service Started" ), response, request );

    }

    @RequestMapping( value = STOPSMSSERVICE, method = RequestMethod.GET )
    public void stopSMSService( HttpServletResponse response, HttpServletRequest request )

    {

        if ( isTransportServiceAvailable( request, response ) )
        {
            return;
        }

        if ( transportService.getServiceStatusEnum() == SMSServiceStatus.STOPPED
            || transportService.getServiceStatusEnum() == SMSServiceStatus.STOPPING )
        {
            webMessageService.send(
                WebMessageUtils.ok( "Service already stopped ", "SMSServiceStatus is STOPPED OR STOPPING" ), response,
                request );
            return;
        }

        transportService.stopService();
        webMessageService.send( WebMessageUtils.ok( "SMS Service Stopped" ), response, request );

    }

    @RequestMapping( value = GETDEFAULTSMSGATEWAY, method = RequestMethod.GET )
    public void getDefaultSMSGateway( HttpServletRequest request, HttpServletResponse response )

    {

        if ( isTransportServiceAvailable( request, response ) )
        {
            return;
        }

        String defaultGateway = transportService.getDefaultGateway();

        if ( defaultGateway == null || defaultGateway.length() <= 0 )
        {
            webMessageService.send( WebMessageUtils.error( "Could not get default gateway", "Default Gateway is NULL" ),
                response, request );
            return;
        }

        webMessageService.send( WebMessageUtils.ok( defaultGateway ), response, request );

    }

    @RequestMapping( value = SETDEFAULTSMSGATEWAY, method = RequestMethod.GET )
    public void setDefaultSMSGateway( @RequestParam String gatewayId, HttpServletRequest request,
        HttpServletResponse response )

    {

        if ( isTransportServiceAvailable( request, response ) )
        {
            return;
        }

        if ( !smsConfigManager.gatewayExists( gatewayId ) )
        {
            webMessageService.send( WebMessageUtils.badRequest( "Server Does not Exist" ), response, request );
            return;
        }

        if ( smsConfigManager.setDefaultSMSGateway( gatewayId ) )
        {
            webMessageService.send( WebMessageUtils.ok( gatewayId + ": Set to default" ), response, request );

        }
        else
        {
            webMessageService.send( WebMessageUtils.createWebMessage( gatewayId + ": Not set to default",
                WebMessageStatus.ERROR, HttpStatus.INTERNAL_SERVER_ERROR ), response, request );
        }

    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @ResponseStatus( value = HttpStatus.OK )
    @RequestMapping( value = REMOVESMSGATEWAY, method = RequestMethod.DELETE )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_MOBILE_SETTINGS')" )
    public void removeSMSGateway( @RequestParam String gatewayId, HttpServletRequest request,
        HttpServletResponse response )

    {

    }

    // -------------------------------------------------------------------------
    // Utility Methods
    // -------------------------------------------------------------------------

    private boolean isTransportServiceAvailable( HttpServletRequest request, HttpServletResponse response )
    {
        if ( transportService != null )
        {
            return true;
        }

        webMessageService.send(
            WebMessageUtils.error( "Could not find service status", "Transport Service Unavailable" ), response,
            request );
        return false;
    }

}
