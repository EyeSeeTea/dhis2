package org.hisp.dhis.datastatistics;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by JulieHillRoa on 08.03.2016.
 */
public class AggregatedStatistics {

    Date startInterval;
    Date endInterval;

    int maxNumberOfActiveUsers;
    double aggregatedMapViews;
    double aggregatedChartViews;
    double aggregatedReportTablesViews;
    double aggregatedEventReportViews;
    double aggregatedEventChartViews;
    double aggregatedDashboardViews;
    double aggregatedIndicatorsViews;
    double maxTotalNumberOfViews;
    double averageNumberOfViews;
    double aggregatedSavedMaps;
    double aggregatedSavedCharts;
    double aggregatedSavedReportTables;
    double aggregatedSavedEventReports;
    double aggregatedSavedEventCharts;
    double aggregatedSavedDashboards;
    double aggregatedSavedIndicators;

    int maxTotalNumberOfUsers;

    public AggregatedStatistics(Date startInterval, Date endInterval, int maxNumberOfActiveUsers, double aggregatedMapViews, double aggregatedChartViews, double aggregatedReportTablesViews, double aggregatedEventReportViews, double aggregatedEventChartViews, double aggregatedDashboardViews, double aggregatedIndicatorsViews, double maxTotalNumberOfViews, double averageNumberOfViews, double aggregatedSavedMaps, double aggregatedSavedCharts, double aggregatedSavedReportTables, double aggregatedSavedEventReports, double aggregatedSavedEventCharts, double aggregatedSavedDashboards, double aggregatedSavedIndicators, int maxTotalNumberOfUsers) {
        this.startInterval = startInterval;
        this.endInterval = endInterval;
        this.maxNumberOfActiveUsers = maxNumberOfActiveUsers;
        this.aggregatedMapViews = aggregatedMapViews;
        this.aggregatedChartViews = aggregatedChartViews;
        this.aggregatedReportTablesViews = aggregatedReportTablesViews;
        this.aggregatedEventReportViews = aggregatedEventReportViews;
        this.aggregatedEventChartViews = aggregatedEventChartViews;
        this.aggregatedDashboardViews = aggregatedDashboardViews;
        this.aggregatedIndicatorsViews = aggregatedIndicatorsViews;
        this.maxTotalNumberOfViews = maxTotalNumberOfViews;
        this.averageNumberOfViews = averageNumberOfViews;
        this.aggregatedSavedMaps = aggregatedSavedMaps;
        this.aggregatedSavedCharts = aggregatedSavedCharts;
        this.aggregatedSavedReportTables = aggregatedSavedReportTables;
        this.aggregatedSavedEventReports = aggregatedSavedEventReports;
        this.aggregatedSavedEventCharts = aggregatedSavedEventCharts;
        this.aggregatedSavedDashboards = aggregatedSavedDashboards;
        this.aggregatedSavedIndicators = aggregatedSavedIndicators;
        this.maxTotalNumberOfUsers = maxTotalNumberOfUsers;
    }

    public AggregatedStatistics(){}

