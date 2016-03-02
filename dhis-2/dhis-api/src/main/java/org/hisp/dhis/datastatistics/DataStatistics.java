package org.hisp.dhis.datastatistics;
/*
 * Copyright (c) 2004-2016, University of Oslo
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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hisp.dhis.common.BaseIdentifiableObject;

/**
 * @author Julie Hill Roa
 * @author Yrjan A. F. Fraschetti
 *
 * DataStatistics object to be saved as snapshot
 */
public class DataStatistics extends BaseIdentifiableObject
{
    int numberOfActiveUsers;
    double numberOfMapViews;
    double numberOfChartViews;
    double numberOfReportTablesViews;
    double numberOfEventReportViews;
    double numberOfEventChartViews;
    double numberOfDashboardViews;
    double numberOfIndicatorsViews;
    double totalNumberOfViews;
    double averageNumberOfViews;
    double numberOfSavedMaps;
    double numberOfSavedCharts;
    double numberOfSavedReportTables;
    double numberOfSavedEventReports;
    double numberOfSavedEventCharts;
    double numberOfSavedDashboards;
    double numberOfSavedIndicators;

    int totalNumberOfUsers;

    public DataStatistics( )
    {
        super.setAutoFields();
    }

    public DataStatistics( int numberOfActiveUsers, double numberOfMapViews, double numberOfChartViews, double numberOfReportTablesViews, double numberOfEventReportViews, double numberOfEventChartViews, double numberOfDashboardViews, double numberOfIndicatorsViews, double totalNumberOfViews, double numberOfViews, double numberOfSavedMaps, double numberOfSavedCharts, double numberOfSavedReportTables, double numberOfSavedEventReports, double numberOfSavedEventCharts, double numberOfSavedDashboards, double numberOfSavedIndicators, int totalNumberOfUsers )
    {
        this.numberOfActiveUsers = numberOfActiveUsers;
        this.numberOfMapViews = numberOfMapViews;
        this.numberOfChartViews = numberOfChartViews;
        this.numberOfReportTablesViews = numberOfReportTablesViews;
        this.numberOfEventReportViews = numberOfEventReportViews;
        this.numberOfEventChartViews = numberOfEventChartViews;
        this.numberOfDashboardViews = numberOfDashboardViews;
        this.numberOfIndicatorsViews = numberOfIndicatorsViews;
        this.totalNumberOfViews = totalNumberOfViews;
        this.averageNumberOfViews = numberOfViews;
        this.numberOfSavedMaps = numberOfSavedMaps;
        this.numberOfSavedCharts = numberOfSavedCharts;
        this.numberOfSavedReportTables = numberOfSavedReportTables;
        this.numberOfSavedEventReports = numberOfSavedEventReports;
        this.numberOfSavedEventCharts = numberOfSavedEventCharts;
        this.numberOfSavedDashboards = numberOfSavedDashboards;
        this.numberOfSavedIndicators = numberOfSavedIndicators;
        this.totalNumberOfUsers = totalNumberOfUsers;

        super.setAutoFields();
    }

    @JsonProperty
    public int getNumberOfActiveUsers()
    {
        return numberOfActiveUsers;
    }

    public void setNumberOfActiveUsers( int numberOfActiveUsers )
    {
        this.numberOfActiveUsers = numberOfActiveUsers;
    }

    @JsonProperty
    public double getNumberOfMapViews()
    {
        return numberOfMapViews;
    }

    public void setNumberOfMapViews( double numberOfMapViews )
    {
        this.numberOfMapViews = numberOfMapViews;
    }

    @JsonProperty
    public double getNumberOfChartViews()
    {
        return numberOfChartViews;
    }

    public void setNumberOfChartViews( double numberOfChartViews )
    {
        this.numberOfChartViews = numberOfChartViews;
    }

    @JsonProperty
    public double getNumberOfReportTablesViews()
    {
        return numberOfReportTablesViews;
    }

    public void setNumberOfReportTablesViews( double numberOfReportTablesViews )
    {
        this.numberOfReportTablesViews = numberOfReportTablesViews;
    }

    @JsonProperty
    public double getNumberOfEventReportViews()
    {
        return numberOfEventReportViews;
    }

    public void setNumberOfEventReportViews( double numberOfEventReportViews )
    {
        this.numberOfEventReportViews = numberOfEventReportViews;
    }

    @JsonProperty
    public double getNumberOfEventChartViews()
    {
        return numberOfEventChartViews;
    }

    public void setNumberOfEventChartViews( double numberOfEventChartViews )
    {
        this.numberOfEventChartViews = numberOfEventChartViews;
    }

