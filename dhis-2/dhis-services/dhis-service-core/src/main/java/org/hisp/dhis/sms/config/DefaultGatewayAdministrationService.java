
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.sms.outbound.OutboundSmsTransportService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Zubair <rajazubair.asghar@gmail.com>
 *
 */
public class DefaultGatewayAdministrationService
    implements GatewayAdministratonService
{

    private final String BULK_GATEWAY = "bulk_gw";

    private final String CLICKATELL_GATEWAY = "clickatell_gw";

    private final String HTTP_GATEWAY = "generic_http_gw";

    private final String MODEM_GATEWAY = "modem_gw";

    private final String SMPP_GATEWAY = "smpp_gw";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private SmsConfigurationManager smsConfigMgr;

    @Autowired
    private OutboundSmsTransportService transportService;

    @Override
    public List<Map<String, Object>> toList()
    {
        SmsConfiguration smsConfig = getSmsConfiguration();
        List<SmsGatewayConfig> gatewayList = smsConfig.getGateways();

        List<Map<String, Object>> listOfMap = new ArrayList<Map<String, Object>>();

        Map<String, Object> configMap;

        int index = 0;

        if ( !gatewayList.isEmpty() )
        {
            for ( SmsGatewayConfig gw : gatewayList )
            {
                configMap = new HashMap<String, Object>();
                configMap.put( "name", gw.getName() );
                configMap.put( "id", index );
                listOfMap.add( configMap );

                index++;
            }

            return listOfMap;
        }
        else
        {
            return null;
        }
    }

    @Override
    public String addOrUpdateGateway( Map<String, Object> config )
    {
        int gatewayType = Integer.parseInt( config.get( "type" ).toString() );
        if ( gatewayType == 1 )
        {
            return addOrUpdateClickatel( config );
        }
        if ( gatewayType == 2 )
        {
            return addOrUpdateBulkSms( config );
        }
        if ( gatewayType == 3 )
        {
            return addOrUpdateGenericHttp( config );
        }
        if ( gatewayType == 4 )
        {
            return addOrUpdateModem( config );
        }
        if ( gatewayType == 5 )
        {
            return addOrUpdateSmpp( config );
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

    private String addOrUpdateClickatel( Map<String, Object> payLoad )
    {
        SmsConfiguration smsConfig = getSmsConfiguration();

        if ( smsConfig != null )
        {
            ClickatellGatewayConfig gatewayConfig = (ClickatellGatewayConfig) smsConfigMgr
                .checkInstanceOfGateway( ClickatellGatewayConfig.class );

            int index = -1;

            if ( gatewayConfig == null )
            {
                gatewayConfig = new ClickatellGatewayConfig();
            }
            else
            {
                index = smsConfig.getGateways().indexOf( gatewayConfig );
            }

            gatewayConfig.setName( payLoad.get( "name" ).toString() );
            gatewayConfig.setPassword( payLoad.get( "password" ).toString() );
            gatewayConfig.setUsername( payLoad.get( "username" ).toString() );
            gatewayConfig.setApiId( payLoad.get( "apiId" ).toString() );

            if ( smsConfig.getGateways() == null || smsConfig.getGateways().isEmpty() )
            {
                gatewayConfig.setDefault( true );
            }

            if ( index >= 0 )
            {
                smsConfig.getGateways().set( index, gatewayConfig );
            }
            else
            {
                smsConfig.getGateways().add( gatewayConfig );
            }

            smsConfigMgr.updateSmsConfiguration( smsConfig );

            return "Clickatel Gateway Added";
        }
        return "No sms configuration found";
    }

    private String addOrUpdateBulkSms( Map<String, Object> payLoad )
    {
        SmsConfiguration smsConfig = getSmsConfiguration();

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

            bulkGatewayConfig.setName( payLoad.get( "name" ).toString() );
            bulkGatewayConfig.setPassword( payLoad.get( "password" ).toString() );
            bulkGatewayConfig.setUsername( payLoad.get( "username" ).toString() );
            bulkGatewayConfig.setRegion( payLoad.get( "region" ).toString() );

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

    private String addOrUpdateSmpp( Map<String, Object> payLoad )
    {
        SmsConfiguration smsConfig = getSmsConfiguration();

        if ( smsConfig != null )
        {
            SMPPGatewayConfig gatewayConfig = (SMPPGatewayConfig) smsConfigMgr
                .checkInstanceOfGateway( SMPPGatewayConfig.class );

            int index = -1;

            if ( gatewayConfig == null )
            {
                gatewayConfig = new SMPPGatewayConfig();
            }
            else
            {
                index = smsConfig.getGateways().indexOf( gatewayConfig );
            }

            gatewayConfig.setName( payLoad.get( "name" ).toString() );
            gatewayConfig.setPassword( payLoad.get( "password" ).toString() );
            gatewayConfig.setUsername( payLoad.get( "username" ).toString() );
            gatewayConfig.setAddress( payLoad.get( "address" ).toString() );
            gatewayConfig.setPort( (Integer) payLoad.get( "port" ) );

            if ( smsConfig.getGateways() == null || smsConfig.getGateways().isEmpty() )
            {
                gatewayConfig.setDefault( true );
            }

            if ( index >= 0 )
            {
                smsConfig.getGateways().set( index, gatewayConfig );
            }
            else
            {
                smsConfig.getGateways().add( gatewayConfig );
            }

            smsConfigMgr.updateSmsConfiguration( smsConfig );

            return "SMPP Gateway Added";
        }
        return "No sms configuration found";
    }

    private String addOrUpdateModem( Map<String, Object> payLoad )
    {
        SmsConfiguration smsConfig = getSmsConfiguration();

        if ( smsConfig != null )
        {
            ModemGatewayConfig gatewayConfig = (ModemGatewayConfig) smsConfigMgr
                .checkInstanceOfGateway( ModemGatewayConfig.class );

            int index = -1;

            if ( gatewayConfig == null )
            {
                gatewayConfig = new ModemGatewayConfig();
            }
            else
            {
                index = smsConfig.getGateways().indexOf( gatewayConfig );
            }

            gatewayConfig.setName( payLoad.get( "name" ).toString() );
            gatewayConfig.setPort( payLoad.get( "port" ).toString() );
            gatewayConfig.setBaudRate( (Integer) payLoad.get( "naudRate" ) );
            gatewayConfig.setPollingInterval( (Integer) payLoad.get( "pollinginterval" ) );
            gatewayConfig.setManufacturer( payLoad.get( "manufacturer" ).toString() );
            gatewayConfig.setModel( payLoad.get( "model" ).toString() );
            gatewayConfig.setPin( payLoad.get( "pin" ).toString() );
            gatewayConfig.setInbound( (boolean) payLoad.get( "inbound" ) );
            gatewayConfig.setOutbound( (boolean) payLoad.get( "outbound" ) );

            if ( smsConfig.getGateways() == null || smsConfig.getGateways().isEmpty() )
            {
                gatewayConfig.setDefault( true );
            }

            if ( index >= 0 )
            {
                smsConfig.getGateways().set( index, gatewayConfig );
            }
            else
            {
                smsConfig.getGateways().add( gatewayConfig );
            }

            smsConfigMgr.updateSmsConfiguration( smsConfig );

            return "Modem Gateway Added";
        }
        return "No sms configuration found";
    }

    private String addOrUpdateGenericHttp( Map<String, Object> payLoad )
    {
        SmsConfiguration smsConfig = getSmsConfiguration();

        if ( payLoad != null )
        {
            GenericHttpGatewayConfig gatewayConfig = (GenericHttpGatewayConfig) smsConfigMgr
                .checkInstanceOfGateway( GenericHttpGatewayConfig.class );

            int index = -1;

            if ( gatewayConfig == null )
            {
                gatewayConfig = new GenericHttpGatewayConfig();
            }
            else
            {
                index = smsConfig.getGateways().indexOf( gatewayConfig );
            }

            Map<String, String> map = new HashMap<>();

            map.put( "username", payLoad.get( "username" ).toString() );
            map.put( "password", payLoad.get( "password" ).toString() );

            gatewayConfig.setParameters( map );
            gatewayConfig.setName( payLoad.get( "name" ).toString() );
            gatewayConfig.setUrlTemplate( payLoad.get( "urlTemplate" ).toString() );

            if ( smsConfig.getGateways() == null || smsConfig.getGateways().isEmpty() )
            {
                gatewayConfig.setDefault( true );
            }

            if ( index >= 0 )
            {
                smsConfig.getGateways().set( index, gatewayConfig );
            }
            else
            {
                smsConfig.getGateways().add( gatewayConfig );
            }

            smsConfigMgr.updateSmsConfiguration( smsConfig );

            return "Generic Http Gateway Added";
        }
        return "No Sms configuration found";
    }

    @Override
    public boolean removeGateway( int id )
    {
        SmsConfiguration smsConfig = getSmsConfiguration();

        Iterator<SmsGatewayConfig> it = smsConfig.getGateways().iterator();

        while ( it.hasNext() )
        {
            if ( smsConfig.getGateways().indexOf( it.next() ) == id )
            {
                SmsGatewayConfig gatewayConfig = smsConfig.getGateways().get( id );

                it.remove();

                smsConfigMgr.updateSmsConfiguration( smsConfig );

                if ( gatewayConfig instanceof BulkSmsGatewayConfig )
                {
                    transportService.getGatewayMap().remove( BULK_GATEWAY );
                    return true;
                }

                if ( gatewayConfig instanceof ClickatellGatewayConfig )
                {
                    transportService.getGatewayMap().remove( CLICKATELL_GATEWAY );
                    return true;
                }

                if ( gatewayConfig instanceof ModemGatewayConfig )
                {
                    transportService.getGatewayMap().remove( MODEM_GATEWAY );
                    return true;
                }

                if ( gatewayConfig instanceof GenericHttpGatewayConfig )
                {
                    transportService.getGatewayMap().remove( HTTP_GATEWAY );
                    return true;
                }

                if ( gatewayConfig instanceof SMPPGatewayConfig )
                {
                    transportService.getGatewayMap().remove( SMPP_GATEWAY );
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public SmsGatewayConfig getGatewayByIndex( int index )
    {
        SmsConfiguration smsConfig = getSmsConfiguration();
        if ( smsConfig.getGateways().size() > index )
        {
            return smsConfig.getGateways().get( index );
        }
        else
        {
            return null;
        }
    }
}
