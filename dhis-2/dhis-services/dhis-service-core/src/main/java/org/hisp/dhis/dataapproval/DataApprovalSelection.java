package org.hisp.dhis.dataapproval;

/*
 * Copyright (c) 2004-2014, University of Oslo
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.CategoryOptionGroup;
import org.hisp.dhis.dataelement.CategoryOptionGroupSet;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodService;

/**
 * This package-private class is used by the data approval service to
 * describe selected data from a data set, such as could appear in a data set
 * report or data approval report, to determine its data approval status.
 * <p>
 * The entire reason for this class is to make the code more readable.
 * The use of instance variables greatly reduces the need to pass parameters
 * between methods.
 *
 * @author Jim Grace
 * @version $Id$
 */
class DataApprovalSelection
{
    private final static Log log = LogFactory.getLog( DataApprovalSelection.class );

    private final static int INDEX_NOT_FOUND = -1;

    // -------------------------------------------------------------------------
    // Data selection parameters
    // -------------------------------------------------------------------------

    private List<DataApproval> dataApprovals;

    private DataApproval originalDataApproval;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataApprovalStore dataApprovalStore;

    private DataApprovalLevelService dataApprovalLevelService;

    private OrganisationUnitService organisationUnitService;

    private DataElementCategoryService categoryService;

    private PeriodService periodService;

    // -------------------------------------------------------------------------
    // Internal instance variables
    // -------------------------------------------------------------------------

    private List<DataApprovalLevel> allApprovalLevels;

    private DataApproval daIn; // Current DataApproval being checked.

    private DataApproval daOut = null; // DataApproval returned from DB.

    private OrganisationUnit selectedOrgUnit; // Selection org unit.

    private int organisationUnitLevel; // Selection's org unit level.

    private List<OrganisationUnit> organisationUnitAncestors;

    // -------------------------------------------------------------------------
    // Preconstructed Status object
    // -------------------------------------------------------------------------

