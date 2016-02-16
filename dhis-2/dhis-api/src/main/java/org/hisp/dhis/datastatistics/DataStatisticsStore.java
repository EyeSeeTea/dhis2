package org.hisp.dhis.datastatistics;

import java.util.Date;
import java.util.List;

/**
 * Created by yrjanaff on 16.02.2016.
 */
public interface DataStatisticsStore
{
    int addSnapshot(DataStatistics dataStatistics);
    List<DataStatistics> getSnapshotsInInterval(Date startDate, Date endDate);
}
