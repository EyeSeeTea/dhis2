package org.hisp.dhis.keyjsonvalue;

import java.util.List;

/**
 * Created by Stian Sandvold on 27.09.2015.
 */
public interface KeyJsonValueService
{
    int addKeyJsonValue( KeyJsonValue keyJsonValue );

    void updateKeyJsonValue( KeyJsonValue keyJsonValue );

    KeyJsonValue getKeyJsonValue( String namespace, String key );

    List<KeyJsonValue> getKeysInNamespace( String namespace );

    void deleteKeyJsonValue( KeyJsonValue keyJsonValue );
}