    public AggregatedStatistics(int maxNumberOfActiveUsers, double aggregatedMapViews, double aggregatedChartViews, double aggregatedReportTablesViews, double aggregatedEventReportViews, double aggregatedEventChartViews, double aggregatedDashboardViews, double aggregatedIndicatorsViews, double maxTotalNumberOfViews, double averageNumberOfViews, double aggregatedSavedMaps, double aggregatedSavedCharts, double aggregatedSavedReportTables, double aggregatedSavedEventReports, double aggregatedSavedEventCharts, double aggregatedSavedDashboards, double aggregatedSavedIndicators, int maxTotalNumberOfUsers) {
        this.maxNumberOfActiveUsers = maxNumberOfActiveUsers;
        this.aggregatedMapViews = aggregatedMapViews;
        this.aggregatedChartViews = aggregatedChartViews;
        this.aggregatedReportTablesViews = aggregatedReportTablesViews;
        this.aggregatedEventReportViews = aggregatedEventReportViews;
        this.aggregatedEventChartViews = aggregatedEventChartViews;
        this.aggregatedDashboardViews = aggregatedDashboardViews;
        this.aggregatedIndicatorsViews = aggregatedIndicatorsViews;
        this.maxTotalNumberOfViews = maxTotalNumberOfViews;
        this.averageNumberOfViews = averageNumberOfViews;
        this.aggregatedSavedMaps = aggregatedSavedMaps;
        this.aggregatedSavedCharts = aggregatedSavedCharts;
        this.aggregatedSavedReportTables = aggregatedSavedReportTables;
        this.aggregatedSavedEventReports = aggregatedSavedEventReports;
        this.aggregatedSavedEventCharts = aggregatedSavedEventCharts;
        this.aggregatedSavedDashboards = aggregatedSavedDashboards;
        this.aggregatedSavedIndicators = aggregatedSavedIndicators;
        this.maxTotalNumberOfUsers = maxTotalNumberOfUsers;
    }

    @JsonProperty
    public Date getStartInterval() {
        return startInterval;
    }

    public void setStartInterval(Date startInterval) {
        this.startInterval = startInterval;
    }

    @JsonProperty
    public Date getEndInterval() {
        return endInterval;
    }

    @JsonProperty
    public void setEndInterval(Date endInterval) {
        this.endInterval = endInterval;
    }



    @JsonProperty
    public double getAggregatedMapViews() {
        return aggregatedMapViews;
    }

    public void setAggregatedMapViews(double aggregatedMapViews) {
        this.aggregatedMapViews = aggregatedMapViews;
    }

    @JsonProperty
    public double getAggregatedChartViews() {
        return aggregatedChartViews;
    }

    public void setAggregatedChartViews(double aggregatedChartViews) {
        this.aggregatedChartViews = aggregatedChartViews;
    }

    @JsonProperty
    public double getAggregatedReportTablesViews() {
        return aggregatedReportTablesViews;
    }

    public void setAggregatedReportTablesViews(double aggregatedReportTablesViews) {
        this.aggregatedReportTablesViews = aggregatedReportTablesViews;
    }

    @JsonProperty
    public double getAggregatedEventReportViews() {
        return aggregatedEventReportViews;
    }

    public void setAggregatedEventReportViews(double aggregatedEventReportViews) {
        this.aggregatedEventReportViews = aggregatedEventReportViews;
    }

    @JsonProperty
    public double getAggregatedEventChartViews() {
        return aggregatedEventChartViews;
    }

    public void setAggregatedEventChartViews(double aggregatedEventChartViews) {
        this.aggregatedEventChartViews = aggregatedEventChartViews;
    }

    @JsonProperty
    public double getAggregatedDashboardViews() {
        return aggregatedDashboardViews;
    }

    public void setAggregatedDashboardViews(double aggregatedDashboardViews) {
        this.aggregatedDashboardViews = aggregatedDashboardViews;
    }

    @JsonProperty
    public double getAggregatedIndicatorsViews() {
        return aggregatedIndicatorsViews;
    }

    public void setAggregatedIndicatorsViews(double aggregatedIndicatorsViews) {
        this.aggregatedIndicatorsViews = aggregatedIndicatorsViews;
    }

    @JsonProperty
    public int getMaxNumberOfActiveUsers() {
        return maxNumberOfActiveUsers;
    }

    public void setMaxNumberOfActiveUsers(int maxNumberOfActiveUsers) {
        this.maxNumberOfActiveUsers = maxNumberOfActiveUsers;
    }

    @JsonProperty
    public double getMaxTotalNumberOfViews() {
        return maxTotalNumberOfViews;
    }

    public void setMaxTotalNumberOfViews(double maxTotalNumberOfViews) {
        this.maxTotalNumberOfViews = maxTotalNumberOfViews;
    }

    @JsonProperty
    public double getAverageNumberOfViews() {
        return averageNumberOfViews;
    }