    private static final DataApprovalStatus STATUS_UNAPPROVABLE = new DataApprovalStatus( DataApprovalState.UNAPPROVABLE, null, null);

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructs a data approval selection.
     *
     * @param dataApprovals describes the parts of the selection
     * @param originalDataApproval contains original (undivided) period, etc.
     * @param dataApprovalStore service object reference
     * @param dataApprovalLevelService service object reference
     * @param organisationUnitService service object reference
     * @param categoryService service object reference
     * @param periodService service object reference
     */
    DataApprovalSelection( List<DataApproval> dataApprovals,
                           DataApproval originalDataApproval,
                           DataApprovalStore dataApprovalStore,
                           DataApprovalLevelService dataApprovalLevelService,
                           OrganisationUnitService organisationUnitService,
                           DataElementCategoryService categoryService,
                           PeriodService periodService )
    {
        this.dataApprovals = dataApprovals;
        this.originalDataApproval = originalDataApproval;
        this.dataApprovalStore = dataApprovalStore;
        this.dataApprovalLevelService = dataApprovalLevelService;
        this.categoryService = categoryService;
        this.organisationUnitService = organisationUnitService;
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // Package-private method
    // -------------------------------------------------------------------------

    /**
     * Gets the data approval status for the selection, where the selection is
     * defined by a list of approvals objects. Note that all of the approvals
     * objects in the list must have the same organisation unit and the same
     * approval level.
     * <p>
     * This is done by looping through the list of approvals objects and
     * finding the status for the data described in each approvals object.
     * Each status is combined with the previous status by means of a state
     * machine, to get the "lowest common" status for all approvals objects.
     *
     * @return data approval status (lowest common status for the selection.)
     */
    DataApprovalStatus getDataApprovalStatus()
    {
        allApprovalLevels = dataApprovalLevelService.getAllDataApprovalLevels();

        if ( allApprovalLevels.isEmpty() ) // No approval levels defined!
        {
            return new DataApprovalStatus( DataApprovalState.UNAPPROVABLE, null, null );
        }

        DataApprovalStatus status = null;

        selectedOrgUnit = originalDataApproval.getOrganisationUnit();
        organisationUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( selectedOrgUnit );
        organisationUnitAncestors = selectedOrgUnit.getAncestors();

        log.debug( "----------------------------------------------------------------------" );
        log.debug( "getDataApprovalStatus() org unit " +  selectedOrgUnit.getName()
                + " (" + organisationUnitLevel + ") "
                + ") data set " + originalDataApproval.getDataSet().getName()
                + " original period " + originalDataApproval.getPeriod().getPeriodType().getName() + " " + originalDataApproval.getPeriod().getName()
                + " approval level " + originalDataApproval.getDataApprovalLevel().getLevel()
                + " approval count " + dataApprovals.size()
                + " starting." );

        for ( DataApproval dLoop : dataApprovals )
        {
            daIn = dLoop;

            if ( daIn.getOrganisationUnit() != selectedOrgUnit )        // Should not happen.
            {
                log.info( "Mismatch org unit " + ( daIn.getOrganisationUnit() == null ? "(null)" : daIn.getOrganisationUnit().getName() )
                        + " with " + ( selectedOrgUnit == null ? "(null)" : selectedOrgUnit.getName() ) );

                return new DataApprovalStatus( DataApprovalState.UNAPPROVABLE, null, null );
            }

            status = combineStatus( status, getStatus() );
        }

        if ( status.getDataApproval() != null )
        {
            status.getDataApproval().setPeriod( originalDataApproval.getPeriod() );
        }

        log.debug( "getDataApprovalStatus() org unit " +  selectedOrgUnit.getName()
                + " (" + organisationUnitLevel + ") "
                + ") data set " + originalDataApproval.getDataSet().getName()
                + " original period " + originalDataApproval.getPeriod().getPeriodType().getName() + " " + originalDataApproval.getPeriod().getName()
                + " approval level " + originalDataApproval.getDataApprovalLevel().getLevel()
                + " approval count " + dataApprovals.size()
                + " returning " + logStatus( status ) );

        return status;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Combine old (existing) approval status with new approval status
     * (from testing the status from another dataApproval object), resulting
     * in a new combined status.
     *
     * @param oldStatus old (existing) approval status
     * @param newStatus new approval status
     * @return new (combined) approval status
     */
    private DataApprovalStatus combineStatus( DataApprovalStatus oldStatus, DataApprovalStatus newStatus )
    {
        DataApprovalStatus status = newStatus;

        if ( oldStatus != null )
        {
            if ( oldStatus.getDataApprovalLevel().getLevel() > newStatus.getDataApprovalLevel().getLevel() )
            {
                status = oldStatus;
            }
            else if ( oldStatus.getDataApprovalLevel().getLevel() == newStatus.getDataApprovalLevel().getLevel() )
            {
                DataApprovalState state = DataApprovalAggregator.nextState( oldStatus.getDataApprovalState(), newStatus.getDataApprovalState() );

                DataApproval da = newStatus.getDataApproval().isAccepted() ? oldStatus.getDataApproval() : newStatus.getDataApproval();

                status = new DataApprovalStatus( state, da, oldStatus.getDataApprovalLevel() );
            }
        }

        log.debug( "combineStatus( " + logStatus( oldStatus ) + ", " + logStatus( newStatus ) + " ) -> " + logStatus ( status ) );

        return status;
    }

    /**
     * Formats the essential fields from status for display in the log.
     *
     * @param status status to log
     * @return string representing approval level and state
     */
    private String logStatus( DataApprovalStatus status )
    {
        return status == null ? "(null)" : status.getDataApprovalLevel().getLevel() + "-" + status.getDataApprovalState().name();
    }

    /**
     * Finds the approval status from a single approval data selection.
     *
     * @return the approval status
     */
    private DataApprovalStatus getStatus()
    {
        for ( DataApprovalLevel dal : allApprovalLevels )
        {
            if ( dal.getCategoryOptionGroupSet() != null && daIn.getAttributeOptionCombo().equals( categoryService.getDefaultDataElementCategoryOptionCombo() ) )
            {
                return new DataApprovalStatus( DataApprovalState.UNAPPROVED_READY, daIn, dal );
            }

            if ( dal.getOrgUnitLevel() < organisationUnitLevel )
            {
                if ( isApproved( dal, organisationUnitAncestors.get( dal.getOrgUnitLevel() ) ) )
                {
                    if ( daOut.isAccepted() )
                    {
                        return new DataApprovalStatus( DataApprovalState.ACCEPTED_ELSEWHERE, daOut, dal );
                    }
                    else
                    {
                        return new DataApprovalStatus( DataApprovalState.APPROVED_ELSEWHERE, daOut, dal );
                    }
                }
            }
            else if ( dal.getOrgUnitLevel() == organisationUnitLevel )
            {
                if ( isApproved( dal, selectedOrgUnit ) )
                {
                    if ( daOut.isAccepted() )
                    {
                        return new DataApprovalStatus( DataApprovalState.ACCEPTED_HERE, daOut, dal );
                    }
                    else
                    {
                        return new DataApprovalStatus( DataApprovalState.APPROVED_HERE, daOut, dal );
                    }
                }
            }
            else if ( isUnapprovedBelow( dal, selectedOrgUnit, organisationUnitLevel ) )
            {
                return new DataApprovalStatus( DataApprovalState.UNAPPROVED_WAITING, daIn, dal );
            }
            else
            {
                return new DataApprovalStatus( DataApprovalState.UNAPPROVED_READY, daIn, dal );
            }
        }

        return new DataApprovalStatus( DataApprovalState.UNAPPROVED_READY, daIn, allApprovalLevels.get( allApprovalLevels.size() - 1 ) );
    }

    private boolean isApproved( DataApprovalLevel dal, OrganisationUnit orgUnit )
    {
        daOut = dataApprovalStore.getDataApproval( dal, daIn.getDataSet(), daIn.getPeriod(), orgUnit, daIn.getAttributeOptionCombo() );

        return daOut != null;
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
     * @param dal data approval level to test
     * @param orgUnit Organisation unit to test
     * @param orgUnitLevel The corresponding organisation unit level
     * @return true if we find an approval level and org unit for which
     * an approval object does not exist, else false
     */
    private boolean isUnapprovedBelow ( DataApprovalLevel dal, OrganisationUnit orgUnit, int orgUnitLevel )
    {
        log.debug( "isUnapprovedBelow( " + dal.getLevel() + ", " + orgUnit.getName() + ", " + orgUnitLevel + " )" );

        if ( orgUnitLevel == dal.getOrgUnitLevel() )
        {
            if ( isApproved( dal, orgUnit ) )
            {
                return false; // OK here because there's an approval.
            }
        }

        if ( orgUnitLevel >= dal.getOrgUnitLevel() && orgUnit.getAllDataSets().contains( daIn.getDataSet() ) )
        {
            return true; // Not OK because we see a data set assignment but no approval.
        }

        for ( OrganisationUnit child : orgUnit.getChildren() )
        {
            if ( isUnapprovedBelow( dal, child, orgUnitLevel + 1 ) )
            {
                log.debug( "isUnapprovedBelow( " + dal.getLevel() + ", " + orgUnit.getName() + ", " + orgUnitLevel
                        + " ) returns true because unapproved from below." );

                return true;
            }
        }

        return false;
    }
}
