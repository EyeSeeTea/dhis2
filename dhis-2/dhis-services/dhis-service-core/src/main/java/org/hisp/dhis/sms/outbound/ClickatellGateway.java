package org.hisp.dhis.sms.outbound;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

public class ClickatellGateway
{

    private static final Log log = LogFactory.getLog( ClickatellGateway.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private RestTemplate restTemplate;

    public GatewayResponse send( OutboundSms sms )
    {
        return null;
    }

    public GatewayResponse send( List<OutboundSms> smsBatch )
    {
        return null;
    }
}
