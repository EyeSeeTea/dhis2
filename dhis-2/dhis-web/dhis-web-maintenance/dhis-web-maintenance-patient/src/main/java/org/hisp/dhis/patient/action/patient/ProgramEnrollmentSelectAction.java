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

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patient.state.SelectedStateManager;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramService;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class ProgramEnrollmentSelectAction
    implements Action
{
    private static final String PROGRAM_ENROLLMENT_FORM = "enrollmentform";

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

    private Patient patient;

    public Patient getPatient()
    {
        return patient;
    }

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    private Collection<Program> programs = new ArrayList<Program>();

    public Collection<Program> getPrograms()
    {
        return programs;
    }   

    private ProgramInstance programInstance;

    public ProgramInstance getProgramInstance()
    {
        return programInstance;
    }

    public void setProgramInstance( ProgramInstance programInstance )
    {
        this.programInstance = programInstance;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        // ---------------------------------------------------------------------
        // Validate selected Patient
        // ---------------------------------------------------------------------

        if ( id != null )
        {
            patient = patientService.getPatient( id );
        }

        if ( id == null )
        {
            patient = selectedStateManager.getSelectedPatient();
        }

        if ( patient == null )
        {
            programId = null;

            selectedStateManager.clearSelectedPatient();
            selectedStateManager.clearSelectedProgram();

            return SUCCESS;
        }

        selectedStateManager.setSelectedPatient( patient );

        // ---------------------------------------------------------------------
        // Load Programs
        // ---------------------------------------------------------------------

        programs = programService.getAllPrograms();

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

            selectedStateManager.clearSelectedProgram();

            return SUCCESS;
        }

        return PROGRAM_ENROLLMENT_FORM;
    }
}
