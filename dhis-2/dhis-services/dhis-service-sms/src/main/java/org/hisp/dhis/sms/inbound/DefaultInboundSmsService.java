package org.hisp.dhis.sms.inbound;

/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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
import java.util.List;
import org.hisp.dhis.sms.incoming.IncomingSms;
import org.hisp.dhis.sms.incoming.IncomingSmsService;
import org.hisp.dhis.sms.incoming.IncomingSmsStore;
import org.hisp.dhis.sms.incoming.SmsMessageEncoding;
import org.hisp.dhis.sms.incoming.SmsMessageStatus;
import org.smslib.InboundMessage;
import org.smslib.Service;

public class DefaultInboundSmsService
    implements IncomingSmsService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private IncomingSmsStore incomingSmsStore;
    
    public void setIncomingSmsStore( IncomingSmsStore incomingSmsStore )
    {
        this.incomingSmsStore = incomingSmsStore;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<InboundMessage> msgList = new ArrayList<InboundMessage>();

    public void setMsgList( List<InboundMessage> msgList )
    {
        this.msgList = msgList;
    }

    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------

    @Override
    public List<IncomingSms> listAllMessage()
    {
        List<IncomingSms> result = new ArrayList<IncomingSms>();

        try
        {
            Service.getInstance().readMessages( msgList, InboundMessage.MessageClasses.ALL );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        if ( msgList.size() > 0 )
        {
            for ( InboundMessage each : msgList )
            {
                IncomingSms incomingSms = new IncomingSms();

                incomingSms.setOriginator( each.getOriginator() );
                
                incomingSms.setEncoding( SmsMessageEncoding.ENC7BIT );
                
                incomingSms.setSentDate( each.getDate() );
                
                incomingSms.setReceivedDate( each.getDate() );
                
                incomingSms.setText( each.getText() );
                
                incomingSms.setGatewayId( each.getGatewayId() );
                
                incomingSms.setStatus( SmsMessageStatus.PROCESSED );
                
                incomingSms.setStatusMessage( "imported" );

                result.add( incomingSms );
            }

            msgList.clear();
        }

        return result;
    }

    @Override
    public List<InboundMessage> getMsgList()
    {
        try
        {
            Service.getInstance().readMessages( msgList, InboundMessage.MessageClasses.ALL );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        return msgList;
    }
    
    @Override
    public IncomingSms get( int index )
    {
        try
        {
            Service.getInstance().readMessages( msgList, InboundMessage.MessageClasses.ALL );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        
        InboundMessage specifiedMsg = msgList.get( index - 1 );
        
        IncomingSms incomingSms = new IncomingSms();
        
        incomingSms.setOriginator( specifiedMsg.getOriginator() );
        
        incomingSms.setEncoding( SmsMessageEncoding.ENC7BIT );
        
        incomingSms.setSentDate( specifiedMsg.getDate() );
        
        incomingSms.setReceivedDate( specifiedMsg.getDate() );
        
        incomingSms.setText( specifiedMsg.getText() );
        
        incomingSms.setGatewayId( specifiedMsg.getGatewayId() );
        
        incomingSms.setStatus( SmsMessageStatus.PROCESSED );
        
        incomingSms.setStatusMessage( "imported" );
        
        msgList.clear();
        
        return incomingSms;
    }
    
    @Override
    public void save( IncomingSms incomingSms )
    {
        incomingSmsStore.save( incomingSms );
    }

    @Override
    public IncomingSms getNextUnprocessed()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void update( IncomingSms sms )
    {
        // TODO Auto-generated method stub

    }


}
