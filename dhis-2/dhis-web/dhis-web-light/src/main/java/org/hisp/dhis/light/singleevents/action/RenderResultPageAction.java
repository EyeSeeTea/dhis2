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

import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.ProgramService;
import com.opensymphony.xwork2.Action;

/**
 * @author Group1 Fall 2011
 */
public class RenderResultPageAction implements Action {
	
	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------
	
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
	
	// -------------------------------------------------------------------------
	// Input & Output
	// -------------------------------------------------------------------------
	
    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }
    
    public Integer getOrganisationUnitId(){
    	return this.organisationUnitId;
    }
    
    private Integer singleEventId;
    
    public void setSingleEventId( Integer singleEventId){
    	this.singleEventId = singleEventId;
    }
    
    public Integer getSingleEventId(){
    	return this.singleEventId;
    }
    
    private Integer patientId;
    
    public void  setPatientId( Integer patientId ){
    	this.patientId = patientId;
    }
    
    private String eventName;
    
    public String getEventName(){
    	return this.eventName;
    }
    
    private String patientName;
    
    public String getPatientName(){
    	return this.patientName;
    }
    
    private boolean update;
    
    public void setUpdate( boolean update )
    {
    	this.update = update;
    }
    
    public boolean getUpdate()
    {
    	return this.update;
    }
    
	// -------------------------------------------------------------------------
	// Action Implementation
	// -------------------------------------------------------------------------

	@Override
	public String execute() {
		eventName = programService.getProgram(singleEventId).getName();
		patientName = patientService.getPatient(patientId).getFullName();
		return SUCCESS;
	}
}
