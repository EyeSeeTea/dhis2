package org.hisp.dhis.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import java.util.Date;

/**
 * @author Halvdan Hoem Grelland
 */
public class HashMethodMigrationPasswordEncoder
    implements PasswordEncoder
{
    private static final Log log = LogFactory.getLog( HashMethodMigrationPasswordEncoder.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private org.springframework.security.authentication.encoding.PasswordEncoder legacyPasswordEncoder;

    public void setLegacyPasswordEncoder( org.springframework.security.authentication.encoding.PasswordEncoder legacyPasswordEncoder )
    {
        this.legacyPasswordEncoder = legacyPasswordEncoder;
    }

    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public void setPasswordEncoder( org.springframework.security.crypto.password.PasswordEncoder passwordEncoder )
    {
        this.passwordEncoder = passwordEncoder;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    // -------------------------------------------------------------------------
    // PasswordEncoder implementation
    // -------------------------------------------------------------------------

    @Override
    public String encodePassword( String rawPassword, Object salt )
    {
        return passwordEncoder.encode( rawPassword );
    }

    @Override
    public boolean isPasswordValid( String encodedPassword, String rawPassword, Object salt )
    {
        if( legacyPasswordEncoder.isPasswordValid( encodedPassword, rawPassword, salt ) )
        {
            migrateUserPasswordHash( encodedPassword, rawPassword );

            return true;
        }

        return passwordEncoder.matches( rawPassword, encodedPassword );
    }

    // -------------------------------------------------------------------------
    // Hash method migration
    // ------------------------------------------------------------------------

    /**
     * Switches the stored legacy password hash for a user to the new hash method.
     * @param legacyHash the legacy hash of the password
     * @param rawPassword the raw password (not encoded)
     */
    private void migrateUserPasswordHash( String legacyHash, String rawPassword )
    {
        // Reset admin to md5:district for debugging
        // update users set password = '48e8f1207baef1ef7fe478a57d19f2e5' where username = 'admin'

        final String sql = "SELECT userid from users WHERE password = ?";
        Integer userId = jdbcTemplate.queryForObject( sql, new Object[] { legacyHash }, Integer.class );

        User user = userService.getUser( userId );
        user.getUserCredentials().setPassword( passwordEncoder.encode( rawPassword ) );
        user.getUserCredentials().setPasswordLastUpdated( new Date() );
        userService.updateUser( user );

        log.info( "User " + user.getUsername() + " was migrated from "+ legacyPasswordEncoder.getClass().getSimpleName() +
           " to " + passwordEncoder.getClass().getSimpleName() + " based password hashing on login" );
    }
}
