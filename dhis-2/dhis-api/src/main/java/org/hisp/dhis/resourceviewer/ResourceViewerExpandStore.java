package org.hisp.dhis.resourceviewer;

import java.util.Collection;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 * @since 2010-07-06
 */
public interface ResourceViewerExpandStore
{
    String ID = ResourceViewerExpandStore.class.getName();

    // -------------------------------------------------------------------------
    // ResourceViewer Expanded
    // -------------------------------------------------------------------------

    Collection<String> getAllResourceViewerNames();

    void setUpDataResourceViewerTable( ResourceViewerTable resourceViewerTable, String viewerTableName );
}
