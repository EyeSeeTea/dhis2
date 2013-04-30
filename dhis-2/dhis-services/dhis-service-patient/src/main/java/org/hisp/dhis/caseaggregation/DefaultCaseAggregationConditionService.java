/*
 * Copyright (c) 2004-2012, University of Oslo
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

package org.hisp.dhis.caseaggregation;

import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.AGGRERATION_SUM;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_PATIENT;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_PATIENT_ATTRIBUTE;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_PROGRAM;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_PROGRAM_STAGE;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_PROGRAM_STAGE_DATAELEMENT;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.SEPARATOR_ID;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.SEPARATOR_OBJECT;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_ORGUNIT_COMPLETE_PROGRAM_STAGE;

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.system.util.ConcurrentUtils;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.SystemUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 */
@Transactional
public class DefaultCaseAggregationConditionService
    implements CaseAggregationConditionService
{
    private final String INVALID_CONDITION = "Invalid condition";

    private final String TOTAL_OF_PATIENTS_REGISTERED = "Total of patient registration";

    private final String IN_CONDITION_GET_ALL = "*";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CaseAggregationConditionStore aggregationConditionStore;

    private CaseAggregationConditionManager aggregationConditionManager;

    private DataElementService dataElementService;

    private PatientService patientService;

    private PatientDataValueService dataValueService;

    private ProgramStageService programStageService;

    private ProgramService programService;

    private PatientAttributeService patientAttributeService;

    private ProgramStageInstanceService programStageInstanceService;

    private I18nService i18nService;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setAggregationConditionStore( CaseAggregationConditionStore aggregationConditionStore )
    {
        this.aggregationConditionStore = aggregationConditionStore;
    }

    public void setAggregationConditionManager( CaseAggregationConditionManager aggregationConditionManager )
    {
        this.aggregationConditionManager = aggregationConditionManager;
    }

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // Implementation Methods
    // -------------------------------------------------------------------------

    @Override
    public int addCaseAggregationCondition( CaseAggregationCondition caseAggregationCondition )
    {
        return aggregationConditionStore.save( caseAggregationCondition );
    }

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public void setDataValueService( PatientDataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    @Override
    public void deleteCaseAggregationCondition( CaseAggregationCondition caseAggregationCondition )
    {
        aggregationConditionStore.delete( caseAggregationCondition );
    }

    @Override
    public Collection<CaseAggregationCondition> getAllCaseAggregationCondition()
    {
        return i18n( i18nService, aggregationConditionStore.getAll() );
    }

    @Override
    public CaseAggregationCondition getCaseAggregationCondition( int id )
    {
        return i18n( i18nService, aggregationConditionStore.get( id ) );

    }

    @Override
    public void updateCaseAggregationCondition( CaseAggregationCondition caseAggregationCondition )
    {
        aggregationConditionStore.update( caseAggregationCondition );
    }

    @Override
    public Collection<CaseAggregationCondition> getCaseAggregationCondition( DataElement dataElement )
    {
        return i18n( i18nService, aggregationConditionStore.get( dataElement ) );
    }

    @Override
    public CaseAggregationCondition getCaseAggregationCondition( DataElement dataElement,
        DataElementCategoryOptionCombo optionCombo )
    {
        return i18n( i18nService, aggregationConditionStore.get( dataElement, optionCombo ) );
    }

    @Override
    public Double getAggregateValue( CaseAggregationCondition aggregationCondition, OrganisationUnit orgunit,
        Period period )
    {
        DataElement deSum = aggregationCondition.getDeSum();
        Integer deSumId = (deSum == null) ? null : deSum.getId();

        return aggregationConditionManager.getAggregateValue( aggregationCondition.getAggregationExpression(),
            aggregationCondition.getOperator(), deSumId, orgunit.getId(), period );
    }

    @Override
    public Collection<PatientDataValue> getPatientDataValues( CaseAggregationCondition aggregationCondition,
        OrganisationUnit orgunit, Period period )
    {
        // get params

        int orgunitId = orgunit.getId();
        String startDate = DateUtils.getMediumDateString( period.getStartDate() );
        String endDate = DateUtils.getMediumDateString( period.getEndDate() );
        DataElement deSum = aggregationCondition.getDeSum();
        Integer deSumId = (deSum == null) ? null : deSum.getId();

        Collection<PatientDataValue> result = new HashSet<PatientDataValue>();

        String sql = aggregationConditionManager.parseExpressionToSql( aggregationCondition.getAggregationExpression(),
            aggregationCondition.getOperator(), deSumId, orgunitId, startDate, endDate );

        Collection<DataElement> dataElements = getDataElementsInCondition( aggregationCondition
            .getAggregationExpression() );

        if ( dataElements.size() > 0 )
        {
            Collection<Integer> patientIds = aggregationConditionManager.executeSQL( sql );

            for ( Integer patientId : patientIds )
            {
                Patient patient = patientService.getPatient( patientId );

                Collection<PatientDataValue> dataValues = dataValueService.getPatientDataValues( patient, dataElements,
                    period.getStartDate(), period.getEndDate() );

                result.addAll( dataValues );
            }
        }

        return result;
    }

    public Collection<Patient> getPatients( CaseAggregationCondition aggregationCondition, OrganisationUnit orgunit,
        Period period )
    {
        DataElement deSum = aggregationCondition.getDeSum();
        Integer deSumId = (deSum == null) ? null : deSum.getId();

        String sql = aggregationConditionManager
            .parseExpressionToSql( aggregationCondition.getAggregationExpression(), aggregationCondition.getOperator(),
                deSumId, orgunit.getId(), DateUtils.getMediumDateString( period.getStartDate() ),
                DateUtils.getMediumDateString( period.getEndDate() ) );

        Collection<Patient> result = new HashSet<Patient>();

        Collection<Integer> patientIds = aggregationConditionManager.executeSQL( sql );

        if ( patientIds != null )
        {
            for ( Integer patientId : patientIds )
            {
                result.add( patientService.getPatient( patientId ) );
            }
        }

        return result;
    }

    public Collection<ProgramStageInstance> getProgramStageInstances( CaseAggregationCondition aggregationCondition,
        OrganisationUnit orgunit, Period period )
    {
        Collection<ProgramStageInstance> result = new HashSet<ProgramStageInstance>();

        // get params
        int orgunitId = orgunit.getId();
        String startDate = DateUtils.getMediumDateString( period.getStartDate() );
        String endDate = DateUtils.getMediumDateString( period.getEndDate() );

        String operator = aggregationCondition.getOperator();
        String sql = "";
        if ( operator.equals( CaseAggregationCondition.AGGRERATION_COUNT )
            || operator.equals( CaseAggregationCondition.AGGRERATION_SUM ) )
        {
            aggregationCondition.setOperator( AGGRERATION_SUM );

            // get params

            DataElement deSum = aggregationCondition.getDeSum();
            Integer deSumId = (deSum == null) ? null : deSum.getId();

            sql = aggregationConditionManager.parseExpressionToSql( aggregationCondition.getAggregationExpression(),
                aggregationCondition.getOperator(), deSumId, orgunitId, startDate, endDate );
        }
        else
        {
            sql = "SELECT psi.programstageinstanceid ";
            sql += "FROM patientdatavalue pdv ";
            sql += "    INNER JOIN programstageinstance psi  ";
            sql += "    ON psi.programstageinstanceid = pdv.programstageinstanceid ";
            sql += "WHERE executiondate >='" + DateUtils.getMediumDateString( period.getStartDate() ) + "'  ";
            sql += "    AND executiondate <='" + DateUtils.getMediumDateString( period.getEndDate() )
                + "' AND pdv.dataelementid=" + aggregationCondition.getDeSum().getId();

            if ( aggregationCondition.getAggregationExpression() != null
                && !aggregationCondition.getAggregationExpression().isEmpty() )
            {
                sql = sql + " AND pdv.programstageinstanceid in ( ";

                // Get params

                DataElement deSum = aggregationCondition.getDeSum();
                Integer deSumId = (deSum == null) ? null : deSum.getId();

                String conditionSql = aggregationConditionManager.parseExpressionToSql(
                    aggregationCondition.getAggregationExpression(), aggregationCondition.getOperator(), deSumId,
                    orgunit.getId(), DateUtils.getMediumDateString( period.getStartDate() ),
                    DateUtils.getMediumDateString( period.getEndDate() ) );

                sql += conditionSql + " ) ";
            }
        }

        Collection<Integer> stageInstanceIds = aggregationConditionManager.executeSQL( sql );

        for ( Integer stageInstanceId : stageInstanceIds )
        {
            result.add( programStageInstanceService.getProgramStageInstance( stageInstanceId ) );
        }

        return result;
    }

    public String getConditionDescription( String condition )
    {
        StringBuffer description = new StringBuffer();

        Pattern patternCondition = Pattern.compile( CaseAggregationCondition.regExp );

        Matcher matcher = patternCondition.matcher( condition );

        while ( matcher.find() )
        {
            String match = matcher.group();
            match = match.replaceAll( "[\\[\\]]", "" );

            String[] info = match.split( SEPARATOR_OBJECT );

            if ( info[0].equalsIgnoreCase( OBJECT_PROGRAM_STAGE_DATAELEMENT ) )
            {
                String[] ids = info[1].split( SEPARATOR_ID );

                int programId = Integer.parseInt( ids[0] );
                Program program = programService.getProgram( programId );

                String programStage = ids[1];
                if ( !programStage.equals( IN_CONDITION_GET_ALL ) )
                {
                    programStage = programStageService.getProgramStage( Integer.parseInt( programStage ) ).getName();
                }
                int dataElementId = Integer.parseInt( ids[2] );
                DataElement dataElement = dataElementService.getDataElement( dataElementId );

                if ( program == null || dataElement == null )
                {
                    return INVALID_CONDITION;
                }

                matcher.appendReplacement( description, "[" + program.getDisplayName() + SEPARATOR_ID + programStage
                    + SEPARATOR_ID + dataElement.getName() + "]" );
            }
            else
            {
                String[] ids = info[1].split( SEPARATOR_ID );

                if ( info[0].equalsIgnoreCase( OBJECT_PATIENT ) )
                {
                    matcher.appendReplacement( description, "[" + OBJECT_PATIENT + SEPARATOR_OBJECT
                        + TOTAL_OF_PATIENTS_REGISTERED + "]" );
                }
                else if ( info[0].equalsIgnoreCase( OBJECT_PATIENT_ATTRIBUTE ) )
                {
                    int objectId = Integer.parseInt( ids[0] );

                    PatientAttribute patientAttribute = patientAttributeService.getPatientAttribute( objectId );

                    if ( patientAttribute == null )
                    {
                        return INVALID_CONDITION;
                    }

                    matcher.appendReplacement( description, "[" + OBJECT_PATIENT_ATTRIBUTE + SEPARATOR_OBJECT
                        + patientAttribute.getDisplayName() + "]" );
                }
                else if ( info[0].equalsIgnoreCase( OBJECT_PROGRAM ) )
                {
                    int objectId = Integer.parseInt( ids[0] );

                    Program program = programService.getProgram( objectId );

                    if ( program == null )
                    {
                        return INVALID_CONDITION;
                    }

                    String programDes = OBJECT_PROGRAM + SEPARATOR_ID + program.getDisplayName();
                    if ( ids.length == 2 )
                    {
                        programDes += SEPARATOR_OBJECT + ids[1];
                    }
                    matcher.appendReplacement( description, "[" + programDes + "]" );
                }
                else if ( info[0].equalsIgnoreCase( OBJECT_PROGRAM_STAGE ) 
                        || info[0].equalsIgnoreCase( OBJECT_ORGUNIT_COMPLETE_PROGRAM_STAGE ) )
                {
                    int objectId = Integer.parseInt( ids[0] );
                    ProgramStage programStage = programStageService.getProgramStage( objectId );

                    if ( programStage == null )
                    {
                        return INVALID_CONDITION;
                    }

                    String count = (ids.length == 2) ? SEPARATOR_ID + ids[1] : "";
                    matcher.appendReplacement( description, "[" + info[0] + SEPARATOR_OBJECT
                        + programStage.getDisplayName() + count + "]" );
                }
            }

        }

        matcher.appendTail( description );

        return description.toString();
    }

    public Collection<DataElement> getDataElementsInCondition( String aggregationExpression )
    {
        String regExp = "\\[" + OBJECT_PROGRAM_STAGE_DATAELEMENT + SEPARATOR_OBJECT + "[0-9]+" + SEPARATOR_ID
            + "[0-9]+" + SEPARATOR_ID + "[0-9]+" + "\\]";

        Collection<DataElement> dataElements = new HashSet<DataElement>();

        // ---------------------------------------------------------------------
        // parse expressions
        // ---------------------------------------------------------------------

        Pattern pattern = Pattern.compile( regExp );

        Matcher matcher = pattern.matcher( aggregationExpression );

        while ( matcher.find() )
        {
            String match = matcher.group();
            match = match.replaceAll( "[\\[\\]]", "" );

            String[] info = match.split( SEPARATOR_OBJECT );
            String[] ids = info[1].split( SEPARATOR_ID );

            int dataElementId = Integer.parseInt( ids[2] );
            DataElement dataElement = dataElementService.getDataElement( dataElementId );

            dataElements.add( dataElement );
        }

        return dataElements;
    }

    public Collection<Program> getProgramsInCondition( String aggregationExpression )
    {
        String regExp = "\\[(" + OBJECT_PROGRAM + "|" + OBJECT_PROGRAM_STAGE_DATAELEMENT + ")" + SEPARATOR_OBJECT
            + "[a-zA-Z0-9\\- ]+";

        Collection<Program> programs = new HashSet<Program>();

        // ---------------------------------------------------------------------
        // parse expressions
        // ---------------------------------------------------------------------

        Pattern pattern = Pattern.compile( regExp );

        Matcher matcher = pattern.matcher( aggregationExpression );

        while ( matcher.find() )
        {
            String match = matcher.group();
            match = match.replaceAll( "[\\[\\]]", "" );

            String[] info = match.split( SEPARATOR_OBJECT );
            String[] ids = info[1].split( SEPARATOR_ID );

            int programId = Integer.parseInt( ids[0] );
            Program program = programService.getProgram( programId );

            programs.add( program );
        }

        return programs;
    }

    public Collection<PatientAttribute> getPatientAttributesInCondition( String aggregationExpression )
    {
        String regExp = "\\[" + OBJECT_PATIENT_ATTRIBUTE + SEPARATOR_OBJECT + "[0-9]+\\]";

        Collection<PatientAttribute> patientAttributes = new HashSet<PatientAttribute>();

        // ---------------------------------------------------------------------
        // parse expressions
        // ---------------------------------------------------------------------

        Pattern pattern = Pattern.compile( regExp );

        Matcher matcher = pattern.matcher( aggregationExpression );

        while ( matcher.find() )
        {
            String match = matcher.group();
            match = match.replaceAll( "[\\[\\]]", "" );

            String[] info = match.split( SEPARATOR_OBJECT );

            int patientAttributeId = Integer.parseInt( info[1] );
            PatientAttribute patientAttribute = patientAttributeService.getPatientAttribute( patientAttributeId );

            patientAttributes.add( patientAttribute );
        }

        return patientAttributes;
    }

    public Collection<CaseAggregationCondition> getCaseAggregationCondition( Collection<DataElement> dataElements )
    {
        return i18n( i18nService, aggregationConditionStore.get( dataElements ) );
    }

    public void aggregate( List<CaseAggregateSchedule> caseAggregateSchedules, String taskStrategy )
    {
        ConcurrentLinkedQueue<CaseAggregateSchedule> datasetQ = new ConcurrentLinkedQueue<CaseAggregateSchedule>(
            caseAggregateSchedules );

        List<Future<?>> futures = new ArrayList<Future<?>>();

        for ( int i = 0; i < getProcessNo(); i++ )
        {
            futures.add( aggregationConditionManager.aggregate( datasetQ, taskStrategy ) );
        }

        ConcurrentUtils.waitForCompletion( futures );
    }

    public boolean hasOrgunitProgramStageCompleted( String expresstion )
    {
        return aggregationConditionManager.hasOrgunitProgramStageCompleted( expresstion );
    }

    // -------------------------------------------------------------------------
    // Support Methods
    // -------------------------------------------------------------------------

    private int getProcessNo()
    {
        return Math.max( (SystemUtils.getCpuCores() - 1), 1 );
    }

    public Integer calValue( Collection<Integer> patientIds, String operator )
    {
        return patientIds.size();
    }
}
