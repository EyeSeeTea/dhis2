package org.hisp.dhis.security.migration;

import org.hisp.dhis.security.PasswordManager;

/**
 * TODO document the purpose of this class
 *
 * Some useful debugging stuff:
 * Reset admin to md5(district) for debugging:
 * update users set password = '48e8f1207baef1ef7fe478a57d19f2e5' where username = 'admin'
 * @author Halvdan Hoem Grelland
 */
public interface MigrationPasswordManager
    extends PasswordManager
{
    /**
     * Cryptographically hash a password using a legacy method.
     * Useful for access to the former (legacy) hash method when implementing migration to a new method.
     * @param password the password to encode.
     * @param username the username (used for seeding salt generation).
     * @return the encoded (hashed) password.
     */
    public String legacyEncodePassword( String password, String username );

    /**
     * Determines whether the supplied password equals the encoded password or not.
     * Uses the legacy hashing method to do so and is useful in implementing migration to a new method.
     * @param encodedPassword the encoded password.
     * @param password the password to match.
     * @param username the username (used for salt generation).
     * @return true if the password matches the encoded password, false otherwise.
     */
    public boolean legacyMatches( String encodedPassword, String password, String username );

    /**
     * Return the class name of the legacy password encoder.
     * @return the name of the legacy password encoder class.
     */
    public String getLegacyPasswordEncoderClassName();
}
