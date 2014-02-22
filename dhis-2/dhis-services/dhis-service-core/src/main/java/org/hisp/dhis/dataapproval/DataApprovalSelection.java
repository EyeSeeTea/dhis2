package org.hisp.dhis.dataapproval;

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

import org.hisp.dhis.dataelement.*;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * This package-private class is used by the data approval service to
 * describe selected data from a data set, such as could appear in a data set
 * report, and to determine its data approval status.
 * <p>
 * The entire reason for this class is to make the code more readable.
 * The whole class is focused on one goal. And the use of instance variables
 * greatly reduces the need to pass parameters between methods.
 *
 * @author Jim Grace
 * @version $Id$
 */
class DataApprovalSelection
{
    // -------------------------------------------------------------------------
    // Data selection parameters
    // -------------------------------------------------------------------------

    private DataSet dataSet;

    private Period period;

    private OrganisationUnit organisationUnit;

    private CategoryOptionGroup categoryOptionGroup;

    private Set<DataElementCategoryOption> dataElementCategoryOptions;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataApprovalStore dataApprovalStore;

    private DataApprovalLevelService dataApprovalLevelService;

    private DataElementCategoryService categoryService;

    private PeriodService periodService;

    // -------------------------------------------------------------------------
    // Internal variables
    // -------------------------------------------------------------------------

    private Map<CategoryOptionGroupSet, Set<CategoryOptionGroup>> dataGroups = null;

    private List<DataApprovalLevel> matchingApprovalLevels;

    private List<Set<CategoryOptionGroup>> categoryOptionGroupsByLevel;

    boolean approvableAtLevel;

    int thisIndex;

    int higherIndex;

    int lowerIndex;

    boolean dataSetAssignedAtOrBelowLevel = false;

    private DataApprovalState state = null;

    private DataApproval dataApproval = null;

    private static final boolean LOG = false;
    
    // -------------------------------------------------------------------------
    // Preconstructed Status object
    // -------------------------------------------------------------------------

    private static final DataApprovalStatus STATUS_UNAPPROVABLE = new DataApprovalStatus( DataApprovalState.UNAPPROVABLE, null);

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    DataApprovalSelection( DataSet dataSet,
                           Period period,
                           OrganisationUnit organisationUnit,
                           CategoryOptionGroup categoryOptionGroup,
                           Set<DataElementCategoryOption> dataElementCategoryOptions,
                           DataApprovalStore dataApprovalStore,
                           DataApprovalLevelService dataApprovalLevelService,
                           DataElementCategoryService categoryService,
                           PeriodService periodService )
    {
        this.dataSet = dataSet;
        this.period = period;
        this.organisationUnit = organisationUnit;
        this.categoryOptionGroup = categoryOptionGroup;
        this.dataElementCategoryOptions = dataElementCategoryOptions;
        this.dataApprovalStore = dataApprovalStore;
        this.dataApprovalLevelService = dataApprovalLevelService;
        this.categoryService = categoryService;
        this.periodService = periodService;
    }

