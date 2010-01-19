package org.hisp.dhis.organisationunit;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import org.hisp.dhis.system.startup.AbstractStartupRoutine;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class OrganisationUnitGroupSetPopulator
    extends AbstractStartupRoutine
{
    public static final String NAME_TYPE = "Type";
    public static final String NAME_OWNERSHIP = "Ownership";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitGroupService organisationUnitGroupService;
    
    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    // -------------------------------------------------------------------------
    // AbstractStartupRoutine implementation
    // -------------------------------------------------------------------------

    public void execute()
        throws Exception
    {
        OrganisationUnitGroupSet type = organisationUnitGroupService.getOrganisationUnitGroupSetByName( NAME_TYPE );
        
        if ( type == null )
        {
            type = new OrganisationUnitGroupSet();
            type.setName( "Type" );
            type.setDescription( "Type of organisation unit, examples are PHU, chiefdom and district" );
            type.setCompulsory( true );
            
            organisationUnitGroupService.addOrganisationUnitGroupSet( type );
        }
        
        OrganisationUnitGroupSet ownership = organisationUnitGroupService.getOrganisationUnitGroupSetByName( NAME_OWNERSHIP );
        
        if ( ownership == null )
        {
            ownership = new OrganisationUnitGroupSet();
            ownership.setName( "Ownership" );
            ownership.setDescription( "Ownership of organisation unit, examples are private and public" );
            ownership.setCompulsory( true );
            
            organisationUnitGroupService.addOrganisationUnitGroupSet( ownership );
        }
    }
}
