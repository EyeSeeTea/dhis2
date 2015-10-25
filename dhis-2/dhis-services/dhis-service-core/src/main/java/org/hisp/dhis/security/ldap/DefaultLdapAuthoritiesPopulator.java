package org.hisp.dhis.security.ldap;

import java.util.Collection;

import org.hisp.dhis.system.util.SecurityUtils;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.transaction.annotation.Transactional;

public class DefaultLdapAuthoritiesPopulator
    implements LdapAuthoritiesPopulator
{
    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public Collection<? extends GrantedAuthority> getGrantedAuthorities( DirContextOperations userData, String username )
    {
        UserCredentials credentials = userService.getUserCredentialsByUsername( username );
        
        if ( credentials == null )
        {
            return null;
        }
        
        return SecurityUtils.getGrantedAuthorities( credentials );
    }
}
