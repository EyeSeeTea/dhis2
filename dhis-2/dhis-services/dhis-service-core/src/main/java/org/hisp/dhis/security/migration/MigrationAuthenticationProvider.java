package org.hisp.dhis.security.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

/**
 * TODO document the purpose of this class
 * @author Halvdan Hoem Grelland
 */
public class MigrationAuthenticationProvider
    extends DaoAuthenticationProvider
{
    private static final Log log = LogFactory.getLog( MigrationAuthenticationProvider.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    private MigrationPasswordManager passwordManager;

    public void setPasswordManager( MigrationPasswordManager passwordManager )
    {
        this.passwordManager = passwordManager;
    }

    // -------------------------------------------------------------------------
    // Pre-auth check-and-switch for legacy password hash match
    // -------------------------------------------------------------------------

    @Override
    protected void additionalAuthenticationChecks( UserDetails userDetails,
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken )
        throws AuthenticationException
    {
        String password = (String) usernamePasswordAuthenticationToken.getCredentials();
        String username = userDetails.getUsername();

        // If legacyHash(password, username) matches stored hash, re-hash password with current method and switch with stored hash
        if( passwordManager.legacyMatches( userDetails.getPassword(), password, username ) )
        {
            UserCredentials userCredentials = userService.getUserCredentialsByUsername( username );

            if ( userCredentials != null )
            {
                userCredentials.setPassword( passwordManager.encodePassword( password ) );
                userCredentials.setPasswordLastUpdated( new Date() );
                userService.updateUser( userCredentials.getUser() );

                log.info( "User " + userCredentials.getUsername() + " was migrated from " + passwordManager.getLegacyPasswordEncoderClassName() +
                    " to " + passwordManager.getPasswordEncoderClassName() + " based password hashing on login." );

                userDetails = getUserDetailsService().loadUserByUsername( username ); // refresh userDetails to use new password on auth
            }
        }
        super.additionalAuthenticationChecks( userDetails, usernamePasswordAuthenticationToken );
    }
}
