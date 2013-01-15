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
package org.hisp.dhis.program.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patientreport.PatientAggregateReport;
import org.hisp.dhis.patientreport.TabularReportColumn;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceStore;
import org.hisp.dhis.program.SchedulingProgramObject;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.system.grid.GridUtils;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.TextUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * @author Abyot Asalefew
 */
public class HibernateProgramStageInstanceStore
    extends HibernateGenericStore<ProgramStageInstance>
    implements ProgramStageInstanceStore
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private StatementBuilder statementBuilder;

    public void setStatementBuilder( StatementBuilder statementBuilder )
    {
        this.statementBuilder = statementBuilder;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    // -------------------------------------------------------------------------
    // Implemented methods
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    public ProgramStageInstance get( ProgramInstance programInstance, ProgramStage programStage )
    {
        List<ProgramStageInstance> list = new ArrayList<ProgramStageInstance>( getCriteria(
            Restrictions.eq( "programInstance", programInstance ), Restrictions.eq( "programStage", programStage ) )
            .addOrder( Order.asc( "id" ) ).list() );

        return (list == null || list.size() == 0) ? null : list.get( list.size() - 1 );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ProgramStageInstance> get( ProgramStage programStage )
    {
        return getCriteria( Restrictions.eq( "programStage", programStage ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ProgramStageInstance> get( Collection<ProgramInstance> programInstances )
    {
        return getCriteria( Restrictions.in( "programInstance", programInstances ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ProgramStageInstance> get( Date dueDate )
    {
        return getCriteria( Restrictions.eq( "dueDate", dueDate ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ProgramStageInstance> get( Date dueDate, Boolean completed )
    {
        return getCriteria( Restrictions.eq( "dueDate", dueDate ), Restrictions.eq( "completed", completed ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ProgramStageInstance> get( Date startDate, Date endDate )
    {
        return (getCriteria( Restrictions.ge( "dueDate", startDate ), Restrictions.le( "dueDate", endDate ) )).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ProgramStageInstance> get( Date startDate, Date endDate, Boolean completed )
    {
        return (getCriteria( Restrictions.ge( "dueDate", startDate ), Restrictions.le( "dueDate", endDate ),
            Restrictions.eq( "completed", completed ) )).list();
    }

    @SuppressWarnings( "unchecked" )
    public List<ProgramStageInstance> get( OrganisationUnit unit, Date after, Date before, Boolean completed )
    {
        String hql = "from ProgramStageInstance psi where psi.organisationUnit = :unit";

        if ( after != null )
        {
            hql += " and dueDate >= :after";
        }

        if ( before != null )
        {
            hql += " and dueDate <= :before";
        }

        if ( completed != null )
        {
            hql += " and completed = :completed";
        }

        Query q = getQuery( hql ).setEntity( "unit", unit );

        if ( after != null )
        {
            q.setDate( "after", after );
        }

        if ( before != null )
        {
            q.setDate( "before", before );
        }

        if ( completed != null )
        {
            q.setBoolean( "completed", completed );
        }

        return q.list();
    }

    @SuppressWarnings( "unchecked" )
    public List<ProgramStageInstance> get( Patient patient, Boolean completed )
    {
        String hql = "from ProgramStageInstance where programInstance.patient = :patient and completed = :completed";

        return getQuery( hql ).setEntity( "patient", patient ).setBoolean( "completed", completed ).list();
    }

    @SuppressWarnings( "unchecked" )
    public List<ProgramStageInstance> get( ProgramStage programStage, OrganisationUnit orgunit, Date startDate,
        Date endDate, int min, int max )
    {
        return getCriteria( Restrictions.eq( "programStage", programStage ),
            Restrictions.eq( "organisationUnit", orgunit ), Restrictions.between( "dueDate", startDate, endDate ) )
            .setFirstResult( min ).setMaxResults( max ).list();
    }

    public Grid getTabularReport( ProgramStage programStage, Map<Integer, OrganisationUnitLevel> orgUnitLevelMap,
        Collection<Integer> orgUnits, List<TabularReportColumn> columns, int level, int maxLevel, Date startDate,
        Date endDate, boolean descOrder, Boolean completed, Integer min, Integer max )
    {
        // ---------------------------------------------------------------------
        // Headers cols
        // ---------------------------------------------------------------------

        Grid grid = new ListGrid();

        grid.addHeader( new GridHeader( "id", true, true ) );
        grid.addHeader( new GridHeader( programStage.getReportDateDescription(), false, true ) );

        for ( int i = level; i <= maxLevel; i++ )
        {
            String name = orgUnitLevelMap.containsKey( i ) ? orgUnitLevelMap.get( i ).getName() : "Level " + i;
            grid.addHeader( new GridHeader( name, false, true ) );
        }

        Collection<String> deKeys = new HashSet<String>();
        for ( TabularReportColumn column : columns )
        {
            if ( !column.isMeta() )
            {
                String deKey = "element_" + column.getIdentifier();
                if ( !deKeys.contains( deKey ) )
                {
                    grid.addHeader( new GridHeader( column.getName(), column.isHidden(), true ) );
                    deKeys.add( deKey );
                }
            }
        }

        grid.addHeader( new GridHeader( "Complete", true, true ) );

        // ---------------------------------------------------------------------
        // Get SQL and build grid
        // ---------------------------------------------------------------------

        String sql = getTabularReportSql( false, programStage, columns, orgUnits, level, maxLevel, startDate, endDate,
            descOrder, completed, min, max );

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

        GridUtils.addRows( grid, rowSet );

        return grid;
    }

    public int getTabularReportCount( ProgramStage programStage, List<TabularReportColumn> columns,
        Collection<Integer> organisationUnits, int level, int maxLevel, Date startDate, Date endDate, Boolean completed )
    {
        String sql = getTabularReportSql( true, programStage, columns, organisationUnits, level, maxLevel, startDate,
            endDate, false, completed, null, null );

        return jdbcTemplate.queryForInt( sql );
    }

    public void removeEmptyEvents( ProgramStage programStage, OrganisationUnit organisationUnit )
    {
        String sql = "delete from programstageinstance where programstageid=" + programStage.getId()
            + " and organisationunitid=" + organisationUnit.getId() + " and programstageinstanceid not in "
            + "(select pdv.programstageinstanceid from patientdatavalue pdv )";
        jdbcTemplate.execute( sql );
    }

    @Override
    public void update( Collection<Integer> programStageInstanceIds, OutboundSms outboundSms )
    {
        for ( Integer programStageInstanceId : programStageInstanceIds )
        {
            if ( programStageInstanceId != null && programStageInstanceId != 0 )
            {
                ProgramStageInstance programStageInstance = get( programStageInstanceId );

                List<OutboundSms> outboundSmsList = programStageInstance.getOutboundSms();

                if ( outboundSmsList == null )
                {
                    outboundSmsList = new ArrayList<OutboundSms>();
                }

                outboundSmsList.add( outboundSms );
                programStageInstance.setOutboundSms( outboundSmsList );
                update( programStageInstance );
            }
        }
    }

    public Collection<SchedulingProgramObject> getSendMesssageEvents()
    {
        String sql = "select psi.programstageinstanceid, p.phonenumber, prm.templatemessage, p.firstname, p.middlename, p.lastname, org.name as orgunitName "
            + ",pg.name as programName, ps.name as programStageName, psi.duedate,(DATE(now()) - DATE(psi.duedate) ) as days_since_due_date,psi.duedate "
            + "from patient p INNER JOIN programinstance pi "
            + "     ON p.patientid=pi.patientid "
            + " INNER JOIN programstageinstance psi  "
            + "     ON psi.programinstanceid=pi.programinstanceid "
            + " INNER JOIN program pg  "
            + "     ON pg.programid=pi.programid "
            + " INNER JOIN programstage ps  "
            + "     ON ps.programstageid=psi.programstageid "
            + " INNER JOIN organisationunit org  "
            + "     ON org.organisationunitid = p.organisationunitid "
            + " INNER JOIN patientreminder prm  "
            + "     ON prm.programstageid = ps.programstageid "
            + "WHERE pi.completed=false  "
            + "     and p.phonenumber is not NULL and p.phonenumber != '' "
            + "     and prm.templatemessage is not NULL and prm.templatemessage != '' "
            + "     and pg.type=1 and prm.daysallowedsendmessage is not null  "
            + "     and psi.executiondate is null "
            + "     and (  DATE(now()) - DATE(psi.duedate) ) = prm.daysallowedsendmessage ";

        SqlRowSet rs = jdbcTemplate.queryForRowSet( sql );

        int cols = rs.getMetaData().getColumnCount();

        Collection<SchedulingProgramObject> schedulingProgramObjects = new HashSet<SchedulingProgramObject>();

        while ( rs.next() )
        {
            String message = "";
            for ( int i = 1; i <= cols; i++ )
            {

                message = rs.getString( "templatemessage" );
                String patientName = rs.getString( "firstName" );
                String organisationunitName = rs.getString( "orgunitName" );
                String programName = rs.getString( "programName" );
                String programStageName = rs.getString( "programStageName" );
                String daysSinceDueDate = rs.getString( "days_since_due_date" );
                String dueDate = rs.getString( "duedate" );

                message = message.replace( ProgramStage.TEMPLATE_MESSSAGE_PATIENT_NAME, patientName );
                message = message.replace( ProgramStage.TEMPLATE_MESSSAGE_PROGRAM_NAME, programName );
                message = message.replace( ProgramStage.TEMPLATE_MESSSAGE_PROGAM_STAGE_NAME, programStageName );
                message = message.replace( ProgramStage.TEMPLATE_MESSSAGE_DUE_DATE, dueDate );
                message = message.replace( ProgramStage.TEMPLATE_MESSSAGE_ORGUNIT_NAME, organisationunitName );
                message = message.replace( ProgramStage.TEMPLATE_MESSSAGE_DAYS_SINCE_DUE_DATE, daysSinceDueDate );
            }

            SchedulingProgramObject schedulingProgramObject = new SchedulingProgramObject();
            schedulingProgramObject.setProgramStageInstanceId( rs.getInt( "programstageinstanceid" ) );
            schedulingProgramObject.setPhoneNumber( rs.getString( "phonenumber" ) );
            schedulingProgramObject.setMessage( message );

            schedulingProgramObjects.add( schedulingProgramObject );
        }

        return schedulingProgramObjects;
    }

    public int getStatisticalProgramStageReport( ProgramStage programStage, Collection<Integer> orgunitIds,
        Date startDate, Date endDate, int status )
    {
        Criteria criteria = getStatisticalProgramStageCriteria( programStage, orgunitIds, startDate, endDate, status );

        Number rs = (Number) criteria.setProjection( Projections.rowCount() ).uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    @SuppressWarnings( "unchecked" )
    public List<ProgramStageInstance> getStatisticalProgramStageDetailsReport( ProgramStage programStage,
        Collection<Integer> orgunitIds, Date startDate, Date endDate, int status, Integer min, Integer max )
    {
        Criteria criteria = getStatisticalProgramStageCriteria( programStage, orgunitIds, startDate, endDate, status );

        if ( min != null && max != null )
        {
            criteria.setFirstResult( min );
            criteria.setMaxResults( max );
        }

        return criteria.list();
    }

    public Grid getAggregateReport( int position, ProgramStage programStage, Collection<Integer> orgunitIds,
        Integer dataElementId, Map<Integer, String> deFilters, Collection<Period> periods, String aggregateType,
        Integer limit, I18nFormat format, I18n i18n )
    {
        String sql = "";
        String filterSQL = filterSQLStatement( deFilters );

        Grid grid = new ListGrid();
        grid.setTitle( programStage.getProgram().getName() );
        grid.setSubtitle( programStage.getName() );

        // Type = 1
        if ( position == PatientAggregateReport.POSITION_ROW_ORGUNIT_COLUMN_PERIOD )
        {
            // ---------------------------------------------------------------------
            // Headers cols
            // ---------------------------------------------------------------------

            grid.addHeader( new GridHeader( i18n.getString( "orgunit" ), false, true ) );

            for ( Period period : periods )
            {
                grid.addHeader( new GridHeader( format.formatPeriod( period ), false, false ) );
            }

            // ---------------------------------------------------------------------
            // Get SQL and build grid
            // ---------------------------------------------------------------------

            sql = getAggregateReportSQL12( programStage, orgunitIds, filterSQL, periods, aggregateType, format );
        }
        // Type = 2
        if ( position == PatientAggregateReport.POSITION_ROW_PERIOD_COLUMN_ORGUNIT )
        {
            // ---------------------------------------------------------------------
            // Get SQL and build grid
            // ---------------------------------------------------------------------

            sql = getAggregateReportSQL12( programStage, orgunitIds, filterSQL, periods, aggregateType, format );

        }
        // Type = 3
        else if ( position == PatientAggregateReport.POSITION_ROW_ORGUNIT_ROW_PERIOD )
        {
            // ---------------------------------------------------------------------
            // Headers cols
            // ---------------------------------------------------------------------

            grid.addHeader( new GridHeader( i18n.getString( "orgunit" ), false, true ) );
            grid.addHeader( new GridHeader( i18n.getString( "period" ), false, true ) );
            grid.addHeader( new GridHeader( i18n.getString( aggregateType ), false, false ) );

            // ---------------------------------------------------------------------
            // Get SQL and build grid
            // ---------------------------------------------------------------------

            sql = getAggregateReportSQL34( position, programStage, orgunitIds, filterSQL, periods, aggregateType,
                format );

        }
        // Type = 4
        else if ( position == PatientAggregateReport.POSITION_ROW_PERIOD )
        {
            // ---------------------------------------------------------------------
            // Headers cols
            // ---------------------------------------------------------------------

            grid.addHeader( new GridHeader( i18n.getString( "period" ), false, true ) );
            grid.addHeader( new GridHeader( i18n.getString( aggregateType ), false, false ) );

            // ---------------------------------------------------------------------
            // Get SQL and build grid
            // ---------------------------------------------------------------------

            sql = getAggregateReportSQL34( position, programStage, orgunitIds, filterSQL, periods, aggregateType,
                format );

        }
        // type = 5
        else if ( position == PatientAggregateReport.POSITION_ROW_ORGUNIT )
        {
            // ---------------------------------------------------------------------
            // Headers cols
            // ---------------------------------------------------------------------

            grid.addHeader( new GridHeader( i18n.getString( "orgunit" ), false, true ) );
            grid.addHeader( new GridHeader( i18n.getString( aggregateType ), false, false ) );

            // ---------------------------------------------------------------------
            // Get SQL and build grid
            // ---------------------------------------------------------------------

            List<Period> firstPeriod = new ArrayList<Period>();
            firstPeriod.add( periods.iterator().next() );
            sql = getAggregateReportSQL5( position, programStage, orgunitIds, filterSQL, periods.iterator().next(),
                aggregateType, format );

        }
        // Type = 6 && With group-by
        else if ( position == PatientAggregateReport.POSITION_ROW_PERIOD_COLUMN_DATA && dataElementId != null )
        {
            List<String> deValues = new ArrayList<String>();

            deValues = dataElementService.getDataElement( dataElementId ).getOptionSet().getOptions();

            // ---------------------------------------------------------------------
            // Headers cols
            // ---------------------------------------------------------------------

            grid.addHeader( new GridHeader( i18n.getString( "period" ), false, true ) );

            for ( String deValue : deValues )
            {
                grid.addHeader( new GridHeader( deValue, false, false ) );
            }

            // ---------------------------------------------------------------------
            // Get SQL and build grid
            // ---------------------------------------------------------------------

            sql = getAggregateReportSQL6( programStage, orgunitIds, filterSQL, dataElementId, deValues, periods,
                aggregateType, format );
        }

        // Type = 6 && Without group-by

        else if ( position == PatientAggregateReport.POSITION_ROW_PERIOD_COLUMN_DATA && dataElementId == null )
        {
            grid.addHeader( new GridHeader( i18n.getString( "period" ), false, true ) );

            grid.addHeader( new GridHeader( i18n.getString( aggregateType ), false, false ) );

            // ---------------------------------------------------------------------
            // Get SQL and build grid
            // ---------------------------------------------------------------------

            sql = getAggregateReportSQL6NotGroupBy( programStage, orgunitIds, filterSQL, periods, aggregateType, format );
        }

        // Type = 7 && With group-by
        else if ( position == PatientAggregateReport.POSITION_ROW_ORGUNIT_COLUMN_DATA && dataElementId != null )
        {
            List<String> deValues = dataElementService.getDataElement( dataElementId ).getOptionSet().getOptions();

            // ---------------------------------------------------------------------
            // Headers cols
            // ---------------------------------------------------------------------

            grid.addHeader( new GridHeader( i18n.getString( "orgunit" ), false, true ) );

            for ( String deValue : deValues )
            {
                grid.addHeader( new GridHeader( deValue, false, false ) );
            }

            // ---------------------------------------------------------------------
            // Get SQL and build grid
            // ---------------------------------------------------------------------

            sql = getAggregateReportSQL7( programStage, orgunitIds, filterSQL, dataElementId, deValues, periods
                .iterator().next(), aggregateType, format );

        }

        // Type = 7 && Without group-by
        else if ( position == PatientAggregateReport.POSITION_ROW_ORGUNIT_COLUMN_DATA && dataElementId == null )
        {
            // ---------------------------------------------------------------------
            // Headers cols
            // ---------------------------------------------------------------------

            grid.addHeader( new GridHeader( i18n.getString( "orgunit" ), false, true ) );

            grid.addHeader( new GridHeader( i18n.getString( aggregateType ), false, true ) );

            // ---------------------------------------------------------------------
            // Get SQL and build grid
            // ---------------------------------------------------------------------

            sql = getAggregateReportSQL7WithoutGroupBy( programStage, orgunitIds, filterSQL, periods.iterator().next(),
                aggregateType, format );
        }

        // type = 8 && With group-by
        else if ( position == PatientAggregateReport.POSITION_ROW_DATA )
        {
            DataElement dataElement = dataElementService.getDataElement( dataElementId );

            // ---------------------------------------------------------------------
            // Headers cols
            // ---------------------------------------------------------------------

            grid.addHeader( new GridHeader( dataElement.getDisplayName(), false, true ) );
            grid.addHeader( new GridHeader( i18n.getString( aggregateType ), false, false ) );

            // ---------------------------------------------------------------------
            // Get SQL and build grid
            // ---------------------------------------------------------------------

            sql = getAggregateReportSQL8( programStage, orgunitIds, dataElementId, periods.iterator().next(),
                aggregateType, limit, format );
        }

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

        // Type != 2
        if ( position != PatientAggregateReport.POSITION_ROW_PERIOD_COLUMN_ORGUNIT )
        {
            GridUtils.addRows( grid, rowSet );
        }
        else
        {
            // addColumns( grid, rowSet );
            pivotTable( grid, rowSet );
        }

        return grid;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String getTabularReportSql( boolean count, ProgramStage programStage, List<TabularReportColumn> columns,
        Collection<Integer> orgUnits, int level, int maxLevel, Date startDate, Date endDate, boolean descOrder,
        Boolean completed, Integer min, Integer max )
    {
        Set<String> deKeys = new HashSet<String>();
        String selector = count ? "count(*) " : "* ";

        String sql = "select " + selector + "from ( select DISTINCT psi.programstageinstanceid, psi.executiondate,";
        String where = "";
        String operator = "where ";

        for ( int i = level; i <= maxLevel; i++ )
        {
            sql += "(select name from organisationunit where organisationunitid=ous.idlevel" + i + ") as level_" + i
                + ",";
        }

        for ( TabularReportColumn column : columns )
        {
            if ( column.isFixedAttribute() )
            {
                sql += "p." + column.getIdentifier() + ",";

                if ( column.hasQuery() )
                {
                    where += operator + "lower(" + column.getIdentifier() + ") " + column.getQuery() + " ";
                    operator = "and ";
                }
            }
            else if ( column.isIdentifierType() )
            {
                String deKey = "identifier_" + column.getIdentifier();
                if ( !deKeys.contains( deKey ) )
                {
                    sql += "(select identifier from patientidentifier where patientid=p.patientid and patientidentifiertypeid="
                        + column.getIdentifier() + ") as identifier_" + column.getIdentifier() + ",";
                }

                if ( column.hasQuery() )
                {
                    where += operator + "lower(identifier_" + column.getIdentifier() + ") " + column.getQuery() + " ";
                    operator = "and ";
                }
            }
            else if ( column.isDynamicAttribute() )
            {
                String deKey = "attribute_" + column.getIdentifier();
                if ( !deKeys.contains( deKey ) )
                {
                    sql += "(select value from patientattributevalue where patientid=p.patientid and patientattributeid="
                        + column.getIdentifier() + ") as attribute_" + column.getIdentifier() + ",";
                }

                if ( column.hasQuery() )
                {
                    where += operator + "lower(attribute_" + column.getIdentifier() + ") " + column.getQuery() + " ";
                    operator = "and ";
                }
            }
            if ( column.isNumberDataElement() )
            {
                String deKey = "element_" + column.getIdentifier();
                if ( !deKeys.contains( deKey ) )
                {
                    sql += "(select cast( value as "
                        + statementBuilder.getDoubleColumnType()
                        + " ) from patientdatavalue where programstageinstanceid=psi.programstageinstanceid and dataelementid="
                        + column.getIdentifier() + ") as element_" + column.getIdentifier() + ",";
                    deKeys.add( deKey );
                }

                if ( column.hasQuery() )
                {
                    where += operator + "element_" + column.getIdentifier() + " " + column.getQuery() + " ";
                    operator = "and ";
                }
            }
            else if ( column.isDataElement() )
            {
                String deKey = "element_" + column.getIdentifier();
                if ( !deKeys.contains( deKey ) )
                {
                    sql += "(select value from patientdatavalue where programstageinstanceid=psi.programstageinstanceid and dataelementid="
                        + column.getIdentifier() + ") as element_" + column.getIdentifier() + ",";
                    deKeys.add( deKey );
                }

                if ( column.hasQuery() )
                {
                    where += operator + "lower(element_" + column.getIdentifier() + ") " + column.getQuery() + " ";
                    operator = "and ";
                }
            }
        }

        sql += " psi.completed ";
        sql += "from programstageinstance psi ";
        sql += "left join programinstance pi on (psi.programinstanceid=pi.programinstanceid) ";
        sql += "left join patient p on (pi.patientid=p.patientid) ";
        sql += "join organisationunit ou on (ou.organisationunitid=psi.organisationunitid) ";
        sql += "join _orgunitstructure ous on (psi.organisationunitid=ous.organisationunitid) ";

        sql += "where psi.programstageid=" + programStage.getId() + " ";

        if ( startDate != null && endDate != null )
        {
            String sDate = DateUtils.getMediumDateString( startDate );
            String eDate = DateUtils.getMediumDateString( endDate );

            sql += "and psi.executiondate >= '" + sDate + "' ";
            sql += "and psi.executiondate <= '" + eDate + "' ";
        }

        if ( orgUnits != null )
        {
            sql += "and ou.organisationunitid in (" + TextUtils.getCommaDelimitedString( orgUnits ) + ") ";
        }
        if ( completed != null )
        {
            sql += "and psi.completed=" + completed + " ";
        }

        sql += "order by ";

        for ( int i = level; i <= maxLevel; i++ )
        {
            sql += "level_" + i + ",";
        }

        sql += "psi.executiondate ";
        sql += descOrder ? "desc " : "";
        sql += ") as tabular ";
        sql += where; // filters
        sql = sql.substring( 0, sql.length() - 1 ) + " "; // Remove last comma
        sql += (min != null && max != null) ? statementBuilder.limitRecord( min, max ) : "";

        return sql;
    }

    private Criteria getStatisticalProgramStageCriteria( ProgramStage programStage, Collection<Integer> orgunitIds,
        Date startDate, Date endDate, int status )
    {
        Criteria criteria = getCriteria( Restrictions.eq( "programStage", programStage ),
            Restrictions.isNull( "programInstance.endDate" ) );
        criteria.createAlias( "programInstance", "programInstance" );
        criteria.createAlias( "programInstance.patient", "patient" );
        criteria.createAlias( "patient.organisationUnit", "regOrgunit" );
        criteria.add( Restrictions.in( "regOrgunit.id", orgunitIds ) );

        switch ( status )
        {
        case ProgramStageInstance.COMPLETED_STATUS:
            criteria.add( Restrictions.eq( "completed", true ) );
            criteria.add( Restrictions.between( "executionDate", startDate, endDate ) );
            break;
        case ProgramStageInstance.VISITED_STATUS:
            criteria.add( Restrictions.eq( "completed", false ) );
            criteria.add( Restrictions.between( "executionDate", startDate, endDate ) );
            break;
        case ProgramStageInstance.FUTURE_VISIT_STATUS:
            criteria.add( Restrictions.between( "programInstance.enrollmentDate", startDate, endDate ) );
            criteria.add( Restrictions.isNull( "executionDate" ) );
            criteria.add( Restrictions.ge( "dueDate", new Date() ) );
            break;
        case ProgramStageInstance.LATE_VISIT_STATUS:
            criteria.add( Restrictions.between( "programInstance.enrollmentDate", startDate, endDate ) );
            criteria.add( Restrictions.isNull( "executionDate" ) );
            criteria.add( Restrictions.lt( "dueDate", new Date() ) );
            break;
        default:
            break;
        }

        return criteria;
    }

    /**
     * Aggregate report Position Orgunit Rows - Period Columns - Data Filter
     * Aggregate report Position Orgunit Columns - Period Rows - Data Filter
     **/
    private String getAggregateReportSQL12( ProgramStage programStage, Collection<Integer> orgunitIds,
        String filterSQL, Collection<Period> periods, String aggregateType, I18nFormat format )
    {
        String sql = "select ou.name as orgunit, ";

        for ( Period period : periods )
        {
            sql += "( select count(*) ";
            sql += "FROM programstageinstance psi_1 JOIN patientdatavalue pdv_1 ";
            sql += "    ON psi_1.programstageinstanceid=pdv_1.programstageinstanceid ";
            sql += "WHERE ";
            sql += "    psi_1.organisationunitid = psi.organisationunitid AND ";
            sql += "    psi_1.executiondate >= '" + format.formatDate( period.getStartDate() ) + "' AND ";
            sql += "    psi_1.executiondate <= '" + format.formatDate( period.getEndDate() ) + "' ";

            sql += filterSQL + " ) as \"" + format.formatPeriod( period ) + "\",";
        }

        sql = sql.substring( 0, sql.length() - 1 ) + " ";

        sql += "FROM programstageinstance psi ";
        sql += "        RIGHT OUTER JOIN organisationunit ou on ou.organisationunitid=psi.organisationunitid ";
        sql += "WHERE ";
        sql += "   ou.organisationunitid in ( " + TextUtils.getCommaDelimitedString( orgunitIds ) + " )  AND ";
        sql += "   psi.programstageid=" + programStage.getId() + " AND ";
        sql += "   psi.completed = true  ";
        sql += "GROUP BY ";
        sql += "   ou.name, psi.organisationunitid ";
        sql += "ORDER BY orgunit desc";

        return sql;
    }

    /**
     * Generate SQL statement for 3 report type - Aggregate report Position
     * Orgunit Rows - Period Rows - Data Filter Aggregate report Period Rows -
     * Orgunit Filter - Data Filter
     * 
     **/
    private String getAggregateReportSQL34( int position, ProgramStage programStage, Collection<Integer> orgunitIds,
        String filterSQL, Collection<Period> periods, String aggregateType, I18nFormat format )
    {
        String sql = "";

        for ( Period period : periods )
        {
            sql += "( " + getColumnAggregateReportSQL34( position, format.formatPeriod( period ), aggregateType ) + " ";
            sql += "FROM ";
            sql += "patientdatavalue pdv_1 JOIN programstageinstance psi_1 ";
            sql += "       ON psi_1.programstageinstanceid=pdv_1.programstageinstanceid ";
            sql += "JOIN organisationunit ou on (ou.organisationunitid=psi_1.organisationunitid ) ";
            sql += " WHERE ";
            sql += "        ou.organisationunitid in ( " + TextUtils.getCommaDelimitedString( orgunitIds ) + " ) AND ";
            sql += "      psi_1.programstageid=" + programStage.getId() + " AND ";
            sql += "      psi_1.completed = true AND ";
            sql += "      psi_1.executiondate >= '" + format.formatDate( period.getStartDate() ) + "' AND ";
            sql += "      psi_1.executiondate <= '" + format.formatDate( period.getEndDate() ) + "' ";
            sql += filterSQL;
            if ( position == PatientAggregateReport.POSITION_ROW_ORGUNIT_ROW_PERIOD )
            {
                sql += "GROUP BY ou.name  ";
            }
            sql += ")  UNION ";
        }

        sql += sql.substring( 0, sql.length() - 6 );
        if ( position == PatientAggregateReport.POSITION_ROW_ORGUNIT_ROW_PERIOD )
        {
            sql += " ORDER BY orgunit desc";
        }
        return sql;
    }

    /**
     * Aggregate report Position Orgunit Rows -Period Filter - Data Filter
     * 
     **/
    private String getAggregateReportSQL5( int position, ProgramStage programStage, Collection<Integer> orgunitIds,
        String filterSQL, Period period, String aggregateType, I18nFormat format )
    {
        String sql = "SELECT ou.name as orgunit, count(pdv_1.value) ";
        sql += "FROM ";
        sql += "        patientdatavalue pdv_1 RIGHT JOIN programstageinstance psi_1 ";
        sql += "                ON psi_1.programstageinstanceid=pdv_1.programstageinstanceid ";
        sql += "        JOIN organisationunit ou on (ou.organisationunitid=psi_1.organisationunitid ) ";
        sql += "WHERE ";
        sql += "        ou.organisationunitid in ( " + TextUtils.getCommaDelimitedString( orgunitIds ) + " ) AND ";
        sql += "        psi_1.programstageid=" + programStage.getId() + " AND ";
        sql += "        psi_1.completed = true AND ";
        sql += "        psi_1.executiondate >= '" + format.formatDate( period.getStartDate() ) + "' AND ";
        sql += "        psi_1.executiondate <= '" + format.formatDate( period.getEndDate() ) + "' ";
        sql += filterSQL;
        sql += "GROUP BY ou.name ";
        sql += "ORDER BY orgunit desc  ";

        return sql;
    }

    /**
     * Aggregate report Position Orgunit Filter - Period Rows - Data Columns
     * with group-by
     **/
    private String getAggregateReportSQL6( ProgramStage programStage, Collection<Integer> orgunitIds, String filterSQL,
        Integer dataElementId, Collection<String> deValues, Collection<Period> periods, String aggregateType,
        I18nFormat format )
    {
        String sql = "";

        int index = 0;
        for ( Period period : periods )
        {
            sql += "(SELECT '" + format.formatPeriod( period ) + "', ";
            for ( String deValue : deValues )
            {
                sql += "(SELECT " + aggregateType + "(value)  ";
                sql += "FROM programstageinstance psi_1 JOIN patientdatavalue pdv_1 ";
                sql += "    on psi_1.programstageinstanceid = pdv_1.programstageinstanceid ";
                sql += "WHERE ";
                sql += "    psi_1.organisationunitid in ( " + TextUtils.getCommaDelimitedString( orgunitIds )
                    + "     ) AND ";
                sql += "    psi_1.executiondate >= '" + format.formatDate( period.getStartDate() ) + "' AND ";
                sql += "    psi_1.executiondate <= '" + format.formatDate( period.getEndDate() ) + "' ";
                sql += filterSQL + " AND ";
                sql += "        (SELECT value from patientdatavalue ";
                sql += "        WHERE programstageinstanceid=psi_1.programstageinstanceid ";
                sql += "                AND dataelementid=" + dataElementId + " ";
                sql += "        ) = '" + deValue + "' ";
                sql += ") as de_value" + index + ",";

                index++;
            }
            sql = sql.substring( 0, sql.length() - 1 ) + " ";

            sql += "FROM  programstageinstance psi JOIN patientdatavalue pdv ";
            sql += "    on psi.programstageinstanceid = pdv.programstageinstanceid ";
            sql += "WHERE ";
            sql += "    psi.organisationunitid in ( " + TextUtils.getCommaDelimitedString( orgunitIds ) + " ) AND ";
            sql += "    psi.programstageid=" + programStage.getId() + " AND ";
            sql += "    psi.completed = true ";
            sql += "GROUP BY dataelementid ";
            sql += "  LIMIT 1 ";
            sql += ") UNION ";
        }

        return sql.substring( 0, sql.length() - 6 ) + " ";
    }

    /**
     * Aggregate report Position Orgunit Filter - Period Rows - Data Columns
     * without group-by
     **/
    private String getAggregateReportSQL6NotGroupBy( ProgramStage programStage, Collection<Integer> orgunitIds,
        String filterSQL, Collection<Period> periods, String aggregateType, I18nFormat format )
    {
        String sql = "";

        int index = 0;
        for ( Period period : periods )
        {
            sql += "(SELECT '" + format.formatPeriod( period ) + "', ";
            sql += "(SELECT " + aggregateType + "(value)  ";
            sql += "FROM programstageinstance psi_1 JOIN patientdatavalue pdv_1 ";
            sql += "    on psi_1.programstageinstanceid = pdv_1.programstageinstanceid ";
            sql += "WHERE ";
            sql += "    psi_1.organisationunitid in ( " + TextUtils.getCommaDelimitedString( orgunitIds )
                + "     ) AND ";
            sql += "    psi_1.executiondate >= '" + format.formatDate( period.getStartDate() ) + "' AND ";
            sql += "    psi_1.executiondate <= '" + format.formatDate( period.getEndDate() ) + "' ";
            sql += filterSQL + ") as de_value" + index + ",";

            sql = sql.substring( 0, sql.length() - 1 ) + " ";

            sql += "FROM  programstageinstance psi JOIN patientdatavalue pdv ";
            sql += "    on psi.programstageinstanceid = pdv.programstageinstanceid ";
            sql += "WHERE ";
            sql += "    psi.organisationunitid in ( " + TextUtils.getCommaDelimitedString( orgunitIds ) + " ) AND ";
            sql += "    psi.programstageid=" + programStage.getId() + " AND ";
            sql += "    psi.completed = true ";
            sql += "GROUP BY dataelementid ";
            sql += "  LIMIT 1 ";
            sql += ") UNION ";
            index++;
        }

        return sql.substring( 0, sql.length() - 6 ) + " ";
    }

    /**
     * Aggregate report Position Orgunit Rows - Period Filter - Data Columns
     * 
     **/
    private String getAggregateReportSQL7( ProgramStage programStage, Collection<Integer> orgunitIds, String filterSQL,
        Integer dataElementId, List<String> deValues, Period period, String aggregateType, I18nFormat format )
    {
        String sql = "select ou.name as orgunit, ";

        int index = 0;
        for ( String deValue : deValues )
        {
            sql += "( select " + aggregateType + "(value) ";
            sql += "FROM patientdatavalue pdv_1 ";
            sql += "    inner join programstageinstance psi_1 ";
            sql += "    on psi_1.programstageinstanceid = pdv_1.programstageinstanceid ";
            sql += "WHERE ";
            sql += "    psi_1.organisationunitid=psi.organisationunitid AND ";
            sql += "    psi_1.executiondate >= '" + format.formatDate( period.getStartDate() ) + "' AND ";
            sql += "    psi_1.executiondate <= '" + format.formatDate( period.getEndDate() ) + "' AND ";
            sql += "    psi_1.programstageid=" + programStage.getId() + " AND ";
            sql += "    psi_1.completed = true AND ";
            sql += "    pdv_1.value='" + deValue + "' ";
            sql += filterSQL + " AND ";
            sql += "    (SELECT value from patientdatavalue ";
            sql += "     WHERE programstageinstanceid=psi_1.programstageinstanceid ";
            sql += "            AND dataelementid=" + dataElementId + " ";
            sql += "     ) = '" + deValue + "' ";

            sql += ") as v_" + index + ",";

            index++;
        }
        sql = sql.substring( 0, sql.length() - 1 ) + " ";

        sql += "FROM programstageinstance psi ";
        sql += "        RIGHT JOIN organisationunit ou on ou.organisationunitid=psi.organisationunitid ";
        sql += "WHERE ";
        sql += "        ou.organisationunitid in ( " + TextUtils.getCommaDelimitedString( orgunitIds ) + ") ";
        sql += "GROUP BY ";
        sql += "        psi.organisationunitid,ou.name ";
        sql += "ORDER BY orgunit desc ";

        return sql;
    }

    /**
     * Aggregate report Position Orgunit Rows - Period Filter - Data Columns
     * without group-by
     **/
    private String getAggregateReportSQL7WithoutGroupBy( ProgramStage programStage, Collection<Integer> orgunitIds,
        String filterSQL, Period period, String aggregateType, I18nFormat format )
    {
        String sql = "select ou.name as orgunit, ";

        sql += "( select " + aggregateType + "(value) ";
        sql += "FROM patientdatavalue pdv_1 ";
        sql += "    inner join programstageinstance psi_1 ";
        sql += "    on psi_1.programstageinstanceid = pdv_1.programstageinstanceid ";
        sql += "WHERE ";
        sql += "    psi_1.organisationunitid=psi.organisationunitid AND ";
        sql += "    psi_1.executiondate >= '" + format.formatDate( period.getStartDate() ) + "' AND ";
        sql += "    psi_1.executiondate <= '" + format.formatDate( period.getEndDate() ) + "' AND ";
        sql += "    psi_1.programstageid=" + programStage.getId() + " AND ";
        sql += "    psi_1.completed = true ";
        sql += filterSQL + " ) ";
        sql += "FROM programstageinstance psi ";
        sql += "        RIGHT JOIN organisationunit ou on ou.organisationunitid=psi.organisationunitid ";
        sql += "WHERE ";
        sql += "        ou.organisationunitid in ( " + TextUtils.getCommaDelimitedString( orgunitIds ) + ") ";
        sql += "GROUP BY ";
        sql += "        psi.organisationunitid,ou.name ";
        sql += "ORDER BY orgunit desc ";

        return sql;
    }

    /**
     * Aggregate report Position Data Rows
     * 
     **/
    private String getAggregateReportSQL8( ProgramStage programStage, Collection<Integer> orgunitIds,
        Integer dataElementId, Period period, String aggregateType, Integer limit, I18nFormat format )
    {
        String from = "FROM patientdatavalue pdv ";
        from += "        JOIN programstageinstance psi ";
        from += "          ON psi.programstageinstanceid = pdv.programstageinstanceid  ";
        from += "WHERE ";
        from += "    pdv.dataelementid=" + dataElementId + " AND  ";
        from += "    psi.programstageid=" + programStage.getId() + " AND ";
        from += "    psi.completed = true  AND ";
        from += "    psi.executiondate >= '" + format.formatDate( period.getStartDate() ) + "' AND ";
        from += "    psi.executiondate <= '" + format.formatDate( period.getEndDate() ) + "' AND ";
        from += "    psi.organisationunitid in ( " + TextUtils.getCommaDelimitedString( orgunitIds ) + " ) ";

        String sql = " SELECT ov.optionvalue,'0' ";
        sql += "FROM optionset op JOIN optionsetmembers ov ";
        sql += "        ON op.optionsetid=ov.optionsetid ";
        sql += "JOIN dataelement de ";
        sql += "        ON de.optionsetid=op.optionsetid ";
        sql += "WHERE de.dataelementid=" + dataElementId + " AND ov.optionvalue not in  ";

        sql = "( SELECT pdv.value, " + aggregateType + "(value) " + from + "GROUP BY pdv.value " + "ORDER BY "
            + aggregateType + "(value) desc ) UNION ( " + sql + " " + "(SELECT DISTINCT pdv.value " + from + ") )";
        if ( limit != null )
        {
            sql += "LIMIT " + limit;
        }

        return sql;
    }
    

    /**
     * Generate SELECT statement for 3 report type - Aggregate report Position
     * Orgunit Rows - Period Rows - Data Filter
     * 
     **/
    private String getColumnAggregateReportSQL34( int position, String periodColumnName, String aggregateType )
    {
        switch ( position )
        {
        case PatientAggregateReport.POSITION_ROW_ORGUNIT_ROW_PERIOD:
            return "select ou.name as orgunit, '" + periodColumnName + "', " + aggregateType + "(pdv_1.value) ";
        case PatientAggregateReport.POSITION_ROW_PERIOD:
            return "select '" + periodColumnName + "', count(pdv_1.value) ";
        default:
            return "";
        }
    }

    private void pivotTable( Grid grid, SqlRowSet rowSet )
    {
        try
        {
            int cols = rowSet.getMetaData().getColumnCount();

            Map<Integer, List<Object>> columnValues = new HashMap<Integer, List<Object>>();
            int index = 1;

            grid.addHeader( new GridHeader( "", false, true ) );
            while ( rowSet.next() )
            {
                // Header grid
                grid.addHeader( new GridHeader( rowSet.getString( 1 ), false, false ) );

                // Column values
                List<Object> column = new ArrayList<Object>();
                for ( int i = 2; i <= cols; i++ )
                {
                    column.add( rowSet.getObject( i ) );
                }
                columnValues.put( index, column );

                index++;
            }

            // Rows grid
            List<Object> firstColumn = new ArrayList<Object>();
            for ( int i = 2; i <= cols; i++ )
            {
                grid.addRow();
                // first column
                firstColumn.add( rowSet.getMetaData().getColumnLabel( i ) );
            }
            grid.addColumn( firstColumn );

            // Other columns
            for ( int i = 1; i < index; i++ )
            {
                grid.addColumn( columnValues.get( i ) );
            }
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
    }

    private String filterSQLStatement( Map<Integer, String> deFilters )
    {
        String filter = "";
        if ( deFilters != null )
        {
            // Get filter criteria
            Iterator<Integer> iterFilter = deFilters.keySet().iterator();
            boolean flag = false;
            while ( iterFilter.hasNext() )
            {
                Integer id = iterFilter.next();
                String[] filterKey = deFilters.get( id ).split( "_" );

                filter += "AND (SELECT value ";
                filter += "FROM patientdatavalue ";
                filter += "WHERE programstageinstanceid=psi_1.programstageinstanceid AND ";
                if ( !flag )
                {
                    filter += "dataelementid= pdv_1.dataelementid AND ";
                    flag = true;
                }
                filter += "dataelementid=" + id + "  ";
                filter += ") " + filterKey[0] + " '" + filterKey[1] + "' ";

                if ( filterKey.length == 4 )
                {
                    filter += "AND (SELECT value ";
                    filter += "FROM patientdatavalue ";
                    filter += "WHERE programstageinstanceid=psi_1.programstageinstanceid AND ";
                    filter += "dataelementid=" + id + "  ";
                    filter += ") " + filterKey[2] + " '" + filterKey[3] + "' ";
                }
            }
        }

        return filter;
    }

}
