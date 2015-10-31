package org.hisp.dhis.settings.action.system;

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

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.dataapproval.DataApprovalLevel;
import org.hisp.dhis.dataapproval.DataApprovalLevelService;
import org.hisp.dhis.dataapproval.DataApprovalWorkflow;
import org.hisp.dhis.dataapproval.DataApprovalWorkflowService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides form data for adding or editing a DataApprovalWorkflow
 *
 * @author Jim Grace
 */

public class GetEditApprovalWorkflowParameters
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataApprovalWorkflowService workflowService;

    public void setWorkflowService( DataApprovalWorkflowService workflowService )
    {
        this.workflowService = workflowService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataApprovalLevelService dataApprovalLevelService;

    public void setDataApprovalLevelService( DataApprovalLevelService dataApprovalLevelService )
    {
        this.dataApprovalLevelService = dataApprovalLevelService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer workflowId;

    public void setDataApprovalWorkflowId( Integer workflowId )
    {
        this.workflowId = workflowId;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<PeriodType> periodTypes = new ArrayList<>();

    public List<PeriodType> getPeriodTypes()
    {
        return periodTypes;
    }

    private List<DataApprovalLevel> dataApprovalLevels = new ArrayList<>();

    public List<DataApprovalLevel> getDataApprovalLevels()
    {
        return dataApprovalLevels;
    }

    private DataApprovalWorkflow workflow;

    public DataApprovalWorkflow getWorkflow()
    {
        return workflow;
    }

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        periodTypes = periodService.getAllPeriodTypes();

        dataApprovalLevels = dataApprovalLevelService.getAllDataApprovalLevels();

        if ( workflowId != null )
        {
            workflow = workflowService.getWorkflow( workflowId );
        }

        return SUCCESS;
    }
}
