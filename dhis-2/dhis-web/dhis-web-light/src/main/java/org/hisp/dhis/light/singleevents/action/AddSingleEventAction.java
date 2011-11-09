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
import java.util.Date;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;

import com.opensymphony.xwork2.Action;

/**
 * @author Group1 Fall 2011
 */
public class AddSingleEventAction implements Action  {
	
	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------
	
	private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }
    
	private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }
    
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
    	this.patientService = patientService;
    }
    
    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    // -------------------------------------------------------------------------
	// Input Output
	// -------------------------------------------------------------------------   

    private Integer singleEventId;
    
    public void setSingleEventId( Integer singleEventId ){
    	this.singleEventId = singleEventId;
    }
    
    private Integer patientId;
    
    public void  setPatientId( Integer patientId ){
    	this.patientId = patientId;
    }
    
    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }
    
    public Integer getOrganisationUnitId(){
    	return this.organisationUnitId;
    }
    
    private String dynForm[];

    public void setDynForm(String[] dynForm) {
    	this.dynForm = dynForm;
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

		Program program = programService.getProgram(singleEventId);
		Patient patient = patientService.getPatient(patientId) ;
		ProgramStage programStage = program.getProgramStages().iterator().next();
		OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit(organisationUnitId);

        ProgramInstance programInstance = new ProgramInstance();
        programInstance.setEnrollmentDate( new Date() );
        programInstance.setDateOfIncident( new Date() );
        programInstance.setProgram( program );
        programInstance.setPatient( patient );
        programInstance.setCompleted( false );

        programInstanceService.addProgramInstance( programInstance );
        
        ProgramStageInstance programStageInstance = new ProgramStageInstance();
        programStageInstance.setProgramInstance(programInstance);
        programStageInstance.setProgramStage(programStage);
        programStageInstance.setDueDate(new Date());
        programStageInstance.setExecutionDate(new Date());
        programStageInstance.setCompleted(false);
        programStageInstanceService.addProgramStageInstance(programStageInstance);
        
		ArrayList<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>(programStage.getProgramStageDataElements());
		Collections.sort(programStageDataElements, OrderBySortOrder);
        
        int i = 0;
		for (ProgramStageDataElement programStageDataElement : programStageDataElements) {
			DataElement dataElement = programStageDataElement.getDataElement();
			//dataElement.getType()
			
	        PatientDataValue patientDataValue = new PatientDataValue();
	        patientDataValue.setDataElement(dataElement);
	        patientDataValue.setProgramStageInstance(programStageInstance);
	        patientDataValue.setOrganisationUnit(organisationUnit);
	        patientDataValue.setValue(dynForm[i]);
			patientDataValueService.savePatientDataValue(patientDataValue);
			i++;
		}

		return SUCCESS;
	}
}
