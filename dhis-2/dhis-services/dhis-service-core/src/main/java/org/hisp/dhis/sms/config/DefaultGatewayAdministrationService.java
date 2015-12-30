
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
package org.hisp.dhis.sms.config;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Zubair <rajazubair.asghar@gmail.com>
 *
 */
public class DefaultGatewayAdministrationService
    implements GatewayAdministratonService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private SmsConfigurationManager smsConfigMgr;

    @Override
    public SmsConfiguration toList()
    {
        SmsConfiguration smsConfig = getSmsConfiguration();

        return smsConfig != null ? smsConfig : null;
    }

    @Override
    public String addGateway( Map<String, Object> config )
    {
        int gatewayType = Integer.parseInt( config.get( "type" ).toString() );
        if ( gatewayType == 1 )
        {
            return addClickatel( config );

        }
        if ( gatewayType == 2 )
        {
            return addBulkSms( config );

        }

        return "No Gateway against this ID";
    }

    @Override
    public boolean setDefault( int index )
    {
        int trackIndex = 0;

        SmsConfiguration smsConfig = getSmsConfiguration();
        List<SmsGatewayConfig> gateways = smsConfig.getGateways();

        if ( gateways.size() > index && gateways.get( index ) != null )
        {
            for ( SmsGatewayConfig gw : gateways )
            {
                if ( index == trackIndex )
                {
                    gw.setDefault( true );
                }
                else
                {
                    gw.setDefault( false );
                }
                gateways.set( trackIndex++, gw );
            }

            smsConfigMgr.updateSmsConfiguration( smsConfig );

            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean validateJSON( Map<String, Object> config )
    {

        if ( config.containsKey( "type" ) && config.containsKey( "name" ) && config.containsKey( "username" )
            && config.containsKey( "password" ) && config.containsKey( "region" ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private SmsConfiguration getSmsConfiguration()
    {
        return smsConfigMgr.getSmsConfiguration();
    }

    private String addClickatel( Map<String, Object> config )
    {
        return null;
    }

    private String addBulkSms( Map<String, Object> config )
    {
        SmsConfiguration smsConfig = smsConfigMgr.getSmsConfiguration();

        if ( smsConfig != null )
        {
            BulkSmsGatewayConfig bulkGatewayConfig = (BulkSmsGatewayConfig) smsConfigMgr
                .checkInstanceOfGateway( BulkSmsGatewayConfig.class );

            int index = -1;

            if ( bulkGatewayConfig == null )
            {
                bulkGatewayConfig = new BulkSmsGatewayConfig();
            }
            else
            {
                index = smsConfig.getGateways().indexOf( bulkGatewayConfig );
            }

            bulkGatewayConfig.setName( config.get( "name" ).toString() );
            bulkGatewayConfig.setPassword( config.get( "password" ).toString() );
            bulkGatewayConfig.setUsername( config.get( "username" ).toString() );
            bulkGatewayConfig.setRegion( config.get( "region" ).toString() );

            if ( smsConfig.getGateways() == null || smsConfig.getGateways().isEmpty() )
            {
                bulkGatewayConfig.setDefault( true );
            }

            if ( index >= 0 )
            {
                smsConfig.getGateways().set( index, bulkGatewayConfig );
            }
            else
            {
                smsConfig.getGateways().add( bulkGatewayConfig );
            }

            smsConfigMgr.updateSmsConfiguration( smsConfig );

            return "BulkSms Gateway Added";
        }
        return "No Sms Configuraiton";
    }
}
