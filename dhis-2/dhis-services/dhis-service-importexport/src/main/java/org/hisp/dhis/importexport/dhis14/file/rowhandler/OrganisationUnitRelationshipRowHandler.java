package org.hisp.dhis.importexport.dhis14.file.rowhandler;

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

import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.hisp.dhis.importexport.AssociationType;
import org.hisp.dhis.importexport.GroupMemberAssociation;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.converter.AbstractOrganisationUnitRelationshipConverter;
import org.hisp.dhis.importexport.dhis14.object.Dhis14GroupMemberAssociation;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.ibatis.sqlmap.client.event.RowHandler;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class OrganisationUnitRelationshipRowHandler
    extends AbstractOrganisationUnitRelationshipConverter implements RowHandler
{
    private ImportParams params;

    private Map<Object, Integer> organisationUnitMapping;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public OrganisationUnitRelationshipRowHandler( BatchHandler<OrganisationUnit> organisationUnitBatchHandler,
        ImportObjectService importObjectService,
        OrganisationUnitService organisationUnitService,
        Map<Object, Integer> organisationUnitMapping,
        ImportParams params )
    {
        this.organisationUnitBatchHandler = organisationUnitBatchHandler;
        this.importObjectService = importObjectService;
        this.organisationUnitService = organisationUnitService;
        this.organisationUnitMapping = organisationUnitMapping;
        this.params = params;
    }
    
    // -------------------------------------------------------------------------
    // BatchRowHandler implementation
    // -------------------------------------------------------------------------

    public void handleRow( Object object )
    {
        final Dhis14GroupMemberAssociation dhis14Association = (Dhis14GroupMemberAssociation) object;

        final GroupMemberAssociation association = new GroupMemberAssociation( AssociationType.SET );
        
        final Integer parentId = organisationUnitMapping.get( dhis14Association.getGroupId() );
        final Integer childId = organisationUnitMapping.get( dhis14Association.getMemberId() );
        
        if ( parentId != null && childId != null && parentId != childId ) // Parent id equals child id for root
        {
            association.setGroupId( parentId );
            association.setMemberId( childId );
            
            read( association, GroupMemberType.ORGANISATIONUNITRELATIONSHIP, params );
        }
    }
}
