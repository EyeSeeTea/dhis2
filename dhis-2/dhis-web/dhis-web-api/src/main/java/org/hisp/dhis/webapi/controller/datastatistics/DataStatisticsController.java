package org.hisp.dhis.webapi.controller.datastatistics;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.hisp.dhis.datastatistics.DataStatistics;
import org.hisp.dhis.datastatistics.DataStatisticsStore;
//import org.hisp.dhis.datastatistics.DataStatisticsEvent;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.eventchart.EventChartService;
import org.hisp.dhis.eventreport.EventReportService;
import org.hisp.dhis.indicator.IndicatorService;


import java.lang.String;
import java.lang.System;
import java.util.List;


/**
 * @author Yrjan A. F. Fraschetti
 * @author Julie Hill Roa
 */

@Controller
public class DataStatisticsController
{

    @Autowired
    DataStatisticsStore hibernateDataStatisticsStore;

    /*@Autowired
    DataStatisticsEvent defaultDataStatisticsEvent;*/

    /*
        Statistics
     */
    @Autowired
    private ReportTableService reportTableService;
    @Autowired
    private ChartService chartService;
    @Autowired
    private EventReportService eventReportService;
    @Autowired
    private EventChartService eventChartService;
    @Autowired
    private IndicatorService indicatorService;


    @RequestMapping(value = "/datastatistics/{id}", produces = { "application/json", "text/*" }, method = RequestMethod.GET)
    public @ResponseBody String helloworld(@PathVariable int id){
        DataStatistics ds = hibernateDataStatisticsStore.getDataStatisticsById(id);
        return ds.toString();
    }

    @RequestMapping(value = "/datastatistics/text", method = RequestMethod.GET)
    public @ResponseBody String createObject(@RequestParam String text){

        DataStatistics dataStatistics = new DataStatistics();
        hibernateDataStatisticsStore.addDataStatistics(dataStatistics);
        return "Hello World! Det funka!";
    }


    @RequestMapping(value = "/datastatistics/delete/{id}", method = RequestMethod.GET)
    public @ResponseBody String delete(@PathVariable int id){
        DataStatistics ds =  hibernateDataStatisticsStore.getDataStatisticsById(id);
        hibernateDataStatisticsStore.deleteDataStatistics(ds);
        return "Hello World! Det funka å slette!";
    }

    @RequestMapping(value = "/datastatistics/", produces = { "application/json", "text/*" }, method = RequestMethod.GET)
    public @ResponseBody String getReport(){

        List<ReportTable> table = reportTableService.getAllReportTables();
        System.out.println("\n\nReportTable: ");
        System.out.println(table.size());
        System.out.println("\n\nChart: ");
        System.out.println(chartService.getAllCharts().size());
        System.out.println("\n\nEventReport: ");
        System.out.println(eventReportService.getAllEventReports().size());
        System.out.println("\n\nEventChartService:");
        System.out.println(eventChartService.getAllEventCharts().size());
        System.out.println("\n\nIndicatorService");
        System.out.println(indicatorService.getAllIndicators().size());


        //defaultDataStatisticsEvent.getReportTable();

        return "Funka dette????";
    }


}
