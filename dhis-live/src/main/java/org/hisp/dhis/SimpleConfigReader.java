/*
 * Copyright (c) 2004-2010, University of Oslo
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
package org.hisp.dhis;


import java.util.Properties;
import java.io.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Jason P. Pickering
 */
class SimpleConfigReader
{

    private static final String CONFIG_FILE = "conf/dhis2live.cfg";

    private static final int DEFAULT_JETTY_PORT = 8080;

    private static final String PREFERRED_BROWSER_PROPERTY = "preferredBrowser";

    private static final String JETTY_PORT_PROPERTY = "jettyPort";

    private static final Log log = LogFactory.getLog( SimpleConfigReader.class );

    protected Properties getDefaultProperties()
    {
        FileInputStream configInputStream;
        Properties defaultProps = new Properties();
        try
        {
            configInputStream = new FileInputStream( CONFIG_FILE );
            defaultProps.load( configInputStream );
            configInputStream.close();
        } catch ( FileNotFoundException e )
        {
            log.info( "No properties file found." );
        } catch ( IOException ex )
        {
            log.error( "There was an input/output error while loading the properties file." );
        }
        return defaultProps;
    }

    protected String preferredBrowserPath()
    {
        String thisBrowserPath = null;
        if ( getDefaultProperties().containsKey( PREFERRED_BROWSER_PROPERTY ) )
        {
            try
            {
                thisBrowserPath = getDefaultProperties().getProperty( PREFERRED_BROWSER_PROPERTY );
                log.info( "Browser path reported as" + thisBrowserPath );
            } catch ( Exception e )
            {
                log.error( "Could not load preferred browser property" );
                thisBrowserPath = null;
            }
        }
        return thisBrowserPath;
    }

    protected int preferredJettyPort()
    {
        int preferredJettyPort = WebAppServer.DEFAULT_JETTY_PORT;
        if ( getDefaultProperties().containsKey( JETTY_PORT_PROPERTY ) )
        {
            try
            {
                preferredJettyPort = Integer.parseInt( getDefaultProperties().getProperty( JETTY_PORT_PROPERTY ) );
            } catch ( NumberFormatException e )
            {
                log.error( "Port is not in the specified format.Using default." );
                preferredJettyPort = WebAppServer.DEFAULT_JETTY_PORT;
            }
        }
        log.info( "Preferred jetty port will be configured to be " + preferredJettyPort );

        return preferredJettyPort;
    }
}
