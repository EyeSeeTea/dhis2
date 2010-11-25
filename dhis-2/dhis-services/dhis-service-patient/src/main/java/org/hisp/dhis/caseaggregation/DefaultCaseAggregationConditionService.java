/*
 * Copyright (c) 2004-2010, University of Oslo
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
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_PATIENT_ATTRIBUTE;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_PATIENT_PROPERTY;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_PROGRAM_STAGE_DATAELEMENT;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OPERATOR_AND;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.SEPARATOR_ID;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.SEPARATOR_OBJECT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.MathUtils;
import org.nfunk.jep.JEP;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 * 
 * @version DefaultPatientAggregationExpressionService.java Nov 17, 2010
 *          11:16:37 AM
 */

@Transactional
public class DefaultCaseAggregationConditionService
    implements CaseAggregationConditionService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CaseAggregationConditionStore aggregationConditionStore;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setAggregationConditionStore( CaseAggregationConditionStore aggregationConditionStore )
    {
        this.aggregationConditionStore = aggregationConditionStore;
    }

    // -------------------------------------------------------------------------
    // Implementation Method
    // -------------------------------------------------------------------------

    @Override
    public int addCaseAggregationCondition( CaseAggregationCondition caseAggregationCondition )
    {
        return aggregationConditionStore.save( caseAggregationCondition );
    }

    @Override
    public void deleteCaseAggregationCondition( CaseAggregationCondition caseAggregationCondition )
    {
        aggregationConditionStore.delete( caseAggregationCondition );
    }

    @Override
    public Collection<CaseAggregationCondition> getAllCaseAggregationCondition()
    {
        return aggregationConditionStore.getAll();
    }

    @Override
    public CaseAggregationCondition getCaseAggregationCondition( int id )
    {
        return aggregationConditionStore.get( id );

    }

    @Override
    public void updateCaseAggregationCondition( CaseAggregationCondition caseAggregationCondition )
    {
        aggregationConditionStore.update( caseAggregationCondition );
    }

    @Override
    public CaseAggregationCondition getCaseAggregationCondition( DataElement dataElement,
        DataElementCategoryOptionCombo optionCombo )
    {
        return aggregationConditionStore.get( dataElement, optionCombo );
    }

    @Override
    public double parseConditition( CaseAggregationCondition aggregationCondition, OrganisationUnit orgunit,
        Period period )
    {
        int orgunitId = orgunit.getId();
        String startDate = DateUtils.getMediumDateString( period.getStartDate() );
        String endDate = DateUtils.getMediumDateString( period.getEndDate() );

        // ---------------------------------------------------------------------
        // get operators
        // ---------------------------------------------------------------------

        Pattern patternOperator = Pattern.compile( "(AND|OR)" );

        Matcher matcherOperator = patternOperator.matcher( aggregationCondition.getAggregationExpression() );

        List<String> operators = new ArrayList<String>();

        while ( matcherOperator.find() )
        {
            operators.add( matcherOperator.group() );
        }

        String[] expression = aggregationCondition.getAggregationExpression().split( "(AND|OR)" );

        String regExp = "\\[(" + OBJECT_PROGRAM_STAGE_DATAELEMENT + "|" + OBJECT_PATIENT_ATTRIBUTE + "|"
            + OBJECT_PATIENT_PROPERTY + ")" + SEPARATOR_OBJECT + "([a-zA-Z0-9]+[" + SEPARATOR_ID + "[0-9]*]*)" + "\\]";

        // ---------------------------------------------------------------------
        // parse expressions
        // ---------------------------------------------------------------------

        Pattern patternCondition = Pattern.compile( regExp );

        List<String> conditions = new ArrayList<String>();
        double value = 0.0;

        for ( int i = 0; i < expression.length; i++ )
        {
            String subExp = expression[i];
            List<String> subConditions = new ArrayList<String>();

            Matcher matcherCondition = patternCondition.matcher( expression[i] );
            String condition = "";

            while ( matcherCondition.find() )
            {
                String match = matcherCondition.group();
                subExp = subExp.replace( match, "~" );
                match = match.replaceAll( "[\\[\\]]", "" );

                String[] info = match.split( SEPARATOR_OBJECT );

                if ( info[0].equalsIgnoreCase( OBJECT_PATIENT_PROPERTY ) )
                {
                    String propertyName = info[1];
                    condition = getCondititionForPatientProperty( propertyName, orgunitId, startDate, endDate );

                }
                else if ( info[0].equalsIgnoreCase( OBJECT_PATIENT_ATTRIBUTE ) )
                {
                    int attributeId = Integer.parseInt( info[1] );
                    condition = getCondititionForPatientAttribute( attributeId, orgunitId, startDate, endDate );
                }
                else if ( info[0].equalsIgnoreCase( OBJECT_PROGRAM_STAGE_DATAELEMENT ) )
                {
                    String[] ids = info[1].split( SEPARATOR_ID );

                    int programStageId = Integer.parseInt( ids[0] );
                    int dataElementId = Integer.parseInt( ids[1] );
                    int optionComboId = Integer.parseInt( ids[2] );

                    condition = getCondititionForDataElement( programStageId, dataElementId, optionComboId, orgunitId,
                        startDate, endDate );
                    if ( !expression[i].contains( "+" ) )
                    {
                        condition += " AND pd.value ";
                    }
                    else
                    {
                        subConditions.add( condition );
                    }
                }

                // -------------------------------------------------------------
                // Replacing the operand with 1 in order to later be able to
                // verify
                // that the formula is mathematically valid
                // -------------------------------------------------------------

                if ( expression[i].contains( "+" ) )
                {
                    Collection<Integer> patientIds = aggregationConditionStore.executeSQL( condition );
                    value = calValue( patientIds, AGGRERATION_SUM );
                        
                    subExp = subExp.replace(  "~", value + "" );
                }

                condition = expression[i].replace( match, condition ).replaceAll( "[\\[\\]]", "" );
            }

            if ( expression[i].contains( "+" ) )
            {
                final JEP parser = new JEP();
                
                parser.parseExpression( subExp );
                
                String _subExp = (  parser.getValue() == 1.0 ) ? " AND 1 = 1 " : " AND 0 = 1 ";
                
                int noPlus = expression[i].split( "\\+" ).length - 1;
                List<String> subOperators = new ArrayList<String>();
                for ( int j = 0; j < noPlus; j++ )
                {
                    subOperators.add( "AND" );
                }

                condition = getSQL( subConditions, subOperators ) + _subExp;
            }

            conditions.add( condition );
        }

        String sqlResult = getSQL( conditions, operators );
        
        Collection<Integer> patientIds = aggregationConditionStore.executeSQL( sqlResult );

        return calValue( patientIds, aggregationCondition.getOperator() );
    }

    // -------------------------------------------------------------------------
    // Support Methods
    // -------------------------------------------------------------------------

    private String getCondititionForDataElement( int programStageId, int dataElementId, int optionComboId,
        int orgunitId, String startDate, String endDate )
    {
        return "SELECT distinct(pi.patientid) FROM programstageinstance as psi "
            + "INNER JOIN programstage as ps ON psi.programstageid = ps.programstageid "
            + "INNER JOIN patientdatavalue as pd ON psi.programstageinstanceid = pd.programstageinstanceid "
            + "INNER JOIN programinstance as pi ON pi.programinstanceid = psi.programinstanceid "
            + "WHERE pd.categoryoptioncomboid = " + optionComboId + " AND pd.dataelementid = " + dataElementId + " "
            + " AND pd.organisationunitid = " + orgunitId + " AND ps.programstageid = " + programStageId + " "
            + " AND psi.executionDate >= '" + startDate + "' AND psi.executionDate <= '" + endDate + "' ";

    }

    private String getCondititionForPatientAttribute( int attributeId, int orgunitId, String startDate, String endDate )
    {
        return "distinct(pi.patientid) FROM programstageinstance as psi "
            + "INNER JOIN programstage as ps ON psi.programstageid = ps.programstageid "
            + "INNER JOIN patientdatavalue as pd ON psi.programstageinstanceid = pd.programstageinstanceid "
            + "INNER JOIN programinstance as pi ON pi.programinstanceid = psi.programinstanceid "
            + "INNER JOIN patientattributevalue as pav ON pav.patientid = pi.patientid "
            + "WHERE pa.patientattributevalueid = " + attributeId + " " + " AND pd.organisationunitid = " + orgunitId
            + " " + " AND psi.executionDate >= '" + startDate + "' AND psi.executionDate <= '" + endDate + "' "
            + "AND pav.value ";
    }

    private String getCondititionForPatientProperty( String propertyName, int orgunitId, String startDate,
        String endDate )
    {
        return "SELECT distinct(p.patientid) FROM programstageinstance as psi INNER JOIN programstage as ps "
            + "ON psi.programstageid = ps.programstageid INNER JOIN patientdatavalue as pd ON "
            + "psi.programstageinstanceid = pd.programstageinstanceid INNER JOIN programinstance as pi ON "
            + "psi.programinstanceid = pi.programinstanceid INNER JOIN patient as p ON "
            + "p.patientid = pi.patientid WHERE pd.organisationunitid = " + orgunitId + " "
            + "AND psi.executionDate >= '" + startDate + "' AND psi.executionDate <= '" + endDate + "' AND p."
            + propertyName + " ";
    }

    private String getSQL( List<String> conditions, List<String> operators )
    {
        String sql = conditions.get( 0 );

        String sqlAnd = "";

        int index = 0;

        for ( index = 0; index < operators.size(); index++ )
        {
            if ( operators.get( index ).equalsIgnoreCase( OPERATOR_AND ) )
            {
                sql += " AND pi.patientid IN ( " + conditions.get( index + 1 );
                sqlAnd += ")";
            }
            else
            {
                sql += sqlAnd;
                sql += " UNION ( " + conditions.get( index + 1 ) + " ) ";
                sqlAnd = "";
            }

        }
        sql += sqlAnd;

        return sql;

    }

    public double calValue( Collection<Integer> patientIds, String operator )
    {
        if ( patientIds == null )
            return 0.0;

        // if ( operator.equalsIgnoreCase( AGGRERATION_SUM ) )
        // {
        // double value = 0.0;
        // }

        return patientIds.size();
    }

//    public static void main( String[] args )
//    {
//
//        CaseAggregationCondition aggregationCondition = new CaseAggregationCondition();
//
//        aggregationCondition.setOperator( CaseAggregationCondition.AGGRERATION_COUNT );
//
//        // aggregationCondition.setAggregationExpression(
//        // "( [DE:1.1.1] + [DE:1.1.1] + [DE:1.1.1] > 0 " );
//        aggregationCondition
//            .setAggregationExpression( " [DE:15.116.1]  +  [DE:16.117.1] > 0" );
//        // );
//
//        DefaultCaseAggregationConditionService service = new DefaultCaseAggregationConditionService();
//
//        service.parseConditition( aggregationCondition, null, null );
//    }

}
