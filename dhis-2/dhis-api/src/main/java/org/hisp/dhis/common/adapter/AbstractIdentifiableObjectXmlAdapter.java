package org.hisp.dhis.common.adapter;

import org.apache.commons.lang.NotImplementedException;
import org.hisp.dhis.common.AbstractIdentifiableObject;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class AbstractIdentifiableObjectXmlAdapter extends XmlAdapter<AbstractIdentifiableObject, AbstractIdentifiableObject>
{
    @Override
    public AbstractIdentifiableObject unmarshal( AbstractIdentifiableObject abstractIdentifiableObject ) throws Exception
    {
        throw new NotImplementedException();
    }

    @Override
    public AbstractIdentifiableObject marshal( AbstractIdentifiableObject abstractIdentifiableObject ) throws Exception
    {
        return (AbstractIdentifiableObject) abstractIdentifiableObject;
    }
}
