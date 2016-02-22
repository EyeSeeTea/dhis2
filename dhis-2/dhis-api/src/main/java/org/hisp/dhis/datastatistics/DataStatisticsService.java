package org.hisp.dhis.datastatistics;

import java.util.Date;
import java.util.List;

/**
 * @author Yrjan A. F. Fraschetti
 * @author Julie Hill Roa
 */

public interface DataStatisticsService
{
    int addEvent(DataStatisticsEvent event);

    List<DataStatistics> getReports(Date startDate, Date endDate);

    int getNumberOfCharts(Date startDate);

    int getNumberOfReportTables(Date date);

    int getNumberOfFavoriteViews(Date startDate, Date endDate);

    int getNumberOfMaps(Date date);

    int getNumberOfEventReports(Date date);

    int getNumberOfEventCharts(Date date);

    int getNumberOfDashboards(Date date);

    int getNumberOfIndicators(Date date);

    int getNumberOfUsers(Date date);

    void saveSnapshot();
}
