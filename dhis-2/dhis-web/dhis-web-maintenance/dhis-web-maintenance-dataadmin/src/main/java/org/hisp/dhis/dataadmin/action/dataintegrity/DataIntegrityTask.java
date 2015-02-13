package org.hisp.dhis.dataadmin.action.dataintegrity;

/*
 * Copyright (c) 2004-2015, University of Oslo
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataintegrity.DataIntegrityResult;
import org.hisp.dhis.dataintegrity.DataIntegrityService;
import org.hisp.dhis.scheduling.TaskId;
import org.hisp.dhis.system.notification.NotificationLevel;
import org.hisp.dhis.system.notification.Notifier;
import org.hisp.dhis.system.timer.SystemTimer;
import org.hisp.dhis.system.timer.Timer;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Halvdan Hoem Grelland
 */
public class DataIntegrityTask implements Runnable
{
    private static final Log log = LogFactory.getLog( DataIntegrityTask.class );

    private TaskId taskId;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataIntegrityService dataIntegrityService;

    private Notifier notifier;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataIntegrityTask( DataIntegrityService dataIntegrityService, Notifier notifier , TaskId taskId )
    {
        this.dataIntegrityService = dataIntegrityService;
        this.notifier = notifier;
        this.taskId = taskId;
    }

    // -------------------------------------------------------------------------
    // Runnable implementation
    // -------------------------------------------------------------------------

    @Override
    public void run()
    {
        Timer timer = new SystemTimer().start();

        DataIntegrityResult dataIntegrityResult = new DataIntegrityResult();

        dataIntegrityResult.setDataElementsWithoutDataSet( new ArrayList<>( dataIntegrityService.getDataElementsWithoutDataSet() ) );

        dataIntegrityResult.setDataElementsWithoutGroups( new ArrayList<>( dataIntegrityService.getDataElementsWithoutGroups() ) );
        dataIntegrityResult.setDataElementsAssignedToDataSetsWithDifferentPeriodTypes( dataIntegrityService.getDataElementsAssignedToDataSetsWithDifferentPeriodTypes() );
        dataIntegrityResult.setDataElementsViolatingExclusiveGroupSets( dataIntegrityService.getDataElementsViolatingExclusiveGroupSets() );
        dataIntegrityResult.setDataElementsInDataSetNotInForm( dataIntegrityService.getDataElementsInDataSetNotInForm() );

        log.info( "Checked data elements" );

        dataIntegrityResult.setCategoryOptionCombosNotInDataElementCategoryCombo( dataIntegrityService.getCategoryOptionCombosNotInDataElementCategoryCombo() );

        log.info( "Checked operands" );

        dataIntegrityResult.setDataSetsNotAssignedToOrganisationUnits( new ArrayList<>( dataIntegrityService.getDataSetsNotAssignedToOrganisationUnits() ) );
        dataIntegrityResult.setSectionsWithInvalidCategoryCombinations( new ArrayList<>( dataIntegrityService.getSectionsWithInvalidCategoryCombinations() ) );

        log.info( "Checked data sets" );

        dataIntegrityResult.setIndicatorsWithIdenticalFormulas( dataIntegrityService.getIndicatorsWithIdenticalFormulas() );
        dataIntegrityResult.setIndicatorsWithoutGroups( new ArrayList<>( dataIntegrityService.getIndicatorsWithoutGroups() ) );
        dataIntegrityResult.setInvalidIndicatorNumerators( dataIntegrityService.getInvalidIndicatorNumerators() );
        dataIntegrityResult.setInvalidIndicatorDenominators( dataIntegrityService.getInvalidIndicatorDenominators() );
        dataIntegrityResult.setIndicatorsViolatingExclusiveGroupSets( dataIntegrityService.getIndicatorsViolatingExclusiveGroupSets() );

        log.info( "Checked indicators" );

        dataIntegrityResult.setDuplicatePeriods( dataIntegrityService.getDuplicatePeriods() );

        log.info( "Checked periods" );

        dataIntegrityResult.setOrganisationUnitsWithCyclicReferences( new ArrayList<>( dataIntegrityService.getOrganisationUnitsWithCyclicReferences() ) );
        dataIntegrityResult.setOrphanedOrganisationUnits( new ArrayList<>( dataIntegrityService.getOrphanedOrganisationUnits() ) );
        dataIntegrityResult.setOrganisationUnitsWithoutGroups( new ArrayList<>( dataIntegrityService.getOrganisationUnitsWithoutGroups() ) );
        dataIntegrityResult.setOrganisationUnitsViolatingExclusiveGroupSets( dataIntegrityService.getOrganisationUnitsViolatingExclusiveGroupSets() );
        dataIntegrityResult.setOrganisationUnitGroupsWithoutGroupSets( new ArrayList<>( dataIntegrityService.getOrganisationUnitGroupsWithoutGroupSets() ) );
        dataIntegrityResult.setValidationRulesWithoutGroups( new ArrayList<>( dataIntegrityService.getValidationRulesWithoutGroups() ) );

        log.info( "Checked organisation units" );

        dataIntegrityResult.setInvalidValidationRuleLeftSideExpressions( dataIntegrityService.getInvalidValidationRuleLeftSideExpressions() );
        dataIntegrityResult.setInvalidValidationRuleRightSideExpressions( dataIntegrityService.getInvalidValidationRuleRightSideExpressions() );

        log.info( "Checked validation rules" );

        Collections.sort( dataIntegrityResult.getDataElementsWithoutDataSet(), IdentifiableObjectNameComparator.INSTANCE );
        Collections.sort( dataIntegrityResult.getDataElementsWithoutGroups(), IdentifiableObjectNameComparator.INSTANCE );
        Collections.sort( dataIntegrityResult.getDataSetsNotAssignedToOrganisationUnits(), IdentifiableObjectNameComparator.INSTANCE );
        Collections.sort( dataIntegrityResult.getSectionsWithInvalidCategoryCombinations(), IdentifiableObjectNameComparator.INSTANCE );
        Collections.sort( dataIntegrityResult.getIndicatorsWithoutGroups(), IdentifiableObjectNameComparator.INSTANCE );
        Collections.sort( dataIntegrityResult.getOrganisationUnitsWithCyclicReferences(), IdentifiableObjectNameComparator.INSTANCE );
        Collections.sort( dataIntegrityResult.getOrphanedOrganisationUnits(), IdentifiableObjectNameComparator.INSTANCE );
        Collections.sort( dataIntegrityResult.getOrganisationUnitsWithoutGroups(), IdentifiableObjectNameComparator.INSTANCE );
        Collections.sort( dataIntegrityResult.getOrganisationUnitGroupsWithoutGroupSets(), IdentifiableObjectNameComparator.INSTANCE );
        Collections.sort( dataIntegrityResult.getValidationRulesWithoutGroups(), IdentifiableObjectNameComparator.INSTANCE );

        timer.stop();

        if ( taskId != null )
        {
            notifier.notify( taskId, NotificationLevel.INFO, "Data integrity checks completed in " + timer.toString() + ".", true )
                .addTaskSummary( taskId, dataIntegrityResult );
        }
    }
}
