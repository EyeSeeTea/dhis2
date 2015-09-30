package org.hisp.dhis.keyjsonvalue;

import java.util.List;

/**
 * Created by Stian Sandvold on 27.09.2015.
 */
public interface KeyJsonValueService
{
    List<String> getNamespaces();

    List<String> getKeysInNamespace( String namespace );

    void deleteNamespace( String namespace );

    KeyJsonValue getKeyJsonValue( String namespace, String key );

    int addKeyJsonValue( KeyJsonValue keyJsonValue );

    void updateKeyJsonValue( KeyJsonValue keyJsonValue );

    void deleteKeyJsonValue( KeyJsonValue keyJsonValue );
}
