package org.hisp.dhis.keyjsonvalue.hibernate;

import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.keyjsonvalue.KeyJsonValue;
import org.hisp.dhis.keyjsonvalue.KeyJsonValueStore;
import org.hisp.dhis.query.Restriction;

import java.util.List;

/**
 * Created by Stian Sandvold on 27.09.2015.
 */
public class HibernateKeyJsonValueStore
    extends HibernateIdentifiableObjectStore<KeyJsonValue>
    implements KeyJsonValueStore
{

    @Override
    public KeyJsonValue getKeyJsonValue( String namespace, String key )
    {
        return (KeyJsonValue) getCriteria( Restrictions.eq( "namespace", namespace), Restrictions.eq( "key", key ) ).uniqueResult();
    }

    @Override
    public List<KeyJsonValue> getKeyJsonValueByNamespace( String namespace )
    {
        return getCriteria( Restrictions.eq( "namespace", namespace ) ).list();
    }

    @Override
    public List<KeyJsonValue> getNamespaces()
    {
        return null;
    }
}
