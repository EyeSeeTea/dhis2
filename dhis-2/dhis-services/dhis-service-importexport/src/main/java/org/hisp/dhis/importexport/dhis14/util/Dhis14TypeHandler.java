package org.hisp.dhis.importexport.dhis14.util;

import static org.hisp.dhis.dataelement.DataElement.AGGREGATION_OPERATOR_AVERAGE;
import static org.hisp.dhis.dataelement.DataElement.AGGREGATION_OPERATOR_COUNT;
import static org.hisp.dhis.dataelement.DataElement.AGGREGATION_OPERATOR_SUM;
import static org.hisp.dhis.dataelement.DataElement.TYPE_BOOL;
import static org.hisp.dhis.dataelement.DataElement.TYPE_INT;
import static org.hisp.dhis.dataelement.DataElement.TYPE_STRING;

/**
 * @author Lars Helge Overland
 * @version $Id: TypeHandler.java 6216 2008-11-06 18:06:42Z eivindwa $
 */
public class Dhis14TypeHandler
{
    private static final String DHIS14_TRUE = "1";
    private static final String DHIS14_FALSE = "0";
    
    private static final String DHIS14_AGGREGATION_OPERATOR_SUM = "Sum";
    private static final String DHIS14_AGGREGATION_OPERATOR_AVERAGE = "Average";
    private static final String DHIS14_AGGREGATION_OPERATOR_COUNT = "Count";
    
    private static final String DHIS14_TYPE_INT = "3";
    private static final String DHIS14_TYPE_STRING = "4";
    private static final String DHIS14_TYPE_BOOL = "5";
    
    // -------------------------------------------------------------------------
    // Boolean
    // -------------------------------------------------------------------------
    
    public static String convertBooleanToDhis14( boolean value )
    {
        return value ? DHIS14_TRUE : DHIS14_FALSE;
    }
    
    public static boolean convertBooleanFromDhis14( String value )
    {
        return value.equals( DHIS14_TRUE );
    }

    // -------------------------------------------------------------------------
    // Aggregation operator
    // -------------------------------------------------------------------------
    
    public static String convertAggregationOperatorToDhis14( String value )
    {
        if ( value == null || value.equals( AGGREGATION_OPERATOR_SUM ) )
        {
            return DHIS14_AGGREGATION_OPERATOR_SUM;
        }
        if ( value.equals( AGGREGATION_OPERATOR_AVERAGE ) )
        {
            return DHIS14_AGGREGATION_OPERATOR_AVERAGE;
        }
        if ( value.equals( AGGREGATION_OPERATOR_COUNT ) )
        {
            return DHIS14_AGGREGATION_OPERATOR_COUNT;
        }
        
        return DHIS14_AGGREGATION_OPERATOR_SUM;
    }
    
    public static String convertAggregationOperatorFromDhis14( String value )
    {
        if ( value == null || value.equals( DHIS14_AGGREGATION_OPERATOR_SUM ) )
        {
            return AGGREGATION_OPERATOR_SUM;
        }
        if ( value.equals( DHIS14_AGGREGATION_OPERATOR_AVERAGE ) )
        {
            return AGGREGATION_OPERATOR_AVERAGE;            
        }
        if ( value.equals( DHIS14_AGGREGATION_OPERATOR_COUNT ) )
        {
            return AGGREGATION_OPERATOR_COUNT;
        }
        
        return AGGREGATION_OPERATOR_SUM;
    }

    // -------------------------------------------------------------------------
    // Type
    // -------------------------------------------------------------------------
    
    public static String convertTypeToDhis14( String value )
    {
        if ( value == null || value.equals( TYPE_INT ) )
        {
            return DHIS14_TYPE_INT;
        }
        if ( value.equals( TYPE_STRING ) )
        {
            return DHIS14_TYPE_STRING;
        }
        if ( value.equals( TYPE_BOOL ) )
        {
            return DHIS14_TYPE_BOOL;
        }
        
        return DHIS14_TYPE_INT;
    }
    
    public static String convertTypeFromDhis14( String value )
    {
        if ( value == null || value.equals( DHIS14_TYPE_INT ) )
        {
            return TYPE_INT;
        }
        if ( value.equals( DHIS14_TYPE_STRING ) )
        {
            return TYPE_STRING;
        }
        if ( value.equals( DHIS14_TYPE_BOOL ) )
        {
            return TYPE_BOOL;
        }
        
        return TYPE_INT;
    }
}
