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

import org.hisp.dhis.dataelement.CategoryOptionGroup;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

import java.util.List;
import java.util.Set;

/**
 * @author Jim Grace
 * @version $Id$
 */
public interface DataApprovalService
{
    String ID = DataApprovalService.class.getName();

    /**
     * Approves data.
     * <p>
     * If present, dataSets, attributeOptionGroups and/or attributeOptions
     * override the properties specified in the dataApproval object.
     *
     * @param dataApproval the data approval properties to approve.
     * @param dataSets data sets to check for approval (if any).
     * @param attributeOptionGroups attribute option groups to approve.
     * @param attributeOptions attribute options to approve.
     */
    void approveData( DataApproval dataApproval,
                      Set<DataSet> dataSets,
                      Set<CategoryOptionGroup> attributeOptionGroups,
                      Set<DataElementCategoryOption> attributeOptions )
            throws ApprovalException;

    /**
     * Approves data from a list of data approval objects.
     *
     * @param dataApprovalList describes the data to be approved.
     */
    void approveData(List<DataApproval> dataApprovalList)
            throws ApprovalException;

    /**
     * Unapproves data.
     * <p>
     * If present, dataSets, attributeOptionGroups and/or attributeOptions
     * override the properties specified in the dataApproval object.
     *
     * @param dataApproval the data approval properties to unapprove.
     * @param dataSets data sets to check for approval (if any).
     * @param attributeOptionGroups attribute option groups to unapprove.
     * @param attributeOptions attribute options to unapprove.
     */
    void unapproveData( DataApproval dataApproval,
                        Set<DataSet> dataSets,
                        Set<CategoryOptionGroup> attributeOptionGroups,
                        Set<DataElementCategoryOption> attributeOptions )
            throws ApprovalException;

    /**
     * Unapproves data from a list of data approval objects.
     *
     * @param dataApprovalList describes the data to be unapproved.
     */
    void unapproveData(List<DataApproval> dataApprovalList)
            throws ApprovalException;

    /**
     * Accepts data.
     * <p>
     * If present, dataSets, attributeOptionGroups and/or attributeOptions
     * override the properties specified in the dataApproval object.
     *
     * @param dataApproval the data approval properties to accept.
     * @param dataSets data sets to check for approval (if any).
     * @param attributeOptionGroups attribute option groups to accept.
     * @param attributeOptions attribute options to accept.
     */
    void acceptData( DataApproval dataApproval,
                     Set<DataSet> dataSets,
                     Set<CategoryOptionGroup> attributeOptionGroups,
                     Set<DataElementCategoryOption> attributeOptions )
            throws ApprovalException;

    /**
     * Accepts data from a list of data approval objects.
     *
     * @param dataApprovalList describes the data to be accepted.
     */
    void acceptData(List<DataApproval> dataApprovalList)
            throws ApprovalException;

    /**
     * Unaccepts data.
     * <p>
     * If present, attributeOptionGroups and/or attributeOptions
     * override the attribute option group combination specified
     * in the dataApproval object.
     *
     * @param dataApproval the data approval properties to unaccept.
     * @param dataSets data sets to check for approval (if any).
     * @param attributeOptionGroups attribute option groups to unaccept.
     * @param attributeOptions attribute options to unaccept.
     */
    void unacceptData( DataApproval dataApproval,
                       Set<DataSet> dataSets,
                       Set<CategoryOptionGroup> attributeOptionGroups,
                       Set<DataElementCategoryOption> attributeOptions )
            throws ApprovalException;

    /**
     * Unaccepts data from a list of data approval objects.
     *
     * @param dataApprovalList describes the data to be unaccepted.
     */
    void unacceptData(List<DataApproval> dataApprovalList)
            throws ApprovalException;

    /**
     * Returns the data approval status for a given data set, period,
     * organisation unit and attribute category combination.
     * If attributeOptionCombo is null, the default option combo will be used.
     * If data is approved at multiple levels, the lowest level is returned.
     *
     * @param dataSet DataSet to check for approval.
     * @param period Period to check for approval.
     * @param organisationUnit OrganisationUnit to check for approval.
     * @param attributeOptionCombo CategoryOptionCombo (if any) for approval.
     * @return the data approval status.
     */
    DataApprovalStatus getDataApprovalStatus( DataSet dataSet, Period period,
                                              OrganisationUnit organisationUnit,
                                              DataElementCategoryOptionCombo attributeOptionCombo )
            throws ApprovalException;

    /**
     * Returns the data approval permissions and status for a given data set,
     * period, organisation unit, category option group and/or and attribute
     * category combination. If attributeOptionCombo is null, the default
     * option combo will be used.
     *
     * @param dataSets Data set(s) to check for approval.
     * @param period Period to check for approval.
     * @param organisationUnit OrganisationUnit to check for approval.
     * @param categoryOptionGroups CategoryOptionGroups (if any) for approval.
     * @param dataElementCategoryOptions Selected category options (if any).
     * @param dataApprovalLevel Approval level to test (if specified).
     * @return the data approval permissions (including status.)
     */
    DataApprovalStatusAndPermissions getDataApprovalPermissions( Set<DataSet> dataSets, Period period,
                                                        OrganisationUnit organisationUnit,
                                                        Set<CategoryOptionGroup> categoryOptionGroups,
                                                        Set<DataElementCategoryOption> dataElementCategoryOptions,
                                                        DataApprovalLevel dataApprovalLevel )
            throws ApprovalException;
}
