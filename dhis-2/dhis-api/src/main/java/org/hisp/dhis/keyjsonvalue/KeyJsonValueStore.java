package org.hisp.dhis.keyjsonvalue;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;

import java.util.List;

/**
 * Created by Stian Sandvold on 27.09.2015.
 */
public interface KeyJsonValueStore
    extends GenericIdentifiableObjectStore<KeyJsonValue>
{

    public KeyJsonValue getKeyJsonValue( String namespace, String key );

    public List<KeyJsonValue> getKeyJsonValueByNamespace( String namespace );

    public List<KeyJsonValue> getNamespaces();

}
