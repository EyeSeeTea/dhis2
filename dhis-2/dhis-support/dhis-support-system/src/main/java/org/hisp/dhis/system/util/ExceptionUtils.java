package org.hisp.dhis.system.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExceptionUtils
{
    private static final Log log = LogFactory.getLog( ExceptionUtils.class );
    
    public static void throwException( boolean condition, String message )
    {
        if ( condition )
        {
            throw new RuntimeException( message );
        }
    }
    
    public static void logException( boolean condition, String message )
    {
        if ( condition )
        {
            log.error( message );
        }
    }
}
