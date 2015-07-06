package org.hisp.dhis.dataapproval;

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

import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.hierarchy.HierarchyViolationException;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.user.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jim Grace
 */
public interface DataApprovalWorkflowService
{
    String ID = DataApprovalWorkflowService.class.getName();

    /**
     * Adds a DataApprovalWorkflow.
     *
     * @param dataApprovalWorkflow the DataApprovalWorkflow to add.
     * @return a generated unique id of the added Workflow.
     */
    int addDataApprovalWorkflow( DataApprovalWorkflow dataApprovalWorkflow );

    /**
     * Updates a DataApprovalWorkflow.
     *
     * @param dataApprovalWorkflow the DataApprovalWorkflow to update.
     */
    void updateDataApprovalWorkflow( DataApprovalWorkflow dataApprovalWorkflow );

    /**
     * Deletes a DataApprovalWorkflow.
     *
     * @param dataApprovalWorkflow the DataApprovalWorkflow to delete.
     */
    void deleteDataApprovalWorkflow( DataApprovalWorkflow dataApprovalWorkflow );

    /**
     * Returns a DataApprovalWorkflow.
     *
     * @param id the id of the DataApprovalWorkflow to return.
     * @return the DataApprovalWorkflow with the given id, or null if no match.
     */
    DataApprovalWorkflow getDataApprovalWorkflow( int id );

    /**
     * Returns all DataApprovalWorkflows.
     *
     * @return a list of all DataApprovalWorkflows.
     */
    List<DataApprovalWorkflow> getAllDataApprovalWorkflows();
}
