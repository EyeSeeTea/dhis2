
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
    public List<Map<Integer, String>> listAll()
    {
        SmsConfiguration smsConfig = getSmsConfiguration();
        List<SmsGatewayConfig> gateways = smsConfig.getGateways();

        List<Map<Integer, String>> gatewayList = new ArrayList<Map<Integer, String>>();
        Map<Integer, String> gatewayMap = new HashMap<Integer, String>();
        int key = 1;
        for ( SmsGatewayConfig gw : gateways )
        {
            gatewayMap.put( key, gw.getName() );
            key++;
        }

        gatewayList.add( gatewayMap );
        return gatewayList;
    }

    @Override
    public String addGateway()
    {

        return null;
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

    private SmsConfiguration getSmsConfiguration()
    {
        return smsConfigMgr.getSmsConfiguration();
    }
}
