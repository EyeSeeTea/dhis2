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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Yrjan A. F. Fraschetti
 * @author Julie Hill Roa
 */

public interface DataStatisticsStore
{
    /**
     * Adds a snapshot (DataStatistics object)
     * @param dataStatistics snapshot
     * @return id of snapshot
     */
    int addSnapshot(DataStatistics dataStatistics);

    /**
     * Creates a list of snapshots in interval (day)
     * @param startDate of interval
     * @param endDate of interval
     * @return List of DataStatistics (snapshot)
     */
    List<DataStatistics> getSnapshotsInIntervalDay(Date startDate, Date endDate);

    /**
     * Creates an aggregated list of snapshots in interval (week)
     * @param start of interval
     * @param end of interval
     * @return List of DataStatistics (snapshot)
     */
    List<DataStatistics> getSnapshotsInIntervalWeek(Calendar start, Calendar end);

    /**
     * Creates an aggregated list of snapshots in interval (month)
     * @param start of interval
     * @param end of interval
     * @return List of DataStatistics (snapshot)
     */
    List<DataStatistics> getSnapshotsInIntervalMonth(Calendar start, Calendar end);

    /**
     * Creates an aggregated list of snapshots in interval (year)
     * @param start of interval
     * @param end of interval
     * @return List of DataStatistics (snapshot)
     */
    List<DataStatistics> getSnapshotsInIntervalYear(Calendar start, Calendar end);

}
