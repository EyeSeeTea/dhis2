package org.hisp.dhis.dashboard.action;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import static org.hisp.dhis.user.UserSettingService.DEFAULT_CHARTS_IN_DASHBOARD;
import static org.hisp.dhis.user.UserSettingService.KEY_CHARTS_IN_DASHBOARD;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dashboard.DashboardManager;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.user.UserSettingService;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ProvideContentAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DashboardManager manager;

    public void setManager( DashboardManager manager )
    {
        this.manager = manager;
    }

    private ChartService chartService;

    public void setChartService( ChartService chartService )
    {
        this.chartService = chartService;
    }

    private UserSettingService userSettingService;

    public void setUserSettingService( UserSettingService userSettingService )
    {
        this.userSettingService = userSettingService;
    }

    private MessageService messageService;

    public void setMessageService( MessageService messageService )
    {
        this.messageService = messageService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Collection<String> providerNames;

    public Collection<String> getProviderNames()
    {
        return providerNames;
    }

    private List<Chart> charts;

    public List<Chart> getCharts()
    {
        return charts;
    }

    private List<Object> chartAreas = new ArrayList<Object>();

    public List<Object> getChartAreas()
    {
        return chartAreas;
    }

    private long messageCount;

    public long getMessageCount()
    {
        return messageCount;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        Map<String, Object> content = manager.getContent();

        ActionContext.getContext().getActionInvocation().getStack().push( content );

        providerNames = manager.getContentProviderNames();

        charts = new ArrayList<Chart>( chartService.getSystemAndUserCharts() );

        Collections.sort( charts, IdentifiableObjectNameComparator.INSTANCE );

        int chartsInDashboardCount = (Integer) userSettingService.getUserSetting( KEY_CHARTS_IN_DASHBOARD, DEFAULT_CHARTS_IN_DASHBOARD );

        for ( int i = 1; i <= chartsInDashboardCount; i++ )
        {
            chartAreas.add( content.get( DashboardManager.CHART_AREA_PREFIX + i ) );
        }

        messageCount = messageService.getUnreadMessageConversationCount();

        return SUCCESS;
    }
}
