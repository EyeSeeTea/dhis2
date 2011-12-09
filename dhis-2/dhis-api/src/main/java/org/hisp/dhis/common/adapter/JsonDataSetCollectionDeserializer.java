package org.hisp.dhis.common.adapter;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.hisp.dhis.dataset.DataSet;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class JsonDataSetCollectionDeserializer extends JsonDeserializer<Set<DataSet>>
{
    @Override
    public Set<DataSet> deserialize( JsonParser jp, DeserializationContext context ) throws IOException, JsonProcessingException
    {
        Set<DataSet> dataSets = new HashSet<DataSet>();

        System.err.println( "deserialize" );

        return dataSets;
    }
}
