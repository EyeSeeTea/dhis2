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
package org.hisp.dhis.patient;

import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class PatientAddress
{
    private Integer id;

    private String address1;

    private String address2;

    private String landMark;

    private String cityVillage;

    private String stateProvince;

    private String country;

    private String postalCode;

    private OrganisationUnit house;

    private Boolean preferred = false;

    private Patient patient;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public PatientAddress()
    {
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
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

        if ( !(obj instanceof PatientAddress) )
        {
            return false;
        }

        final PatientAddress other = (PatientAddress) obj;

        return id.equals( other.getId() );
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    /**
     * @return the id
     */
    public Integer getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId( Integer id )
    {
        this.id = id;
    }

    /**
     * @return the address1
     */
    public String getAddress1()
    {
        return address1;
    }

    /**
     * @param address1 the address1 to set
     */
    public void setAddress1( String address1 )
    {
        this.address1 = address1;
    }

    /**
     * @return the address2
     */
    public String getAddress2()
    {
        return address2;
    }

    /**
     * @param address2 the address2 to set
     */
    public void setAddress2( String address2 )
    {
        this.address2 = address2;
    }

    /**
     * @return the countyDistrict
     */
    public String getCityVillage()
    {
        return cityVillage;
    }

    /**
     * @param countyDistrict the countyDistrict to set
     */
    public void setCityVillage( String cityVillage )
    {
        this.cityVillage = cityVillage;
    }

    /**
     * @return the stateProvince
     */
    public String getStateProvince()
    {
        return stateProvince;
    }

    /**
     * @param stateProvince the stateProvince to set
     */
    public void setStateProvince( String stateProvince )
    {
        this.stateProvince = stateProvince;
    }

    /**
     * @return the country
     */
    public String getCountry()
    {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry( String country )
    {
        this.country = country;
    }

    /**
     * @return the postalCode
     */
    public String getPostalCode()
    {
        return postalCode;
    }

    /**
     * @param postalCode the postalCode to set
     */
    public void setPostalCode( String postalCode )
    {
        this.postalCode = postalCode;
    }

    /**
     * @return the house
     */
    public OrganisationUnit getHouse()
    {
        return house;
    }

    /**
     * @param house the house to set
     */
    public void setHouse( OrganisationUnit house )
    {
        this.house = house;
    }

    /**
     * @return the preferred
     */
    public Boolean getPreferred()
    {
        return preferred;
    }

    /**
     * @param preferred the preferred to set
     */
    public void setPreferred( Boolean preferred )
    {
        this.preferred = preferred;
    }

    /**
     * @return the patient
     */
    public Patient getPatient()
    {
        return patient;
    }

    /**
     * @param patient the patient to set
     */
    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    /**
     * @param landMark the landMark to set
     */
    public void setLandMark( String landMark )
    {
        this.landMark = landMark;
    }

    /**
     * @return the landMark
     */
    public String getLandMark()
    {
        return landMark;
    }

}
