package org.hisp.dhis.sms.outbound;

/*
 * Copyright (c) 2004-2016, University of Oslo
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

import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.sms.config.BulkSmsGatewayConfig;
import org.hisp.dhis.sms.config.ClickatellGatewayConfig;
import org.hisp.dhis.sms.config.GatewayAdministratonService;
import org.hisp.dhis.sms.config.SmsGatewayConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultOutboundSmsTransportService
    implements OutboundSmsTransportService
{
    private static final Log log = LogFactory.getLog( DefaultOutboundSmsTransportService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OutboundSmsService outboundSmsService;

    public void setOutboundSmsService( OutboundSmsService outboundSmsService )
    {
        this.outboundSmsService = outboundSmsService;
    }

    @Autowired
    private GatewayAdministratonService gatewayAdminService;

    // -------------------------------------------------------------------------
    // OutboundSmsTransportService implementation
    // -------------------------------------------------------------------------

    @Override
    public GatewayResponse sendMessage( OutboundSms sms, String gatewayName )
    {
        SmsGatewayConfig gatewayConfiguration = gatewayAdminService.getGatewayConfigurationMap().get( gatewayName );

        String recipient = null;

        Set<String> recipients = sms.getRecipients();

        if ( recipients.size() == 0 )
        {
            log.warn( "Trying to send sms without recipients: " + sms );

            return GatewayResponse.DELIVERED;
        }
        else if ( recipients.size() == 1 )
        {
            recipient = recipients.iterator().next();
        }
        else
        {
            recipient = createTmpGroup( recipients );
        }

        if ( sms.getId() == 0 )
        {
            outboundSmsService.saveOutboundSms( sms );
        }
        else
        {
            outboundSmsService.updateOutboundSms( sms );
        }

        return GatewayResponse.DELIVERED;
    }

    @Override
    public GatewayResponse sendMessage( OutboundSms sms )
    {
        SmsGatewayConfig gatewayConfiguration = gatewayAdminService.getDefaultGateway();

        return GatewayResponse.PENDING;
    }

    @Override
    public GatewayResponse sendMessage( List<OutboundSms> smsBatch )
    {
        return null;
    }

    @Override
    public GatewayResponse sendMessage( List<OutboundSms> smsBatch, String gatewayId )
    {
        return null;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String createTmpGroup( Set<String> recipients )
    {
        String recipientList = "";

        for ( String recipient : recipients )
        {
            recipientList += recipient;
            recipientList += ",";
        }

        recipientList = recipientList.substring( 0, recipientList.length() - 1 );

        return recipientList;
    }
}
