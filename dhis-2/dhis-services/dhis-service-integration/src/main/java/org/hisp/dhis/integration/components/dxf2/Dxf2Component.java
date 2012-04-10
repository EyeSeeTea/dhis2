package org.hisp.dhis.integration.components.dxf2;

/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

import java.util.Map;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

/**
 * A DHIS2 specific camel component for creating dxf2 endpoints
 * 
 * Two forms of uri are supported for the endpoints:
 * 1.  uri="dxf2:metadata?<options>" creates a metadata endpoint for importing metadata 
 * 2.  uri="dxf2:data?<options>" creates a datavalueset endpoint for importing data
 * 
 * See the respective endpoint classes for details of the options supported on each
 * 
 * @author bobj
 */
public class Dxf2Component extends DefaultComponent
{

    public static final String DATA = "data";

    public static final String METADATA = "metadata";

    @Override
    protected Endpoint createEndpoint( String uri, String remaining, Map<String, Object> parameters ) throws Exception
    {
        System.out.println( "URI: " + uri + ", remaining: " + remaining );
        System.out.println( parameters );
        
        if ( !remaining.equals( DATA ) && !remaining.equals( METADATA ) ) 
        {
            throw new UnsupportedOperationException( "Invalid dxf2 uri part " + remaining);
        }
        
        Endpoint endpoint = remaining.equals( DATA ) ? new Dxf2DataEndpoint( uri, this ) : new Dxf2MetaDataEndpoint( uri, this );
  
        setProperties( endpoint, parameters );
        return endpoint;
    }
}
