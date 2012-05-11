package org.hisp.dhis.reportsheet.cogroup.action;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.reportsheet.OptionComboAssociation;
import org.hisp.dhis.reportsheet.OptionComboAssociationService;
import org.hisp.dhis.reportsheet.action.ActionSupport;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */

public class UpdateOptionComboAssociationsAction
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    @Autowired
    private DataElementCategoryService categoryService;

    @Autowired
    private OptionComboAssociationService associationService;

    @Autowired
    private SelectionTreeManager selectionTreeManager;

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer optionComboId;

    public void setOptionComboId( Integer optionComboId )
    {
        this.optionComboId = optionComboId;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        DataElementCategoryOptionCombo optionCombo = categoryService.getDataElementCategoryOptionCombo( optionComboId );

        Set<OrganisationUnit> sources = new HashSet<OrganisationUnit>();

        if ( optionCombo != null )
        {
            for ( OptionComboAssociation association : associationService.getOptionComboAssociations( optionCombo ) )
            {
                sources.add( association.getSource() );
            }
        }

        Set<OrganisationUnit> cloneSources = new HashSet<OrganisationUnit>( sources );

        Collection<OrganisationUnit> selectedUnits = selectionTreeManager.getReloadedSelectedOrganisationUnits();

        sources.removeAll( selectedUnits );

        if ( sources != null && !sources.isEmpty() )
        {
            associationService.deleteOptionComboAssociations( sources, optionCombo );
        }

        selectedUnits.removeAll( cloneSources );

        for ( OrganisationUnit newSource : selectedUnits )
        {
            OptionComboAssociation association = new OptionComboAssociation( newSource, optionCombo );

            associationService.saveOptionComboAssociation( association );
        }

        sources = null;
        cloneSources = null;
        selectedUnits = null;

        message = i18n.getString( "update_associations_successful" );

        return SUCCESS;
    }
}
