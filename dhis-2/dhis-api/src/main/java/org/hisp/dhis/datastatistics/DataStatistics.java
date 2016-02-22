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

import java.util.Date;

/**
 * @author Julie Hill Roa
 * @author Yrjan A. F. Fraschetti
 *
 * DataStatistics object to be saved as snapshot
 */
public class DataStatistics extends BaseIdentifiableObject
{
    int numberOfUsers;
    int numberOfMapViews;
    int numberOfChartViews;
    int numberOfReportTablesViews;
    int numberOfEventReportViews;
    int numberOfEventChartViews;
    int numberOfDashboardViews;
    int numberOfIndicatorsViews;
    int totalNumberOfViews;
    int averageNumberOfViews;

    int averageNumberOfSavedMaps;
    int averageNumberOfSavedCharts;
    int averageNumberOfSavedReportTables;
    int averageNumberOfSavedEventReports;
    int averageNumberOfSavedEventCharts;
    int averageNumberOfSavedDashboards;
    int averageNumberOfSavedIndicators;

    int totalNumberOfUsers;


    public DataStatistics( ) { super.setAutoFields(); }

    public DataStatistics( int numberOfUsers, int numberOfMapViews, int numberOfChartViews,
        int numberOfReportTablesViews, int numberOfEventReportViews, int numberOfEventChartViews,
        int numberOfDashboardViews, int numberOfIndicatorsViews, int totalNumberOfViews,
        int averageNumberOfViews, int averageNumberOfSavedMaps, int averageNumberOfSavedCharts,
        int averageNumberOfSavedReportTables, int averageNumberOfSavedEventReports,
        int averageNumberOfSavedEventCharts, int averageNumberOfSavedDashboards,
        int averageNumberOfSavedIndicators, int totalNumberOfUsers )
    {
        this.numberOfUsers = numberOfUsers;
        this.numberOfMapViews = numberOfMapViews;
        this.numberOfChartViews = numberOfChartViews;
        this.numberOfReportTablesViews = numberOfReportTablesViews;
        this.numberOfEventReportViews = numberOfEventReportViews;
        this.numberOfEventChartViews = numberOfEventChartViews;
        this.numberOfDashboardViews = numberOfDashboardViews;
        this.numberOfIndicatorsViews = numberOfIndicatorsViews;
        this.totalNumberOfViews = totalNumberOfViews;
        this.averageNumberOfViews = averageNumberOfViews;
        this.averageNumberOfSavedMaps = averageNumberOfSavedMaps;
        this.averageNumberOfSavedCharts = averageNumberOfSavedCharts;
        this.averageNumberOfSavedReportTables = averageNumberOfSavedReportTables;
        this.averageNumberOfSavedEventReports = averageNumberOfSavedEventReports;
        this.averageNumberOfSavedEventCharts = averageNumberOfSavedEventCharts;
        this.averageNumberOfSavedDashboards = averageNumberOfSavedDashboards;
        this.averageNumberOfSavedIndicators = averageNumberOfSavedIndicators;
        this.totalNumberOfUsers = totalNumberOfUsers;

        super.setAutoFields();
    }

    @JsonProperty
    public int getNumberOfUsers()
    {
        return numberOfUsers;
    }

    public void setNumberOfUsers( int numberOfUsers )
    {
        this.numberOfUsers = numberOfUsers;
    }

    @JsonProperty
    public int getNumberOfMapViews()
    {
        return numberOfMapViews;
    }

    public void setNumberOfMapViews( int numberOfMapViews )
    {
        this.numberOfMapViews = numberOfMapViews;
    }

    @JsonProperty
    public int getNumberOfChartViews()
    {
        return numberOfChartViews;
    }

    public void setNumberOfChartViews( int numberOfChartViews )
    {
        this.numberOfChartViews = numberOfChartViews;
    }

    @JsonProperty
    public int getNumberOfReportTablesViews()
    {
        return numberOfReportTablesViews;
    }

    public void setNumberOfReportTablesViews( int numberOfReportTablesViews )
    {
        this.numberOfReportTablesViews = numberOfReportTablesViews;
    }

    @JsonProperty
    public int getNumberOfEventReportViews()
    {
        return numberOfEventReportViews;
    }

    public void setNumberOfEventReportViews( int numberOfEventReportViews )
    {
        this.numberOfEventReportViews = numberOfEventReportViews;
    }

    @JsonProperty
    public int getNumberOfEventChartViews()
    {
        return numberOfEventChartViews;
    }

    public void setNumberOfEventChartViews( int numberOfEventChartViews )
    {
        this.numberOfEventChartViews = numberOfEventChartViews;
    }

    @JsonProperty
    public int getNumberOfDashboardViews()
    {
        return numberOfDashboardViews;
    }

