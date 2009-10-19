package org.hisp.dhis.reportexcel.dataentrystatus.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.reportexcel.ReportExcelService;
import org.hisp.dhis.reportexcel.status.DataEntryStatus;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserStore;

import com.opensymphony.xwork2.Action;

/*
 * Copyright (c) 2004-2007, University of Oslo
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
/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class ListDataEntryStatusAction
    implements Action
{
    // -------------------------------------------------
    // Dependency
    // -------------------------------------------------

    private ReportExcelService reportService;

    private CurrentUserService currentUserService;

    private UserStore userStore;

    private DataSetService dataSetService;

    // -------------------------------------------------
    // Output
    // -------------------------------------------------

    private List<DataEntryStatus> dataStatus;

    // -------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }

    public List<DataEntryStatus> getDataStatus()
    {
        return dataStatus;
    }

    public void setReportService( ReportExcelService reportService )
    {
        this.reportService = reportService;
    }

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    public String execute()
        throws Exception
    {
        List<DataSet> dataSets = new ArrayList<DataSet>( dataSetService.getAllDataSets() );

        if ( !currentUserService.currentUserIsSuper() )
        {
            UserCredentials userCredentials = userStore.getUserCredentials( currentUserService.getCurrentUser() );

            Set<DataSet> dataSetUserAuthorityGroups = new HashSet<DataSet>();

            for ( UserAuthorityGroup userAuthorityGroup : userCredentials.getUserAuthorityGroups() )
            {
                dataSetUserAuthorityGroups.addAll( userAuthorityGroup.getDataSets() );
            }

            dataSets.retainAll( dataSetUserAuthorityGroups );
        }       

        dataStatus = new ArrayList<DataEntryStatus>( reportService.getDataEntryStatusDefaultByDataSets( dataSets ) );

        return SUCCESS;
    }
}
