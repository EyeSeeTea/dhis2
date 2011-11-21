package org.hisp.dhis.common.adapter;

import org.apache.commons.lang.NotImplementedException;
import org.hisp.dhis.common.AbstractNameableObject;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class AbstractNameableObjectXmlAdapter extends XmlAdapter<AbstractNameableObject, AbstractNameableObject>
{
    @Override
    public AbstractNameableObject unmarshal( AbstractNameableObject abstractNameableObject ) throws Exception
    {
        throw new NotImplementedException();
    }

    @Override
    public AbstractNameableObject marshal( AbstractNameableObject abstractNameableObject ) throws Exception
    {
        return (AbstractNameableObject) abstractNameableObject;
    }
}
