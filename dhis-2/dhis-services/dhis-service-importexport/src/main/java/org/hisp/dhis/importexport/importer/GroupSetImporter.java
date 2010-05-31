package org.hisp.dhis.importexport.importer;

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

import org.amplecode.quick.BatchHandler;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.Importer;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;

/**
 * @author Lars Helge Overland
 * @version $Id: AbstractGroupSetConverter.java 4646 2008-02-26 14:54:29Z larshelg $
 */
public class GroupSetImporter
    extends AbstractImporter<OrganisationUnitGroupSet> implements Importer<OrganisationUnitGroupSet>
{
    protected OrganisationUnitGroupService organisationUnitGroupService;

    public GroupSetImporter()
    {
    }
    
    public GroupSetImporter( BatchHandler<OrganisationUnitGroupSet> batchHandler, OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.batchHandler = batchHandler;
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    @Override
    public void importObject( OrganisationUnitGroupSet object, ImportParams params )
    {
        NameMappingUtil.addGroupSetMapping( object.getId(), object.getName() );
        
        read( object, GroupMemberType.NONE, params );
    }

    @Override
    protected void importUnique( OrganisationUnitGroupSet object )
    {
        batchHandler.addObject( object );
    }

    @Override
    protected void importMatching( OrganisationUnitGroupSet object, OrganisationUnitGroupSet match )
    {
        match.setName( object.getName() );
        match.setDescription( object.getDescription() );
        match.setCompulsory( object.isCompulsory() );
        
        organisationUnitGroupService.updateOrganisationUnitGroupSet( match );
    }

    @Override
    protected OrganisationUnitGroupSet getMatching( OrganisationUnitGroupSet object )
    {
        return organisationUnitGroupService.getOrganisationUnitGroupSetByName( object.getName() );
    }

    @Override
    protected boolean isIdentical( OrganisationUnitGroupSet object, OrganisationUnitGroupSet existing )
    {
        if ( !object.getName().equals( existing.getName() ) )
        {
            return false;
        }
        if ( !object.getDescription().equals( existing.getDescription() ) )
        {
            return false;
        }
        if ( object.isCompulsory() != existing.isCompulsory() )
        {
            return false;
        }
        
        return true;
    }
}
