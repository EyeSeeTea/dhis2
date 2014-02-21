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

    int thisLevel;

    private DataApprovalState state = null;

    private DataApprovalStatus status;

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
        if ( !dataSet.isApproveData() )
        {
            return STATUS_UNAPPROVABLE;
        }

        findMatchingApprovalLevels();

        if ( matchingApprovalLevels.size() == 0 )
        {
            return STATUS_UNAPPROVABLE;
        }

        findThisLevel();

        status = new DataApprovalStatus();

        if ( period.getPeriodType() != dataSet.getPeriodType() )
        {
            if ( period.getPeriodType().getFrequencyOrder() > dataSet.getPeriodType().getFrequencyOrder() )
            {
                findStatusForLongerPeriodType();
            }
            else
            {
                return STATUS_UNAPPROVABLE;
            }
        }
        else
        {
            state = getApprovableState();
        }

        status.setDataApprovalState( state );

        return status;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

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

            DataApprovalState s = getApprovableState();

            switch ( s )
            {
                case APPROVED_HERE:
                case APPROVED_ELSEWHERE:
                    status.setDataApproval( null );
                    if ( state == null )
                    {
                        state = DataApprovalState.APPROVED_ELSEWHERE;
                    }

                case UNAPPROVED_READY:
                    state = s;
                    break;

                case UNAPPROVED_WAITING:
                case UNAPPROVED_ELSEWHERE:
                case UNAPPROVABLE: // (Not expected.)
                default: // (Not expected.)
                    state = s;
                    return;
            }
        }

        return;
    }

    /**
     * Find the approval status from a data selection that is already
     * determined to be "approvable" (whether or not it has been approved.)
     *
     * @return the approval status.
     */
    private DataApprovalState getApprovableState()
    {
        if ( isApprovedAtHigherLevel() )
        {
            return DataApprovalState.APPROVED_ELSEWHERE;
        }
        else if ( approvableAtLevel )
        {
            DataApproval d = getDataApproval( thisLevel, organisationUnit );

            if ( d != null )
            {
                if ( dataElementCategoryOptions == null || dataElementCategoryOptions.size() == 0 )
                {
                    status.setDataApproval( d );

                    return DataApprovalState.APPROVED_HERE;
                }

                return DataApprovalState.APPROVED_ELSEWHERE;
            }

            if ( isApprovedJustBelow( organisationUnit ) )
            {
                return DataApprovalState.UNAPPROVED_READY;
            }

            return DataApprovalState.UNAPPROVED_WAITING;
        }

        return DataApprovalState.UNAPPROVED_ELSEWHERE;
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

        for ( DataApprovalLevel level : dataApprovalLevelService.getAllDataApprovalLevels() )
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
        for ( int i = matchingApprovalLevels.size() - 1; i <= 0; i-- )
        {
            if ( matchingApprovalLevels.get( i ).getOrganisationUnitLevel().getLevel() == organisationUnit.getLevel() )
            {
                approvableAtLevel = true;

                thisLevel = i;

                return;
            }
            else if ( matchingApprovalLevels.get( i ).getOrganisationUnitLevel().getLevel() > organisationUnit.getLevel() )
            {
                approvableAtLevel = false;

                thisLevel = i;

                return;
            }
        }

        approvableAtLevel = false;

        thisLevel = matchingApprovalLevels.size();

        return;
    }

    private boolean isApprovedAtHigherLevel()
    {
        for (int i = thisLevel - 1; i <= 0; i-- )
        {
            int orgLevel = organisationUnit.getLevel();

            while ( orgLevel > matchingApprovalLevels.get( i ).getLevel() )
            {
                organisationUnit = organisationUnit.getParent();

                orgLevel--;
            }

            if ( getDataApproval( i, organisationUnit ) != null )
            {
                return true;
            }
        }

        return false;
    }

    private DataApproval getDataApproval( int i, OrganisationUnit orgUnit )
    {
        Set<CategoryOptionGroup> groups = categoryOptionGroupsByLevel.get( i );

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

    private boolean isApprovedJustBelow ( OrganisationUnit orgUnit )
    {
        if ( thisLevel + 1 >= matchingApprovalLevels.size() )
        {
            return false;
        }

        if ( orgUnit.getLevel() > matchingApprovalLevels.get( thisLevel + 1 ).getLevel() )
        {
            if ( orgUnit.getChildren() == null || orgUnit.getChildren().size() == 0 )
            {
                return false;
            }

            for ( OrganisationUnit child : orgUnit.getChildren() )
            {
                if ( !isApprovedJustBelow( child ) )
                {
                    return false;
                }
            }

            return true;
        }

        return ( null != getDataApproval( thisLevel + 1, orgUnit ) );
    }
}
