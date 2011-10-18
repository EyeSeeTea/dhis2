package org.hisp.dhis.mobile.api;

/*
 * Copyright (c) 2011, University of Oslo
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

import java.net.URL;

/**
 * SmsService provides support for sending and receiving SMSes.
 */
public interface SmsService
{

    String ID = SmsService.class.getName();

    public void start()
        throws SmsServiceException;

    public void stop()
        throws SmsServiceException;

    /**
     * To check if SmsService is running or not
     * 
     * @return true is service is started
     */
    public boolean isServiceRunning();

    /**
     * Send an SMS message to the list of recipients.
     * 
     * @param message the message to be sent
     * @param recipients The phone numbers to send to
     * @throws SmsServiceException if unable to sent Message
     */
    public void sendMessage( String message, String... recipients )
        throws SmsServiceException;

    /**
     * Send an Over-the-Air (OTA) message. Used to enable download of settings,
     * applications or sending multimedia messages
     * 
     * @param url The download URL to send
     * @param prompt The message to be displayed to the recipients
     * @param recipients The phone numbers to send to
     * @throws SmsServiceException if unable to sent Message
     */
    public void sendOtaMessage( URL url, String prompt, String... recipients )
        throws SmsServiceException;

    public Sms getNextSms();

    public void setSmsStatus( int id, SmsMessageStatus status, String message );

}
