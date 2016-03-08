package org.hisp.dhis.datastatistics;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * @author  Julie Hill Roa
 * @author Yrjan Fraschetti
 *
 * Aggregated DataStatistics object
 */
public class AggregatedStatistics
{

    Date startInterval;
    Date endInterval;
    Integer activeUsers;
    Double mapViews;
    Double chartViews;
    Double reportTablesViews;
    Double eventReportViews;
    Double eventChartViews;
    Double dashboardViews;
    Double indicatorsViews;
    Double totalViews;
    Double averageViews;
    Double savedMaps;
    Double savedCharts;
    Double savedReportTables;
    Double savedEventReports;
    Double savedEventCharts;
    Double savedDashboards;
    Double savedIndicators;
    Integer users;

    public AggregatedStatistics( Date startInterval, Date endInterval, Integer activeUsers, Double mapViews, Double chartViews,
        Double reportTablesViews, Double eventReportViews, Double eventChartViews, Double dashboardViews, Double indicatorsViews,
        Double totalViews, Double averageViews, Double savedMaps, Double savedCharts, Double savedReportTables, Double savedEventReports,
        Double savedEventCharts, Double savedDashboards, Double savedIndicators, Integer users )
    {
        this.startInterval = startInterval;
        this.endInterval = endInterval;
        this.activeUsers = activeUsers;
        this.mapViews = mapViews;
        this.chartViews = chartViews;
        this.reportTablesViews = reportTablesViews;
        this.eventReportViews = eventReportViews;
        this.eventChartViews = eventChartViews;
        this.dashboardViews = dashboardViews;
        this.indicatorsViews = indicatorsViews;
        this.totalViews = totalViews;
        this.averageViews = averageViews;
        this.savedMaps = savedMaps;
        this.savedCharts = savedCharts;
        this.savedReportTables = savedReportTables;
        this.savedEventReports = savedEventReports;
        this.savedEventCharts = savedEventCharts;
        this.savedDashboards = savedDashboards;
        this.savedIndicators = savedIndicators;
        this.users = users;
    }

    public AggregatedStatistics( Integer activeUsers, Double mapViews, Double chartViews, Double reportTablesViews,
        Double eventReportViews, Double eventChartViews, Double dashboardViews, Double indicatorsViews, Double totalViews,
        Double averageViews, Double savedMaps, Double savedCharts, Double savedReportTables, Double savedEventReports,
        Double savedEventCharts, Double savedDashboards, Double savedIndicators, Integer users )
    {
        this.activeUsers = activeUsers;
        this.mapViews = mapViews;
        this.chartViews = chartViews;
        this.reportTablesViews = reportTablesViews;
        this.eventReportViews = eventReportViews;
        this.eventChartViews = eventChartViews;
        this.dashboardViews = dashboardViews;
        this.indicatorsViews = indicatorsViews;
        this.totalViews = totalViews;
        this.averageViews = averageViews;
        this.savedMaps = savedMaps;
        this.savedCharts = savedCharts;
        this.savedReportTables = savedReportTables;
        this.savedEventReports = savedEventReports;
        this.savedEventCharts = savedEventCharts;
        this.savedDashboards = savedDashboards;
        this.savedIndicators = savedIndicators;
        this.users = users;
    }

    @JsonProperty
    public Date getStartInterval( )
    {
        return startInterval;
    }

    public void setStartInterval( Date startInterval )
    {
        this.startInterval = startInterval;
    }

    @JsonProperty
    public Date getEndInterval( )
    {
        return endInterval;
    }

    public void setEndInterval( Date endInterval )
    {
        this.endInterval = endInterval;
    }

    @JsonProperty
    public Integer getActiveUsers( )
    {
        return activeUsers;
    }

    public void setActiveUsers( Integer activeUsers )
    {
        this.activeUsers = activeUsers;
    }

    @JsonProperty
    public Double getMapViews( )
    {
        return mapViews;
    }

    public void setMapViews( Double mapViews )
    {
        this.mapViews = mapViews;
    }

    @JsonProperty
    public Double getChartViews( )
    {
        return chartViews;
    }

    public void setChartViews( Double chartViews )
    {
        this.chartViews = chartViews;
    }

