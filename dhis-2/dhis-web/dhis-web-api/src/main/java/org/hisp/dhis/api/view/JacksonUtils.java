package org.hisp.dhis.api.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class JacksonUtils
{
    @Deprecated
    public static JsonFactory createJsonFactory()
    {
        ObjectMapper objectMapper = new ObjectMapper();

        AnnotationIntrospector jacksonAnnotationIntrospector = new JacksonAnnotationIntrospector();
        //AnnotationIntrospector jaxAnnotationIntrospector = new JaxbAnnotationIntrospector();
        //AnnotationIntrospector pair = new AnnotationIntrospector.Pair( jacksonAnnotationIntrospector, jaxAnnotationIntrospector );

        objectMapper.setAnnotationIntrospector( jacksonAnnotationIntrospector );

/*
        objectMapper.configure( SerializationConfig.Feature.AUTO_DETECT_FIELDS, false );
        objectMapper.configure( SerializationConfig.Feature.AUTO_DETECT_GETTERS, false );
        objectMapper.configure( SerializationConfig.Feature.AUTO_DETECT_IS_GETTERS, false );
*/

        objectMapper.setSerializationInclusion( JsonInclude.Include.NON_NULL );
        objectMapper.configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true );
        objectMapper.configure( SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true );
        objectMapper.configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false );

        JsonFactory factory = objectMapper.getJsonFactory();
        factory.enable( JsonGenerator.Feature.QUOTE_FIELD_NAMES );

        return factory;
    }

    //---------------------------------------------------------------------------------------------------
    // Json Serializer
    //---------------------------------------------------------------------------------------------------

    @Deprecated
    public static JsonGenerator createJsonGenerator( OutputStream output ) throws IOException
    {
        return JacksonUtils.createJsonFactory().createJsonGenerator( output, JsonEncoding.UTF8 );
    }

    @Deprecated
    public static void writeObject( Object value, OutputStream output ) throws IOException
    {
        JacksonUtils.createJsonGenerator( output ).writeObject( value );
    }

    //---------------------------------------------------------------------------------------------------
    // Json Deserializer
    //---------------------------------------------------------------------------------------------------

    @Deprecated
    public static JsonParser createJsonParser( InputStream input ) throws IOException
    {
        return JacksonUtils.createJsonFactory().createJsonParser( input );
    }

    @Deprecated
    @SuppressWarnings( "unchecked" )
    public static <T> T readValueAs( Class<?> clazz, InputStream input ) throws IOException
    {
        return (T) JacksonUtils.createJsonParser( input ).readValueAs( clazz );
    }

    private static ObjectMapper jsonMapper = new ObjectMapper();

    private static XmlMapper xmlMapper = new XmlMapper();

    static
    {
        jsonMapper.setSerializationInclusion( JsonInclude.Include.NON_NULL );
        jsonMapper.configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true );
        jsonMapper.configure( SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true );
        jsonMapper.configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false );

        jsonMapper.getJsonFactory().enable( JsonGenerator.Feature.QUOTE_FIELD_NAMES );

        xmlMapper.configure( ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true );
        xmlMapper.configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true );
        xmlMapper.configure( SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true );
        xmlMapper.configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false );
    }

    //---------------------------------------------------------------------------------------------------
    // JSON
    //---------------------------------------------------------------------------------------------------

    public static void toJson( OutputStream output, Object value ) throws IOException
    {
        jsonMapper.writeValue( output, value );
    }

    public static void toJsonWithView( OutputStream output, Object value, Class<?> viewClass ) throws IOException
    {
        jsonMapper.writerWithView( viewClass ).writeValue( output, value );
    }

    public static <T> T fromJson( InputStream input, Class<?> clazz ) throws IOException
    {
        return (T) jsonMapper.readValue( input, clazz );
    }

    //---------------------------------------------------------------------------------------------------
    // XML
    //---------------------------------------------------------------------------------------------------

    public static void toXml( OutputStream output, Object value ) throws IOException
    {
        xmlMapper.writeValue( output, value );
    }

    public static void toXmlWithView( OutputStream output, Object value, Class<?> viewClass ) throws IOException
    {
        xmlMapper.writerWithView( viewClass ).writeValue( output, value );
    }

    public static <T> T fromXml( InputStream input, Class<?> clazz ) throws IOException
    {
        return (T) xmlMapper.readValue( input, clazz );
    }
}
