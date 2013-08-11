package org.hisp.dhis.dxf2.metadata;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.system.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Ovidiu Rosu <rosu.ovi@gmail.com>
 */
public class MetaDataDependencies
{
    private static final Log log = LogFactory.getLog( MetaDataDependencies.class );

    private List<String> dependencyUids;

    // -------------------------------------------------------------------------
    // Getters and setters properties
    // -------------------------------------------------------------------------

    public List<String> getDependencyUids()
    {
        return dependencyUids;
    }

    public void setDependencyUids( List<String> dependencyUids )
    {
        this.dependencyUids = dependencyUids;
    }

    //--------------------------------------------------------------------------
    // Logic
    //--------------------------------------------------------------------------

    public List<String> getDependenciesUids( IdentifiableObject identifiableObject )
    {
        List<String> uids = new ArrayList<String>();

        List<Set<? extends IdentifiableObject>> dependencySets = getAllDependencies( identifiableObject );

        for ( Set<? extends IdentifiableObject> dependencySet : dependencySets )
        {
            if ( dependencySet.size() > 0 )
            {
                for ( IdentifiableObject object : dependencySet )
                {
                    uids.add( object.getUid() );
                }
            }
        }

        return uids;
    }

    private List<Set<? extends IdentifiableObject>> getAllDependencies( IdentifiableObject identifiableObject )
    {
        List<Set<? extends IdentifiableObject>> dependencySets = new ArrayList<Set<? extends IdentifiableObject>>();
        List<Field> fields = ReflectionUtils.getAllFields( identifiableObject.getClass() );
        List<Field> dependencyFields = getDependencyFields( fields );

        for ( Field field : dependencyFields )
        {
            Set<? extends IdentifiableObject> dependencySet = ReflectionUtils.invokeGetterMethod( field.getName(), identifiableObject );
            dependencySets.add( dependencySet );
        }

        return dependencySets;
    }

    private List<Field> getDependencyFields( List<Field> fields )
    {
        List<Field> dependencyFields = new ArrayList<Field>();

        for ( Field field : fields )
        {
            if ( ReflectionUtils.isType( field, Set.class ) )
            {
                for ( Map.Entry<Class<? extends IdentifiableObject>, String> entry : ExchangeClasses.getExportMap().entrySet() )
                {
                    if ( isGenericTypeOf( field, entry.getKey() ) )
                    {
                        dependencyFields.add( field );
                    }
                }
            }
        }

        return dependencyFields;
    }

    private boolean isGenericTypeOf( Field field, Class<?> clazz )
    {
        Type type = field.getGenericType();
        if ( type instanceof ParameterizedType )
        {
            ParameterizedType parameterizedType = ( ParameterizedType ) type;
            Type[] types = parameterizedType.getActualTypeArguments();

            for ( Type aType : types )
            {
                if ( aType.equals( clazz ) )
                {
                    return true;
                }
            }
        }

        return false;
    }
}