    @JsonProperty
    public Double getReportTablesViews( )
    {
        return reportTablesViews;
    }

    public void setReportTablesViews( Double reportTablesViews )
    {
        this.reportTablesViews = reportTablesViews;
    }

    @JsonProperty
    public Double getEventReportViews( )
    {
        return eventReportViews;
    }

    public void setEventReportViews( Double eventReportViews )
    {
        this.eventReportViews = eventReportViews;
    }

    @JsonProperty
    public Double getEventChartViews( )
    {
        return eventChartViews;
    }

    public void setEventChartViews( Double eventChartViews )
    {
        this.eventChartViews = eventChartViews;
    }

    @JsonProperty
    public Double getDashboardViews( )
    {
        return dashboardViews;
    }

    public void setDashboardViews( Double dashboardViews )
    {
        this.dashboardViews = dashboardViews;
    }

    @JsonProperty
    public Double getIndicatorsViews( )
    {
        return indicatorsViews;
    }

    public void setIndicatorsViews( Double indicatorsViews )
    {
        this.indicatorsViews = indicatorsViews;
    }

    @JsonProperty
    public Double getTotalViews( )
    {
        return totalViews;
    }

    public void setTotalViews( Double totalViews )
    {
        this.totalViews = totalViews;
    }

    @JsonProperty
    public Double getAverageViews( )
    {
        return averageViews;
    }

    public void setAverageViews( Double averageViews )
    {
        this.averageViews = averageViews;
    }

    @JsonProperty
    public Double getSavedMaps( )
    {
        return savedMaps;
    }

    public void setSavedMaps( Double savedMaps )
    {
        this.savedMaps = savedMaps;
    }

    @JsonProperty
    public Double getSavedCharts( )
    {
        return savedCharts;
    }

    public void setSavedCharts( Double savedCharts )
    {
        this.savedCharts = savedCharts;
    }

    @JsonProperty
    public Double getSavedReportTables( )
    {
        return savedReportTables;
    }

    public void setSavedReportTables( Double savedReportTables )
    {
        this.savedReportTables = savedReportTables;
    }

    @JsonProperty
    public Double getSavedEventReports( )
    {
        return savedEventReports;
    }

    public void setSavedEventReports( Double savedEventReports )
    {
        this.savedEventReports = savedEventReports;
    }

    @JsonProperty
    public Double getSavedEventCharts( )
    {
        return savedEventCharts;
    }

    public void setSavedEventCharts( Double savedEventCharts )
    {
        this.savedEventCharts = savedEventCharts;
    }

    @JsonProperty
    public Double getSavedDashboards( )
    {
        return savedDashboards;
    }

    public void setSavedDashboards( Double savedDashboards )
    {
        this.savedDashboards = savedDashboards;
    }

    @JsonProperty
    public Double getSavedIndicators( )
    {
        return savedIndicators;
    }

    public void setSavedIndicators( Double savedIndicators )
    {
        this.savedIndicators = savedIndicators;
    }

    @JsonProperty
    public Integer getusers( )
    {
        return users;
    }

    public void setusers( Integer users )
    {
        this.users = users;
    }

    @Override
    public String toString( )
    {
        return "AggregatedStatistics{" +
            "startInterval=" + startInterval +
            ", endInterval=" + endInterval +
            ", activeUsers=" + activeUsers +
            ", mapViews=" + mapViews +
            ", chartViews=" + chartViews +
            ", reportTablesViews=" + reportTablesViews +
            ", eventReportViews=" + eventReportViews +
            ", eventChartViews=" + eventChartViews +
            ", dashboardViews=" + dashboardViews +
            ", indicatorsViews=" + indicatorsViews +
            ", totalViews=" + totalViews +
            ", averageViews=" + averageViews +
            ", savedMaps=" + savedMaps +
            ", savedCharts=" + savedCharts +
            ", savedReportTables=" + savedReportTables +
            ", savedEventReports=" + savedEventReports +
            ", savedEventCharts=" + savedEventCharts +
            ", savedDashboards=" + savedDashboards +
            ", savedIndicators=" + savedIndicators +
            ", users=" + users +
            '}';
    }
}