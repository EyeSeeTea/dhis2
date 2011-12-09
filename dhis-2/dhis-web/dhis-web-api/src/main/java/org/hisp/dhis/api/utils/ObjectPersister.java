package org.hisp.dhis.api.utils;

import org.hisp.dhis.dataelement.DataElement;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public interface ObjectPersister
{
    public void persistDataElement( DataElement dataElement );
}
