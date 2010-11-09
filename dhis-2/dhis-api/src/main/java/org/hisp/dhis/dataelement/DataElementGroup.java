package org.hisp.dhis.dataelement;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dimension.DimensionOption;
import org.hisp.dhis.dimension.DimensionOptionElement;
import org.hisp.dhis.dimension.DimensionType;

/**
 * @author Kristian Nordal
 * @version $Id: DataElementGroup.java 5540 2008-08-19 10:47:07Z larshelg $
 */
public class DataElementGroup
    extends IdentifiableObject implements DimensionOption
{
    private Set<DataElement> members = new HashSet<DataElement>();

    private DataElementGroupSet groupSet;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataElementGroup()
    {
    }

    public DataElementGroup( String name )
    {
        this.name = name;
    }

    // -------------------------------------------------------------------------
    // Dimension
    // -------------------------------------------------------------------------

    public DimensionType getDimensionType()
    {
        return DimensionType.DATAELEMENTGROUPSET;
    }
    
    public Set<? extends DimensionOptionElement> getDimensionOptionElements()
    {
        return members;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
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

        if ( !(o instanceof DataElementGroup) )
        {
            return false;
        }

        final DataElementGroup other = (DataElementGroup) o;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public Set<DataElement> getMembers()
    {
        return members;
    }

    public void setMembers( Set<DataElement> members )
    {
        this.members = members;
    }

    public DataElementGroupSet getGroupSet()
    {
        return groupSet;
    }

    public void setGroupSet( DataElementGroupSet groupSet )
    {
        this.groupSet = groupSet;
    }
}
