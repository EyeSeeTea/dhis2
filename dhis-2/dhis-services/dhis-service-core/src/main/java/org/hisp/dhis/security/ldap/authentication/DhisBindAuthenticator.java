package org.hisp.dhis.security.ldap.authentication;

/*
 * Copyright (c) 2004-2015, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.hisp.dhis.external.conf.DhisConfigurationProvider;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.authentication.BindAuthenticator;

/**
 * Authenticator which checks whether LDAP authentication is configured. If not,
 * the authentication will be aborted, otherwise authentication is delegated to
 * Spring BindAuthenticator.
 * 
 * @author Lars Helge Overland
 */
public class DhisBindAuthenticator
    extends BindAuthenticator
{
    @Autowired
    private DhisConfigurationProvider configurationProvider;
    
    @Autowired
    private UserService userService;
    
    public DhisBindAuthenticator( BaseLdapPathContextSource contextSource )
    {
        super( contextSource );
    }

    @Override
    public DirContextOperations authenticate( Authentication authentication )
    {
        System.out.println( "AUTH " + authentication );
        System.out.println( "AT NAME " + authentication.getName() );
        
        System.out.println( "A princ " + authentication.getPrincipal() );
        System.out.println( "A princ " + authentication.getPrincipal().getClass() );
        System.out.println( "A cred " + authentication.getCredentials() );
        System.out.println( "A cred " + authentication.getCredentials().getClass() );
        
        boolean ldapConf = configurationProvider.isLdapConfigured();
        
        if ( !ldapConf )
        {
            throw new BadCredentialsException( "LDAP authentication is not configured" );
        }

        UserCredentials userCredentials = userService.getUserCredentialsByUsername( authentication.getName() );
        
        System.out.println( "CURR USER " + userCredentials );
        
        if ( userCredentials == null )
        {
            throw new BadCredentialsException( "Bad user credentials" );
        }
        
        if ( userCredentials.hasLdapId() )
        {
            authentication = new UsernamePasswordAuthenticationToken( userCredentials.getLdapId(), authentication.getCredentials() );
            System.out.println( "LDAP AUTH " + authentication );
            System.out.println( "LDAP A NAME " + authentication.getName() );
            System.out.println( "LDAP PW "  + (String)authentication.getCredentials() );
        }
        
        return super.authenticate( authentication );
    }
}
