package org.hisp.dhis.dxf2.gml;

import org.apache.commons.math.util.MathUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Methods to parse various GML coordinate formats and output the DHIS 2 internal representation.
 * @author Halvdan Hoem Grelland
 */
public class GmlConversionUtils
{
    private static final NumberFormat NF = NumberFormat.getInstance( Locale.ENGLISH );

    /**
     * Parses a gml:coordinates element and outputs the DHIS 2 internal string representation.
     *
     * @param coordinates contents of gml:coordinates element to parse.
     * @param precision decimal precision to use in output.
     * @return a string representation of the coordinates.
     * @throws ParseException
     */
    public static String gmlCoordinatesToString( String coordinates, String precision )
        throws ParseException
    {
        int nDecimals = Integer.parseInt( precision );

        StringBuilder sb = new StringBuilder();

        for ( String c : coordinates.trim().split( "\\s" ) )
        {
            String[] p = c.split( "," );

            String lat = parseCoordinate( p[0], nDecimals, NF ),
                   lon = parseCoordinate( p[1], nDecimals, NF );

            sb.append( "[" ).append( lat ).append( "," ).append( lon ).append( "]," );
        }

        return sb.length() > 0 ? sb.deleteCharAt( sb.length() - 1 ).toString() : "";
    }

    /**
     * Parses a gml:pos element and outputs the DHIS 2 internal string representation.
     *
     * @param pos contents of gml:pos element to parse.
     * @param precision decimal precision to use in output.
     * @return a string representation of the point.
     * @throws ParseException
     */
    public static String gmlPosToString( String pos, String precision )
        throws ParseException
    {
        int nDecimals = Integer.parseInt( precision );

        String[] c = pos.trim().split( "\\s", 2 );

        if( c.length != 2 )
        {
            return "";
        }

        String lat = parseCoordinate( c[0], nDecimals, NF ),
               lon = parseCoordinate( c[1], nDecimals, NF );

        return "[" + lat + "," + lon + "]";
    }

    /**
     * Parses a gml:posList element and outputs the DHIS 2 internal string representation.
     *
     * @param posList contents of gml:posList element to parse.
     * @param precision decimal precision to use in output.
     * @return a string representation of the posList.
     * @throws ParseException
     */
    public static String gmlPosListToString( String posList, String precision )
        throws ParseException
    {
        int nDecimals = Integer.parseInt( precision );

        StringBuilder sb = new StringBuilder();

        String[] c = posList.trim().split( "\\s" );

        if( c.length % 2 != 0 )
        {
            return ""; // Badly formed gml:posList
        }

        for( int i = 0 ; i <  c.length ; i += 2 )
        {
            String lat = parseCoordinate( c[i], nDecimals, NF ),
                   lon = parseCoordinate( c[i + 1], nDecimals, NF );

            sb.append( "[" ).append( lat ).append(",").append( lon ).append( "]," );
        }

        return sb.length() > 0 ? sb.deleteCharAt( sb.length() - 1 ).toString() : "";
    }

    private static String parseCoordinate( String number, int precision, NumberFormat nf )
        throws ParseException
    {
        return Double.toString( MathUtils.round( nf.parse( number).doubleValue(), precision ) );
    }
}
