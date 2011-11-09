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

import java.util.Date;
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
    //
    private Patient patient;
    public Patient getPatient()
    {
    	return patient;
    }

    private String fullName;
    
    public void setFullName( String fullName )
    {
        this.fullName = fullName;
    }
    
    public String getFullName(){
    	return fullName;
    }

    private String birthDate;
    
    public void setBirthDate( String birthDate )
    {
        this.birthDate = birthDate;
    }
    
    public String getBirthDate()
    {
    	return birthDate;
    }

    private Character dobType;
    
    public void setDobType( Character dobType )
    {
    	this.dobType = dobType;
    }
    //
    public char getDobType(){
    	return dobType;
    }

    private String gender;
    
    public void setGender( String gender )
    {
    	this.gender = gender;
    }
    //
    public String getGender(){
    	return gender;
    }

    private String bloodGroup;
    
    public void setBloodGroup( String bloodGroup ){
    	this.bloodGroup = bloodGroup;
    }
    //
    public String getBloodGroup(){
    	return bloodGroup;
    }
    
    private String registrationDate;
    
    public void setRegistrationDate( String registrationDate ){
    	this.registrationDate = registrationDate;
    }
    
    public String getRegistrationDate()
    {
    	return registrationDate;
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
	// Validation
	// -------------------------------------------------------------------------

    private Date rD,bD;
    
    private boolean fullNameIsToLong;
    private boolean invalidFullName;
    private boolean invalidRegistrationDate;
    private boolean invalidBirthDate;
    private boolean noGender;
    private boolean noDobType;
    private boolean invalidDobType;
    private boolean invalidBloodGroup;
    private boolean invalidGender;
    
    public boolean getFullNameIsToLong()
    {
    	return fullNameIsToLong;
    }
    
    public boolean getInvalidFullName()
    {
    	return invalidFullName;
    }
    
    public boolean getInvalidRegistrationDate()
    {
    	return invalidRegistrationDate;
    }
    
    public boolean getInvalidBirthDate()
    {
    	return invalidBirthDate;
    }
    
    public boolean getNoGender()
    {
    	return noGender;
    }
    
    public boolean getNoDobType()
    {
    	return noDobType;
    }
    
    public boolean getInvalidDobType()
    {
    	return invalidDobType;
    }
    
    public boolean getInvalidGender()
    {
    	return invalidGender;
    }
    
    public boolean getInvalidBloodGroup()
    {
    	return invalidBloodGroup;
    }
    
    private boolean validate()
    {
    	boolean valid = true;
    	
    	if(validateStringLength(fullName,7,50) == false){
    		fullNameIsToLong = true;
    		valid = false;
    	}
    	
    	if(validName(fullName) == false){
    		invalidFullName = true;
    		valid = false;
    	}
    	
    	if(validateDateNotNull(rD) == false){
    		invalidRegistrationDate = true;
    		valid = false;
    	}
    	
    	if(validateDateNotNull(bD) == false){
    		System.out.println("bD");
    		invalidBirthDate = true;
    		valid = false;
    	}
    	
    	if(validateDropDown(gender) == false){
    		noGender = true;
    		valid = false;
    	}
    	
    	if(validateDropDown(dobType) == false){
    		noDobType = true;
    		valid = false;
    	}
    	
    	if(validateDobType(dobType) == false){
    		invalidDobType = true;
    		valid = false;
    	}
    	
    	if(validateGender(gender) == false){
    		invalidGender = true;
    		valid = false;
    	}
    	
    	if(validateBloodGroup(bloodGroup) == false){
    		invalidBloodGroup = true;
    		valid = false;
    	}
    	
    	return valid;
    }
    
    private boolean validateStringLength(String s, int min, int max)
    {
    	if((s.length() >= min) && (s.length() <= max)){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    private boolean validName(String s)
    {
    	if(s.matches("^[A-Za-zÀ-ÿ]+[[A-Za-zÀ-ÿ]*\\s?-?.?'?]*$")){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    private boolean validateDateNotNull(Date d){
    	if(d == null){
    		return false;
    	}else{
    		return true;
    	}
    }
    
    private boolean validateDropDown(String s){
    	if(s.equalsIgnoreCase("please_select")){
    		return false;
    	}else{
    		return true;
    	}
    }
    
    private boolean validateDropDown(Character c){
    	if(c.equals('p')){
    		return false;
    	}else{
    		return true;
    	}
    }
    
    private boolean validateDobType(Character c)
    {
    	if(c == 'D' || c == 'V'){
    		return true;
    	}else{
    		return false;
    	}
    }

    private boolean validateGender(String s)
    {
    	if(s.equals("M") || s.equals("F") || s.equals("T")){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    private boolean validateBloodGroup(String s)
    {
    	if(s.matches("^\\w{1,2}\\-?\\+?$") || s.equalsIgnoreCase("please_select")){
    		return true;
    	}else{
    		return false;
    	}
    }
    



	// -------------------------------------------------------------------------
	// Action Implementation
	// -------------------------------------------------------------------------
    
	@Override
	public String execute() {
		//
	    eventName = programService.getProgram(singleEventId).getName();
	    
		fullNameIsToLong = false;
		invalidFullName = false;
	    invalidRegistrationDate = false;
	    invalidBirthDate = false;
	    noGender = false;
	    noDobType = false;
	    invalidDobType = false;
	    invalidBloodGroup = false;
	    invalidGender = false;

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
		if(!bloodGroup.equalsIgnoreCase("please_select")){
			patient.setBloodGroup( bloodGroup );
		}
        birthDate = birthDate.trim();
        bD = format.parseDate( birthDate );
        patient.setBirthDate( bD );
        
        registrationDate = registrationDate.trim();
        rD = format.parseDate( registrationDate );
        patient.setRegistrationDate( rD );
        
		if(validate() == false) {
			return ERROR;
		}else{
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
}
