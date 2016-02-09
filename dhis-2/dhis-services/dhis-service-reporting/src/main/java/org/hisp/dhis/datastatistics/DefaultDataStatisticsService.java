package org.hisp.dhis.datastatistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by yrjanaff on 08.02.2016.
 */

@Transactional
public class DefaultDataStatisticsService implements DataStatisticsService
{
    @Autowired
   DataStatisticsStore hibernateDataStatisticsStore;

    @Override
    public int addEvent(DataStatisticsEvent event)
    {
        return hibernateDataStatisticsStore.addDataStatisticsEvent( event );
    }
}
