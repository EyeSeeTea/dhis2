package org.hisp.dhis.webapi.controller.datastatistics;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.hisp.dhis.datastatistics.DataStatistics;
import org.hisp.dhis.datastatistics.DataStatisticsStore;
import org.hisp.dhis.datastatistics.DataStatisticsEvent;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.eventchart.EventChartService;
import org.hisp.dhis.eventreport.EventReportService;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.datastatistics.EventType;
import org.hisp.dhis.datastatistics.DataStatisticsService;


import java.lang.String;
import java.lang.System;
import java.util.List;
import java.util.Date;


/**
 * @author Yrjan A. F. Fraschetti
 * @author Julie Hill Roa
 */

@Controller
public class DataStatisticsController
{
    @Autowired
    protected CurrentUserService currentUserService;

    @Autowired
    DataStatisticsService defaultDataStatisticsService;


    @RequestMapping(value = "/datastatistics/create", method = RequestMethod.GET)
    public @ResponseBody String createObject(@RequestParam EventType eventType ){


        Date timestamp = new Date();
        User user = currentUserService.getCurrentUser();

        //Legge dette i DefaultDataStatisticsService?
        DataStatisticsEvent event = new DataStatisticsEvent(eventType, timestamp, user.getId());
        int id = defaultDataStatisticsService.addEvent(event);
        //int id = hibernateDataStatisticsStore.addDataStatisticsEvent(event);
        return "Date: " + timestamp + " User: " + user.toString() + " EventType: " + eventType + " databaseid: " + id;
    }


}
