package org.hisp.dhis.webapi.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.email.EmailService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Halvdan Hoem Grelland <halvdanhg@gmail.com>
 */
@Controller
@RequestMapping( value = EmailController.RESOURCE_PATH )
public class EmailController
{
    private static final Log log = LogFactory.getLog( EmailController.class );

    public static final String RESOURCE_PATH = "/email";

    //--------------------------------------------------------------------------
    // Dependencies
    //--------------------------------------------------------------------------

    @Autowired
    private EmailService emailService;

    @Autowired
    private CurrentUserService currentUserService;

    @RequestMapping( value = "/sendTestEmail" , method = RequestMethod.POST, produces = ContextUtils.CONTENT_TYPE_TEXT )
    public @ResponseBody String sendTestEmail( HttpServletRequest request, HttpServletResponse response )
    {
        emailService.sendTestEmail( currentUserService.getCurrentUser( ) );

        response.setStatus( HttpServletResponse.SC_OK );
        return "A test email has been sent to you";
    }
}
