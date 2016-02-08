package org.hisp.dhis.datastatistics;

import java.util.Date;

/**
 * @author Yrjan A. F. Fraschetti
 * @author Julie Hill Roa
 */
public class DataStatisticsEvent
{
    int id;
    EventType type;
    Date timestamp;
    int userId;

    public DataStatisticsEvent(EventType type, Date timestamp, int userId){
        this.type = type;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public DataStatisticsEvent(){}

    public int getId()
    {
        return id;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public EventType getType()
    {
        return type;
    }

    public int getUserId()
    {
        return userId;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public void setType( EventType type )
    {
        this.type = type;
    }

    public void setUserId( int userId )
    {
        this.userId = userId;
    }
}
