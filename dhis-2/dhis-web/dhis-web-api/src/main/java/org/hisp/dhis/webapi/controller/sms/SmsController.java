package org.hisp.dhis.webapi.controller.sms;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hisp.dhis.dxf2.render.RenderService;
import org.hisp.dhis.sms.SmsServiceException;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.sms.outbound.OutboundSmsStatus;
import org.hisp.dhis.sms.outbound.OutboundSmsTransportService;
import org.hisp.dhis.user.CurrentUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SmsController
{

    private static final Log log = LogFactory.getLog( SmsController.class );

  

    // 
    // Dependencies
    // 

    @Autowired
    private OutboundSmsTransportService transportService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    RenderService renderService;

    

    
    
    // -------------------------------------------------------------------------
    // GET
    // -------------------------------------------------------------------------

    @RequestMapping( value = SmsApiMapping.SENDSMS, method = RequestMethod.GET )
    public void sendSMSMessage(

        @RequestParam String recipient, @RequestParam String textMessage, HttpServletResponse response,
        HttpServletRequest request )
            throws IOException
    {

        int sms_id = 0;
        Set<String> recipients = new HashSet<String>();
        recipients.add( recipient );
        OutboundSms sms = new OutboundSms();
        sms.setRecipients( recipients );
        sms.setMessage( textMessage );
        sms.setStatus( OutboundSmsStatus.OUTBOUND );
        sms.setUser( currentUserService.getCurrentUser() );

        String gateWayId = transportService.getDefaultGateway();
        boolean isServiceEnabled = transportService.isEnabled();
        ServletOutputStream output = response.getOutputStream();

        try
        {
            if ( !isServiceEnabled || gateWayId == null || gateWayId.trim().length() <= 0 )
            {

                renderService.toJson( output, "Service Not Enabled Or Incorrect Gateway " );
                return;

            }

            String result = transportService.sendMessage( sms, gateWayId );

            if ( result.equals( "success" ) )
            {

                log.info( " SMS Sent Successfully " );
                renderService.toJson( output, result + " SMS ID :" + sms_id );
            }
            else
            {
                log.info( " SMS Sending Failed " );
                renderService.toJson( output, result + " SMS ID :" + sms_id );

            }

        }
        catch (SmsServiceException e)
        {
            log.warn( " SMSServiceException "+ sms + e.getMessage() );
        }
        catch ( Exception e )
        {
            log.warn( "Unable to send message: " + sms, e );

        }

    }
 

}
