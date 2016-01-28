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
 * @author Yrjan A. F. Fraschetti
 * @author Julie Hill Roa
 */

@Controller
public class DataStatisticsController
{

    @Autowired
    DataStatisticsStore hibernateDataStatisticsStore;


    @RequestMapping(value = "/datastatistics/{id}", method = RequestMethod.GET)
    public @ResponseBody DataStatistics helloworld(@PathVariable int id){
        DataStatistics ds = hibernateDataStatisticsStore.getDataStatisticsById(id);
        return ds;
    }

    @RequestMapping(value = "/datastatistics/text", method = RequestMethod.GET)
    public @ResponseBody String createObject(@RequestParam String text){

        DataStatistics dataStatistics = new DataStatistics(text);
        hibernateDataStatisticsStore.addDataStatistics(dataStatistics);
        return "Hello World! Det funka!";
    }


    @RequestMapping(value = "/datastatistics/update", method = RequestMethod.GET)
    public @ResponseBody String updateText(@RequestParam String text){
        hibernateDataStatisticsStore.updateDataStatisticsTest(text);
        return "Hello World! Det funka å oppdtere!";
    }

    @RequestMapping(value = "/datastatistics/delete/{id}", method = RequestMethod.GET)
    public @ResponseBody String delete(@PathVariable int id){
        DataStatistics ds =  hibernateDataStatisticsStore.getDataStatisticsById(id);
        hibernateDataStatisticsStore.deleteDataStatistics(ds);
        return "Hello World! Det funka å slette!";
    }

}
