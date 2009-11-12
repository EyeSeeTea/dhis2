package org.hisp.dhis.indicator;

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
import java.util.Date;
import java.util.Iterator;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.system.util.UUIdUtils;
import org.springframework.transaction.annotation.Transactional;

import static org.hisp.dhis.i18n.I18nUtils.*;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
@Transactional
public class DefaultIndicatorService
    implements IndicatorService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private IndicatorStore indicatorStore;

    public void setIndicatorStore( IndicatorStore indicatorStore )
    {
        this.indicatorStore = indicatorStore;
    }
    
    private GenericIdentifiableObjectStore<IndicatorType> indicatorTypeStore;
    
    public void setIndicatorTypeStore( GenericIdentifiableObjectStore<IndicatorType> indicatorTypeStore )
    {
        this.indicatorTypeStore = indicatorTypeStore;
    }

    private GenericIdentifiableObjectStore<IndicatorGroup> indicatorGroupStore;

    public void setIndicatorGroupStore( GenericIdentifiableObjectStore<IndicatorGroup> indicatorGroupStore )
    {
        this.indicatorGroupStore = indicatorGroupStore;
    }

    private GenericIdentifiableObjectStore<IndicatorGroupSet> indicatorGroupSetStore;

    public void setIndicatorGroupSetStore( GenericIdentifiableObjectStore<IndicatorGroupSet> indicatorGroupSetStore )
    {
        this.indicatorGroupSetStore = indicatorGroupSetStore;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // Indicator
    // -------------------------------------------------------------------------
    
    public int addIndicator( Indicator indicator )
    {
        if ( indicator.getUuid() == null )
        {
            indicator.setUuid( UUIdUtils.getUUId() );            
        }
        
        indicator.setLastUpdated( new Date() );
        
        int id = indicatorStore.addIndicator( indicator );
        
        i18nService.addObject( indicator );
        
        return id;
    }

    public void updateIndicator( Indicator indicator )
    {
        indicator.setLastUpdated( new Date() );
        
        indicatorStore.updateIndicator( indicator );
        
        i18nService.verify( indicator );
    }
    
    public void deleteIndicator( Indicator indicator )
    {
        i18nService.removeObject( indicator );
        
        indicatorStore.deleteIndicator( indicator );    
    }
    
    public Indicator getIndicator( int id )
    {
        return i18n( i18nService, indicatorStore.getIndicator( id ) );
    }
    
    public Indicator getIndicator( String uuid )
    {
        return i18n( i18nService, indicatorStore.getIndicator( uuid ) );
    }
    
    public Collection<Indicator> getAllIndicators()
    {
        return i18n( i18nService, indicatorStore.getAllIndicators() );
    }
    
    public Collection<Indicator> getIndicators( Collection<Integer> identifiers )
    {
        if ( identifiers == null )
        {
            return getAllIndicators();
        }
        
        Collection<Indicator> objects = new ArrayList<Indicator>();
        
        for ( Integer id : identifiers )
        {
            objects.add( getIndicator( id ) );
        }
        
        return objects;
    }
    
    public Indicator getIndicatorByName( String name )
    {
        return i18n( i18nService, indicatorStore.getIndicatorByName( name ) );
    }
    
    public Indicator getIndicatorByShortName( String shortName )
    {
        return i18n( i18nService, indicatorStore.getIndicatorByShortName( shortName ) );
    }
    
    public Indicator getIndicatorByAlternativeName( String alternativeName )
    {
        return i18n( i18nService, indicatorStore.getIndicatorByAlternativeName( alternativeName ) );
    }
    
    public Indicator getIndicatorByCode( String code )
    {
        return i18n( i18nService, indicatorStore.getIndicatorByCode( code ) );
    }
    
    public Collection<Indicator> getIndicatorsWithGroupSets()
    {
        return i18n( i18nService, indicatorStore.getIndicatorsWithGroupSets() );
    }
    
    // -------------------------------------------------------------------------
    // IndicatorType
    // -------------------------------------------------------------------------

    public int addIndicatorType( IndicatorType indicatorType )
    {
        int id = indicatorTypeStore.save( indicatorType );
        
        i18nService.addObject( indicatorType );
        
        return id;
    }
    
    public void updateIndicatorType( IndicatorType indicatorType )
    {
        indicatorTypeStore.update( indicatorType );
        
        i18nService.verify( indicatorType );
    }

    public void deleteIndicatorType( IndicatorType indicatorType )
    {
        i18nService.removeObject( indicatorType );
        
        indicatorTypeStore.delete( indicatorType );
    }

    public IndicatorType getIndicatorType( int id )
    {
        return i18n( i18nService, indicatorTypeStore.get( id ) );
    }
    
    public Collection<IndicatorType> getIndicatorTypes( Collection<Integer> identifiers )
    {
        if ( identifiers == null )
        {
            return getAllIndicatorTypes();
        }
        
        Collection<IndicatorType> types = new ArrayList<IndicatorType>();
        
        for ( Integer id : identifiers )
        {
            types.add( getIndicatorType( id ) );
        }
        
        return types;
    }
    
    public Collection<IndicatorType> getAllIndicatorTypes()
    {
        return i18n( i18nService, indicatorTypeStore.getAll() );
    }
    
    public IndicatorType getIndicatorTypeByName( String name )
    {
        return i18n( i18nService, indicatorTypeStore.getByName( name ) );
    }    

    // -------------------------------------------------------------------------
    // IndicatorGroup
    // -------------------------------------------------------------------------

    public int addIndicatorGroup( IndicatorGroup indicatorGroup )
    {
        if ( indicatorGroup.getUuid() == null )
        {
            indicatorGroup.setUuid( UUIdUtils.getUUId() );
        }
        
        int id = indicatorGroupStore.save( indicatorGroup );
        
        i18nService.addObject( indicatorGroup );
        
        return id;
    }
    
    public void updateIndicatorGroup( IndicatorGroup indicatorGroup )
    {
        indicatorGroupStore.update( indicatorGroup );
        
        i18nService.verify( indicatorGroup );
    }
    
    public void deleteIndicatorGroup( IndicatorGroup indicatorGroup )
    {
        i18nService.removeObject( indicatorGroup );
        
        indicatorGroupStore.delete( indicatorGroup );
    }
    
    public IndicatorGroup getIndicatorGroup( int id )
    {
        return i18n( i18nService, indicatorGroupStore.get( id ) );
    }
    
    public Collection<IndicatorGroup> getIndicatorGroups( Collection<Integer> identifiers )
    {
        if ( identifiers == null )
        {
            return getAllIndicatorGroups();
        }
        
        Collection<IndicatorGroup> groups = new ArrayList<IndicatorGroup>();
        
        for ( Integer id : identifiers )
        {
            groups.add( getIndicatorGroup( id ) );
        }
        
        return groups;
    }
    
    public IndicatorGroup getIndicatorGroup( String uuid )
    {
        return i18n( i18nService, indicatorGroupStore.getByUuid( uuid ) );
    }
    
    public Collection<IndicatorGroup> getAllIndicatorGroups()
    {
        return i18n( i18nService, indicatorGroupStore.getAll() );
    }
    
    public IndicatorGroup getIndicatorGroupByName( String name )
    {
        return i18n( i18nService, indicatorGroupStore.getByName( name ) );
    }

    public Collection<IndicatorGroup> getGroupsContainingIndicator( Indicator indicator )
    {
        Collection<IndicatorGroup> groups = getAllIndicatorGroups();
        
        Iterator<IndicatorGroup> iterator = groups.iterator();
        
        while ( iterator.hasNext() )
        {
            IndicatorGroup group = iterator.next();
            
            if ( !group.getMembers().contains( indicator ) )
            {
                iterator.remove();
            }
        }
        
        return groups;       
    }

    // -------------------------------------------------------------------------
    // IndicatorGroupSet
    // -------------------------------------------------------------------------

    public int addIndicatorGroupSet( IndicatorGroupSet groupSet )
    {
        int id = indicatorGroupSetStore.save( groupSet );
        
        i18nService.addObject( groupSet );
        
        return id;
    }
    
    public void updateIndicatorGroupSet( IndicatorGroupSet groupSet )
    {
        indicatorGroupSetStore.update( groupSet );
        
        i18nService.verify( groupSet );
    }
    
    public void deleteIndicatorGroupSet( IndicatorGroupSet groupSet )
    {
        i18nService.removeObject( groupSet );
        
        indicatorGroupSetStore.delete( groupSet );
    }
    
    public IndicatorGroupSet getIndicatorGroupSet( int id )
    {
        return i18n( i18nService, indicatorGroupSetStore.get( id ) );
    }

    public IndicatorGroupSet getIndicatorGroupSetByName( String name )
    {
        return i18n( i18nService, indicatorGroupSetStore.getByName( name ) );
    }
    
    public Collection<IndicatorGroupSet> getAllIndicatorGroupSets()
    {
        return i18n( i18nService, indicatorGroupSetStore.getAll() );
    }
    
    public Collection<IndicatorGroupSet> getIndicatorGroupSets( Collection<Integer> identifiers )
    {
        if ( identifiers == null )
        {
            return getAllIndicatorGroupSets();
        }
        
        Collection<IndicatorGroupSet> groupSets = new ArrayList<IndicatorGroupSet>();
        
        for ( Integer id : identifiers )
        {
            groupSets.add( getIndicatorGroupSet( id ) );
        }
        
        return groupSets;       
    }
}
