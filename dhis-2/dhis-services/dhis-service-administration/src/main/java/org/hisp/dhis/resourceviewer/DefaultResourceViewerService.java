package org.hisp.dhis.resourceviewer;

/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import java.util.Collection;
import java.util.regex.Pattern;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.resourceviewer.ResourceViewerService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 * @since 2010-07-06
 */
@Transactional
public class DefaultResourceViewerService
    implements ResourceViewerService
{

    private static final Pattern p = Pattern.compile( "\\W" );

    private static final String PREFIX_VIEWNAME = "__resourceviewer";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<ResourceViewer> resourceViewerStore;

    public void setResourceViewerStore( GenericIdentifiableObjectStore<ResourceViewer> resourceViewerStore )
    {
        this.resourceViewerStore = resourceViewerStore;
    }

    private ResourceViewerExpandStore resourceViewerExpandStore;

    public void setResourceViewerExpandStore( ResourceViewerExpandStore resourceViewerExpandStore )
    {
        this.resourceViewerExpandStore = resourceViewerExpandStore;
    }

    // -------------------------------------------------------------------------
    // Implement methods
    // -------------------------------------------------------------------------

    @Override
    public void deleteResourceViewer( ResourceViewer resourceViewerObject )
    {
        resourceViewerStore.delete( resourceViewerObject );
    }

    @Override
    public Collection<ResourceViewer> getAllResourceViewers()
    {
        return resourceViewerStore.getAll();
    }

    @Override
    public ResourceViewer getResourceViewer( int viewId )
    {
        return resourceViewerStore.get( viewId );
    }

    @Override
    public ResourceViewer getResourceViewer( String viewName )
    {
        return resourceViewerStore.getByName( viewName );
    }

    @Override
    public int saveResourceViewer( ResourceViewer resourceViewerObject )
    {
        return resourceViewerStore.save( resourceViewerObject );
    }

    @Override
    public void updateResourceViewer( ResourceViewer resourceViewerObject )
    {
        resourceViewerStore.update( resourceViewerObject );
    }

    @Override
    public String makeUpForQueryStatement( String query )
    {
        return query.replaceAll( ";\\s+", ";" ).replaceAll( ";+", ";" ).replaceAll( "\\s+", " " ).trim();
    }

    @Override
    public String setUpViewTableName( String input )
    {
        String[] items = p.split( input.trim().replaceAll( "_", "" ) );

        input = "";

        for ( String s : items )
        {
            input += (s.equals( "" ) == true) ? "" : ("_" + s);
        }

        return PREFIX_VIEWNAME + input;
    }

    // -------------------------------------------------------------------------
    // ResourceViewer Expanded
    // -------------------------------------------------------------------------

    @Override
    public Collection<String> getAllResourceViewerNames()
    {
        return resourceViewerExpandStore.getAllResourceViewerNames();
    }

    @Override
    public ResourceViewerTable getDataResourceViewerTable( String viewerTableName )
    {
        ResourceViewerTable resourceViewerTable = new ResourceViewerTable();
        
        resourceViewerExpandStore.setUpDataResourceViewerTable( resourceViewerTable, viewerTableName );
        
        return resourceViewerTable;
    }
}
