package org.hisp.dhis.dataadmin.action.resourceviewer;

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

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.hisp.dhis.resourceviewer.ResourceViewerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 * @since 2010-07-07
 */
@Transactional
public class DropAllResourceViewerTablesAction
    extends ActionSupport
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private StatementManager statementManager;

    private ResourceViewerService resourceViewerService;

    public void setResourceViewerService( ResourceViewerService resourceViewerService )
    {
        this.resourceViewerService = resourceViewerService;
    }


    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Set<String> viewerNames = new HashSet<String>( resourceViewerService.getAllResourceViewerNames() );

        if ( viewerNames != null )
        {
            System.out.println( "viewerNames :: " + viewerNames );
        }

        for ( String viewerName : viewerNames )
        {
            System.out.println( "\n\n viewer name = " + viewerName );

            this.dropView( viewerName );
        }

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supporting methods
    // -------------------------------------------------------------------------

    private void dropView( String view )
    {
        final StatementHolder holder = statementManager.getHolder();

        try
        {
            holder.getStatement().executeUpdate( "DROP VIEW IF EXISTS " + view );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to drop view: " + view, ex );
        }
        finally
        {
            holder.close();
        }
    }
}
