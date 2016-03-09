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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.render.RenderService;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.sms.config.BulkSmsGatewayConfig;
import org.hisp.dhis.sms.config.ClickatellGatewayConfig;
import org.hisp.dhis.sms.config.GatewayAdministratonService;
import org.hisp.dhis.sms.config.GenericHttpGatewayConfig;
import org.hisp.dhis.sms.config.ModemGatewayConfig;
import org.hisp.dhis.sms.config.SMPPGatewayConfig;
import org.hisp.dhis.sms.config.SmsGatewayConfig;
import org.hisp.dhis.sms.outbound.OutboundSmsTransportService;
import org.hisp.dhis.webapi.service.WebMessageService;
import org.hisp.dhis.webapi.utils.WebMessageUtils;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Zubair <rajazubair.asghar@gmail.com>
 */

@RestController
@RequestMapping( value = "/sms/gateways" )
public class SmsGatewayController
{
    @Autowired
    private WebMessageService webMessageService;

    @Autowired
    private OutboundSmsTransportService outboundSmsTransportService;

    @Autowired
    private RenderService renderService;

    @Autowired
    private GatewayAdministratonService gatewayAdminService;

    // -------------------------------------------------------------------------
    // GET
    // -------------------------------------------------------------------------

    @PreAuthorize( "hasRole('ALL') or hasRole('F_MOBILE_SENDSMS')" )
    @RequestMapping( method = RequestMethod.GET, produces = { "application/json" } )
    public void getGateways( HttpServletRequest request, HttpServletResponse response )
        throws WebMessageException, IOException
    {
        if ( gatewayAdminService == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Gateway admin service is not available" ) );
        }

        renderService.toJson( response.getOutputStream(), gatewayAdminService.listGateways() );
    }

    @PreAuthorize( "hasRole('ALL') or hasRole('F_MOBILE_SENDSMS')" )
    @RequestMapping( value = "/default", method = RequestMethod.GET )
    public void getDefault( HttpServletRequest request, HttpServletResponse response )
        throws WebMessageException
    {
        if ( outboundSmsTransportService == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Transport service is not available" ) );
        }

        String defaultGateway = outboundSmsTransportService.getDefaultGateway();

        webMessageService.send( WebMessageUtils.ok( "Default Gateway " + defaultGateway ), response, request );
    }

    @PreAuthorize( "hasRole('ALL') or hasRole('F_MOBILE_SENDSMS')" )
    @RequestMapping( value = "/{uid}", method = RequestMethod.GET, produces = "application/json" )
    public void getClickatellConfiguration( @PathVariable String uid, HttpServletRequest request,
        HttpServletResponse response )
            throws WebMessageException, IOException
    {
        if ( gatewayAdminService == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Gateway admin service is not available" ) );
        }

        renderService.toJson( response.getOutputStream(), gatewayAdminService.getGatewayConfiguration( uid ) );
    }

    // -------------------------------------------------------------------------
    // PUT,POST
    // -------------------------------------------------------------------------

    @PreAuthorize( "hasRole('ALL') or hasRole('F_MOBILE_SENDSMS')" )
    @RequestMapping( value = "/clickatell", method = { RequestMethod.POST,
        RequestMethod.PUT }, produces = "application/json" )
    public void addOrUpdateClickatellConfiguration( HttpServletRequest request, HttpServletResponse response )
        throws WebMessageException, IOException
    {
        if ( gatewayAdminService == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Gateway admin service is not available" ) );
        }

        SmsGatewayConfig payLoad = renderService.fromJson( request.getInputStream(), ClickatellGatewayConfig.class );

        renderService.toJson( response.getOutputStream(),
            gatewayAdminService.addOrUpdateGateway( payLoad, ClickatellGatewayConfig.class ) );
    }

