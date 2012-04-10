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

import java.io.InputStream;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dxf2.datavalueset.DefaultDataValueSetService;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author bobj
 */
public class Dxf2DataProducer extends DefaultProducer
{
    @Autowired
    private DefaultDataValueSetService dataValueSetService;
    
    private static final transient Log log = LogFactory.getLog( Dxf2DataProducer.class );

    private Dxf2DataEndpoint endpoint;

    public Dxf2DataProducer( Dxf2DataEndpoint endpoint )
    {
        super( endpoint );
        this.endpoint = endpoint;
    }

    @Override
    public void process( Exchange exchange ) throws Exception
    {
        log.info( this.getEndpoint().getEndpointUri() + " : " + exchange.getIn().getBody() );
        
        ImportSummary summary = dataValueSetService.saveDataValueSet( (InputStream)exchange.getIn().getBody(), 
            endpoint.getDataElementIdScheme(), endpoint.getOrgUnitIdScheme(),
            endpoint.getDryRun(), endpoint.getImportStrategy() );
        
        exchange.getOut().setBody(JacksonUtils.toXmlAsString( summary ) );
    }
}
