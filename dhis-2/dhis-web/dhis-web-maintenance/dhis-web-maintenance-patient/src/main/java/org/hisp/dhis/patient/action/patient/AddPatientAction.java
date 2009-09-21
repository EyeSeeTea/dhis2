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

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAddressService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.i18n.I18nFormat;


import com.opensymphony.xwork2.Action;
/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class AddPatientAction 
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

	private PatientIdentifierService patientIdentifierService;

	public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService ) 
	{
		this.patientIdentifierService = patientIdentifierService;
	}
	
	private PatientAddressService patientAddressService;

	public void setPatientAddressService( PatientAddressService patientAddressService ) 
	{
		this.patientAddressService = patientAddressService;
	}
	
	private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    // -------------------------------------------------------------------------
    // Input - identifier
    // -------------------------------------------------------------------------
	
	private String identifier;
	
	public void setIdentifier( String identifier )
	{
		this.identifier = identifier;
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
    // Input - address
    // -------------------------------------------------------------------------
	
	private String address1;
	
	public void setAddress1( String address1 )
	{
		this.address1 = address1;
	}
	
	private String address2;
	
	public void setAddress2( String address2 )
	{
		this.address2 = address2;
	}
	
	private String landMark;
	
	public void setLandMark( String landMark )
	{
		this.landMark = landMark;
	}
	
	private String cityVillage;
	
	public void setCityVillage( String cityVillage )
	{
		this.cityVillage = cityVillage;
	}
	
	private String stateProvince;
	
	public void setStateProvince( String stateProvince )
	{
		this.stateProvince = stateProvince;
	}
	
	private String country;
	
	public void setCountry( String country )
	{
		this.country = country;
	}

	private String postalCode;
	
	public void setPostalCode( String postalCode )
	{
		this.postalCode = postalCode;
	}
	
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()        
    {
    	// ---------------------------------------------------------------------
        // Prepare values
        // ---------------------------------------------------------------------
    	
    	OrganisationUnit organisationUnit = selectionManager.getSelectedOrganisationUnit();
    	
    	Patient patient = new Patient();
    	
    	patient.setFirstName(firstName);
    	patient.setMiddleName(middleName);
    	patient.setLastName(lastName);
    	patient.setGender(gender);    	
    	patient.setBirthDate( format.parseDate( birthDate ) );
    	
    	patientService.addPatient( patient );
    	
    	PatientIdentifier patientIdentifier = new PatientIdentifier();
    	patientIdentifier.setIdentifier(identifier);
    	patientIdentifier.setOrganisationUnit(organisationUnit);
    	patientIdentifier.setPatient( patient );
    	patientIdentifier.setPreferred( true );
    	
    	patientIdentifierService.addPatientIdentifier( patientIdentifier );   	
    	
        return SUCCESS;
    }
}
