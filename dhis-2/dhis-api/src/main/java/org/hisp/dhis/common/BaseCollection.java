package org.hisp.dhis.common;

/*
 * Copyright (c) 2004-2011, University of Oslo
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "collection", namespace = Dxf2Namespace.NAMESPACE )
public class BaseCollection
    implements LinkableObject
{
    private Pager pager;

    private LinkableObject linkableObject;

    public BaseCollection()
    {
        linkableObject = new BaseLinkableObject();
    }

    //-------------------------------------------------------------------------------------
    // Dependencies
    //-------------------------------------------------------------------------------------

    @JsonProperty
    public Pager getPager()
    {
        return pager;
    }

    public void setPager( Pager pager )
    {
        this.pager = pager;
    }

    @JsonProperty
    public LinkableObject getLinkableObject()
    {
        return linkableObject;
    }

    public void setLinkableObject( LinkableObject linkableObject )
    {
        this.linkableObject = linkableObject;
    }

    //-------------------------------------------------------------------------------------
    // Serializable fields
    //-------------------------------------------------------------------------------------

    @JsonProperty
    @JacksonXmlProperty( isAttribute = true )
    public String getLink()
    {
        if ( linkableObject == null )
        {
            return null;
        }

        return linkableObject.getLink();
    }

    /**
     * Set link for collection. This will be replaced for 2.7 with a real linkableObject.
     *
     * @param link
     */
    public void setLink( String link )
    {
        if ( linkableObject != null )
        {
            linkableObject.setLink( link );
        }
    }

    /**
     * Get current page.
     *
     * @return Current page
     */
    @JsonProperty
    @JacksonXmlProperty( isAttribute = true )
    public Integer getPage()
    {
        if ( pager == null )
        {
            return null;
        }

        return pager.getPage();
    }

    /**
     * Total number of items.
     *
     * @return number of items in collection
     */
    @JsonProperty
    @JacksonXmlProperty( isAttribute = true )
    public Integer getTotal()
    {
        if ( pager == null )
        {
            return null;
        }

        return pager.getTotal();
    }

    /**
     * How many items per page.
     *
     * @return items per page
     */
    @JsonProperty
    @JacksonXmlProperty( isAttribute = true )
    public Integer getPageSize()
    {
        if ( pager == null )
        {
            return null;
        }

        return pager.getPageSize();
    }

    /**
     * How many pages in total.
     *
     * @return total page count
     */
    @XmlAttribute
    public Integer getPageCount()
    {
        if ( pager == null )
        {
            return null;
        }

        return pager.getPageCount();
    }

    @JsonProperty
    @JacksonXmlProperty( isAttribute = true )
    public String getNextPage()
    {
        if ( pager == null )
        {
            return null;
        }

        return pager.getNextPage();
    }

    @JsonProperty
    @JacksonXmlProperty( isAttribute = true )
    public String getPrevPage()
    {
        if ( pager == null )
        {
            return null;
        }

        return pager.getPrevPage();
    }
}
