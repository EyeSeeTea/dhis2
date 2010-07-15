package org.hisp.dhis.resourceviewer.hibernate;

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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.hibernate.SessionFactory;
import org.hisp.dhis.resourceviewer.ResourceViewerExpandStore;
import org.hisp.dhis.resourceviewer.ResourceViewerTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 * @since 2010-07-06
 */
public class HibernateResourceViewerExpandStore
    implements ResourceViewerExpandStore
{
    private static final String PREFIX_SELECT_QUERY = "SELECT * FROM ";

    private static final String PREFIX_VIEWNAME = "__resourceviewer" + "%";

    private static final String[] types = { "VIEW" };

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private StatementManager statementManager;

    // -------------------------------------------------------------------------
    // Implementing methods
    // -------------------------------------------------------------------------

    @Override
    public Collection<String> getAllResourceViewerNames()
    {
        Connection conn = getConnection();
        DatabaseMetaData mtdt;
        Set<String> viewersName = new HashSet<String>();

        try
        {
            mtdt = conn.getMetaData();

            ResultSet rs = mtdt.getTables( null, null, PREFIX_VIEWNAME, types );

            while ( rs.next() )
            {
                viewersName.add( rs.getString( "TABLE_NAME" ) );
            }

            conn.close();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
        }

        return viewersName;

    }

    @Override
    public void setUpDataResourceViewerTable( ResourceViewerTable resourceViewerTable, String viewerTableName )
    {
        final StatementHolder holder = statementManager.getHolder();

        ResultSet rs;
        try
        {
            rs = this.getScrollableResult( PREFIX_SELECT_QUERY + viewerTableName, holder );
        }
        catch ( SQLException e )
        {
            throw new RuntimeException( "Failed to get data from view " + PREFIX_SELECT_QUERY + viewerTableName, e );
        }
        
        resourceViewerTable.createViewerStructure( rs );
        resourceViewerTable.addRecord( rs );

        holder.close();
    }

    // -------------------------------------------------------------------------
    // Supporting methods
    // -------------------------------------------------------------------------

    private Connection getConnection()
    {
        return statementManager.getHolder().getConnection();
    }

    /**
     * Uses StatementManager to obtain a scrollable, read-only ResultSet based
     * on the query string.
     * 
     * @param sql the query
     * @param holder the StatementHolder object
     * @return null or the ResultSet
     */
    private ResultSet getScrollableResult( String sql, StatementHolder holder )
        throws SQLException
    {
        Connection con = holder.getConnection();
        Statement stm = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
        stm.execute( sql );
        return stm.getResultSet();
    }

}
