package org.hisp.dhis.fileresource;

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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.hash.Hashing;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;

/**
 * TODO Implement haveUniqueNames == false
 * @author Halvdan Hoem Grelland
 */
public class FileResource
    extends BaseIdentifiableObject
{
    private String contentType;         // MIME type

    private String contentMD5;          // MD5 digest of the content

    private String storageKey;          // Key to fetch content from external storage

    private boolean assigned = false;   // Is this resource assigned (e.g. to a datavalue)?

    private FileResourceDomain domain;  // What is the domain of this fileResource? Might affect backend storage location or provider

    public FileResource()
    {
    }

    public FileResource( String name, String contentType, String contentMD5, String storageKey, FileResourceDomain domain )
    {
        this.name = name;
        this.contentType = contentType;
        this.contentMD5 = contentMD5;
        this.storageKey = storageKey;
        this.domain = domain;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getContentType()
    {
        return contentType;
    }

    public void setContentType( String contentType )
    {
        this.contentType = contentType;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getContentMD5()
    {
        return contentMD5;
    }

    public void setContentMD5( String contentMD5 )
    {
        this.contentMD5 = contentMD5;
    }

    public String getStorageKey()
    {
        return storageKey;
    }

    public void setStorageKey( String storageKey )
    {
        this.storageKey = storageKey;
    }

    public boolean isAssigned()
    {
        return assigned;
    }

    public void setAssigned( boolean assigned )
    {
        this.assigned = assigned;
    }

    public FileResourceDomain getDomain()
    {
        return domain;
    }

    public void setDomain( FileResourceDomain domain )
    {
        this.domain = domain;
    }

    private String autoGenerateName()
    {
        return Hashing.md5().newHasher().hash().toString();
    }
}
