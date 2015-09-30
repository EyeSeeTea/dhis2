package org.hisp.dhis.keyjsonvalue.hibernate;

import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.keyjsonvalue.KeyJsonValue;
import org.hisp.dhis.keyjsonvalue.KeyJsonValueStore;

import java.util.List;

/**
 * Created by Stian Sandvold on 27.09.2015.
 */
public class HibernateKeyJsonValueStore
    extends HibernateIdentifiableObjectStore<KeyJsonValue>
    implements KeyJsonValueStore
{
    @Override
    public List<String> getNamespaces()
    {
        return getQuery( "SELECT distinct namespace FROM org.hisp.dhis.keyjsonvalue.KeyJsonValue" ).list();
    }

    @Override
    public List<String> getKeysInNamespace( String namespace )
    {
        return getQuery(
            "SELECT distinct key FROM org.hisp.dhis.keyjsonvalue.KeyJsonValue WHERE namespace LIKE '" + namespace +
                "'" ).list();
    }

    @Override
    public void deleteKeysInNamespace( String namespace )
    {
        getCriteria( Restrictions.eq( "namespace", namespace ) ).list().forEach( o -> delete( (KeyJsonValue) o ) );
    }

    @Override
    public KeyJsonValue getKeyJsonValue( String namespace, String key )
    {
        return (KeyJsonValue) getCriteria( Restrictions.eq( "namespace", namespace ), Restrictions.eq( "key", key ) )
            .uniqueResult();
    }
}
