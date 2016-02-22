package org.hisp.dhis.datastatistics;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * @author Yrjan A. F. Fraschetti
 * @author Julie Hill Roa
 */

public class DataStatisticsTask implements Runnable
{
    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */

    @Autowired
    DataStatisticsService dataStatisticsService;

    @Override
    public void run()
    {
        dataStatisticsService.saveSnapshot();
    }

    @PostConstruct
    public void init(){

    }
}
