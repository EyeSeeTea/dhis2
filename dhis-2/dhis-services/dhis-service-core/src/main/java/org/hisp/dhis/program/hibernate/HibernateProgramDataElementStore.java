package org.hisp.dhis.program.hibernate;

import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramDataElement;
import org.hisp.dhis.program.ProgramDataElementStore;

public class HibernateProgramDataElementStore
    extends HibernateIdentifiableObjectStore<ProgramDataElement>
        implements ProgramDataElementStore
{
    public ProgramDataElement get( Program program, DataElement dataElement )
    {
        return (ProgramDataElement) getCriteria( 
            Restrictions.eq( "program", program ),
            Restrictions.eq( "dataElement", dataElement ) ).uniqueResult();
    }
}
