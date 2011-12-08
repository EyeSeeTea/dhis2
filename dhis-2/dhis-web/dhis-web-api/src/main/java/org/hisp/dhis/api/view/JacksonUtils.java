package org.hisp.dhis.api.view;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class JacksonUtils
{
    public static JsonFactory createJsonFactory()
    {
        ObjectMapper objectMapper = new ObjectMapper();

        AnnotationIntrospector jacksonAnnotationIntrospector = new JacksonAnnotationIntrospector();
        AnnotationIntrospector jaxAnnotationIntrospector = new JaxbAnnotationIntrospector();
        AnnotationIntrospector pair = new AnnotationIntrospector.Pair( jacksonAnnotationIntrospector, jaxAnnotationIntrospector );

        objectMapper.setAnnotationIntrospector( pair );

        JsonFactory factory = objectMapper.getJsonFactory();
        factory.enable( JsonGenerator.Feature.QUOTE_FIELD_NAMES );

        return factory;
    }

    //---------------------------------------------------------------------------------------------------
    // Json Serializer
    //---------------------------------------------------------------------------------------------------

    public static JsonGenerator createJsonGenerator( OutputStream output ) throws IOException
    {
        return JacksonUtils.createJsonFactory().createJsonGenerator( output, JsonEncoding.UTF8 );
    }

    public static void writeObject( Object value, OutputStream output ) throws IOException
    {
        JacksonUtils.createJsonGenerator( output ).writeObject( value );
    }

    //---------------------------------------------------------------------------------------------------
    // Json Deserializer
    //---------------------------------------------------------------------------------------------------

    public static JsonParser createJsonParser( InputStream input ) throws IOException
    {
        return JacksonUtils.createJsonFactory().createJsonParser( input );
    }

    public static <T> T readValueAs( Class<?> clazz, InputStream input ) throws IOException
    {
        return (T) JacksonUtils.createJsonParser( input ).readValueAs( clazz );
    }
}
