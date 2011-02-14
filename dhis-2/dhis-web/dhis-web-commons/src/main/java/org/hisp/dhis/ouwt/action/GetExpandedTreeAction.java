package org.hisp.dhis.ouwt.action;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.ouwt.manager.TreeStateManager;

import com.opensymphony.xwork2.Action;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: GetExpandedTreeAction.java 5282 2008-05-28 10:41:06Z larshelg $
 */
public class GetExpandedTreeAction
    implements Action
{
    private static final Log LOG = LogFactory.getLog( GetExpandedTreeAction.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManger )
    {
        this.selectionManager = selectionManger;
    }

    private TreeStateManager treeStateManager;

    public void setTreeStateManager( TreeStateManager treeStateManager )
    {
        this.treeStateManager = treeStateManager;
    }

    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------

    private Comparator<OrganisationUnit> organisationUnitComparator;

    public void setOrganisationUnitComparator( Comparator<OrganisationUnit> organisationUnitComparator )
    {
        this.organisationUnitComparator = organisationUnitComparator;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<OrganisationUnit> roots;

    public List<OrganisationUnit> getRoots()
    {
        return roots;
    }

    private List<OrganisationUnit> parents = new ArrayList<OrganisationUnit>();

    public List<OrganisationUnit> getParents()
    {
        return parents;
    }

    private Map<OrganisationUnit, List<OrganisationUnit>> childrenMap = new HashMap<OrganisationUnit, List<OrganisationUnit>>();

    public Map<OrganisationUnit, List<OrganisationUnit>> getChildrenMap()
    {
        return childrenMap;
    }

    private Collection<OrganisationUnit> selected;

    public Collection<OrganisationUnit> getSelected()
    {
        return selected;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Get the roots
        // ---------------------------------------------------------------------

        roots = new ArrayList<OrganisationUnit>( selectionManager.getRootOrganisationUnits() );

        Collections.sort( roots, organisationUnitComparator );

        // ---------------------------------------------------------------------
        // Get the children of the roots
        // ---------------------------------------------------------------------

        for ( OrganisationUnit root : roots )
        {
            boolean hasChildren = root.getChildren().size() > 0; // Dirty
                                                                 // loading

            LOG.debug( "OrganisationUnit " + root.getId() + " has children = " + hasChildren );

            if ( treeStateManager.isSubtreeExpanded( root ) )
            {
                addParentWithChildren( root );
            }
        }

        // ---------------------------------------------------------------------
        // Get the selected units
        // ---------------------------------------------------------------------

        selected = selectionManager.getSelectedOrganisationUnits();

        return SUCCESS;
    }

    private void addParentWithChildren( OrganisationUnit parent )
        throws Exception
    {
        List<OrganisationUnit> children = getChildren( parent );

        parents.add( parent );

        childrenMap.put( parent, children );

        for ( OrganisationUnit child : children )
        {
            boolean hasChildren = child.getChildren().size() > 0; // Dirty
                                                                  // loading

            LOG.debug( "OrganisationUnit " + child.getId() + " has children = " + hasChildren );

            if ( treeStateManager.isSubtreeExpanded( child ) )
            {
                addParentWithChildren( child );
            }
        }
    }

    private final List<OrganisationUnit> getChildren( OrganisationUnit parent )
    {
        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>( parent.getChildren() );

        Collections.sort( children, organisationUnitComparator );

        return children;
    }
}
