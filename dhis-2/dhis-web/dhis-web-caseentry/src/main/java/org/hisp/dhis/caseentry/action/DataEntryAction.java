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
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.caseentry.state.SelectedStateManager;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramInstanceStage;
import org.hisp.dhis.program.ProgramInstanceStageService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class DataEntryAction
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

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private ProgramInstanceStageService programInstanceStageService;

    public void setProgramInstanceStageService( ProgramInstanceStageService programInstanceStageService )
    {
        this.programInstanceStageService = programInstanceStageService;
    }

    private PatientIdentifierService patientIdentifierService;

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
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

    private Program program;

    public Program getProgram()
    {
        return program;
    }

    private ProgramStage programStage;

    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    private Collection<DataElement> dataElements = new ArrayList<DataElement>();

    public Collection<DataElement> getDataElements()
    {
        return dataElements;
    }

    private Map<Integer, Collection<DataElementCategoryOptionCombo>> optionMap = new HashMap<Integer, Collection<DataElementCategoryOptionCombo>>();

    public Map<Integer, Collection<DataElementCategoryOptionCombo>> getOptionMap()
    {
        return optionMap;
    }

    private Map<Integer, PatientDataValue> patientDataValueMap;

    public Map<Integer, PatientDataValue> getPatientDataValueMap()
    {
        return patientDataValueMap;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        OrganisationUnit organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        patient = patientService.getPatient( id );

        patientIdentifier = patientIdentifierService.getPatientIdentifier( patient );

        age = patient.getAge();

        program = programService.getProgram( programId );

        programStage = programStageService.getProgramStage( programStageId );

        dataElements = programStage.getDataElements();

        for ( DataElement dataElement : dataElements )
        {
            optionMap.put( dataElement.getId(), dataElement.getCategoryCombo().getOptionCombos() );
        }

        Collection<ProgramInstance> progamInstances = programInstanceService.getProgramInstances( patient, program,
            false );

        ProgramInstance programInstance = progamInstances.iterator().next();

        ProgramInstanceStage programInstanceStage = programInstanceStageService.getProgramInstanceStage(
            programInstance, programStage );

        Collection<PatientDataValue> patientDataValues = patientDataValueService.getPatientDataValues(
            organisationUnit, programInstanceStage );

        patientDataValueMap = new HashMap<Integer, PatientDataValue>( patientDataValues.size() );

        for ( PatientDataValue patientDataValue : patientDataValues )
        {
            patientDataValueMap.put( patientDataValue.getDataElement().getId(), patientDataValue );
        }

        return SUCCESS;
    }
}
