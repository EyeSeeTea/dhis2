package org.hisp.dhis.security;

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

/**
 * @author Torgeir Lorange Ostby
 * @author Halvdan Hoem Grelland
 */
public interface PasswordManager
{
    String ID = PasswordManager.class.getName();

    /**
     * Cryptographically hash a password.
     * @param password password to encode.
     * @return the hashed password.
     */
    String encodePassword( String password );

    String legacyEncodePassword( String username, String password );

    /**
     * Cryptographically encode a (restore or invite) token.
     * The implementation must employ a hash function of satisfactory security level
     * to perform the hashing such as SHA-2-family functions or better.
     *
     * Depending on the implementation, the supplied salt can be used directly or
     * as a seed for computing a different value. In any case, the salt must be known
     * in order to re-compute the hash on token validation at a later time.
     *
     * @param token The token to encode.
     * @param salt The salt to use.
     * @return the hashed token.
     */
    String encodeToken( String token, String salt );
}
