package org.hisp.dhis.email;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.message.MessageSender;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Halvdan Hoem Grelland
 */
@Transactional
public class DefaultEmailService
    implements EmailService
{
    private static final Log log = LogFactory.getLog( DefaultEmailService.class );

    private static final String TEST_EMAIL_SUBJECT = "Test message from DHIS 2";
    private static final String TEST_EMAIL_TEXT = "This is a test message from DHIS 2";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MessageSender emailMessageSender;

    public void setEmailMessageSender(MessageSender emailMessageSender)
    {
        this.emailMessageSender = emailMessageSender;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService(CurrentUserService currentUserService)
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // EmailService implementation
    // -------------------------------------------------------------------------

    @Override
    public void sendEmail( String subject, String text, User sender, User recipient, boolean forceSend )
    {
        Set<User> recipients = new HashSet<User>( );
        recipients.add( recipient );

        emailMessageSender.sendMessage( subject, text, sender, new HashSet<User>( recipients ), forceSend );
    }

    @Override
    public void sendEmail( String subject, String text, User sender, Set<User> recipients, boolean forceSend )
    {
        emailMessageSender.sendMessage( subject, text, sender, new HashSet<User>( recipients ), forceSend );
    }

    @Override
    public void sendTestEmail( User recipient )
    {
        sendEmail(TEST_EMAIL_SUBJECT, TEST_EMAIL_TEXT, null, recipient, true);
    }
}
