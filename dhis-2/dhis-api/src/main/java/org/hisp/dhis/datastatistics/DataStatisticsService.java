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

import java.util.Date;
import java.util.List;

/**
 * @author Yrjan A. F. Fraschetti
 * @author Julie Hill Roa
 */


public interface DataStatisticsService
{
    /**
     * Adds a DataStatisticsEvent
     *
     * @param event the event to be added
     * @return the id
     */
    int addEvent( DataStatisticsEvent event );

    /**
     * Generates a list of DataStatistics snapshots in the given eventInterval
     *
     * @param startDate
     * @param endDate
     * @param eventInterval
     * @return
     */
    List<AggregatedStatistics> getReports( Date startDate, Date endDate, EventInterval eventInterval );

    /**
     * Finds number of charts in eventInterval date to current date
     *
     * @param date
     * @return
     */
    int getNumberOfCharts( Date date );

    /**
     * Finds number of report tables in eventInterval date to current date
     *
     * @param date
     * @return
     */
    int getNumberOfReportTables( Date date );

    /**
     * Finds number of favorite views in eventInterval
     *
     * @param startDate of eventInterval
     * @param endDate   of eventInterval
     * @return
     */
    int getNumberOfFavoriteViews( Date startDate, Date endDate );

    /**
     * Finds number of maps in eventInterval date to current date
     *
     * @param date
     * @return
     */
    int getNumberOfMaps( Date date );

    /**
     * Finds number of event reports in eventInterval date to current date
     *
     * @param date
     * @return
     */
    int getNumberOfEventReports( Date date );

    /**
     * Finds number of event charts in eventInterval date to current date
     *
     * @param date
     * @return
     */
    int getNumberOfEventCharts( Date date );

    /**
     * Finds number of dashboards in eventInterval date to current date
     *
     * @param date
     * @return
     */
    int getNumberOfDashboards( Date date );

    /**
     * Finds number of indicators in eventInterval date to current date
     *
     * @param date
     * @return
     */
    int getNumberOfIndicators( Date date );

    /**
     * Finds number of active users in eventInterval date to current date
     *
     * @return
     */
    int getNumberOfUsers();

    /**
     * Creates a snapshot of DataStatistics and saves it
     */
    int saveSnapshot();
}