    DataApprovalStatus getDataApprovalStatus()
    {
        if (LOG) log( logSelection() + " starting." );

        if ( !dataSet.isApproveData() )
        {
            if (LOG) log( "\n" + logSelection() + " returning UNAPPROVABLE (dataSet not marked for approval)" );
            return STATUS_UNAPPROVABLE;
        }

        findMatchingApprovalLevels();

        if ( matchingApprovalLevels.size() == 0 )
        {
            if (LOG) log( logSelection() + " returning UNAPPROVABLE (no matching approval levels)" );
            return STATUS_UNAPPROVABLE;
        }

        findThisLevel();

        if ( period.getPeriodType() != dataSet.getPeriodType() )
        {
            if ( period.getPeriodType().getFrequencyOrder() > dataSet.getPeriodType().getFrequencyOrder() )
            {
                findStatusForLongerPeriodType();
            }
            else
            {
                if (LOG) log( logSelection() + " returning UNAPPROVABLE (period type too short)" );
                return STATUS_UNAPPROVABLE;
            }
        }
        else
        {
            state = getState();
        }

        DataApprovalStatus status = new DataApprovalStatus();

        status.setDataApprovalState( state );

        status.setDataApproval( dataApproval );

        if (LOG) log( logSelection() + " returning " + state.name() );

        return status;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String logSelection()
    {
        return "getDataApprovalStatus( " + dataSet.getName() + ", " + period.getPeriodType().getName() + ":" + period.getShortName()
                + ", " + organisationUnit.getName() + " (level " + organisationUnit.getLevel() + "), "
                + ( categoryOptionGroup == null ? "null" : categoryOptionGroup.getName() ) + ", "
                + ( dataElementCategoryOptions == null ? "null" : ( "[" + dataElementCategoryOptions.size() + "]" ) ) + " )";
    }

    private void log(String s)
    {
        System.out.println( s );
    }

    /**
     * Handles the case where the selected period type is longer than the
     * data set period type. The selected period is broken down into data
     * set type periods. The approval status of the selected period is
     * constructed by logic that combines the approval statuses of the
     * constituent periods.
     *
     * @return status status of the longer period
     */
    private void findStatusForLongerPeriodType()
    {
        Collection<Period> testPeriods = periodService.getPeriodsBetweenDates( dataSet.getPeriodType(), period.getStartDate(), period.getEndDate() );

        for ( Period testPeriod : testPeriods )
        {
            period = testPeriod;

            DataApprovalState s = getState();

            switch ( s )
            {
                case APPROVED_HERE:
                case APPROVED_ELSEWHERE:

                    state = DataApprovalState.APPROVED_ELSEWHERE;

                    dataApproval = null;

                    break;

                case UNAPPROVED_READY:
                case UNAPPROVED_WAITING:
                case UNAPPROVED_ELSEWHERE:

                    state = DataApprovalState.UNAPPROVED_ELSEWHERE;

                    return;

                case UNAPPROVABLE:
                default: // (Not expected.)

                    state = s;

                    return;
            }
        }
    }

    /**
     * Find the approval status from a data selection that has the same
     * period type as the data set.
     *
     * @return the approval state.
     */
    private DataApprovalState getState()
    {
        if ( approvableAtLevel )
        {
            dataApproval = getDataApproval( thisIndex, organisationUnit );

            if ( dataApproval != null )
            {
                if ( dataElementCategoryOptions == null || dataElementCategoryOptions.size() == 0 )
                {
                    if (LOG) log( "getState() - approved here." );

                    return DataApprovalState.APPROVED_HERE;
                }

                if (LOG) log( "getState() - approved for a wider selection of category options." );

                return DataApprovalState.APPROVED_ELSEWHERE;
            }
        }

        if ( isApprovedAtHigherLevel() )
        {
            if (LOG) log( "getState() - isApprovedAtHigherLevel() true." );

            return DataApprovalState.APPROVED_ELSEWHERE;
        }

        boolean unapprovedBelow = isUnapprovedBelow( organisationUnit );

        if ( approvableAtLevel )
        {
            if ( !unapprovedBelow )
            {
                if (LOG) log( "getState() - not unapproved below." );

                return DataApprovalState.UNAPPROVED_READY;
            }

            if (LOG) log( "getState() - waiting." );

            return DataApprovalState.UNAPPROVED_WAITING;
        }

        if ( dataSetAssignedAtOrBelowLevel )
        {
            if (LOG) log( "getState() - waiting for higher-level approval at a higher level for data at or below this level." );

            return DataApprovalState.UNAPPROVED_ELSEWHERE;
        }

        if (LOG) log( "getState() - unapprovable because not approvable at level or below, and no dataset assignment." );

        return DataApprovalState.UNAPPROVABLE;
    }

    /**
     * Find approval levels that apply to this selection, based on the
     * approval level's category option groups. Approval levels without
     * category option groups always apply. Approval levels with category
     * option groups only apply if the category option group contains
     * category options that apply to the selected data.
     * <p>
     * For each matching approval level, also remember the category
     * option groups (if any) that apply to this data selection and
     * match this level's category option group set.
     */
    private void findMatchingApprovalLevels()
    {
        matchingApprovalLevels = new ArrayList<DataApprovalLevel>();

        categoryOptionGroupsByLevel = new ArrayList<Set<CategoryOptionGroup>>();

        List<DataApprovalLevel> allDataApprovalLevels = dataApprovalLevelService.getAllDataApprovalLevels();

        if ( allDataApprovalLevels != null )
        {
            for ( DataApprovalLevel level : allDataApprovalLevels )
            {
                if ( level.getCategoryOptionGroupSet() == null )
                {
                    matchingApprovalLevels.add( level );

                    categoryOptionGroupsByLevel.add ( null );
                }
                else
                {
                    initDataGroups();

                    Set<CategoryOptionGroup> groups = dataGroups.get( level.getCategoryOptionGroupSet() );

                    if ( groups != null )
                    {
                        matchingApprovalLevels.add( level );

                        categoryOptionGroupsByLevel.add ( groups );
                    }
                }
            }
        }

        if (LOG) log( "findMatchingApprovalLevels() " + allDataApprovalLevels.size() + " -> " +  matchingApprovalLevels.size() );
    }

    /**
     * Initializes the data groups if they have not yet been initialized.
     * This is a "lazy" operation that is only done if we find approval
     * levels that contain category option group sets which which we need
     * to compare.
     */
    private void initDataGroups()
    {
        if ( dataGroups == null )
        {
            dataGroups = new HashMap<CategoryOptionGroupSet, Set<CategoryOptionGroup>>();

            if ( categoryOptionGroup != null )
            {
                addDataGroup ( categoryOptionGroup.getGroupSet(), categoryOptionGroup );
            }

            if ( dataElementCategoryOptions != null )
            {
                addDataGroups( dataElementCategoryOptions );
            }
        }
    }

    private void addDataGroups( Set<DataElementCategoryOption> dataElementCategoryOptions )
    {
        //TODO: Should we replace this exhaustive search with a Hibernate query?

        Collection<CategoryOptionGroup> allGroups = categoryService.getAllCategoryOptionGroups();

        for ( CategoryOptionGroup group : allGroups )
        {
            if ( CollectionUtils.containsAny( group.getMembers(), dataElementCategoryOptions ) )
            {
                addDataGroup( group.getGroupSet(), group );
            }
        }
    }

    private void addDataGroup( CategoryOptionGroupSet groupSet, CategoryOptionGroup group )
    {
        Set<CategoryOptionGroup> groups = dataGroups.get( groupSet );

        if ( groups == null )
        {
            groups = new HashSet<CategoryOptionGroup>();

            dataGroups.put( groupSet, groups );
        }

        groups.add( group );
    }

    private void findThisLevel()
    {
        if (LOG) log( "findThisLevel() - matchingApprovalLevels.size() = " + matchingApprovalLevels.size() );

        for ( int i = matchingApprovalLevels.size() - 1; i >= 0; i-- )
        {
            if (LOG) log( "findThisLevel() - testing index " + i
                    + " level org level " + matchingApprovalLevels.get( i ).getOrganisationUnitLevel().getLevel()
                    + " selected org level " + organisationUnit.getLevel() );

            if ( matchingApprovalLevels.get( i ).getOrganisationUnitLevel().getLevel() == organisationUnit.getLevel() )
            {
                approvableAtLevel = true;

                thisIndex = i;
                higherIndex = i - 1;
                lowerIndex = i + 1;

                if (LOG) log( "findThisLevel() - approvable at " + thisIndex );

                return;
            }
            else if ( matchingApprovalLevels.get( i ).getOrganisationUnitLevel().getLevel() < organisationUnit.getLevel() )
            {
                approvableAtLevel = false;

                thisIndex = -1;
                higherIndex = i;
                lowerIndex = i + 1;

                if (LOG) log( "findThisLevel() - level too high at " + thisIndex );

                return;
            }
        }

        approvableAtLevel = false;

        thisIndex = -1;
        higherIndex = -1;
        lowerIndex = matchingApprovalLevels.size();

        if (LOG) log( "findThisLevel() - did not find level " );
    }

    private boolean isApprovedAtHigherLevel()
    {
        OrganisationUnit orgUnit = organisationUnit;

        for (int i = higherIndex; i >= 0; i-- )
        {
            if (LOG) log( "isApprovedAtHigherLevel() i = " + i );

            int orgLevel = orgUnit.getLevel();

            while ( orgLevel > matchingApprovalLevels.get( i ).getLevel() )
            {
                orgUnit = orgUnit.getParent();

                orgLevel--;
            }

            dataApproval = getDataApproval( i, orgUnit );

            if ( dataApproval != null )
            {
                return true;
            }
        }

        return false;
    }

    private DataApproval getDataApproval( int index, OrganisationUnit orgUnit )
    {
        Set<CategoryOptionGroup> groups = categoryOptionGroupsByLevel.get( index );

        if ( groups == null )
        {
            return dataApprovalStore.getDataApproval( dataSet, period, orgUnit, null );
        }

        for ( CategoryOptionGroup group : groups )
        {
            DataApproval d = dataApprovalStore.getDataApproval( dataSet, period, orgUnit, group );
            if ( d != null )
            {
                return d;
            }
        }

        return null;
    }

    /**
     * Test to see if we are waiting for approval below that could exist, but
     * does not yet.
     * <p>
     * Also, look to see if the data set is assigned to any descendant
     * organisation units. If there are no approval levels below us, then
     * keep looking to see if there are any data set assignments -- if not,
     * and if the main level is not approvable, then approval does not apply.
     * This means that the recursion down through org units could continue
     * even if we are not waiting for an approval -- because we want to see
     * if there is lower-level data to be entered or not for this data set.
     *
     * @param orgUnit Organisation unit to test
     * @return true if we find an approval level and org unit for which
     * an approval object does not exist, else false
     */
    private boolean isUnapprovedBelow ( OrganisationUnit orgUnit )
    {
        if (LOG) log( "isUnapprovedBelow( " + orgUnit.getName() + " )" );

        if ( dataSetAssignedAtOrBelowLevel == false && orgUnit.getAllDataSets().contains( dataSet ) )
        {
            dataSetAssignedAtOrBelowLevel = true;
        }

        if ( lowerIndex < matchingApprovalLevels.size() )
        {
            if ( orgUnit.getLevel() == matchingApprovalLevels.get( lowerIndex ).getLevel() )
            {
                if (LOG) log( "isUnapprovedBelow() orgUnit level " + orgUnit.getLevel() + " matches approval level." );

                DataApproval d = getDataApproval( lowerIndex, orgUnit );

                if (LOG) log( "isUnapprovedBelow() returns " + ( d == null ) + " after looking for approval for this orgUnit." );

                return ( d == null );
            }
        }
        else if ( dataSetAssignedAtOrBelowLevel )
        {
            if (LOG) log( "isUnapprovedBelow() returns false." );

            return false;
        }

        if ( orgUnit.getChildren() == null || orgUnit.getChildren().size() == 0 )
        {
            if (LOG) log( "isUnapprovedBelow() returns false." );

            return false;
        }

        if (LOG) log( "+++ isUnapprovedBelow( " + orgUnit.getName() + " ) is recursing..." );

        for ( OrganisationUnit child : orgUnit.getChildren() )
        {
            if ( isUnapprovedBelow( child ) )
            {
                if (LOG) log( "--- isUnapprovedBelow( " + orgUnit.getName() + " ) returns true because unapproved from below." );

                return true;
            }
        }

        if (LOG) log( "--- isUnapprovedBelow( " + orgUnit.getName() + " ) returns false after recursing" );

        return false;
    }
}
