package org.hisp.dhis.webapi.controller.datastatistics;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 * Created by yrjanaff on 19.01.2016.
 */

@Controller
//@RequestMapping( value = "/datastatistics", method = RequestMethod.GET )
public class DataStatisticsController
{

    @RequestMapping(value = "/datastatistics", method = RequestMethod.GET)
    public @ResponseBody String helloworld(){
        return "Hello World! Det funka!";
    }
}
