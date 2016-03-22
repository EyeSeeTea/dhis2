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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hisp.dhis.sms.config.BulkSmsGatewayConfig;
import org.hisp.dhis.sms.config.SmsGatewayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Zubair <rajazubair.asghar@gmail.com>
 */

public class BulkSmsGateway
    implements SmsGateway
{
    private static final Log log = LogFactory.getLog( BulkSmsGateway.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private RestTemplate restTemplate;

    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------

    public GatewayResponse send( OutboundSms sms, SmsGatewayConfig config )
    {
        BulkSmsGatewayConfig bulkSmsConfig = (BulkSmsGatewayConfig) config;

        UriComponentsBuilder uriBuilder = buildBaseUrl( bulkSmsConfig, SubmissionType.SINGLE );
        uriBuilder.queryParam( "msisdn", getRecipients( sms.getRecipients() ) );
        uriBuilder.queryParam( "message", sms.getMessage() );

        return send( uriBuilder );
    }

    public GatewayResponse send( List<OutboundSms> smsBatch, SmsGatewayConfig config )
    {
        BulkSmsGatewayConfig bulkSmsConfig = (BulkSmsGatewayConfig) config;

        UriComponentsBuilder uriBuilder = buildBaseUrl( bulkSmsConfig, SubmissionType.BATCH );
        uriBuilder.queryParam( "batch_data", builCsvUrl( smsBatch ) );

        return send( uriBuilder );
    }

    @Override
    public boolean accept( SmsGatewayConfig gatewayConfig )
    {
        return gatewayConfig != null && gatewayConfig instanceof BulkSmsGatewayConfig;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private GatewayResponse send( UriComponentsBuilder uriBuilder )
    {
        ResponseEntity<String> responseEntity = null;

        try
        {
            responseEntity = restTemplate.exchange( uriBuilder.build().encode( "ISO-8859-1" ).toUri(), HttpMethod.POST,
                null, String.class );
        }
        catch ( HttpClientErrorException ex )
        {
            log.error( "Error: " + ex.getMessage() );
        }
        catch ( HttpServerErrorException ex )
        {
            log.error( "Error: " + ex.getMessage() );
        }
        catch ( Exception ex )
        {
            log.error( "Error: " + ex.getMessage() );
        }

        return responseHandler( responseEntity.getBody() );
    }

    private String builCsvUrl( List<OutboundSms> smsBatch )
    {
        String csvData = "msisdn,message\n";

        for ( OutboundSms sms : smsBatch )
        {
            csvData += getRecipients( sms.getRecipients() );
            csvData += "," + sms.getMessage() + "\n";
        }
        return csvData;
    }

    private UriComponentsBuilder buildBaseUrl( BulkSmsGatewayConfig bulkSmsConfiguration, SubmissionType type )
    {
        UriComponentsBuilder uriBuilder = null;

        if ( type.equals( SubmissionType.SINGLE ) )
        {
            uriBuilder = UriComponentsBuilder.fromHttpUrl( bulkSmsConfiguration.getUrlTemplate() );
        }
        else if ( type.equals( SubmissionType.BATCH ) )
        {
            uriBuilder = UriComponentsBuilder.fromHttpUrl( bulkSmsConfiguration.getUrlTemplateForBatchSms() );
        }

        uriBuilder.queryParam( "username", bulkSmsConfiguration.getUsername() ).queryParam( "password",
            bulkSmsConfiguration.getPassword() );

        return uriBuilder;
    }

    private GatewayResponse responseHandler( String response )
    {
        return BULKSMS_GATEWAY_RESPONSE_MAP.get( StringUtils.split( response, "|" )[0] );
    }

    private String getRecipients( Set<String> recipients )
    {
        return StringUtils.join( recipients, "," );
    }
}
