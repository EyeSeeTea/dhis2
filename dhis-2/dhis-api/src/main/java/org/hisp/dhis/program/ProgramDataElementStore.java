package org.hisp.dhis.program;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.dataelement.DataElement;

public interface ProgramDataElementStore
    extends GenericIdentifiableObjectStore<ProgramDataElement>
{
    ProgramDataElement get( Program program, DataElement dataElement );
}
