package org.hisp.dhis.completeness.jdbc;

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

import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.util.ArrayList;
import java.util.Collection;

import org.amplecode.quick.StatementManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.completeness.DataSetCompletenessStore;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.jdbc.StatementBuilder;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class JDBCDataSetCompletenessStore
    implements DataSetCompletenessStore
{
    private static final Log log = LogFactory.getLog( JDBCDataSetCompletenessStore.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }
    
    private StatementBuilder statementBuilder;

    public void setStatementBuilder( StatementBuilder statementBuilder )
    {
        this.statementBuilder = statementBuilder;
    }

    // -------------------------------------------------------------------------
    // DataSetCompletenessStore
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Based on complete data set registrations
    // -------------------------------------------------------------------------

    public Integer getCompleteDataSetRegistrations( DataSet dataSet, Collection<Integer> periods, Collection<Integer> relevantSources )
    {
        if ( relevantSources == null || relevantSources.size() == 0 )
        {
            return 0;
        }        
        
        final String sql =
            "SELECT COUNT(*) " +
            "FROM completedatasetregistration cr " +
            "WHERE cr.datasetid = " + dataSet.getId() + " " +
            "AND cr.periodid IN ( " + getCommaDelimitedString( periods ) + " ) " +
            "AND cr.sourceid IN ( " + getCommaDelimitedString( relevantSources ) + " )";
        
        return statementManager.getHolder().queryForInteger( sql );
    }

    public Integer getCompleteDataSetRegistrations( DataSet dataSet, Collection<Integer> periods, Collection<Integer> relevantSources, int completenessOffset )
    {
        if ( relevantSources == null || relevantSources.size() == 0 )
        {
            return 0;
        }        
        
        final String sql =
            "SELECT COUNT(*) " +
            "FROM completedatasetregistration cr " +
            "JOIN period pe ON (cr.periodid = pe.periodid) " +
            "WHERE cr.datasetid = " + dataSet.getId() + " " +
            "AND cr.periodid IN ( " + getCommaDelimitedString( periods ) + " ) " +
            "AND cr.sourceid IN ( " + getCommaDelimitedString( relevantSources ) + " ) " +
            "AND cr.date <= " + statementBuilder.getAddDate( "pe.enddate", completenessOffset );
        
        return statementManager.getHolder().queryForInteger( sql );
    }
    
    // -------------------------------------------------------------------------
    // Based on compulsory data element operands
    // -------------------------------------------------------------------------

    public Integer getCompulsoryDataElementRegistrations( DataSet dataSet, Collection<Integer> children, Collection<Integer> periods )
    {
        return getCompulsoryDataElementRegistrations( dataSet, children, periods, -1 );
    }
    
    public Integer getCompulsoryDataElementRegistrations( DataSet dataSet, Collection<Integer> children, Collection<Integer> periods, int completenessOffset )
    {           
        final int compulsoryElements = dataSet.getCompulsoryDataElementOperands().size();
        
        final String deadlineCriteria = completenessOffset >= 0 ? "AND lastupdated <= " + statementBuilder.getAddDate( "pe.enddate", completenessOffset ) : "";
        
        final String sql = 
            "SELECT COUNT(completed) FROM ( " +
                "SELECT sourceid, COUNT(sourceid) AS sources " +
                "FROM datavalue dv " +
                "JOIN dataelementoperand deo " +
                "ON dv.dataelementid=deo.dataelementid AND dv.categoryoptioncomboid=deo.categoryoptioncomboid " +
                "JOIN datasetoperands dso " +
                "ON deo.dataelementoperandid=dso.dataelementoperandid " +
                "JOIN period pe " +
                "ON dv.periodid=pe.periodid " +
                "WHERE dv.periodid IN ( " + getCommaDelimitedString( periods ) + " ) " + deadlineCriteria +
                "AND sourceid IN ( " + getCommaDelimitedString( children ) + " ) " +
                "AND datasetid = " + dataSet.getId() + " GROUP BY sourceid) AS completed " +
            "WHERE completed.sources = " + compulsoryElements;
        
        return statementManager.getHolder().queryForInteger( sql );
    }

    // -------------------------------------------------------------------------
    // Based on number of data values
    // -------------------------------------------------------------------------
    
    public Collection<DataSet> getDataSetsWithRegistrations( Collection<DataSet> dataSets )
    {
        Collection<DataSet> selection = new ArrayList<DataSet>();
        
        for ( DataSet dataSet : dataSets )
        {
            final String sql = "SELECT count(*) FROM completedatasetregistration WHERE datasetid = " + dataSet.getId();
            
            if ( statementManager.getHolder().queryForInteger( sql ) > 0 )
            {
                selection.add( dataSet );
            }
        }
        
        return selection;
    }

    // -------------------------------------------------------------------------
    // Aggregated data set completeness methods
    // -------------------------------------------------------------------------

    public Double getPercentage( int dataSetId, int periodId, int organisationUnitId )
    {
        final String sql =
            "SELECT value " +
            "FROM aggregateddatasetcompleteness " +
            "WHERE datasetid = " + dataSetId + " " +
            "AND periodid = " + periodId + " " +
            "AND organisationunitid = " + organisationUnitId;
        
        return statementManager.getHolder().queryForDouble( sql );
    }
    
    public void deleteDataSetCompleteness( Collection<Integer> dataSetIds, Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        final String sql = 
            "DELETE FROM aggregateddatasetcompleteness " +
            "WHERE datasetid IN ( " + getCommaDelimitedString( dataSetIds ) + " ) " +
            "AND periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
            "AND organisationunitid IN ( " + getCommaDelimitedString( organisationUnitIds ) + " )";
        
        statementManager.getHolder().executeUpdate( sql );
    }
    
    public void deleteDataSetCompleteness()
    {
        final String sql = "DELETE FROM aggregateddatasetcompleteness";
        
        statementManager.getHolder().executeUpdate( sql );
    }

    public void createIndex()
    {
        try
        {
            final String sql = "CREATE INDEX aggregateddatasetcompleteness_index ON aggregateddatasetcompleteness (datasetid, periodid, organisationunitid, value)";        
            statementManager.getHolder().executeUpdate( sql, true );
        }
        catch ( Exception ex )
        {
            log.debug( "Index already exists" );
        }
    }
    
    public void dropIndex()
    {
        try
        {
            final String sql = "DROP INDEX aggregateddatasetcompleteness_index";        
            statementManager.getHolder().executeUpdate( sql, true );
        }
        catch ( Exception ex )
        {
            log.debug( "Index does not exist" );
        }
    }
}
