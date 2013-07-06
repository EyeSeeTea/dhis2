package org.hisp.dhis.commons.action;

import com.opensymphony.xwork2.Action;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.util.ContextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetChartsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ChartService chartService;

    public void setChartService( ChartService chartService )
    {
        this.chartService = chartService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<Chart> charts;

    public List<Chart> getCharts()
    {
        return charts;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute() throws Exception
    {
        charts = new ArrayList<Chart>( chartService.getAllCharts() );

        ContextUtils.clearIfNotModified(ServletActionContext.getRequest(), ServletActionContext.getResponse(), charts);

        Collections.sort(charts, IdentifiableObjectNameComparator.INSTANCE);

        return SUCCESS;
    }
}
