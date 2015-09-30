package org.hisp.dhis.keyjsonvalue;

import org.hisp.dhis.user.User;
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
    public List<String> getNamespaces()
    {
        return keyJsonValueStore.getNamespaces();
    }

    @Override
    public List<String> getKeysInNamespace( String namespace )
    {
        return keyJsonValueStore.getKeysInNamespace( namespace );
    }

    @Override
    public void deleteNamespace( String namespace )
    {
        keyJsonValueStore.deleteKeysInNamespace( namespace );
    }

    @Override
    public KeyJsonValue getKeyJsonValue( String namespace, String key )
    {
        return keyJsonValueStore.getKeyJsonValue( namespace, key );
    }

    @Override
    public int addKeyJsonValue( KeyJsonValue keyJsonValue )
    {
        return keyJsonValueStore.save( keyJsonValue );
    }

    @Override
    public void updateKeyJsonValue( KeyJsonValue keyJsonValue )
    {
        keyJsonValueStore.save( keyJsonValue );
    }

    @Override
    public void deleteKeyJsonValue( KeyJsonValue keyJsonValue )
    {
        keyJsonValueStore.delete( keyJsonValue );
    }
}
