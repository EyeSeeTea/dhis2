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

import org.apache.commons.collections.CollectionUtils;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.user.User;

import java.io.Serializable;

/**
 * @author Jim Grace
 * @version $Id$
 */
public class DefaultDataApprovalService
    implements DataApprovalService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataApprovalStore dataApprovalStore;

    public void setDataApprovalStore( DataApprovalStore dataApprovalStore )
    {
        this.dataApprovalStore = dataApprovalStore;
    }

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -------------------------------------------------------------------------
    // DataApproval
    // -------------------------------------------------------------------------

    public void addDataApproval( DataApproval dataApproval )
    {
        dataApprovalStore.addDataApproval( dataApproval );
    }

    public void deleteDataApproval( DataApproval dataApproval )
    {
        dataApprovalStore.deleteDataApproval( dataApproval );

        for ( OrganisationUnit ancestor : dataApproval.getSource().getAncestors() )
        {
            DataApproval ancestorApproval = dataApprovalStore.getDataApproval(
                    dataApproval.getDataSet(), dataApproval.getPeriod(), ancestor );

            if ( ancestorApproval != null ) {
                dataApprovalStore.deleteDataApproval ( ancestorApproval );
            }
        }
    }

    public DataApproval getDataApproval( DataSet dataSet, Period period, OrganisationUnit source )
    {
        return dataApprovalStore.getDataApproval( dataSet, period, source );
    }

    public DataApprovalState getDataApprovalState( DataSet dataSet, Period period, OrganisationUnit source )
    {
        //
        // If not configured globally, or for this data set, return APPROVAL_NOT_NEEDED.
        //
        Serializable enableDataApprovalSetting = systemSettingManager.getSystemSetting( SystemSettingManager.KEY_ENABLE_DATA_APPROVAL );
        if ( enableDataApprovalSetting == null || ! (Boolean) enableDataApprovalSetting || ! dataSet.isApproveData() )
        {
            return DataApprovalState.APPROVAL_NOT_NEEDED;
        }

        //
        // If approved, return APPROVED.
        //
        if ( null != dataApprovalStore.getDataApproval( dataSet, period, source ) )
        {
            return DataApprovalState.APPROVED;
        }

        boolean approvedAtLowerLevels = false; // Until proven otherwise.

        for ( OrganisationUnit child : source.getChildren() )
        {
            switch ( getDataApprovalState( dataSet, period, child ) )
            {
                //
                // If ready or waiting at a lower level, return
                // WAITING_FOR_LOWER_LEVEL_APPROVAL at this level.
                //
                case READY_FOR_APPROVAL:
                case WAITING_FOR_LOWER_LEVEL_APPROVAL:
                    return DataApprovalState.WAITING_FOR_LOWER_LEVEL_APPROVAL;

                case APPROVED:
                    approvedAtLowerLevels = true;
                    break;

                case APPROVAL_NOT_NEEDED:
                    break; // Do nothing.
            }

        }

        //
        // If approved at lower levels (and not ready or waiting at any),
        // and/or if data is configured for entry at this level (whether or
        // not it has been entered), return READY_FOR_APPROVAL.
        //
        if ( approvedAtLowerLevels ||
             source.getAllDataSets().contains ( dataSet ) )
        {
            return DataApprovalState.READY_FOR_APPROVAL;
        }

        //
        // Finally, if we haven't seen any approval action at lower levels,
        // and this level is not configured for data entry from this data set,
        // then return APPROVAL_NOT_NEEDED.
        //
        return DataApprovalState.APPROVAL_NOT_NEEDED;
    }

    public boolean mayApprove( OrganisationUnit source, User user,
                               boolean mayApproveAtSameLevel,
                               boolean mayApproveAtLowerLevels )
    {
        if ( mayApproveAtSameLevel && user.getOrganisationUnits().contains( source ) )
        {
            return true;
        }

        if ( mayApproveAtLowerLevels && CollectionUtils.containsAny( user.getOrganisationUnits(), source.getAncestors() ) )
        {
            return true;
        }

        return false;
    }

    public boolean mayUnapprove( DataApproval dataApproval, User user,
                                 boolean mayApproveAtSameLevel,
                                 boolean mayApproveAtLowerLevels )
    {
        if ( isAuthorizedToUnapprove( dataApproval.getSource(), user, mayApproveAtSameLevel, mayApproveAtLowerLevels ) )
        {
            // Check approvals at higher levels that may block this unapproval:

            for ( OrganisationUnit ancestor : dataApproval.getSource().getAncestors() )
            {
                DataApproval ancestorDataApproval = dataApprovalStore.getDataApproval(
                        dataApproval.getDataSet(), dataApproval.getPeriod(), ancestor );
                if ( ancestorDataApproval != null &&
                        ! isAuthorizedToUnapprove( ancestor, user, mayApproveAtSameLevel, mayApproveAtLowerLevels ) )
                {
                    return false; // Could unapprove at that level, but higher-level approval is blocking.
                }
            }

            return true; // May unapprove at that level, and no higher-level approval is blocking.
        }

        return false; // May not unapprove at that level.
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Tests whether the user is authorized to unapprove for this organisation
     * unit.
     * <p>
     * Whether the user actually may unapprove an existing approval depends
     * also on whether there are higher-level approvals that the user is
     * authorized to unapprove.
     *
     * @param source OrganisationUnit to check for approval.
     * @param user The current user.
     * @param mayApproveAtSameLevel Tells whether the user has the authority
     *        to approve data for the user's assigned organisation unit(s).
     * @param mayApproveAtLowerLevels Tells whether the user has the authority
     *        to approve data below the user's assigned organisation unit(s).
     * @return true if the user may approve, otherwise false
     */
    private boolean isAuthorizedToUnapprove( OrganisationUnit source, User user,
                                             boolean mayApproveAtSameLevel,
                                             boolean mayApproveAtLowerLevels )
    {
        if ( mayApprove( source, user, mayApproveAtSameLevel, mayApproveAtLowerLevels ) )
        {
            return true;
        }

        for ( OrganisationUnit ancestor : source.getAncestors() )
        {
            if ( mayApprove( ancestor, user, mayApproveAtSameLevel, mayApproveAtLowerLevels ) )
            {
                return true;
            }
        }

        return false;
    }
}
