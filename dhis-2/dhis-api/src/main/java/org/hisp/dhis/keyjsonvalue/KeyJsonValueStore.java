package org.hisp.dhis.keyjsonvalue;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;

import java.util.List;

/**
 * Created by Stian Sandvold on 27.09.2015.
 */
public interface KeyJsonValueStore
    extends GenericIdentifiableObjectStore<KeyJsonValue>
{

    List<String> getNamespaces();

    List<String> getKeysInNamespace( String namespace );

    void deleteKeysInNamespace( String namespace );

    KeyJsonValue getKeyJsonValue( String namespace, String key );

}
