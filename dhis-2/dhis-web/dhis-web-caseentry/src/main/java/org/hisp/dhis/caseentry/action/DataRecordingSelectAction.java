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
package org.hisp.dhis.caseentry.action;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.caseentry.state.SelectedStateManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class DataRecordingSelectAction
    implements Action
{

    private static final String DATAENTRY_FORM = "dataentryform";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private PatientIdentifierService patientIdentifierService;

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }

    private Integer programId;

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    public Integer getProgramId()
    {
        return programId;
    }

    private Integer programStageId;

    public Integer getProgramStageId()
    {
        return programStageId;
    }

    public void setProgramStageId( Integer programStageId )
    {
        this.programStageId = programStageId;
    }

    private Patient patient;

    public Patient getPatient()
    {
        return patient;
    }

    private PatientIdentifier patientIdentifier;

    public PatientIdentifier getPatientIdentifier()
    {
        return patientIdentifier;
    }

    private Integer age;

    public Integer getAge()
    {
        return age;
    }

    private Collection<Program> programs = new ArrayList<Program>();

    public Collection<Program> getPrograms()
    {
        return programs;
    }

    private Collection<ProgramStage> programStages;

    public Collection<ProgramStage> getProgramStages()
    {
        return programStages;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        OrganisationUnit organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        // ---------------------------------------------------------------------
        // Validate selected Patient
        // ---------------------------------------------------------------------

        patient = patientService.getPatient( id );

        if ( patient == null )
        {
            programId = null;
            programStageId = null;

            selectedStateManager.clearSelectedPatient();
            selectedStateManager.clearSelectedProgram();
            selectedStateManager.clearSelectedProgramStage();

            return SUCCESS;
        }

        selectedStateManager.setSelectedPatient( patient );

        patientIdentifier = patientIdentifierService.getPatientIdentifier( patient );

        age = patient.getAge();

        // ---------------------------------------------------------------------
        // Load Enrolled Programs
        // ---------------------------------------------------------------------        

        for ( Program program : patient.getPrograms() )
        {
            if ( program.getOrganisationUnits().contains( organisationUnit ) )
            {                
                programs.add( program );
            }
        }

        // ---------------------------------------------------------------------
        // Validate selected Program
        // ---------------------------------------------------------------------

        Program selectedProgram;

        if ( programId != null )
        {
            selectedProgram = programService.getProgram( programId );
        }
        else
        {
            selectedProgram = selectedStateManager.getSelectedProgram();
        }

        if ( selectedProgram != null && programs.contains( selectedProgram ) )
        {
            programId = selectedProgram.getId();
            selectedStateManager.setSelectedProgram( selectedProgram );
        }

        else
        {
            programId = null;
            programStageId = null;

            selectedStateManager.clearSelectedProgram();
            selectedStateManager.clearSelectedProgramStage();

            return SUCCESS;
        }

        // ---------------------------------------------------------------------
        // Load ProgramStages
        // ---------------------------------------------------------------------

        programStages = selectedProgram.getProgramStages();

        // ---------------------------------------------------------------------
        // Validate selected ProgramStage
        // ---------------------------------------------------------------------

        ProgramStage selectedProgramStage;

        if ( programStageId != null )
        {
            selectedProgramStage = programStageService.getProgramStage( programStageId );
        }
        else
        {
            selectedProgramStage = selectedStateManager.getSelectedProgramStage();
        }

        if ( selectedProgramStage != null && programStages.contains( selectedProgramStage ) )
        {
            programStageId = selectedProgramStage.getId();
            selectedStateManager.setSelectedProgramStage( selectedProgramStage );
        }

        else
        {
            programStageId = null;
            selectedStateManager.clearSelectedProgramStage();

            return SUCCESS;
        }

        return DATAENTRY_FORM;

    }
}
