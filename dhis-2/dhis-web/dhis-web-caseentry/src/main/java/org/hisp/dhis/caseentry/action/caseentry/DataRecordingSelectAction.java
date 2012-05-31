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
package org.hisp.dhis.caseentry.action.caseentry;

import java.util.Collection;
import java.util.HashSet;

import org.hisp.dhis.caseentry.state.SelectedStateManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class DataRecordingSelectAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer patientId;

    public void setPatientId( Integer patientId )
    {
        this.patientId = patientId;
    }

    private Patient patient;

    public Patient getPatient()
    {
        return patient;
    }

    private Collection<Program> programs = new HashSet<Program>();

    public Collection<Program> getPrograms()
    {
        return programs;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        OrganisationUnit orgunit = selectedStateManager.getSelectedOrganisationUnit();

        patient = patientService.getPatient( patientId );

        // ---------------------------------------------------------------------
        // Get single programs with un-completed program-instances
        // ---------------------------------------------------------------------

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( patient, true );

        Collection<Program> completedPrograms = new HashSet<Program>();

        for ( ProgramInstance programInstance : programInstances )
        {
            if ( programInstance.getProgram().isSingleEvent() )
            {
                completedPrograms.add( programInstance.getProgram() );
            }
        }

        // ---------------------------------------------------------------------
        // Get programs which patient enrolls
        // ---------------------------------------------------------------------

        programs = programService.getPrograms( orgunit );

        programs.retainAll( patient.getPrograms() );

        programs.addAll( programService.getPrograms( Program.SINGLE_EVENT_WITH_REGISTRATION, orgunit ) );

        programs.removeAll( completedPrograms );

        selectedStateManager.setSelectedPatient( patient );

        return SUCCESS;
    }
}
