package org.hisp.dhis.sqlview;

import java.util.Collection;

/**
 * @author Dang Duy Hieu
 * @version $Id SqlViewExpandStore.java July 06, 2010$
 */
public interface SqlViewExpandStore
{
    String ID = SqlViewExpandStore.class.getName();

    // -------------------------------------------------------------------------
    // SqlView expanded
    // -------------------------------------------------------------------------

    Collection<String> getAllSqlViewNames();

    void setUpDataSqlViewTable( SqlViewTable sqlViewTable, String viewTableName );
}
