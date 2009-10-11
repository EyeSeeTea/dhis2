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
package org.hisp.dhis.patient.action.patient;

import java.util.Date;

import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.household.HouseHold;
import org.hisp.dhis.household.HouseHoldService;
import org.hisp.dhis.i18n.I18nFormat;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class UpdatePatientAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private HouseHoldService houseHoldService;

    public void setHouseHoldService( HouseHoldService houseHoldService )
    {
        this.houseHoldService = houseHoldService;
    }

    // -------------------------------------------------------------------------
    // Input - Id
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    // -------------------------------------------------------------------------
    // Input - name
    // -------------------------------------------------------------------------

    private String firstName;

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    private String middleName;

    public void setMiddleName( String middleName )
    {
        this.middleName = middleName;
    }

    private String lastName;

    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }

    // -------------------------------------------------------------------------
    // Input - demographics
    // -------------------------------------------------------------------------

    private String birthDate;

    public void setBirthDate( String birthDate )
    {
        this.birthDate = birthDate;
    }

    private String gender;

    public void setGender( String gender )
    {
        this.gender = gender;
    }

    // -------------------------------------------------------------------------
    // Input - household
    // -------------------------------------------------------------------------

    private Integer houseHoldSelectId;

    public void setHouseHoldSelectId( Integer houseHoldSelectId )
    {
        this.houseHoldSelectId = houseHoldSelectId;
    }

    /*
     * //
     * -------------------------------------------------------------------------
     * // Input - address //
     * -------------------------------------------------------------------------
     * 
     * private String address1;
     * 
     * public void setAddress1( String address1 ) { this.address1 = address1; }
     * 
     * private String address2;
     * 
     * public void setAddress2( String address2 ) { this.address2 = address2; }
     * 
     * private String landMark;
     * 
     * public void setLandMark( String landMark ) { this.landMark = landMark; }
     * 
     * private String cityVillage;
     * 
     * public void setCityVillage( String cityVillage ) { this.cityVillage =
     * cityVillage; }
     * 
     * private String stateProvince;
     * 
     * public void setStateProvince( String stateProvince ) { this.stateProvince
     * = stateProvince; }
     * 
     * private String country;
     * 
     * public void setCountry( String country ) { this.country = country; }
     * 
     * private String postalCode;
     * 
     * public void setPostalCode( String postalCode ) { this.postalCode =
     * postalCode; }
     */

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        // ---------------------------------------------------------------------
        // Update patient
        // ---------------------------------------------------------------------

        Patient patient = patientService.getPatient( id );
        patient.setFirstName( firstName );
        patient.setMiddleName( middleName );
        patient.setLastName( lastName );
        patient.setGender( gender );
        patient.setBirthDate( format.parseDate( birthDate ) );
        patient.setRegistrationDate( new Date() );

        if ( houseHoldSelectId != null )
        {
            HouseHold houseHold = houseHoldService.getHouseHold( houseHoldSelectId );
            patient.setHouseHold( houseHold );
        }

        patientService.updatePatient( patient );

        return SUCCESS;
    }
}
