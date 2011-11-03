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

package org.hisp.dhis.light.singleevents.action;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;

import com.opensymphony.xwork2.Action;

/**
 * @author Group1 Fall 2011
 */
public class UpdateBeneficiaryAction implements Action  {
	
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
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }    
    
    // -------------------------------------------------------------------------
	// Input
	// -------------------------------------------------------------------------   
    
    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }
    
    private Integer patientId;
    
    public void setPatientId( Integer patientId )
    {
    	this.patientId = patientId;
    }

    private String fullName;
    
    public void setFullName( String fullName )
    {
        this.fullName = fullName;
    }

    private String birthDate;
    
    public void setBirthDate( String birthDate )
    {
        this.birthDate = birthDate;
    }

    private Character dobType;
    
    public void setDobType( Character dobType )
    {
    	this.dobType = dobType;
    }

    private String gender;
    
    public void setGender( String gender )
    {
    	this.gender = gender;
    }

    private String bloodGroup;
    
    public void setBloodGroup( String bloodGroup ){
    	this.bloodGroup = bloodGroup;
    }
    
    private String registrationDate;
    
    public void setRegistrationDate( String registrationDate ){
    	this.registrationDate = registrationDate;
    }
    
	// -------------------------------------------------------------------------
	// Output
	// -------------------------------------------------------------------------
  
    
	// -------------------------------------------------------------------------
	// Action Implementation
	// -------------------------------------------------------------------------
    
	@Override
	public String execute() {
		
		Patient patient = patientService.getPatient(patientId);
		
		 // ---------------------------------------------------------------------
        // Set FirstName, MiddleName, LastName by FullName
        // ---------------------------------------------------------------------

        fullName = fullName.trim();

        int startIndex = fullName.indexOf( ' ' );
        int endIndex = fullName.lastIndexOf( ' ' );

        String firstName = fullName.toString();
        String middleName = "";
        String lastName = "";

        if ( fullName.indexOf( ' ' ) != -1 )
        {
            firstName = fullName.substring( 0, startIndex );
            if ( startIndex == endIndex )
            {
                middleName = "";
                lastName = fullName.substring( startIndex + 1, fullName.length() );
            }
            else
            {
                middleName = fullName.substring( startIndex + 1, endIndex );
                lastName = fullName.substring( endIndex + 1, fullName.length() );
            }
        }
        
        patient.setFirstName( firstName );
        patient.setMiddleName( middleName );
        patient.setLastName( lastName );
        
        // ---------------------------------------------------------------------
        // Set Other information for patient
        // ---------------------------------------------------------------------
        
		OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
		
		patient.setOrganisationUnit( organisationUnit );
		patient.setGender( gender );		
		patient.setDobType( dobType );
		patient.setIsDead( false );
		patient.setBloodGroup( bloodGroup );
		
        birthDate = birthDate.trim();
        patient.setBirthDate( format.parseDate( birthDate ) );
        
        registrationDate = registrationDate.trim();
        patient.setRegistrationDate( format.parseDate( registrationDate ) );
        
        patientService.updatePatient(patient);
        
		return SUCCESS;
	}
}
