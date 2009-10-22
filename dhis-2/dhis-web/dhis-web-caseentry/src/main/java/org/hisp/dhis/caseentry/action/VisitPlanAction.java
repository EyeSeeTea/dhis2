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
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstanceStage;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class VisitPlanAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private Map<Patient, Set<ProgramStage>> visitsByPatients = new HashMap<Patient, Set<ProgramStage>>();

    public Map<Patient, Set<ProgramStage>> getVisitsByPatients()
    {
        return visitsByPatients;
    }
    
    private Collection<Patient> patients;
    
    public Collection<Patient> getPatients()
    {
        return patients;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        Map<ProgramInstance, ProgramStage> visitsByProgramInstances = new HashMap<ProgramInstance, ProgramStage>();

        // ---------------------------------------------------------------------
        // Get the facility planning to do a visit
        // ---------------------------------------------------------------------

        organisationUnit = selectionManager.getSelectedOrganisationUnit();

        // ---------------------------------------------------------------------
        // Get all the programs the facility is providing
        // ---------------------------------------------------------------------

        Collection<Program> programs = programService.getPrograms( organisationUnit );

        // ---------------------------------------------------------------------
        // For all the programs a facility is servicing get the active instances
        // completed = false
        // ---------------------------------------------------------------------

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( programs, false );
        
        Collection<ProgramInstanceStage> programInstanceStages = new ArrayList<ProgramInstanceStage>();

        // ---------------------------------------------------------------------
        // Initially assume to have a first visit for all programInstances
        // ---------------------------------------------------------------------

        for ( ProgramInstance programInstance : programInstances )
        {   
            
            programInstanceStages.addAll( programInstance.getProgramInstanceStages() );
            
            ProgramStage nextStage = programInstance.getProgram().getProgramStageByStage( 1 );

            visitsByProgramInstances.put( programInstance, nextStage );
        }

        // ---------------------------------------------------------------------
        // For each of these active instances, see at which stage they are
        // currently
        // ---------------------------------------------------------------------

        Collection<PatientDataValue> patientDataValues = patientDataValueService
            .getPatientDataValues( programInstanceStages );

        for ( PatientDataValue patientDataValue : patientDataValues )
        {
            ProgramStage currentStage = patientDataValue.getProgramInstanceStage().getProgramStage();

            ProgramStage nextStage = patientDataValue.getProgramInstanceStage().getProgramStage().getProgram().getProgramStageByStage(
                currentStage.getStageInProgram() + 1 );

            if ( nextStage != null )
            {
                visitsByProgramInstances.put( patientDataValue.getProgramInstanceStage().getProgramInstance(), nextStage );
            }
            if ( nextStage == null && visitsByProgramInstances.containsKey( patientDataValue.getProgramInstanceStage().getProgramInstance() ) )
            {
                // This patient has completed all services, programInstance
                // should therefore be closed!
                visitsByProgramInstances.remove( patientDataValue.getProgramInstanceStage().getProgramInstance() );
            }
        }

        for ( ProgramInstance programInstance : visitsByProgramInstances.keySet() )
        {
            if ( visitsByPatients.containsKey( programInstance.getPatient() ) )
            {
                visitsByPatients.get( programInstance.getPatient() ).add(
                    visitsByProgramInstances.get( programInstance ) );
            }
            else
            {
                Set<ProgramStage> programStages = new HashSet<ProgramStage>();
                programStages.add( visitsByProgramInstances.get( programInstance ) );

                visitsByPatients.put( programInstance.getPatient(), programStages );
            }
        }        
        
        patients = visitsByPatients.keySet();

        return SUCCESS;
    }
}
