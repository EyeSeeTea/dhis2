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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.system.util.UUIdUtils;
import org.springframework.transaction.annotation.Transactional;

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
        
        int id = indicatorStore.addIndicator( indicator );
        
        i18nService.addObject( indicator );
        
        return id;
    }

    public void updateIndicator( Indicator indicator )
    {
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
        return indicatorStore.getIndicator( id );
    }
    
    public Indicator getIndicator( String uuid )
    {
        return indicatorStore.getIndicator( uuid );
    }
    
    public Collection<Indicator> getAllIndicators()
    {
        return indicatorStore.getAllIndicators();
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
        return indicatorStore.getIndicatorByName( name );
    }
    
    public Indicator getIndicatorByShortName( String shortName )
    {
        return indicatorStore.getIndicatorByShortName( shortName );
    }
    
    public Indicator getIndicatorByAlternativeName( String alternativeName )
    {
        return indicatorStore.getIndicatorByAlternativeName( alternativeName );
    }
    
    public Indicator getIndicatorByCode( String code )
    {
        return indicatorStore.getIndicatorByCode( code );
    }
    
    public Map<Integer, Integer> getIndicatorFactorMap( Collection<Integer> identifiers )
    {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        
        for ( Integer id : identifiers )
        {
            map.put( id, getIndicator( id ).getIndicatorType().getFactor() );
        }
        
        return map;
    }

    // -------------------------------------------------------------------------
    // IndicatorType
    // -------------------------------------------------------------------------

    public int addIndicatorType( IndicatorType indicatorType )
    {
        int id = indicatorStore.addIndicatorType( indicatorType );
        
        i18nService.addObject( indicatorType );
        
        return id;
    }
    
    public void updateIndicatorType( IndicatorType indicatorType )
    {
        indicatorStore.updateIndicatorType( indicatorType );
        
        i18nService.verify( indicatorType );
    }

    public void deleteIndicatorType( IndicatorType indicatorType )
    {
        i18nService.removeObject( indicatorType );
        
        indicatorStore.deleteIndicatorType( indicatorType );
    }

    public IndicatorType getIndicatorType( int id )
    {
        return indicatorStore.getIndicatorType( id );
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
        return indicatorStore.getAllIndicatorTypes();
    }
    
    public IndicatorType getIndicatorTypeByName( String name )
    {
        return indicatorStore.getIndicatorTypeByName( name );
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
        
        int id = indicatorStore.addIndicatorGroup( indicatorGroup );
        
        i18nService.addObject( indicatorGroup );
        
        return id;
    }
    
    public void updateIndicatorGroup( IndicatorGroup indicatorGroup )
    {
        indicatorStore.updateIndicatorGroup( indicatorGroup );
        
        i18nService.verify( indicatorGroup );
    }
    
    public void deleteIndicatorGroup( IndicatorGroup indicatorGroup )
    {
        i18nService.removeObject( indicatorGroup );
        
        indicatorStore.deleteIndicatorGroup( indicatorGroup );
    }
    
    public IndicatorGroup getIndicatorGroup( int id )
    {
        return indicatorStore.getIndicatorGroup( id );
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
        return indicatorStore.getIndicatorGroup( uuid );
    }
    
    public Collection<IndicatorGroup> getAllIndicatorGroups()
    {
        return indicatorStore.getAllIndicatorGroups();
    }
    
    public IndicatorGroup getIndicatorGroupByName( String name )
    {
        return indicatorStore.getIndicatorGroupByName( name );
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
}
