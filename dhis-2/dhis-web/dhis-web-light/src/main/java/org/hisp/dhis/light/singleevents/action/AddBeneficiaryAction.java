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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;

import com.opensymphony.xwork2.Action;

/**
 * @author Group1 Fall 2011
 */
public class AddBeneficiaryAction implements Action  {
	
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
    
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    // -------------------------------------------------------------------------
	// Input Output
	// -------------------------------------------------------------------------   
    
    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }
    
    public Integer getOrganisationUnitId(){
    	return this.organisationUnitId;
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
    
    private Integer singleEventId;
    
    public void setSingleEventId( Integer singleEventId){
    	this.singleEventId = singleEventId;
    }
    
    public Integer getSingleEventId(){
    	return this.singleEventId;
    }
    
    private String eventName;
    
    public String getEventName(){
    	return this.eventName;
    }
    
    private Integer patientId;
    
    public Integer getPatientId(){
    	return this.patientId;
    }
    
    private ArrayList<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>();
    
    public ArrayList<ProgramStageDataElement> getProgramStageDataElements(){
    	return this.programStageDataElements;
    }
    
	 static final Comparator<ProgramStageDataElement> OrderBySortOrder =
             new Comparator<ProgramStageDataElement>() {
		 public int compare(ProgramStageDataElement i1, ProgramStageDataElement i2) {
			 return i1.getSortOrder().compareTo(i2.getSortOrder());
		 }
	 };
    
	// -------------------------------------------------------------------------
	// Action Implementation
	// -------------------------------------------------------------------------
    
	@Override
	public String execute() {
		
		Patient patient = new Patient();
		
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
        
		OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( getOrganisationUnitId() );
		
		patient.setOrganisationUnit( organisationUnit );
		patient.setGender( gender );		
		patient.setDobType( dobType );
		patient.setIsDead( false );
		patient.setBloodGroup( bloodGroup );
		
        birthDate = birthDate.trim();
        patient.setBirthDate( format.parseDate( birthDate ) );
        
        registrationDate = registrationDate.trim();
        patient.setRegistrationDate( format.parseDate( registrationDate ) );
        
        patientId = patientService.savePatient(patient);
        
        // ---------------------------------------------------------------------
        // Set Data for SingleEventForm
        // ---------------------------------------------------------------------
        Program program = programService.getProgram(singleEventId);
        eventName = program.getName();
        ProgramStage programStage = program.getProgramStages().iterator().next();
        programStageDataElements = new ArrayList<ProgramStageDataElement>(programStage.getProgramStageDataElements());
        Collections.sort(programStageDataElements, OrderBySortOrder);
        
        return SUCCESS;
	}
}
