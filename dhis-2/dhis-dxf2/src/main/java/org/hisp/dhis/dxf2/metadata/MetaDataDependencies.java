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

import com.fasterxml.jackson.annotation.JsonView;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.system.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Ovidiu Rosu <rosu.ovi@gmail.com>
 */
public class MetaDataDependencies
{
    private static final Log log = LogFactory.getLog( MetaDataDependencies.class );

    //--------------------------------------------------------------------------
    // Get dependencies
    //--------------------------------------------------------------------------

    // Get all dependencies for an IdentifiableObject
    public Map<Class<? extends IdentifiableObject>, Set<String>> getAllDependencyUids( IdentifiableObject identifiableObject )
    {
        Map<Class<? extends IdentifiableObject>, Set<String>> dependencyUidsMap = new HashMap<Class<? extends IdentifiableObject>, Set<String>>();
        List<IdentifiableObject> dependencies = computeAllDependencies( identifiableObject );

        for ( IdentifiableObject dependency : dependencies )
        {
            Class<? extends IdentifiableObject> key = dependency.getClass();
            String uidValue = dependency.getUid();

            if ( dependencyUidsMap.containsKey( key ) )
            {
                dependencyUidsMap.get( key ).add( uidValue );
            } else
            {
                Set<String> dependencyUidsSet = new HashSet<String>();
                dependencyUidsSet.add( uidValue );

                dependencyUidsMap.put( key, dependencyUidsSet );
            }
        }

        return dependencyUidsMap;
    }

    // Get all dependencies for a List of IdentifiableObjects
    public Map<Class<? extends IdentifiableObject>, Set<String>> getAllDependencyUids( List<? extends IdentifiableObject> identifiableObjects )
    {
        Map<Class<? extends IdentifiableObject>, Set<String>> dependencyUidsMap = new HashMap<Class<? extends IdentifiableObject>, Set<String>>();

        for ( IdentifiableObject identifiableObject : identifiableObjects )
        {
            List<IdentifiableObject> dependencies = computeAllDependencies( identifiableObject );

            for ( IdentifiableObject dependency : dependencies )
            {
                Class<? extends IdentifiableObject> key = dependency.getClass();
                String uidValue = dependency.getUid();

                if ( dependencyUidsMap.containsKey( key ) )
                {
                    dependencyUidsMap.get( key ).add( uidValue );
                } else
                {
                    Set<String> dependencyUidsSet = new HashSet<String>();
                    dependencyUidsSet.add( uidValue );

                    dependencyUidsMap.put( key, dependencyUidsSet );
                }
            }
        }

        return dependencyUidsMap;
    }

    //--------------------------------------------------------------------------
    // Compute dependencies
    //--------------------------------------------------------------------------

    // Compute all dependencies for an IdentifiableObject
    private List<IdentifiableObject> computeAllDependencies( IdentifiableObject identifiableObject )
    {
        List<IdentifiableObject> finalDependencies = new ArrayList<IdentifiableObject>();
        List<IdentifiableObject> dependencies = getAllDependencies( identifiableObject );

        if ( dependencies.size() == 0 )
        {
            return finalDependencies;
        } else
        {
            for ( IdentifiableObject dependency : dependencies )
            {
                log.info( "[ COMPUTING Dependency ] : " + dependency.getName() );

                finalDependencies.add( dependency );
                finalDependencies.addAll( computeAllDependencies( dependency ) );
            }

            return finalDependencies;
        }
    }

    // Get all dependencies for an IdentifiableObject
    private List<IdentifiableObject> getAllDependencies( IdentifiableObject identifiableObject )
    {
        List<IdentifiableObject> allDependencies = new ArrayList<IdentifiableObject>();

        List<IdentifiableObject> dependencyObjects = getAllDependencyObjects( identifiableObject );
        List<Collection<IdentifiableObject>> dependencyCollections = getAllDependencyCollections( identifiableObject );

        allDependencies.addAll( dependencyObjects );

        for ( Collection<IdentifiableObject> dependencyCollection : dependencyCollections )
        {
            allDependencies.addAll( dependencyCollection );
        }

        return allDependencies;
    }

    // Get all Single dependency objects for an IdentifiableObject
    private List<IdentifiableObject> getAllDependencyObjects( IdentifiableObject identifiableObject )
    {
        List<IdentifiableObject> allDependencyObjects = new ArrayList<IdentifiableObject>();

        List<Field> fields = ReflectionUtils.getAllFields( identifiableObject.getClass() );
        List<Field> dependencyFields = getDependencyFields( fields );

        for ( Field dependencyField : dependencyFields )
        {
            Method getterMethod = ReflectionUtils.findGetterMethod( dependencyField.getName(), identifiableObject );
            IdentifiableObject dependencyObject = ReflectionUtils.invokeGetterMethod( dependencyField.getName(), identifiableObject );

            if ( dependencyObject != null && hasExportView( getterMethod ) )
            {
                log.info( "[ DEPENDENCY OBJECT ] : " + dependencyObject.getName() );

                allDependencyObjects.add( dependencyObject );
            }
        }

        return allDependencyObjects;
    }

    // Get all Collections dependencies for an IdentifiableObject
    private List<Collection<IdentifiableObject>> getAllDependencyCollections( IdentifiableObject identifiableObject )
    {
        List<Collection<IdentifiableObject>> allDependencyCollections = new ArrayList<Collection<IdentifiableObject>>();

        List<Field> fields = ReflectionUtils.getAllFields( identifiableObject.getClass() );
        List<Field> dependencyFieldCollections = getDependencyFieldCollections( fields );

        for ( Field dependencyFieldCollection : dependencyFieldCollections )
        {
            Method getterMethod = ReflectionUtils.findGetterMethod( dependencyFieldCollection.getName(), identifiableObject );
            Collection<IdentifiableObject> dependencyCollection = ReflectionUtils.invokeGetterMethod( dependencyFieldCollection.getName(), identifiableObject );

            if ( dependencyCollection != null && hasExportView( getterMethod ) )
            {
                for ( IdentifiableObject dependencyEntry : dependencyCollection )
                {
                    log.info( "[ DEPENDENCY COLLECTION ENTRY ] : " + dependencyEntry.getName() );
                }

                allDependencyCollections.add( dependencyCollection );
            }
        }

        return allDependencyCollections;
    }

    // Get all Fields that contain a dependency of other IdentifiableObjects
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
                        dependencyFields.add( field );
                    }
                }
            }
        }

        return dependencyFields;
    }

    // Get all Fields that contain a dependency Collection of other IdentifiableObjects
    private List<Field> getDependencyFieldCollections( List<Field> fields )
    {
        List<Field> dependencyFieldCollections = new ArrayList<Field>();

        for ( Field field : fields )
        {
            if ( ReflectionUtils.isType( field, Collection.class ) )
            {
                for ( Map.Entry<Class<? extends IdentifiableObject>, String> entry : ExchangeClasses.getExportMap().entrySet() )
                {
                    if ( ReflectionUtils.isGenericTypeOf( field, entry.getKey() ) )
                    {
                        dependencyFieldCollections.add( field );
                    }
                }
            }
        }

        return dependencyFieldCollections;
    }

    // Check if a Method has ExportView.class
    private boolean hasExportView( Method method )
    {
        if ( method.isAnnotationPresent( JsonView.class ) )
        {
            Class[] viewClasses = method.getAnnotation( JsonView.class ).value();

            for ( Class viewClass : viewClasses )
            {
                if ( viewClass.equals( ExportView.class ) )
                {
                    return true;
                }
            }
        }

        return false;
    }
}
