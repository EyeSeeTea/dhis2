package org.hisp.dhis.common.adapter;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class JsonDateSerializer extends JsonSerializer<Date>
{
    /**
     * This gives us date in the format "2010-02-05T10:58:27.355+0100" which is not really ISO8601.
     *
     * TODO Proper format here should be "2010-02-05T10:58:27.355+01:00", should be fixed.
     */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @Override
    public void serialize( Date date, JsonGenerator jgen, SerializerProvider provider ) throws IOException, JsonProcessingException
    {
        String formattedDate = dateFormat.format( date );

        jgen.writeString( formattedDate );
    }
}
