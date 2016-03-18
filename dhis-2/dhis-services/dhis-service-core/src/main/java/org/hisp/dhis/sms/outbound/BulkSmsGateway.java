package org.hisp.dhis.sms.outbound;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hisp.dhis.sms.config.BulkSmsGatewayConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class BulkSmsGateway
{
    private static final Log log = LogFactory.getLog( BulkSmsGateway.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private RestTemplate restTemplate;

    public GatewayResponse send( OutboundSms sms, BulkSmsGatewayConfig bulkSmsConfiguration )
    {
        UriComponentsBuilder uriBuilder = buildBaseUrl( bulkSmsConfiguration, SubmissionType.SINGLE );
        uriBuilder.queryParam( "msisdn", getRecipients( sms.getRecipients() ) );
        uriBuilder.queryParam( "message", sms.getMessage() );

        return send( uriBuilder );
    }

    public GatewayResponse send( List<OutboundSms> smsBatch, BulkSmsGatewayConfig bulkSmsConfiguration )
    {
        UriComponentsBuilder uriBuilder = buildBaseUrl( bulkSmsConfiguration, SubmissionType.BATCH );
        uriBuilder.queryParam( "batch_data", builCsvUrl( smsBatch ) );

        return send( uriBuilder );
    }

    private GatewayResponse send( UriComponentsBuilder uriBuilder )
    {
        ResponseEntity<String> responseEntity = null;

        try
        {
            responseEntity = restTemplate.exchange( uriBuilder.build().encode( "ISO-8859-1" ).toUri(), HttpMethod.POST,
                null, String.class );
        }
        catch ( RestClientException ex )
        {
            log.error( "Error: " + ex.getMessage() );

            return GatewayResponse.RESULT_CODE_28;
        }
        catch ( UnsupportedEncodingException ex )
        {
            log.error( "Error: " + ex.getMessage() );

            return GatewayResponse.ENCODING_FAILURE;
        }

        log.info( "Response status code: " + responseEntity.getStatusCode() );
        
        return parseGatewayResponse( responseEntity.getBody() );
    }

    private String builCsvUrl( List<OutboundSms> smsBatch )
    {
        String csvData = "msisdn,message,";

        for ( OutboundSms sms : smsBatch )
        {
            csvData += getRecipients( sms.getRecipients() );
            csvData += "," + sms.getMessage();
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
            uriBuilder = UriComponentsBuilder
                .fromHttpUrl( "https://bulksms.vsms.net/eapi/submission/send_batch/1/1.0" );
        }

        uriBuilder.queryParam( "username", bulkSmsConfiguration.getUsername() ).queryParam( "password",
            bulkSmsConfiguration.getPassword() );

        return uriBuilder;
    }

    private GatewayResponse parseGatewayResponse( String response )
    {
        String[] responseCode = StringUtils.split( response, "|" );

        switch ( responseCode[0] )
        {
        case "0":
            return GatewayResponse.RESULT_CODE_0;
        case "1":
            return GatewayResponse.RESULT_CODE_1;
        case "22":
            return GatewayResponse.RESULT_CODE_22;
        case "23":
            return GatewayResponse.RESULT_CODE_23;
        case "24":
            return GatewayResponse.RESULT_CODE_24;
        case "25":
            return GatewayResponse.RESULT_CODE_25;
        case "26":
            return GatewayResponse.RESULT_CODE_26;
        case "27":
            return GatewayResponse.RESULT_CODE_27;
        case "40":
            return GatewayResponse.RESULT_CODE_40;
        default:
            return GatewayResponse.RESULT_CODE_22;
        }
    }

    private String getRecipients( Set<String> recipients )
    {
        return StringUtils.join( recipients, "," );
    }
}