    public void setNumberOfDashboardViews( int numberOfDashboardViews )
    {
        this.numberOfDashboardViews = numberOfDashboardViews;
    }

    @JsonProperty
    public int getNumberOfIndicatorsViews()
    {
        return numberOfIndicatorsViews;
    }

    public void setNumberOfIndicatorsViews( int numberOfIndicatorsViews )
    {
        this.numberOfIndicatorsViews = numberOfIndicatorsViews;
    }

    @JsonProperty
    public int getTotalNumberOfViews()
    {
        return totalNumberOfViews;
    }

    public void setTotalNumberOfViews( int totalNumberOfViews )
    {
        this.totalNumberOfViews = totalNumberOfViews;
    }

    @JsonProperty
    public int getAverageNumberOfViews()
    {
        return averageNumberOfViews;
    }

    public void setAverageNumberOfViews( int averageNumberofViews )
    {
        this.averageNumberOfViews = averageNumberofViews;
    }

    @JsonProperty
    public int getAverageNumberOfSavedMaps()
    {
        return averageNumberOfSavedMaps;
    }

    public void setAverageNumberOfSavedMaps( int averageNumberOfSavedMaps )
    {
        this.averageNumberOfSavedMaps = averageNumberOfSavedMaps;
    }

    @JsonProperty
    public int getAverageNumberOfSavedCharts()
    {
        return averageNumberOfSavedCharts;
    }

    public void setAverageNumberOfSavedCharts( int averageNumberOfSavedCharts )
    {
        this.averageNumberOfSavedCharts = averageNumberOfSavedCharts;
    }

    @JsonProperty
    public int getAverageNumberOfSavedReportTables()
    {
        return averageNumberOfSavedReportTables;
    }

    public void setAverageNumberOfSavedReportTables( int averageNumberOfSavedReportTables )
    {
        this.averageNumberOfSavedReportTables = averageNumberOfSavedReportTables;
    }

    @JsonProperty
    public int getAverageNumberOfSavedEventReports()
    {
        return averageNumberOfSavedEventReports;
    }

    public void setAverageNumberOfSavedEventReports( int averageNumberOfSavedEventReports )
    {
        this.averageNumberOfSavedEventReports = averageNumberOfSavedEventReports;
    }

    @JsonProperty
    public int getAverageNumberOfSavedEventCharts()
    {
        return averageNumberOfSavedEventCharts;
    }

    public void setAverageNumberOfSavedEventCharts( int averageNumberOfSavedEventCharts )
    {
        this.averageNumberOfSavedEventCharts = averageNumberOfSavedEventCharts;
    }

    @JsonProperty
    public int getAverageNumberOfSavedDashboards()
    {
        return averageNumberOfSavedDashboards;
    }

    public void setAverageNumberOfSavedDashboards( int averageNumberOfSavedDashboards )
    {
        this.averageNumberOfSavedDashboards = averageNumberOfSavedDashboards;
    }

    @JsonProperty
    public int getAverageNumberOfSavedIndicators()
    {
        return averageNumberOfSavedIndicators;
    }

    public void setAverageNumberOfSavedIndicators( int averageNumberOfSavedIndicators )
    {
        this.averageNumberOfSavedIndicators = averageNumberOfSavedIndicators;
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
        return super.toString() +
            " DataStatistics{" +
            "numberOfUsers=" + numberOfUsers +
            ", numberOfMapViews=" + numberOfMapViews +
            ", numberOfChartViews=" + numberOfChartViews +
            ", numberOfReportTablesViews=" + numberOfReportTablesViews +
            ", numberOfEventReportViews=" + numberOfEventReportViews +
            ", numberOfEventChartViews=" + numberOfEventChartViews +
            ", numberOfDashboardViews=" + numberOfDashboardViews +
            ", numberOfIndicatorsViews=" + numberOfIndicatorsViews +
            ", totalNumberOfViews=" + totalNumberOfViews +
            ", averageNumberOfViews=" + averageNumberOfViews +
            ", averageNumberOfSavedMaps=" + averageNumberOfSavedMaps +
            ", averageNumberOfSavedCharts=" + averageNumberOfSavedCharts +
            ", averageNumberOfSavedReportTables=" + averageNumberOfSavedReportTables +
            ", averageNumberOfSavedEventReports=" + averageNumberOfSavedEventReports +
            ", averageNumberOfSavedEventCharts=" + averageNumberOfSavedEventCharts +
            ", averageNumberOfSavedDashboards=" + averageNumberOfSavedDashboards +
            ", averageNumberOfSavedIndicators=" + averageNumberOfSavedIndicators +
            ", totalNumberOfUsers=" + totalNumberOfUsers +
            '}';
    }
}
