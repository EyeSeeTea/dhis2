package org.hisp.dhis.webapi.controller.datastatistics;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import org.hisp.dhis.datastatistics.DataStatisticsEvent;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.datastatistics.EventType;
import org.hisp.dhis.datastatistics.DataStatisticsService;
import org.hisp.dhis.datastatistics.DataStatistics;


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


    @RequestMapping(value = "/datastatistics/create", method = RequestMethod.POST)
    public @ResponseBody String createObject(@RequestParam EventType eventType ){
        Date timestamp = new Date();
        User user = currentUserService.getCurrentUser();

        DataStatisticsEvent event = new DataStatisticsEvent(eventType, timestamp, user.getId());
        int id = defaultDataStatisticsService.addEvent(event);

        return "Date: " + timestamp + " User: " + user.toString() + " EventType: " + eventType + " databaseid: " + id;
    }

    @RequestMapping(value = "/datastatistics/report", method = RequestMethod.GET)
    public @ResponseBody DataStatistics report(@RequestParam @DateTimeFormat(pattern="yyyy-mm-dd") Date startDate,
        @RequestParam @DateTimeFormat(pattern="yyyy-mm-dd") Date endDate ){
        System.out.println("\n\nstartDate i controller: " + startDate);
        System.out.println("\n\nstartDate i controller: " + endDate);

        return defaultDataStatisticsService.createReport(startDate, endDate);
    }
}
