package org.hisp.dhis.webapi.controller.datastatistics;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.hisp.dhis.datastatistics.DataStatistics;
import org.hisp.dhis.datastatistics.DataStatisticsStore;

import java.lang.String;
import java.lang.System;


/**
 * @Author yrjanaf
 */

@Controller
public class DataStatisticsController
{

    @Autowired
    DataStatisticsStore hibernateDataStatisticsStore;


    @RequestMapping(value = "/datastatistics/{id}", method = RequestMethod.GET)
    public @ResponseBody DataStatistics helloworld(@PathVariable int id){
        System.out.println("\n\n\n\nid inn: " + id);
        DataStatistics ds = hibernateDataStatisticsStore.getDataStatisticsById(id);
        System.out.println("\n\n\n\nhentet ut:  " + ds + " text: "+ds.getText());
        return ds;
    }

    @RequestMapping(value = "/datastatistics/text", method = RequestMethod.POST)
    public @ResponseBody String setText(@RequestParam String text){
        System.out.println("\n\n\n\n" + text+ "\n\n\n\n");
        DataStatistics dataStatistics = new DataStatistics(text);
        System.out.println("\n\n\n\n" + dataStatistics.getText() + "\n\n\n\n");
        hibernateDataStatisticsStore.addDataStatistics(dataStatistics);
        return "Hello World! Det funka!";
    }
}
