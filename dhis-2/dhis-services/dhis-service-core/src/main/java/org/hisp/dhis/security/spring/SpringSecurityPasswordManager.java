package org.hisp.dhis.security.spring;

/*
 * Copyright (c) 2004-2014, University of Oslo
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

import org.hisp.dhis.security.PasswordManager;
import org.hisp.dhis.security.UsernameSaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: AcegiPasswordManager.java 3109 2007-03-19 17:05:21Z torgeilo $
 */
public class SpringSecurityPasswordManager
    implements PasswordManager
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private org.springframework.security.authentication.encoding.PasswordEncoder legacyPasswordEncoder;

    public void setPasswordEncoder( PasswordEncoder legacyPasswordEncoder )
    {
        this.legacyPasswordEncoder = legacyPasswordEncoder;
    }

    private org.springframework.security.authentication.encoding.PasswordEncoder tokenPasswordEncoder;

    public void setTokenPasswordEncoder( ShaPasswordEncoder tokenPasswordEncoder )
    {
        this.tokenPasswordEncoder = tokenPasswordEncoder;
    }

    private UsernameSaltSource usernameSaltSource;

    public void setUsernameSaltSource( UsernameSaltSource saltSource )
    {
        this.usernameSaltSource = saltSource;
    }

    // -------------------------------------------------------------------------
    // PasswordManager implementation
    // -------------------------------------------------------------------------

    public final String legacyEncodePassword( String username, String password )
    {
        return passwordEncoder.encodePassword( password, usernameSaltSource.getSalt( username ) );
    }

    @Override public String encodePassword( String password )
    {
        return null;
    }

    @Override public String encodeToken( String token, String salt )
    {
        return tokenPasswordEncoder.encodePassword( token, usernameSaltSource.getSalt( salt ) );
    }
}
