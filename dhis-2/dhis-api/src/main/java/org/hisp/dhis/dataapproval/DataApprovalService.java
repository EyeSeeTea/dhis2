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

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.user.User;

import java.util.Date;

/**
 * @author Jim Grace
 * @version $Id$
 */
public interface DataApprovalService
{
    String ID = DataApprovalService.class.getName();

    /**
     * Adds a DataApproval.
     *
     * @param dataApproval the DataApproval to add.
     */
    void addDataApproval( DataApproval dataApproval );

    /**
     * Updates a DataApproval.
     *
     * @param dataApproval the DataApproval to update.
     */
    void updateDataApproval( DataApproval dataApproval );

    /**
     * Deletes a DataValue.
     *
     * @param dataApproval the DataApproval to delete.
     */
    void deleteDataApproval( DataApproval dataApproval );

    /**
     * Checks to see if a given DataSet is approved for a given Period and
     * OrganisationUnit. If data approval is not configured to be used,
     * always returns TRUE.
     *
     * @param dataSet DataSet to check for approval.
     * @param period Period to check for approval.
     * @param source OrganisationUnit to check for approval.
     * @return TRUE if approved (or approval not being used), else FALSE.
     */
    public boolean isApproved( DataSet dataSet, Period period, OrganisationUnit source );

    /**
     * Approves a DataSet for a given Period and OrganisationUnit.
     *
     * @param dataSet DataSet to check for approval.
     * @param period Period to check for approval.
     * @param source OrganisationUnit to check for approval.
     * @param created Date/time at which the approval was made.
     * @param creator User who made the approval.
     */
    public void approve( DataSet dataSet, Period period, OrganisationUnit source,
                         Date created, User creator );

    /**
     * Unapproves a DataSet for a given Period and OrganisationUnit.
     *
     * @param dataSet DataSet to check for approval.
     * @param period Period to check for approval.
     * @param source OrganisationUnit to check for approval.
     */
    public void unapprove( DataSet dataSet, Period period, OrganisationUnit source );
}
