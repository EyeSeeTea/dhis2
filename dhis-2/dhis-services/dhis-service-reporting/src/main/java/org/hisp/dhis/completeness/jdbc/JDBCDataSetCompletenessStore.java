package org.hisp.dhis.completeness.jdbc;

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

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.DateUtils.getMediumDateString;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.util.Collection;
import java.util.Date;

import org.amplecode.quick.StatementManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.completeness.DataSetCompletenessStore;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.TextUtils;

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

    // -------------------------------------------------------------------------
    // DataSetCompletenessStore
    // -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public int getCompleteDataSetRegistrations( DataSet dataSet, Period period, Collection<Integer> sources )
    {
        final Collection<Integer> intersectingSources = CollectionUtils.intersection( sources, getIdentifiers( OrganisationUnit.class, dataSet.getSources() ) );
        
        if ( intersectingSources == null || intersectingSources.size() == 0 )
        {
            return 0;
        }        
        
        final String sql =
            "SELECT COUNT(*) " +
            "FROM completedatasetregistration " +
            "WHERE datasetid = " + dataSet.getId() + " " +
            "AND periodid = " + period.getId() + " " +
            "AND sourceid IN ( " + getCommaDelimitedString( intersectingSources ) + " )";
        
        return statementManager.getHolder().queryForInteger( sql );
    }

    @SuppressWarnings("unchecked")
    public int getCompleteDataSetRegistrations( DataSet dataSet, Period period, Collection<Integer> sources, Date deadline )
    {
        final Collection<Integer> intersectingSources = CollectionUtils.intersection( sources, getIdentifiers( OrganisationUnit.class, dataSet.getSources() ) );
        
        if ( intersectingSources == null || intersectingSources.size() == 0 )
        {
            return 0;
        }        
        
        final String sql =
            "SELECT COUNT(*) " +
            "FROM completedatasetregistration " +
            "WHERE datasetid = " + dataSet.getId() + " " +
            "AND periodid = " + period.getId() + " " +
            "AND sourceid IN ( " + getCommaDelimitedString( intersectingSources ) + " ) " +
            "AND date <= '" + getMediumDateString( deadline ) + "'";
        
        return statementManager.getHolder().queryForInteger( sql );
    }
    
    public double getPercentage( int dataSetId, int periodId, int organisationUnitId )
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

    public int getRegistrations( DataSet dataSet, Collection<Integer> children, Period period )
    {
        return getRegistrations( dataSet, children, period, null );
    }
    
    public int getRegistrations( DataSet dataSet, Collection<Integer> children, Period period, Date deadline )
    {           
        final int compulsoryElements = dataSet.getCompulsoryDataElementOperands().size();        
        final String childrenIds = TextUtils.getCommaDelimitedString( children );
        final String deadlineCriteria = deadline != null ? "AND lastupdated < '" + DateUtils.getMediumDateString( deadline ) + "' " : "";
        
        final String sql = 
            "SELECT COUNT(completed) FROM ( " +
                "SELECT sourceid, COUNT(sourceid) AS sources " +
                "FROM datavalue " +
                "JOIN dataelementoperand USING (dataelementid, categoryoptioncomboid) " +
                "JOIN datasetoperands USING (dataelementoperandid) " +
                "WHERE periodid = " + period.getId() + " " + deadlineCriteria +
                "AND sourceid IN (" + childrenIds + ") " +
                "AND datasetid = " + dataSet.getId() + " GROUP BY sourceid) AS completed " +
            "WHERE completed.sources = " + compulsoryElements;
        
        return statementManager.getHolder().queryForInteger( sql );
    }
    
    public int getNumberOfValues( DataSet dataSet, Collection<Integer> children, Period period, Date deadline )
    {
        final String childrenIds = TextUtils.getCommaDelimitedString( children );
        final String deadlineCriteria = deadline != null ? "AND lastupdated < '" + DateUtils.getMediumDateString( deadline ) + "' " : "";
        
        final String sql =
            "SELECT count(*) FROM datavalue " +
            "JOIN datasetmembers USING (dataelementid) " +
            "JOIN dataset USING (datasetid) " +
            "WHERE datasetid = " + dataSet.getId() + " " + deadlineCriteria +
            "AND periodid = " + period.getId() + " " +
            "AND sourceid IN (" + childrenIds + ")";

        return statementManager.getHolder().queryForInteger( sql );
    }    

    public void createIndex()
    {
        try
        {
            final String sql = "CREATE INDEX aggregateddatasetcompleteness_index ON aggregateddatasetcompleteness (datasetid, periodid, organisationunitid)";        
            statementManager.getHolder().executeUpdate( sql );
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
            statementManager.getHolder().executeUpdate( sql );
        }
        catch ( Exception ex )
        {
            log.debug( "Index does not exist" );
        }
    }
}
