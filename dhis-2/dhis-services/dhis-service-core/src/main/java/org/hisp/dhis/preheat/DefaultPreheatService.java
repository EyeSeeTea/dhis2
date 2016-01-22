package org.hisp.dhis.preheat;

/*
 * Copyright (c) 2004-2016, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.schema.Property;
import org.hisp.dhis.schema.PropertyType;
import org.hisp.dhis.schema.Schema;
import org.hisp.dhis.schema.SchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Transactional
public class DefaultPreheatService implements PreheatService
{
    @Autowired
    private SchemaService schemaService;

    @Override
    public Preheat preheat( PreheatParams params )
    {
        return null;
    }

    @Override
    public void validate( PreheatParams params ) throws PreheatException
    {
        if ( PreheatMode.ALL == params.getPreheatMode() )
        {
            if ( params.getClasses().isEmpty() )
            {
                throw new PreheatException( "PreheatMode.ALL, but no classes was provided." );
            }
        }
        else if ( PreheatMode.REFERENCE == params.getPreheatMode() )
        {
            if ( params.getReferences().isEmpty() )
            {
                throw new PreheatException( "PreheatMode.REFERENCE, but no references was provided." );
            }
        }
        else
        {
            throw new PreheatException( "Invalid preheat mode." );
        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Map<Class<? extends IdentifiableObject>, Collection<String>> scanObjectForReferences( Object object, PreheatIdentifier identifier )
    {
        Map<Class<? extends IdentifiableObject>, Collection<String>> map = new HashMap<>();

        if ( object == null )
        {
            return map;
        }

        Schema schema = schemaService.getDynamicSchema( object.getClass() );
        List<Property> properties = schema.getProperties().stream()
            .filter( p -> p.isPersisted() && p.isOwner() && (PropertyType.REFERENCE == p.getPropertyType() || PropertyType.REFERENCE == p.getItemPropertyType()) )
            .collect( Collectors.toList() );

        properties.forEach( p -> {
            if ( !p.isCollection() )
            {
                Class<? extends IdentifiableObject> klass = (Class<? extends IdentifiableObject>) p.getKlass();
                if ( !map.containsKey( klass ) ) map.put( klass, new ArrayList<>() );
            }
            else
            {
                Class<? extends IdentifiableObject> klass = (Class<? extends IdentifiableObject>) p.getItemKlass();
                if ( !map.containsKey( klass ) ) map.put( klass, new ArrayList<>() );
            }
        } );

        return map;
    }
}
