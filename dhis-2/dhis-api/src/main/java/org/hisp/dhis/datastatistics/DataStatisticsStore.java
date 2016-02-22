package org.hisp.dhis.datastatistics;

import java.util.Date;
import java.util.List;

/**
 * @author Yrjan A. F. Fraschetti
 * @author Julie Hill Roa
 */

public interface DataStatisticsStore
{
    int addSnapshot(DataStatistics dataStatistics);
    List<DataStatistics> getSnapshotsInInterval(Date startDate, Date endDate);

}
