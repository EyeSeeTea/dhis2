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

    /*
     * Get all dependency Uids from the dependency Objects for a MetaData Object
     */
    public List<String> getDependenciesUids( IdentifiableObject identifiableObject )
    {
        List<String> uids = new ArrayList<String>();

        List<Object> dependencyObjects = getAllDependencies( identifiableObject );
        List<Collection<? extends IdentifiableObject>> dependencyCollections = getAllDependencyCollections( identifiableObject );

        for ( Object dependencyObject : dependencyObjects )
        {
            String uid = ( ( IdentifiableObject ) dependencyObject ).getUid();
            uids.add( uid );
        }

        for ( Collection<? extends IdentifiableObject> collection : dependencyCollections )
        {
            for ( IdentifiableObject entry : collection )
            {
                String uid = entry.getUid();
                uids.add( uid );
            }
        }

        return uids;
    }

    /*
     * Get all dependency Objects for a MetaData Object
     */
    private List<Object> getAllDependencies( IdentifiableObject identifiableObject )
    {
        List<Object> dependencyObjects = new ArrayList<Object>();

        List<Field> fields = ReflectionUtils.getAllFields( identifiableObject.getClass() );
        List<Field> dependencyFields = getDependencyFields( fields );

        for ( Field field : dependencyFields )
        {
            Object dependencyObject = ReflectionUtils.invokeGetterMethod( field.getName(), identifiableObject );
            if ( dependencyObject != null )
            {
                dependencyObjects.add( dependencyObject );
            }
        }

        return dependencyObjects;
    }

    /*
     * Get all dependency Collections for a MetaData Object
     */
    private List<Collection<? extends IdentifiableObject>> getAllDependencyCollections( IdentifiableObject identifiableObject )
    {
        List<Collection<? extends IdentifiableObject>> dependencyCollections = new ArrayList<Collection<? extends IdentifiableObject>>();

        List<Field> fields = ReflectionUtils.getAllFields( identifiableObject.getClass() );
        List<Field> dependencyFieldsCollections = getDependencyFieldsCollections( fields );

        for ( Field field : dependencyFieldsCollections )
        {
            Collection<? extends IdentifiableObject> dependencyCollection = ReflectionUtils.invokeGetterMethod( field.getName(), identifiableObject );

            if ( dependencyCollection != null )
            {
                dependencyCollections.add( dependencyCollection );
            }
        }

        return dependencyCollections;
    }

    /*
     * Get all Fields that contain a dependency to other MetaData types
     */
    private List<Field> getDependencyFields( List<Field> fields )
    {
        List<Field> dependencyFields = new ArrayList<Field>();

        for ( Field field : fields )
        {
            if ( ReflectionUtils.isType( field, IdentifiableObject.class ) )
            {
                for ( Map.Entry<Class<? extends IdentifiableObject>, String> entry : ExchangeClasses.getExportMap().entrySet() )
                {
                    if ( ReflectionUtils.isType( field, entry.getKey() ) )
                    {
                        log.info( "\nDEPENDENCY OBJECT FIELD: " + field );
                        dependencyFields.add( field );
                    }
                }
            }
        }

        return dependencyFields;
    }

    /*
     * Get all Fields that contain a dependency collection to other MetaData types
     */
    private List<Field> getDependencyFieldsCollections( List<Field> fields )
    {
        List<Field> dependencyFieldsCollections = new ArrayList<Field>();

        for ( Field field : fields )
        {
            if ( ReflectionUtils.isType( field, Collection.class ) )
            {
                for ( Map.Entry<Class<? extends IdentifiableObject>, String> entry : ExchangeClasses.getExportMap().entrySet() )
                {
                    if ( isGenericTypeOf( field, entry.getKey() ) )
                    {
                        log.info( "\nDEPENDENCY COLLECTION FIELD: " + field );
                        dependencyFieldsCollections.add( field );
                    }
                }
            }
        }

        return dependencyFieldsCollections;
    }

    /*
     * Check the Generic type of a Field
     */
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