    public void setAverageNumberOfViews(double averageNumberOfViews) {
        this.averageNumberOfViews = averageNumberOfViews;
    }

    @JsonProperty
    public int getMaxTotalNumberOfUsers() {
        return maxTotalNumberOfUsers;
    }

    public void setMaxTotalNumberOfUsers(int maxTotalNumberOfUsers) {
        this.maxTotalNumberOfUsers = maxTotalNumberOfUsers;
    }

    @JsonProperty
    public double getAggregatedSavedMaps() {
        return aggregatedSavedMaps;
    }

    public void setAggregatedSavedMaps(double aggregatedSavedMaps) {
        this.aggregatedSavedMaps = aggregatedSavedMaps;
    }

    @JsonProperty
    public double getAggregatedSavedCharts() {
        return aggregatedSavedCharts;
    }

    public void setAggregatedSavedCharts(double aggregatedSavedCharts) {
        this.aggregatedSavedCharts = aggregatedSavedCharts;
    }

    @JsonProperty
    public double getAggregatedSavedReportTables() {
        return aggregatedSavedReportTables;
    }

    public void setAggregatedSavedReportTables(double aggregatedSavedReportTables) {
        this.aggregatedSavedReportTables = aggregatedSavedReportTables;
    }

    @JsonProperty
    public double getAggregatedSavedEventReports() {
        return aggregatedSavedEventReports;
    }

    public void setAggregatedSavedEventReports(double aggregatedSavedEventReports) {
        this.aggregatedSavedEventReports = aggregatedSavedEventReports;
    }

    @JsonProperty
    public double getAggregatedSavedEventCharts() {
        return aggregatedSavedEventCharts;
    }

    @JsonProperty
    public void setAggregatedSavedEventCharts(double aggregatedSavedEventCharts) {
        this.aggregatedSavedEventCharts = aggregatedSavedEventCharts;
    }

    @JsonProperty
    public double getAggregatedSavedDashboards() {
        return aggregatedSavedDashboards;
    }

    @JsonProperty
    public void setAggregatedSavedDashboards(double aggregatedSavedDashboards) {
        this.aggregatedSavedDashboards = aggregatedSavedDashboards;
    }

    public double getAggregatedSavedIndicators() {
        return aggregatedSavedIndicators;
    }

    @JsonProperty
    public void setAggregatedSavedIndicators(double aggregatedSavedIndicators) {
        this.aggregatedSavedIndicators = aggregatedSavedIndicators;
    }


    @Override
    public String toString() {
        return "AggregatedStatistics{" +
                "startInterval=" + startInterval +
                ", endInterval=" + endInterval +
                ", maxNumberOfActiveUsers=" + maxNumberOfActiveUsers +
                ", aggregatedMapViews=" + aggregatedMapViews +
                ", aggregatedChartViews=" + aggregatedChartViews +
                ", aggregatedReportTablesViews=" + aggregatedReportTablesViews +
                ", aggregatedEventReportViews=" + aggregatedEventReportViews +
                ", aggregatedEventChartViews=" + aggregatedEventChartViews +
                ", aggregatedDashboardViews=" + aggregatedDashboardViews +
                ", aggregatedIndicatorsViews=" + aggregatedIndicatorsViews +
                ", maxTotalNumberOfViews=" + maxTotalNumberOfViews +
                ", averageNumberOfViews=" + averageNumberOfViews +
                ", aggregatedSavedMaps=" + aggregatedSavedMaps +
                ", aggregatedSavedCharts=" + aggregatedSavedCharts +
                ", aggregatedSavedReportTables=" + aggregatedSavedReportTables +
                ", aggregatedSavedEventReports=" + aggregatedSavedEventReports +
                ", aggregatedSavedEventCharts=" + aggregatedSavedEventCharts +
                ", aggregatedSavedDashboards=" + aggregatedSavedDashboards +
                ", aggregatedSavedIndicators=" + aggregatedSavedIndicators +
                ", maxTotalNumberOfUsers=" + maxTotalNumberOfUsers +
                '}';
    }
}
