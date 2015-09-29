package org.hisp.dhis.keyjsonvalue;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Stian Sandvold on 27.09.2015.
 */
@Transactional
public class DefaultKeyJsonValueService
    implements KeyJsonValueService
{

    private KeyJsonValueStore keyJsonValueStore;

    public void setKeyJsonValueStore( KeyJsonValueStore keyJsonValueStore )
    {
        this.keyJsonValueStore = keyJsonValueStore;
    }

    @Override
    public int addKeyJsonValue( KeyJsonValue keyJsonValue )
    {
        return keyJsonValueStore.save( keyJsonValue );
    }

    @Override
    public void updateKeyJsonValue( KeyJsonValue keyJsonValue )
    {
        keyJsonValueStore.update( keyJsonValue );
    }

    @Override
    public KeyJsonValue getKeyJsonValue( String namespace, String key )
    {

        return keyJsonValueStore.getKeyJsonValue( namespace, key );
    }

    @Override
    public List<KeyJsonValue> getKeysInNamespace( String namespace )
    {
        return keyJsonValueStore.getKeyJsonValueByNamespace( namespace );
    }

    @Override
    public void deleteKeyJsonValue( KeyJsonValue keyJsonValue )
    {
        keyJsonValueStore.delete( keyJsonValue );
    }
}