    @PreAuthorize( "hasRole('ALL') or hasRole('F_MOBILE_SENDSMS')" )
    @RequestMapping( value = "/bulksms", method = { RequestMethod.POST,
        RequestMethod.PUT }, produces = "application/json" )
    public void addOrUpdatebulksmsConfiguration( HttpServletRequest request, HttpServletResponse response )
        throws WebMessageException, IOException
    {
        if ( gatewayAdminService == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Gateway admin service is not available" ) );
        }

        BulkSmsGatewayConfig payLoad = renderService.fromJson( request.getInputStream(), BulkSmsGatewayConfig.class );

        renderService.toJson( response.getOutputStream(),
            gatewayAdminService.addOrUpdateGateway( payLoad, BulkSmsGatewayConfig.class ) );
    }

    @PreAuthorize( "hasRole('ALL') or hasRole('F_MOBILE_SENDSMS')" )
    @RequestMapping( value = "/smpp", method = { RequestMethod.POST,
        RequestMethod.PUT }, produces = "application/json" )
    public void addOrUpdateSmppConfiguration( HttpServletRequest request, HttpServletResponse response )
        throws WebMessageException, IOException
    {
        if ( gatewayAdminService == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Gateway admin service is not available" ) );
        }

        SMPPGatewayConfig payLoad = renderService.fromJson( request.getInputStream(), SMPPGatewayConfig.class );

        renderService.toJson( response.getOutputStream(),
            gatewayAdminService.addOrUpdateGateway( payLoad, SMPPGatewayConfig.class ) );
    }

    @PreAuthorize( "hasRole('ALL') or hasRole('F_MOBILE_SENDSMS')" )
    @RequestMapping( value = "/generichttp", method = { RequestMethod.POST,
        RequestMethod.PUT }, produces = "application/json" )
    public void addOrUpdateGenericConfiguration( HttpServletRequest request, HttpServletResponse response )
        throws WebMessageException, IOException
    {
        if ( gatewayAdminService == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Gateway admin service is not available" ) );
        }

        GenericHttpGatewayConfig payLoad = renderService.fromJson( request.getInputStream(),
            GenericHttpGatewayConfig.class );

        renderService.toJson( response.getOutputStream(),
            gatewayAdminService.addOrUpdateGateway( payLoad, GenericHttpGatewayConfig.class ) );
    }

    @PreAuthorize( "hasRole('ALL') or hasRole('F_MOBILE_SENDSMS')" )
    @RequestMapping( value = "/modem", method = { RequestMethod.POST,
        RequestMethod.PUT }, produces = "application/json" )
    public void addOrUpdateModemConfiguration( HttpServletRequest request, HttpServletResponse response )
        throws WebMessageException, IOException
    {
        if ( gatewayAdminService == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Gateway admin service is not available" ) );
        }

        ModemGatewayConfig payLoad = renderService.fromJson( request.getInputStream(), ModemGatewayConfig.class );

        renderService.toJson( response.getOutputStream(),
            gatewayAdminService.addOrUpdateGateway( payLoad, ModemGatewayConfig.class ) );
    }

    @PreAuthorize( "hasRole('ALL') or hasRole('F_MOBILE_SENDSMS')" )
    @RequestMapping( value = "/default/{uid}", method = RequestMethod.PUT )
    public void setDefault( @PathVariable String uid, HttpServletRequest request, HttpServletResponse response )
        throws WebMessageException
    {
        if ( gatewayAdminService == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Gateway admin service is not available" ) );
        }

        String gateway = gatewayAdminService.setDefault( uid );

        if ( gateway == null )
        {
            webMessageService.send( WebMessageUtils.conflict( "No gateway against this id" ), response, request );
        }
        else
        {
            webMessageService.send( WebMessageUtils.ok( gateway + " is set to default" ), response, request );
        }
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @PreAuthorize( "hasRole('ALL') or hasRole('F_MOBILE_SENDSMS')" )
    @RequestMapping( value = "/{uid}", method = RequestMethod.DELETE )
    public void removeGateway( @PathVariable String uid, HttpServletRequest request, HttpServletResponse response )
        throws WebMessageException
    {
        if ( gatewayAdminService == null )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Gateway admin service is not available" ) );
        }

        if ( gatewayAdminService.removeGateway( uid ) )
        {
            webMessageService.send( WebMessageUtils.ok( "Gateway removed successfully" ), response, request );
        }
        else
        {
            webMessageService.send( WebMessageUtils.conflict( "No gateway against this id" ), response, request );
        }
    }
}
