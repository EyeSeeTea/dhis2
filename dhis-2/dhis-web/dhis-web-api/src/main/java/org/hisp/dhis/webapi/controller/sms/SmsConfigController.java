package org.hisp.dhis.webapi.controller.sms;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dxf2.render.RenderService;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.sms.SmsServiceException;
import org.hisp.dhis.sms.config.SMSGatewayStatus;
import org.hisp.dhis.sms.config.SmsConfigurationManager;
import org.hisp.dhis.sms.outbound.OutboundSmsTransportService;
import org.hisp.dhis.sms.outbound.SMSServiceStatus;
import org.hisp.dhis.webapi.utils.WebMessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class SmsConfigController
{
    
    
    private static final Log log = LogFactory.getLog( SmsConfigController.class );

    
    

    ///////////////////////////////////////////////////////// Dependencies
    ///////////////////////////////////////////////////////// //////////////////////////////////////////////////////////////

    @Autowired
    private OutboundSmsTransportService transportService;
    

    @Autowired
    private SmsConfigurationManager smsConfigManager;

    @Autowired
    private RenderService renderService;
    
    

    /////////////////////////////////////////////////////////////////// GET
    /////////////////////////////////////////////////////////////////// ///////////////////////////////////////////////////////

    // Get Sms Service Status

    @RequestMapping( value = SmsApiMapping.GETSMSSERICESTATUS, method = RequestMethod.GET )
    public void getSMSSServiceStatus( HttpServletRequest request, HttpServletResponse response )
        throws IOException
    {

        if ( transportService == null )
        {
            renderService.toJson( response.getOutputStream(), " Service Status : No Service Running" );
            return;

        }

        String smsServiceStatus = transportService.getServiceStatus();
        renderService.toJson( response.getOutputStream(), " Service Status : " + smsServiceStatus );

    }

    // Get Gateway Status

    @RequestMapping( value = SmsApiMapping.GETSMSGATEWAYSTATUS, method = RequestMethod.GET )
    public void getSMSGatewayStatus( 
        
        HttpServletRequest request,
        HttpServletResponse response )
            throws IOException
    {

        // Need to implemented in DefaultOutBoundTransportService

        
       if ( transportService.getGatewayStatus() == SMSGatewayStatus.UNDEFINED)
       {
           renderService.toJson( response.getOutputStream(), " No Default Gateway Set ");
           return;
           
       }
     
           renderService.toJson( response.getOutputStream(), " Gateway :"+ transportService.getDefaultGateway()+" :"+ 
               transportService.getGatewayStatus().toString());
  

    }

    // Start SMS Service

    @RequestMapping( value = SmsApiMapping.STARTSMSSERVICE, method = RequestMethod.GET )
    public void startSMSService(

        HttpServletRequest request, HttpServletResponse response )
            throws IOException
    {

        if ( transportService.getServiceStatusEnum() == SMSServiceStatus.STARTED
            || transportService.getServiceStatusEnum() == SMSServiceStatus.STARTING

        )
        {
            renderService.toJson( response.getOutputStream(), "SMS Service Already Started " );
            return;
        }

        transportService.startService();
        renderService.toJson( response.getOutputStream(), "SMS Service Started " );

    }

    // Stop SmS Service

    @RequestMapping( value = SmsApiMapping.STOPSMSSERVICE, method = RequestMethod.GET )
    public void stopSMSService(

        HttpServletResponse response, HttpServletRequest resquest )
            throws IOException
    {

        if ( transportService.getServiceStatusEnum() == SMSServiceStatus.STOPPED
            || transportService.getServiceStatusEnum() == SMSServiceStatus.STOPPING )
        {
            renderService.toJson( response.getOutputStream(), "SMS Service Already Stopped " );
            return;
        }

        transportService.stopService();
        renderService.toJson( response.getOutputStream(), "SMS Service Stopped " );
    }

    @RequestMapping( value = SmsApiMapping.GETDEFAULTSMSGATEWAY, method = RequestMethod.GET )
    public void getDefaultSMSGateway(

        HttpServletRequest request, HttpServletResponse response

    )
        throws IOException
    {
        String defaultGateway = transportService.getDefaultGateway();
        if ( defaultGateway == null || defaultGateway.length() <= 0 )
        {
            renderService.toJson( response.getOutputStream(), "No Default Gateway Set" );
            return;
        }

        renderService.toJson( response.getOutputStream(), " Default GatewayId : " + defaultGateway );

    }

  

    
    
    ///////////////////////////////////////////////////////////// POST
    ///////////////////////////////////////////////////////////// ////////////////////////////////////////////////////////
    
    
    
    
    @RequestMapping( value = SmsApiMapping.SETDEFAULTSMSGATEWAY, method = RequestMethod.GET )
    public void setDefaultSMSGateway(

        @RequestParam String gatewayId, 
        HttpServletRequest request,
        HttpServletResponse response ) throws IOException 
    {

       
        if( !smsConfigManager.gatewayExists( gatewayId ))
        {
            renderService.toJson( response.getOutputStream(), gatewayId + " Does not Exist ");
            return;
        }
             
        if (smsConfigManager.setDefaultSMSGateway( gatewayId ))
        {
            renderService.toJson( response.getOutputStream(), gatewayId + " Set to default ");
            log.info( gatewayId +" set to Default " );
            
        }else
        {
            renderService.toJson( response.getOutputStream(), gatewayId + " Not  set to default ");
            log.info( gatewayId +" NOT set to Default " );
        }
        
    }
    
    
    @RequestMapping( value = SmsApiMapping.ADDSMSGATEWAY, method = RequestMethod.POST)
    public void addSMSGateway(

       
        HttpServletRequest request,
        HttpServletResponse response )
    {
        

        // either through query parameters or through JSON/XML
        
    }

    
    @RequestMapping( value = SmsApiMapping.REMOVESMSGATEWAY, method = RequestMethod.POST)
    public void removeSMSGateway(

        @RequestParam String gatewayId, 
        HttpServletRequest request,
        HttpServletResponse response )
    {

        // either through query parameters or through JSON/XML
        
    }
    
    
    @RequestMapping( value = SmsApiMapping.REMOVEALLSMSGATEWAY, method = RequestMethod.POST)
    public void removeAllSMSGateway(

        HttpServletRequest request,
        HttpServletResponse response )
    {

        // either through query parameters or through JSON/XML
        
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
