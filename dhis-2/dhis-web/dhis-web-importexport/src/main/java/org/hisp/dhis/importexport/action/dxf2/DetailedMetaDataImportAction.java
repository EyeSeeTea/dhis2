package org.hisp.dhis.importexport.action.dxf2;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.Action;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.system.util.StreamUtils;

import java.io.*;

/**
 * @author Ovidiu Rosu <rosu.ovi@gmail.com>
 */
public class DetailedMetaDataImportAction
        implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private static final Log log = LogFactory.getLog( DetailedMetaDataImportAction.class );

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private File upload;

    public void setUpload( File upload )
    {
        this.upload = upload;
    }

    private String metaDataJson;

    public String getMetaDataJson()
    {
        return metaDataJson;
    }

    public void setMetaDataJson( String metaDataJson )
    {
        this.metaDataJson = metaDataJson;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute() throws Exception
    {
        if ( upload != null )
        {
            InputStream in = new FileInputStream( upload );
            in = StreamUtils.wrapAndCheckCompressionFormat( in );
            MetaData metaData;

            try
            {
                metaData = JacksonUtils.fromXml( in, MetaData.class );
                metaDataJson = JacksonUtils.toJsonAsString( metaData );
            } catch ( IOException ignored )
            {
                try
                {
                    ObjectMapper objectMapper = new ObjectMapper();
                    metaData = objectMapper.readValue( upload, MetaData.class );

                    metaDataJson = JacksonUtils.toJsonAsString( metaData );
                } catch ( IOException ex )
                {
                    log.error( "(IOException) Unable to parse meta-data while reading input stream", ex );
                }
            }
        } else
        {
            metaDataJson = "{}";
        }

        return SUCCESS;
    }
}
