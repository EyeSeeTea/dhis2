package org.hisp.dhis.program;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.patientcomment.PatientComment;
import org.hisp.dhis.patientcomment.PatientCommentService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.system.deletion.DeletionHandler;

/**
 * @author Quang Nguyen
 * @version $Id$
 */
public class ProgramInstanceDeletionHandler
    extends DeletionHandler
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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
    
    private PatientCommentService patientCommentService;    

    public void setPatientCommentService(PatientCommentService patientCommentService) 
    {
		this.patientCommentService = patientCommentService;
	}
    
    private PatientAttributeValueService patientAttributeValueService;    

	public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
	{
		this.patientAttributeValueService = patientAttributeValueService;
	}

	public ProgramStageDataElementService programStageDEService;

    public void setProgramStageDEService( ProgramStageDataElementService programStageDEService )
    {
        this.programStageDEService = programStageDEService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    public String getClassName()
    {
        return ProgramInstance.class.getSimpleName();
    }

    @Override
    public void deletePatient( Patient patient )
    {
        Collection<ProgramInstance> programInstances = patient.getProgramInstances();

        if ( programInstances != null )
        {
            // ---------------------------------------------------------------------
            // Delete Patient data values
            // ---------------------------------------------------------------------

            for ( ProgramInstance programInstance : programInstances )
            {
                Set<PatientDataValue> dataValues = new HashSet<PatientDataValue>();

                dataValues.addAll( patientDataValueService.getPatientDataValues( programInstance
                    .getProgramStageInstances() ) );

                if ( !dataValues.isEmpty() )
                {
                    for ( PatientDataValue dataValue : dataValues )
                    {
                        patientDataValueService.deletePatientDataValue( dataValue );
                    }
                }
            }
            
            // ---------------------------------------------------------------------
            // Delete Patient attribute values related to program/programInstance
            // ---------------------------------------------------------------------
            
            PatientAttributeValue patientAttributeValue;
            
            for( ProgramInstance programInstance : programInstances )
            {
            	Program program = programInstance.getProgram();
            	
            	for( PatientAttribute patientAttribute : program.getPatientAttributes() )
            	{
            		patientAttributeValue = patientAttributeValueService.getPatientAttributeValue( patient, patientAttribute );
            		
            		if( patientAttributeValue != null )
            		{
            			patientAttributeValueService.deletePatientAttributeValue(patientAttributeValue);
            		}
            	}            	
            }
          
            // ---------------------------------------------------------------------
            // Delete Patient comments
            // ---------------------------------------------------------------------

            for ( ProgramInstance programInstance : programInstances )
            {
            	if( programInstance.getPatientComments() != null )
            	{
            		for ( PatientComment patientComment : programInstance.getPatientComments() )
                    {
                        patientCommentService.deletePatientComment(patientComment);
                    }
            	}                
            }

            // ---------------------------------------------------------------------
            // Delete Program Stage Instances
            // ---------------------------------------------------------------------

            for ( ProgramInstance programInstance : programInstances )
            {            	
                Set<ProgramStageInstance> programStageInstances = programInstance.getProgramStageInstances();
             
                for ( ProgramStageInstance programStageInstance : programStageInstances )
                {
                	programStageInstanceService.deleteProgramStageInstance( programStageInstance );
                }
            }

            // ---------------------------------------------------------------------
            // Delete Program Instances
            // ---------------------------------------------------------------------

            for ( ProgramInstance programInstance : programInstances )
            {
                programInstanceService.deleteProgramInstance( programInstance );
            }
        }
    }

    @Override
    public void deleteProgram( Program program )
    {
        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( program );
        for( ProgramInstance programInstance : programInstances)
        {
            programInstanceService.deleteProgramInstance( programInstance );
        }
    }
}
