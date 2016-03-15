package org.hisp.dhis.sms.outbound;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.sms.config.BulkSmsGatewayConfig;
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

        boolean gatewayResponse = false;

        if ( gatewayConfiguration instanceof BulkSmsGatewayConfig )
        {
            BulkSmsGatewayConfig bulkSmsConfiguration = (BulkSmsGatewayConfig) gatewayConfiguration;
            gatewayResponse = sendThroughBulkSms( sms, bulkSmsConfiguration );
        }

        if ( gatewayResponse )
        {
            sms.setStatus( OutboundSmsStatus.SENT );
            saveMessage( sms );

            log.info( "Following Message Sent:" + sms );

            return GatewayResponse.SENT;

        }
        else
        {
            sms.setStatus( OutboundSmsStatus.ERROR );
            saveMessage( sms );

            log.info( "Following Message Failed:" + sms );

            return GatewayResponse.FAILED;
        }
    }

    @Override
    public GatewayResponse sendMessage( OutboundSms sms )
    {
        SmsGatewayConfig gatewayConfiguration = gatewayAdminService.getDefaultGateway();

        return sendMessage( sms, gatewayConfiguration.getName() );
    }

    @Override
    public GatewayResponse sendMessage( List<OutboundSms> smsBatch )
    {
        return null;
    }

    @Override
    public GatewayResponse sendMessage( String message, String recipient )
    {
        OutboundSms sms = new OutboundSms( message, recipient );
        
        return sendMessage( sms );
    }

    @Override
    public GatewayResponse sendMessage( List<OutboundSms> smsBatch, String gatewayName )
    {
        return null;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private boolean sendThroughBulkSms( OutboundSms sms, BulkSmsGatewayConfig bulkSmsConfiguration )
    {
        String data = "";
        try
        {
            data += "username=" + URLEncoder.encode( bulkSmsConfiguration.getUsername(), "ISO-8859-1" );
            data += "&password=" + URLEncoder.encode( bulkSmsConfiguration.getPassword(), "ISO-8859-1" );
            data += "&message=" + URLEncoder.encode( sms.getMessage(), "ISO-8859-1" );
            data += "&want_report=1";
            data += "&msisdn=" + getRecipients( sms.getRecipients() );
            
            URL url = new URL( "https://bulksms.vsms.net/eapi/submission/send_sms/2/2.0" );

            URLConnection conn = url.openConnection();
            conn.setDoOutput( true );

            OutputStreamWriter writer = new OutputStreamWriter( conn.getOutputStream() );
            writer.write( data );
            writer.flush();

            BufferedReader reader = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
            String line;

            while ( (line = reader.readLine()) != null )
            {
                System.out.println( line );
            }

            HttpURLConnection httpConnection = (HttpURLConnection) conn;

            if ( httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK )
            {
                return true;
            }

            return false;
        }
        catch ( Exception e )
        {
            log.error( "Error:" + e.getMessage() );
            return false;
        }
    }

    private String getRecipients( Set<String> recipients )
    {
         return StringUtils.join( recipients, "," );
    }

    private void saveMessage( OutboundSms sms )
    {
        if ( sms.getId() == 0 )
        {
            outboundSmsService.saveOutboundSms( sms );
        }
        else
        {
            outboundSmsService.updateOutboundSms( sms );
        }
    }
}
