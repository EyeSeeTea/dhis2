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
 */
public class DataStatistics extends BaseIdentifiableObject
{
    private int favorites;
    private int reportTables;
    private int charts;
    private int maps;
    private int eventReports;
    private int eventCharts;

    public int getFavorites()
    {
        return favorites;
    }

    public void setFavorites( int favorites )
    {
        this.favorites = favorites;
    }

    public int getReportTables()
    {
        return reportTables;
    }

    public void setReportTables( int reportTables )
    {
        this.reportTables = reportTables;
    }

    public int getCharts()
    {
        return charts;
    }

    public void setCharts( int charts )
    {
        this.charts = charts;
    }

    public int getMaps()
    {
        return maps;
    }

    public void setMaps( int maps )
    {
        this.maps = maps;
    }

    public int getEventReports()
    {
        return eventReports;
    }

    public void setEventReports( int eventReports )
    {
        this.eventReports = eventReports;
    }

    public int getEventCharts()
    {
        return eventCharts;
    }

    public void setEventCharts( int eventCharts )
    {
        this.eventCharts = eventCharts;
    }

    public DataStatistics(){}
    /*public DataStatistics()

    public DataStatistics(String text, String uid, String code,
        Date created, Date lastUpdated)
    {
        super();
        this.text = text;

        super.setUid( uid );
        super.setCode( code );
        super.setCreated( created );
        super.setLastUpdated( lastUpdated );
    }*/



    @Override
    public String toString()
    {
        return super.toString() + "\nFavorites: " + favorites;
    }
}
