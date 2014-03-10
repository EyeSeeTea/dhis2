package org.hisp.dhis.api.utils.ops;

/*
 * Copyright (c) 2004-2013, University of Oslo
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
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public abstract class Op
{
    private String left;

    public boolean wantLeft()
    {
        return true;
    }

    public void setLeft( String left )
    {
        this.left = left;
    }

    public String getLeft()
    {
        return left;
    }

    @SuppressWarnings( "unchecked" )
    public <T> T getLeft( Class<?> klass )
    {
        if ( klass.isInstance( left ) )
        {
            return (T) left;
        }

        if ( Boolean.class.isAssignableFrom( klass ) )
        {
            try
            {
                return (T) Boolean.valueOf( left );
            }
            catch ( Exception ignored )
            {
            }
        }
        else if ( Integer.class.isAssignableFrom( klass ) )
        {
            try
            {
                return (T) Integer.valueOf( left );
            }
            catch ( Exception ignored )
            {
            }
        }
        else if ( Float.class.isAssignableFrom( klass ) )
        {
            try
            {
                return (T) Float.valueOf( left );
            }
            catch ( Exception ignored )
            {
            }
        }

        return null;
    }

    public abstract OpStatus evaluate( Object right );
}
