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
package org.hisp.dhis.program;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
@Transactional
public class DefaultProgramInstanceService
    implements ProgramInstanceService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramInstanceStore programInstanceStore;

    public void setProgramInstanceStore( ProgramInstanceStore programInstanceStore )
    {
        this.programInstanceStore = programInstanceStore;
    }

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    // -------------------------------------------------------------------------
    // ProgramInstance implementation
    // -------------------------------------------------------------------------

    public int addProgramInstance( ProgramInstance programInstance )
    {
        return programInstanceStore.save( programInstance );
    }

    public void deleteProgramInstance( ProgramInstance programInstance )
    {
        programInstanceStore.delete( programInstance );
    }

    public Collection<ProgramInstance> getAllProgramInstances()
    {
        return programInstanceStore.getAll();
    }

    public ProgramInstance getProgramInstance( int id )
    {
        return programInstanceStore.get( id );
    }

    public Collection<ProgramInstance> getProgramInstances( boolean completed )
    {
        return programInstanceStore.get( completed );
    }

    public void updateProgramInstance( ProgramInstance programInstance )
    {
        programInstanceStore.update( programInstance );
    }

    public Collection<ProgramInstance> getProgramInstances( Program program )
    {
        return programInstanceStore.get( program );
    }

    public Collection<ProgramInstance> getProgramInstances( Collection<Program> programs )
    {
        return programInstanceStore.get( programs );
    }

    public Collection<ProgramInstance> getProgramInstances( Collection<Program> programs, boolean completed )
    {
        return programInstanceStore.get( programs, completed );
    }

    public Collection<ProgramInstance> getProgramInstances( Program program, boolean completed )
    {
        return programInstanceStore.get( program, completed );
    }

    public Collection<ProgramInstance> getProgramInstances( Patient patient )
    {
        return programInstanceStore.get( patient );
    }

    public Collection<ProgramInstance> getProgramInstances( Patient patient, boolean completed )
    {
        return programInstanceStore.get( patient, completed );
    }

    public Collection<ProgramInstance> getProgramInstances( Patient patient, Program program )
    {
        return programInstanceStore.get( patient, program );
    }

    public Collection<ProgramInstance> getProgramInstances( Patient patient, Program program, boolean completed )
    {
        return programInstanceStore.get( patient, program, completed );
    }

    public Map<Patient, Set<ProgramStageInstance>> getNextVisitsForProgramInstances(
        Collection<ProgramInstance> programInstances )
    {

        Map<Patient, Set<ProgramStageInstance>> visitsByPatients = new HashMap<Patient, Set<ProgramStageInstance>>();

        Collection<ProgramStageInstance> programStageInstances = new ArrayList<ProgramStageInstance>();

        // ---------------------------------------------------------------------
        // Initially assume to have a first visit for all programInstances
        // ---------------------------------------------------------------------

        Map<ProgramInstance, ProgramStageInstance> visitsByProgramInstances = new HashMap<ProgramInstance, ProgramStageInstance>();

        for ( ProgramInstance programInstance : programInstances )
        {

            programStageInstances.addAll( programInstance.getProgramStageInstances() );

            ProgramStageInstance nextStage = programInstance.getProgramStageInstanceByStage( 1 );

            visitsByProgramInstances.put( programInstance, nextStage );
        }

        // ---------------------------------------------------------------------
        // For each of these active instances, see at which stage they are
        // currently (may not necessarily be at the first stage
        // ---------------------------------------------------------------------

        Collection<PatientDataValue> patientDataValues = patientDataValueService
            .getPatientDataValues( programStageInstances );

        for ( PatientDataValue patientDataValue : patientDataValues )
        {
            ProgramStageInstance currentStage = patientDataValue.getProgramStageInstance();

            ProgramStageInstance nextStage = patientDataValue.getProgramStageInstance().getProgramInstance()
                .getProgramStageInstanceByStage( currentStage.getStageInProgram() + 1 );

            if ( nextStage != null )
            {
                visitsByProgramInstances.put( patientDataValue.getProgramStageInstance().getProgramInstance(),
                    nextStage );
            }
            if ( nextStage == null
                && visitsByProgramInstances.containsKey( patientDataValue.getProgramStageInstance()
                    .getProgramInstance() ) )
            {

                // -------------------------------------------------------------
                // This patient has completed all services, programInstance
                // should therefore be closed!
                // -------------------------------------------------------------

                visitsByProgramInstances.remove( patientDataValue.getProgramStageInstance().getProgramInstance() );
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
                Set<ProgramStageInstance> programStages = new HashSet<ProgramStageInstance>();
                programStages.add( visitsByProgramInstances.get( programInstance ) );

                visitsByPatients.put( programInstance.getPatient(), programStages );
            }
        }

        return visitsByPatients;
    }
}
