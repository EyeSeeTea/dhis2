package org.hisp.dhis.mobile.connection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import org.hisp.dhis.mobile.model.DataValue;
import org.hisp.dhis.mobile.model.OrgUnit;
import org.hisp.dhis.mobile.model.User;

public class DataValueUploadManager
    extends Thread
{

    private Hashtable dataValueTable;

    private String url;

    private OrgUnit orgUnit;

    private User user;

    public DataValueUploadManager( Hashtable dataValueTable, String url, OrgUnit orgUnit, User user )
    {
        this.dataValueTable = dataValueTable;
        this.url = url;
        this.orgUnit = orgUnit;
        this.user = user;
    }

    public void run()

    {
        HttpConnection connection = null;
        OutputStream opt = null;
        DataOutputStream dos = null;
        Enumeration en = null;
        try
        {
            for ( int redirectTimes = 0; redirectTimes < 5; redirectTimes++ )
            {
                connection = (HttpConnection) Connector.open( url );
                configureConnection( connection );
                int status = connection.getResponseCode();
                switch ( status )
                {
                case HttpConnection.HTTP_SEE_OTHER:
                case HttpConnection.HTTP_TEMP_REDIRECT:
                case HttpConnection.HTTP_MOVED_TEMP:
                case HttpConnection.HTTP_MOVED_PERM:
                    url = connection.getHeaderField( "location" );
                default:
                    break;
                }
                System.out.println("Status: " + connection.getResponseCode());
            }

             int numOfDataValue = dataValueTable.size();
             opt = connection.openOutputStream();
             dos = new DataOutputStream( opt );
            
             dos.writeInt( numOfDataValue );
             dos.writeInt( orgUnit.getId() );
             en = dataValueTable.elements();
             while ( en.hasMoreElements() )
             {
             DataValue dataValue = (DataValue) en.nextElement();
             dos.writeInt( dataValue.getDataElementId() );
             dos.writeInt( dataValue.getProgramInstanceId() );
             dos.writeUTF( dataValue.getValue() );
             }
        }
        catch ( Exception e )
        {
            System.out.println( e.getMessage() );
        }
        finally
        {
            try
            {
                dos.close();
                opt.close();
                connection.close();
            }
            catch ( IOException e )
            {
                System.out.println( e.getMessage() );
            }

        }

    }

    private void configureConnection( HttpConnection connection )
        throws IOException
    {
        String ua = "Profile/" + System.getProperty( "microedition.profiles" ) + " Configuration/"
            + System.getProperty( "microedition.configuration" );
        String locale = System.getProperty( "microedition.locale" );

        connection.setRequestProperty( "User-Agent", ua );
        connection.setRequestProperty( "Accept-Language", locale );
        connection.setRequestMethod( HttpConnection.GET );

        connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );
        String hash = new String( Base64.encode( user.getUsername() + ":" + user.getPassword() ) );
        connection.setRequestProperty( "Authorization", "Basic " + hash );
        connection.setRequestProperty( "Accept", "application/xml" );

    }
}
