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

import java.util.Collection;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.caseentry.state.SelectedStateManager;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstanceStage;
import org.hisp.dhis.program.ProgramInstanceStageService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class SaveValueAction
    implements Action
{

    private static final Log LOG = LogFactory.getLog( SaveValueAction.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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
    
    private ProgramInstanceStageService programInstanceStageService;

    public void setProgramInstanceStageService( ProgramInstanceStageService programInstanceStageService )
    {
        this.programInstanceStageService = programInstanceStageService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryOptionComboService(
        DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private String value;

    public void setValue( String value )
    {
        this.value = value;
    }

    private int dataElementId;

    public void setDataElementId( int dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    public int getDataElementId()
    {
        return dataElementId;
    }

    private int statusCode;

    public int getStatusCode()
    {
        return statusCode;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        OrganisationUnit organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        Patient patient = selectedStateManager.getSelectedPatient();

        Program program = selectedStateManager.getSelectedProgram();

        ProgramStage programStage = selectedStateManager.getSelectedProgramStage();

        Collection<ProgramInstance> progamInstances = programInstanceService
            .getProgramInstances( patient, program, false );

        ProgramInstance programInstance = progamInstances.iterator().next();
        
        ProgramInstanceStage programInstanceStage = programInstanceStageService.getProgramInstanceStage( programInstance, programStage );

        DataElement dataElement = dataElementService.getDataElement( dataElementId );

        if ( value != null && value.trim().length() == 0 )
        {
            value = null;
        }

        if ( value != null )
        {
            value = value.trim();
        }

        DataElementCategoryOptionCombo optionCombo;

        if ( dataElement.getType().equalsIgnoreCase( DataElement.TYPE_STRING ) )
        {
            optionCombo = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer
                .parseInt( value ) );
        }
        else
        {
            optionCombo = dataElement.getCategoryCombo().getOptionCombos().iterator().next();
        }

        PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programInstanceStage,
            dataElement, optionCombo, organisationUnit );

        if ( patientDataValue == null )
        {
            if ( value != null )
            {
                LOG.debug( "Adding PatientDataValue, value added" );

                patientDataValue = new PatientDataValue( programInstanceStage, dataElement, optionCombo,
                    organisationUnit, new Date(), value );

                patientDataValueService.savePatientDataValue( patientDataValue );
            }
        }
        else
        {
            LOG.debug( "Updating PatientDataValue, value added/changed" );

            patientDataValue.setValue( value );
            patientDataValue.setTimestamp( new Date() );

            patientDataValueService.updatePatientDataValue( patientDataValue );

        }

        return SUCCESS;
    }
}
