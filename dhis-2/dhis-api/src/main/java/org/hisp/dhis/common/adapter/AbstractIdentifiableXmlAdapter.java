package org.hisp.dhis.common.adapter;

import org.apache.commons.lang.NotImplementedException;
import org.hisp.dhis.common.AbstractIdentifiableObject;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class AbstractIdentifiableXmlAdapter extends XmlAdapter<AbstractIdentifiableObject, DataElementCategoryCombo>
{
    @Override
    public DataElementCategoryCombo unmarshal( AbstractIdentifiableObject v ) throws Exception
    {
        throw new NotImplementedException();
    }

    @Override
    public AbstractIdentifiableObject marshal( DataElementCategoryCombo dataElementCategoryCombo ) throws Exception
    {
        return (AbstractIdentifiableObject) dataElementCategoryCombo;
    }
}
