package org.hisp.dhis.datastatistics;

import java.util.Date;

/**
 * Created by yrjanaff on 08.02.2016.
 */
public interface DataStatisticsService
{
    int addEvent(DataStatisticsEvent event);

    DataStatistics createReport(Date startDate, Date endDate);

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
