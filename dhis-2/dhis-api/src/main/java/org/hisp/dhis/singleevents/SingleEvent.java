/*
 * Copyright (c) 2004-2009, University of Oslo
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

package org.hisp.dhis.singleevents;
import java.io.Serializable;

/**
 * @author Group1 Fall 2011
 * @version $Id$
 */
public class SingleEvent implements Serializable
{
    /**
	 * Determines if a de-serialized file is compatible with this class.
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
    private String eventName;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public SingleEvent()
    {
    }
    
    public SingleEvent( String name )
    {
    	this.eventName = name;
    }
    
    // -------------------------------------------------------------------------
    // Equals and hashcode
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return eventName.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof SingleEvent) )
        {
            return false;
        }

        final SingleEvent other = (SingleEvent) o;

        return eventName.equals( other.getName() );
    }
    
    // -------------------------------------------------------------------------
    // Setters and getters
    // -------------------------------------------------------------------------
 
    public Integer getId()
    {
    	return id;
    }
    
    public void setId( Integer id)
    {
    	this.id = id;
    }
    
    public String getName()
    {
    	return eventName;
    }
    
    public void setName( String name )
    {
    	this.eventName = name;
    }
}
