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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.system.util.UUIdUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: DefaultOrganisationUnitGroupService.java 5017 2008-04-25 09:19:19Z larshelg $
 */
@Transactional
public class DefaultOrganisationUnitGroupService
    implements OrganisationUnitGroupService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<OrganisationUnitGroup> organisationUnitGroupStore;

    public void setOrganisationUnitGroupStore( GenericIdentifiableObjectStore<OrganisationUnitGroup> organisationUnitGroupStore )
    {
        this.organisationUnitGroupStore = organisationUnitGroupStore;
    }
    
    private GenericIdentifiableObjectStore<OrganisationUnitGroupSet> organisationUnitGroupSetStore;

    public void setOrganisationUnitGroupSetStore( GenericIdentifiableObjectStore<OrganisationUnitGroupSet> organisationUnitGroupSetStore )
    {
        this.organisationUnitGroupSetStore = organisationUnitGroupSetStore;
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitGroup
    // -------------------------------------------------------------------------

    public int addOrganisationUnitGroup( OrganisationUnitGroup organisationUnitGroup )
    {
        if ( organisationUnitGroup.getUuid() == null )
        {
            organisationUnitGroup.setUuid( UUIdUtils.getUUId() );
        }
        
        return organisationUnitGroupStore.save( organisationUnitGroup );
    }

    public void updateOrganisationUnitGroup( OrganisationUnitGroup organisationUnitGroup )
    {
        organisationUnitGroupStore.update( organisationUnitGroup );
    }

    public void deleteOrganisationUnitGroup( OrganisationUnitGroup organisationUnitGroup )
    {
        organisationUnitGroupStore.delete( organisationUnitGroup );
    }

    public OrganisationUnitGroup getOrganisationUnitGroup( int id )
    {
        return organisationUnitGroupStore.get( id );
    }
    
    public Collection<OrganisationUnitGroup> getOrganisationUnitGroups( Collection<Integer> identifiers )
    {
        if ( identifiers == null )
        {
            return getAllOrganisationUnitGroups();
        }
        
        Collection<OrganisationUnitGroup> groups = new ArrayList<OrganisationUnitGroup>();
        
        for ( Integer id : identifiers )
        {
            groups.add( getOrganisationUnitGroup( id ) );
        }
        
        return groups;
    }

    public OrganisationUnitGroup getOrganisationUnitGroup( String uuid )
    {
        return organisationUnitGroupStore.getByUuid( uuid );
    }

    public OrganisationUnitGroup getOrganisationUnitGroupByName( String name )
    {
        return organisationUnitGroupStore.getByName( name );
    }

    public Collection<OrganisationUnitGroup> getAllOrganisationUnitGroups()
    {
        return organisationUnitGroupStore.getAll();
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitGroupSet
    // -------------------------------------------------------------------------

    public int addOrganisationUnitGroupSet( OrganisationUnitGroupSet organisationUnitGroupSet )
    {
        return organisationUnitGroupSetStore.save( organisationUnitGroupSet );
    }

    public void updateOrganisationUnitGroupSet( OrganisationUnitGroupSet organisationUnitGroupSet )
    {
        organisationUnitGroupSetStore.update( organisationUnitGroupSet );
    }

    public void deleteOrganisationUnitGroupSet( OrganisationUnitGroupSet organisationUnitGroupSet )
    {
        organisationUnitGroupSetStore.delete( organisationUnitGroupSet );
    }

    public OrganisationUnitGroupSet getOrganisationUnitGroupSet( int id )
    {
        return organisationUnitGroupSetStore.get( id );
    }
    
    public Collection<OrganisationUnitGroupSet> getOrganisationUnitGroupSets( Collection<Integer> identifiers )
    {
        if ( identifiers == null )
        {
            return getAllOrganisationUnitGroupSets();
        }
        
        Collection<OrganisationUnitGroupSet> groupSets = new ArrayList<OrganisationUnitGroupSet>();
        
        for ( Integer id : identifiers )
        {
            groupSets.add( getOrganisationUnitGroupSet( id ) );
        }
        
        return groupSets;
    }

    public OrganisationUnitGroupSet getOrganisationUnitGroupSetByName( String name )
    {
        return organisationUnitGroupSetStore.getByName( name );
    }

    public Collection<OrganisationUnitGroupSet> getAllOrganisationUnitGroupSets()
    {
        return organisationUnitGroupSetStore.getAll();
    }

    public Collection<OrganisationUnitGroupSet> getCompulsoryOrganisationUnitGroupSets()
    {
        Collection<OrganisationUnitGroupSet> groupSets = new ArrayList<OrganisationUnitGroupSet>();
        
        for ( OrganisationUnitGroupSet groupSet : getAllOrganisationUnitGroupSets() )
        {
            if ( groupSet.isCompulsory() )
            {
                groupSets.add( groupSet );
            }
        }
        
        return groupSets;
    }

    public Collection<OrganisationUnitGroupSet> getExclusiveOrganisationUnitGroupSets()
    {
        Collection<OrganisationUnitGroupSet> groupSets = new ArrayList<OrganisationUnitGroupSet>();
        
        for ( OrganisationUnitGroupSet groupSet : getAllOrganisationUnitGroupSets() )
        {
            if ( groupSet.isExclusive() )
            {
                groupSets.add( groupSet );
            }
        }
        
        return groupSets;
    }

    public Collection<OrganisationUnitGroupSet> getExclusiveOrganisationUnitGroupSetsContainingGroup(
        OrganisationUnitGroup organisationUnitGroup )
    {
        HashSet<OrganisationUnitGroupSet> result = new HashSet<OrganisationUnitGroupSet>();

        Collection<OrganisationUnitGroupSet> exclusiveGroupSets = getExclusiveOrganisationUnitGroupSets();

        for ( OrganisationUnitGroupSet groupSet : exclusiveGroupSets )
        {
            if ( groupSet.getOrganisationUnitGroups().contains( organisationUnitGroup ) )
            {
                result.add( groupSet );
            }
        }

        return result;
    }
    
    public OrganisationUnitGroup getOrganisationUnitGroup( OrganisationUnitGroupSet groupSet, OrganisationUnit unit )
    {
        for ( OrganisationUnitGroup group : groupSet.getOrganisationUnitGroups() )
        {
            if ( group.getMembers().contains( unit ) )
            {
                return group;
            }
        }
        
        return null;
    }
    
    public Collection<OrganisationUnitGroupSet> getCompulsoryOrganisationUnitGroupSetsNotAssignedTo( OrganisationUnit organisationUnit )
    {
        Collection<OrganisationUnitGroupSet> groupSets = new ArrayList<OrganisationUnitGroupSet>();
        
        for ( OrganisationUnitGroupSet groupSet : getCompulsoryOrganisationUnitGroupSets() )
        {
            if ( !groupSet.isMemberOfOrganisationUnitGroups( organisationUnit ) && groupSet.hasOrganisationUnitGroups() )
            {
                groupSets.add( groupSet );
            }
        }
        
        return groupSets;
    }
}
