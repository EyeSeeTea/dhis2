package org.hisp.dhis.email;

import org.hisp.dhis.user.User;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by halvdan on 11/06/14.
 */
public interface EmailService
{
    public void sendEmail( String subject, String text, User sender, User recipient, boolean forceSend );

    public void sendEmail( String subject, String text, User sender, Set<User> recipients, boolean forceSend);

    public void sendTestEmail( User recipient );
}
