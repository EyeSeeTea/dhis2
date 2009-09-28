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

package org.hisp.dhis.household;

import java.util.Set;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class HouseHold
{
    public static final int HOUSE_NUMBER_INDEX_LENGTH = 5;

    public static final String HOUSE_NUMBER_FIRST_INDEX = ".00000";

    public static final String SEPARATOR = ",";

    private int id;

    private String houseNumber;

    private OrganisationUnit organisationUnit;

    private Set<Patient> members;

    private String address;

    private String landMark;

    private int visitingOrder;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public HouseHold()
    {
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + houseNumber.hashCode();
        result = result * prime + organisationUnit.hashCode();

        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }

        if ( obj == null )
        {
            return false;
        }

        if ( !(obj instanceof HouseHold) )
        {
            return false;
        }

        final HouseHold other = (HouseHold) obj;

        return houseNumber.equals( other.getHouseNumber() ) && organisationUnit.equals( other.getOrganisationUnit() );
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getHouseNumber()
    {
        return houseNumber;
    }

    public void setHouseNumber( String houseNumber )
    {
        this.houseNumber = houseNumber;
    }

    public Set<Patient> getMembers()
    {
        return members;
    }

    public void setMembers( Set<Patient> members )
    {
        this.members = members;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress( String address )
    {
        this.address = address;
    }

    public String getLandMark()
    {
        return landMark;
    }

    public void setLandMark( String landMark )
    {
        this.landMark = landMark;
    }

    public int getVisitingOrder()
    {
        return visitingOrder;
    }

    public void setVisitingOrder( int visitingOrder )
    {
        this.visitingOrder = visitingOrder;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
}
