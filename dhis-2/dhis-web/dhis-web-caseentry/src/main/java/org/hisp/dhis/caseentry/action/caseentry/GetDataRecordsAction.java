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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.caseentry.state.SelectedStateManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.comparator.ProgramStageInstanceComparator;

public class GetDataRecordsAction
    extends ActionPagingSupport<Patient>
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1994974012114016133L;

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

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private PatientAttributeValueService patientAttributeValueService;

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Integer sortPatientAttributeId;

    public void setSortPatientAttributeId( Integer sortPatientAttributeId )
    {
        this.sortPatientAttributeId = sortPatientAttributeId;
    }

    public Integer getSortPatientAttributeId()
    {
        return sortPatientAttributeId;
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

    private Integer total;

    public Integer getTotal()
    {
        return total;
    }

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private Program program;

    public Program getProgram()
    {
        return program;
    }

    private Collection<ProgramInstance> programInstances = new ArrayList<ProgramInstance>();

    public Collection<ProgramInstance> getProgramInstances()
    {
        return programInstances;
    }

    private Map<ProgramInstance, List<ProgramStageInstance>> programStageInstanceMap = new HashMap<ProgramInstance, List<ProgramStageInstance>>();

    public Map<ProgramInstance, List<ProgramStageInstance>> getProgramStageInstanceMap()
    {
        return programStageInstanceMap;
    }

    private Map<Integer, String> colorMap = new HashMap<Integer, String>();

    public Map<Integer, String> getColorMap()
    {
        return colorMap;
    }

    private Map<Patient, ProgramInstance> programInstanceMap = new HashMap<Patient, ProgramInstance>();

    public Map<Patient, ProgramInstance> getProgramInstanceMap()
    {
        return programInstanceMap;
    }

    private Map<Patient, PatientAttributeValue> patinetAttributeValueMap = new HashMap<Patient, PatientAttributeValue>();

    public Map<Patient, PatientAttributeValue> getPatinetAttributeValueMap()
    {
        return patinetAttributeValueMap;
    }

    Collection<Patient> patientListByOrgUnit;

    public Collection<Patient> getPatientListByOrgUnit()
    {
        return patientListByOrgUnit;
    }

    List<Program> programs;

    public List<Program> getPrograms()
    {
        return programs;
    }

    private Collection<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>();

    public Collection<PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }

    private PatientAttribute sortingAttribute;

    public PatientAttribute getSortingAttribute()
    {
        return sortingAttribute;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Patient Attribute List
        // ---------------------------------------------------------------------

        patientAttributes = patientAttributeService.getAllPatientAttributes();

        organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        programs = new ArrayList<Program>( programService.getPrograms( organisationUnit ) );

        if ( programId == 0 )
        {
            selectedStateManager.clearSelectedProgram();

            return SUCCESS;
        }

        program = programService.getProgram( programId );

        selectedStateManager.setSelectedProgram( program );

        // ---------------------------------------------------------------------
        // Program instances for the selected program
        // ---------------------------------------------------------------------

        Collection<ProgramStageInstance> programStageInstances = new ArrayList<ProgramStageInstance>();

        total = patientService.countGetPatientsByOrgUnitProgram( organisationUnit, program );

        this.paging = createPaging( total );

        patientListByOrgUnit = new ArrayList<Patient>( patientService.getPatients( organisationUnit, program, paging
            .getStartPos(), paging.getPageSize() ) );

        for ( Patient patient : patientListByOrgUnit )
        {
            Collection<ProgramInstance> _programInstances = programInstanceService.getProgramInstances( patient,
                program, false );

            if ( _programInstances == null || _programInstances.size() == 0 )
            {
                programInstanceMap.put( patient, null );
            }
            else
            {
                for ( ProgramInstance programInstance : _programInstances )
                {
                    programInstanceMap.put( patient, programInstance );

                    programInstances.add( programInstance );

                    PatientAttributeValue patientAttributeValue = patientAttributeValueService
                        .getPatientAttributeValue( patient, sortingAttribute );

                    patinetAttributeValueMap.put( patient, patientAttributeValue );

                    List<ProgramStageInstance> programStageInstanceList = new ArrayList<ProgramStageInstance>(
                        programInstance.getProgramStageInstances() );
                    Collections.sort( programStageInstanceList, new ProgramStageInstanceComparator() );

                    programStageInstanceMap.put( programInstance, programStageInstanceList );
                    programStageInstances.addAll( programStageInstanceList );
                }
            }
        }

        // ---------------------------------------------------------------------
        // Sorting PatientList by selected Patient Attribute
        // ---------------------------------------------------------------------

        if ( sortPatientAttributeId != null )
        {
            sortingAttribute = patientAttributeService.getPatientAttribute( sortPatientAttributeId );

            patientListByOrgUnit = patientService.sortPatientsByAttribute( patientListByOrgUnit, sortingAttribute );
        }

        colorMap = programStageInstanceService.colorProgramStageInstances( programStageInstances );

        return SUCCESS;
    }
}
