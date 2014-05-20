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

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class DateUnitFormat
{
    private static Map<String, Pattern> compileCache = Maps.newHashMap();

    public static DateUnit parse( String period )
    {
        DateUnitType type = DateUnitType.find( period );

        if ( type == null )
        {
            return null;
        }

        if ( compileCache.get( type.getType() ) == null )
        {
            try
            {
                Pattern pattern = Pattern.compile( type.getFormat() );
                compileCache.put( type.getType(), pattern );
            }
            catch ( PatternSyntaxException ex )
            {
                return null;
            }
        }

        System.err.println( "type: " + type );

        Pattern pattern = compileCache.get( type.getType() );
        System.err.println( "pattern: " + pattern );

        Matcher matcher = pattern.matcher( period );
        System.err.println( "matcher.groupCount: " + matcher.groupCount() );

        while ( matcher.find() )
        {
            for ( int i = 1; i <= matcher.groupCount(); i++ )
            {
                System.err.println( "group" + i + ": " + matcher.group( i ) );
            }
        }

        DateUnit dateUnit = new DateUnit();

        return dateUnit;
    }
}
