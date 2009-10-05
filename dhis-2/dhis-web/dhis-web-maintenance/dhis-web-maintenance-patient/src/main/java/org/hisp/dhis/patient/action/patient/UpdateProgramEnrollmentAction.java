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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
public class UpdateProgramEnrollmentAction
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
    
    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }   

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }
    
    private Collection<String> selectedList = new HashSet<String>();

    public void setSelectedList( Collection<String> selectedList )
    {
        this.selectedList = selectedList;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
        Patient patient = patientService.getPatient( id );
        
        Set<Program> earlierEnrollments = new HashSet<Program>( patient.getPrograms());       

        Set<Program> newEnrollments = new HashSet<Program>();

        for ( String id : selectedList )
        {
            Program program = programService.getProgram( Integer.parseInt( id ) );

            newEnrollments.add( program );
        }
        
        //Check for newly added programs (ProgramInstance should be launched)
        
        for( Program program : newEnrollments )
        {
            if( ! earlierEnrollments.contains( program ) )                
            {
                ProgramInstance programInstance = new ProgramInstance();
                
                programInstance.setPatient( patient );
                programInstance.setProgram( program );
                programInstance.setStartDate( new Date() );
                programInstance.setCompleted( false );
                
                programInstanceService.addProgramInstance( programInstance );              
                
            }
        }        
        
        //Check for removed programs (active ProgramInstance should be closed)
        
        for( Program program : earlierEnrollments )
        {
            if( !newEnrollments.contains(  program  ) )
            {
                Collection<ProgramInstance> activeProgramInstances = programInstanceService.getProgramInstances( patient, program, false );
                
                for( ProgramInstance programInstance : activeProgramInstances )
                {
                    programInstance.setCompleted( true );
                    programInstance.setEndDate( new Date() );
                    
                    programInstanceService.addProgramInstance( programInstance );
                }
            }
        }
        
        patient.setPrograms( newEnrollments );
        
        patientService.updatePatient( patient );

        return SUCCESS;
    }
}
