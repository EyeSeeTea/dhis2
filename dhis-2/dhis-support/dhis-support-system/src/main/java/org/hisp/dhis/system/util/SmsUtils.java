package org.hisp.dhis.system.util;


/*
* Copyright (c) 2004-2015, University of Oslo
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;

import org.hisp.dhis.commons.util.TextUtils;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.sms.incoming.IncomingSms;
import org.hisp.dhis.sms.parse.SMSParserException;
import org.hisp.dhis.user.User;
import org.hisp.dhis.sms.command.SMSCommand;

/**
 * @author Zubair <rajazubair.asghar@gmail.com>
 */
public class SmsUtils
{
    public static String getCommandString( IncomingSms sms )
    {
        String message = sms.getText();
        String commandString = null;

        for ( int i = 0; i < message.length(); i++ )
        {
            String c = String.valueOf( message.charAt( i ) );

            if ( c.matches( "\\W" ) )
            {
                commandString = message.substring( 0, i );
                message = message.substring( commandString.length() + 1 );
                break;
            }
        }

        return commandString;
    }

    public static Collection<OrganisationUnit> getOrganisationUnitsByPhoneNumber( String sender,
        Collection<User> users )
    {
        Collection<OrganisationUnit> orgUnits = new ArrayList<>();
        for ( User u : users )
        {
            if ( u.getOrganisationUnits() != null )
            {
                orgUnits.addAll( u.getOrganisationUnits() );
            }
        }

        return orgUnits;
    }

    public static Date lookForDate( String message )
    {
        if ( !message.contains( " " ) )
        {
            return null;
        }

        Date date = null;
        String dateString = message.trim().split( " " )[0];
        SimpleDateFormat format = new SimpleDateFormat( "ddMM" );

        try
        {
            Calendar cal = Calendar.getInstance();
            date = format.parse( dateString );
            cal.setTime( date );
            int year = Calendar.getInstance().get( Calendar.YEAR );
            int month = Calendar.getInstance().get( Calendar.MONTH );

            if ( cal.get( Calendar.MONTH ) < month )
            {
                cal.set( Calendar.YEAR, year );
            }
            else
            {
                cal.set( Calendar.YEAR, year - 1 );
            }

            date = cal.getTime();
        }
        catch ( Exception e )
        {
            // no date found
        }

        return date;
    }

    public static User getUser( String sender, SMSCommand smsCommand, List<User> userList )
    {
        OrganisationUnit orgunit = null;
        User user = null;

        // Need to be edited
        for ( User u : userList )
        {
            OrganisationUnit ou = u.getOrganisationUnit();

            if ( ou != null )
            {
                // Might be undefined if the user has more than one org units
                if ( orgunit == null )
                {
                    orgunit = ou;
                }
                else if ( orgunit.getId() == ou.getId() )
                {
                    // Same org unit
                }
                else
                {
                    if ( StringUtils.isEmpty( smsCommand.getMoreThanOneOrgUnitMessage() ) )
                    {
                        throw new SMSParserException( SMSCommand.MORE_THAN_ONE_ORGUNIT_MESSAGE );
                    }
                    else
                    {
                        throw new SMSParserException( smsCommand.getMoreThanOneOrgUnitMessage() );
                    }
                }
            }

            user = u;
        }

        if ( user == null )
        {
            throw new SMSParserException( "User is not associated with any orgunit. Please contact your supervisor." );
        }

        return user;
    }

    public static OrganisationUnit selectOrganisationUnit( Collection<OrganisationUnit> orgUnits,
        Map<String, String> parsedMessage, SMSCommand smsCommand )
    {
        OrganisationUnit orgUnit = null;

        for ( OrganisationUnit o : orgUnits )
        {
            if ( orgUnits.size() == 1 )
            {
                orgUnit = o;
            }
            if ( parsedMessage.containsKey( "ORG" ) && o.getCode().equals( parsedMessage.get( "ORG" ) ) )
            {
                orgUnit = o;
                break;
            }
        }

        if ( orgUnit == null && orgUnits.size() > 1 )
        {
            String messageListingOrgUnits = smsCommand.getMoreThanOneOrgUnitMessage();

            for ( Iterator<OrganisationUnit> i = orgUnits.iterator(); i.hasNext(); )
            {
                OrganisationUnit o = i.next();
                messageListingOrgUnits += TextUtils.SPACE + o.getName() + ":" + o.getCode();

                if ( i.hasNext() )
                {
                    messageListingOrgUnits += ",";
                }
            }

            throw new SMSParserException( messageListingOrgUnits );
        }

        return orgUnit;
    }
}
