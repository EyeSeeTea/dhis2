package org.hisp.dhis.dataset.action.editor;

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
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.source.Source;

import com.opensymphony.xwork2.Action;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class SaveAssignMultiDataSetForOrgunitAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Collection<String> selectedDataSets = new HashSet<String>();

    public void setSelectedDataSets( Collection<String> selectedDataSets )
    {
        this.selectedDataSets = selectedDataSets;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Collection<OrganisationUnit> rootUnits = selectionTreeManager.getRootOrganisationUnits();

        Set<OrganisationUnit> unitsInTheTree = new HashSet<OrganisationUnit>();

        getUnitsInTheTree( rootUnits, unitsInTheTree );

        for ( String dataSetId : selectedDataSets )
        {
            DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( dataSetId ) );

            Set<Source> assignedSources = dataSet.getSources();

            Collection<OrganisationUnit> selectedOrganisationUnits = selectionTreeManager
                .getReloadedSelectedOrganisationUnits();

            assignedSources.addAll( convert( selectedOrganisationUnits ) );

            dataSet.setSources( assignedSources );
            
            dataSetService.updateDataSet( dataSet );
        }
        
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Set<Source> convert( Collection<OrganisationUnit> organisationUnits )
    {
        Set<Source> sources = new HashSet<Source>();

        sources.addAll( organisationUnits );

        return sources;
    }

    private void getUnitsInTheTree( Collection<OrganisationUnit> rootUnits, Set<OrganisationUnit> unitsInTheTree )
    {
        for ( OrganisationUnit root : rootUnits )
        {
            unitsInTheTree.add( root );
            getUnitsInTheTree( root.getChildren(), unitsInTheTree );
        }
    }
}
