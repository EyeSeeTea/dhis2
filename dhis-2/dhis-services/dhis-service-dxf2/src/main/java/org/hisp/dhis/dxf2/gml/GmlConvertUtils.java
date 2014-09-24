package org.hisp.dhis.dxf2.gml;

import org.apache.commons.math.util.MathUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * @author Halvdan Hoem Grelland
 */
public class GmlConvertUtils
{
    /**
     * Parses a gml:coordinates element and outputs a string representation.
     *
     * @param coordinates contents of gml:coordinates element to parse.
     * @param precision maximum number of decimals to include.
     * @return string representation of the coordinates.
     * @throws ParseException
     */
    public static String gmlCoordinatesToString( String coordinates, String precision )
        throws ParseException
    {
        NumberFormat nf = NumberFormat.getInstance( Locale.ENGLISH );

        int nDecimals = Integer.parseInt( precision );

        StringBuilder sb = new StringBuilder();

        for ( String c : coordinates.split( "\\s" ) )
        {
            String[] p = c.split( "," );

            String lat = parseCoordinate( p[0], nDecimals, nf ),
                   lon = parseCoordinate( p[1], nDecimals, nf );

            sb.append( "[" ).append( lat ).append( "," ).append( lon ).append( "]," );
        }

        if( sb.length() < 1 )
        {
            return ""; // TODO necessary?
        }

        return sb.deleteCharAt( sb.length() - 1 ).toString();
    }

    private static String parseCoordinate( String number, int precision, NumberFormat nf )
        throws ParseException
    {
        return Double.toString( MathUtils.round( nf.parse( number).doubleValue(), precision ) );
    }
}
