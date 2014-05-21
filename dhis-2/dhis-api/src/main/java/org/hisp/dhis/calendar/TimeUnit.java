package org.hisp.dhis.calendar;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;

/**
 * Simple class to hold information about time, can be used together with DateUnit to form
 * a DateTimeUnit which holds both date and time information. Useful in cases where converting
 * from a JDK Date/Calendar and you don't want to loose the time dimension.
 *
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class TimeUnit
{
    private int hour;

    private int minutes;

    private int seconds;

    public TimeUnit()
    {
    }

    public TimeUnit( int hour, int minutes, int seconds )
    {
        this.hour = hour;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public int getHour()
    {
        return hour;
    }

    public void setHour( int hour )
    {
        this.hour = hour;
    }

    public int getMinutes()
    {
        return minutes;
    }

    public void setMinutes( int minutes )
    {
        this.minutes = minutes;
    }

    public int getSeconds()
    {
        return seconds;
    }

    public void setSeconds( int seconds )
    {
        this.seconds = seconds;
    }

    public static TimeUnit fromDateTime( DateTime dateTime )
    {
        return new TimeUnit( dateTime.getHourOfDay(), dateTime.getMinuteOfHour(), dateTime.getSecondOfMinute() );
    }

    public static TimeUnit fromJdkCalendar( Calendar calendar )
    {
        int amPm = calendar.get( Calendar.AM_PM );

        return new TimeUnit( calendar.get( Calendar.HOUR ) + (amPm * 12), calendar.get( Calendar.MINUTE ),
            calendar.get( Calendar.SECOND ) );
    }

    public static TimeUnit fromJdkDate( Date date )
    {
        return fromDateTime( new DateTime( date.getTime() ) );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        TimeUnit timeUnit = (TimeUnit) o;

        if ( hour != timeUnit.hour ) return false;
        if ( minutes != timeUnit.minutes ) return false;
        if ( seconds != timeUnit.seconds ) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = hour;
        result = 31 * result + minutes;
        result = 31 * result + seconds;
        return result;
    }

    @Override
    public String toString()
    {
        return "TimeUnit{" +
            "hour=" + hour +
            ", minutes=" + minutes +
            ", seconds=" + seconds +
            '}';
    }
}