    @JsonProperty
    public double getNumberOfDashboardViews()
    {
        return numberOfDashboardViews;
    }

    public void setNumberOfDashboardViews( double numberOfDashboardViews )
    {
        this.numberOfDashboardViews = numberOfDashboardViews;
    }

    @JsonProperty
    public double getNumberOfIndicatorsViews()
    {
        return numberOfIndicatorsViews;
    }

    public void setNumberOfIndicatorsViews( double numberOfIndicatorsViews )
    {
        this.numberOfIndicatorsViews = numberOfIndicatorsViews;
    }

    @JsonProperty
    public double getTotalNumberOfViews()
    {
        return totalNumberOfViews;
    }

    public void setTotalNumberOfViews( double totalNumberOfViews )
    {
        this.totalNumberOfViews = totalNumberOfViews;
    }

    @JsonProperty
    public double getAverageNumberOfViews()
    {
        return averageNumberOfViews;
    }

    public void setAverageNumberOfViews( double numberOfViews )
    {
        this.averageNumberOfViews = numberOfViews;
    }

    @JsonProperty
    public double getNumberOfSavedMaps()
    {
        return numberOfSavedMaps;
    }

    public void setNumberOfSavedMaps( double numberOfSavedMaps )
    {
        this.numberOfSavedMaps = numberOfSavedMaps;
    }

    @JsonProperty
    public double getNumberOfSavedCharts()
    {
        return numberOfSavedCharts;
    }

    public void setNumberOfSavedCharts( double numberOfSavedCharts )
    {
        this.numberOfSavedCharts = numberOfSavedCharts;
    }

    @JsonProperty
    public double getNumberOfSavedReportTables()
    {
        return numberOfSavedReportTables;
    }

    public void setNumberOfSavedReportTables( double numberOfSavedReportTables )
    {
        this.numberOfSavedReportTables = numberOfSavedReportTables;
    }

    @JsonProperty
    public double getNumberOfSavedEventReports()
    {
        return numberOfSavedEventReports;
    }

    public void setNumberOfSavedEventReports( double numberOfSavedEventReports )
    {
        this.numberOfSavedEventReports = numberOfSavedEventReports;
    }

    @JsonProperty
    public double getNumberOfSavedEventCharts()
    {
        return numberOfSavedEventCharts;
    }

    public void setNumberOfSavedEventCharts( double numberOfSavedEventCharts )
    {
        this.numberOfSavedEventCharts = numberOfSavedEventCharts;
    }

    @JsonProperty
    public double getNumberOfSavedDashboards()
    {
        return numberOfSavedDashboards;
    }

    public void setNumberOfSavedDashboards( double numberOfSavedDashboards )
    {
        this.numberOfSavedDashboards = numberOfSavedDashboards;
    }

    @JsonProperty
    public double getNumberOfSavedIndicators()
    {
        return numberOfSavedIndicators;
    }

    public void setNumberOfSavedIndicators( double numberOfSavedIndicators )
    {
        this.numberOfSavedIndicators = numberOfSavedIndicators;
    }

    @JsonProperty
    public int getTotalNumberOfUsers()
    {
        return totalNumberOfUsers;
    }

    public void setTotalNumberOfUsers( int totalNumberOfUsers )
    {
        this.totalNumberOfUsers = totalNumberOfUsers;
    }

    @Override public String toString()
    {
        return super.toString() + " DataStatistics{" +
            "numberOfActiveUsers=" + numberOfActiveUsers +
            ", numberOfMapViews=" + numberOfMapViews +
            ", numberOfChartViews=" + numberOfChartViews +
            ", numberOfReportTablesViews=" + numberOfReportTablesViews +
            ", numberOfEventReportViews=" + numberOfEventReportViews +
            ", numberOfEventChartViews=" + numberOfEventChartViews +
            ", numberOfDashboardViews=" + numberOfDashboardViews +
            ", numberOfIndicatorsViews=" + numberOfIndicatorsViews +
            ", totalNumberOfViews=" + totalNumberOfViews +
            ", averageNumberOfViews=" + averageNumberOfViews +
            ", numberOfSavedMaps=" + numberOfSavedMaps +
            ", numberOfSavedCharts=" + numberOfSavedCharts +
            ", numberOfSavedReportTables=" + numberOfSavedReportTables +
            ", numberOfSavedEventReports=" + numberOfSavedEventReports +
            ", numberOfSavedEventCharts=" + numberOfSavedEventCharts +
            ", numberOfSavedDashboards=" + numberOfSavedDashboards +
            ", numberOfSavedIndicators=" + numberOfSavedIndicators +
            ", totalNumberOfUsers=" + totalNumberOfUsers +
            '}';
    }
}
