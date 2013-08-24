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
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.system.util.ReflectionUtils;
import org.hisp.dhis.validation.ValidationRule;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Ovidiu Rosu <rosu.ovi@gmail.com>
 */
public class DefaultMetaDataDependenciesService
        implements MetaDataDependenciesService
{
    private static final Log log = LogFactory.getLog( DefaultMetaDataDependenciesService.class );

    //-------------------------------------------------------------------------------------------------------
    // Dependencies
    //-------------------------------------------------------------------------------------------------------

    @Autowired
    private ExportUtils exportUtils;

    //--------------------------------------------------------------------------
    // Get MetaData dependencies
    //--------------------------------------------------------------------------

    @Override
    public Map<Class<? extends IdentifiableObject>, Set<String>> getAllDependencyUids( IdentifiableObject identifiableObject )
    {
        Map<Class<? extends IdentifiableObject>, Set<String>> dependencyUidsMap = new HashMap<Class<? extends IdentifiableObject>, Set<String>>();

        for ( IdentifiableObject dependency : computeAllDependencies( identifiableObject ) )
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

        if ( isSpecialCase( identifiableObject ) )
        {
            dependencyUidsMap = exportUtils.mergeDependencyMaps( dependencyUidsMap, computeSpecialDependencyCase( identifiableObject ) );
        }

        return dependencyUidsMap;
    }

    @Override
    public Map<Class<? extends IdentifiableObject>, Set<String>> getAllDependencyUids( List<? extends IdentifiableObject> identifiableObjects )
    {
        Map<Class<? extends IdentifiableObject>, Set<String>> dependencyUidsMap = new HashMap<Class<? extends IdentifiableObject>, Set<String>>();

        for ( IdentifiableObject identifiableObject : identifiableObjects )
        {
            for ( IdentifiableObject dependency : computeAllDependencies( identifiableObject ) )
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

            if ( isSpecialCase( identifiableObject ) )
            {
                dependencyUidsMap = exportUtils.mergeDependencyMaps( dependencyUidsMap, computeSpecialDependencyCase( identifiableObject ) );
            }
        }

        return dependencyUidsMap;
    }

    //--------------------------------------------------------------------------
    // Compute dependencies
    //--------------------------------------------------------------------------

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

    private List<IdentifiableObject> getAllDependencies( IdentifiableObject identifiableObject )
    {
        List<IdentifiableObject> allDependencies = new ArrayList<IdentifiableObject>();
        List<Field> dependencyFields = getDependencyFields( ReflectionUtils.getAllFields( identifiableObject.getClass() ) );

        for ( Field dependencyField : dependencyFields )
        {
            if ( ReflectionUtils.isType( dependencyField, Collection.class ) )
            {
                Method getterMethod = ReflectionUtils.findGetterMethod( dependencyField.getName(), identifiableObject );
                Collection<IdentifiableObject> dependencyCollection = ReflectionUtils.invokeGetterMethod( dependencyField.getName(), identifiableObject );

                if ( dependencyCollection != null && exportUtils.isExportView( getterMethod ) )
                {
                    for ( IdentifiableObject dependencyEntry : dependencyCollection )
                    {
                        log.info( "[ DEPENDENCY COLLECTION ENTRY ] : " + dependencyEntry.getName() );

                        allDependencies.add( dependencyEntry );
                    }
                }
            } else
            {
                Method getterMethod = ReflectionUtils.findGetterMethod( dependencyField.getName(), identifiableObject );
                IdentifiableObject dependencyObject = ReflectionUtils.invokeGetterMethod( dependencyField.getName(), identifiableObject );

                if ( dependencyObject != null && exportUtils.isExportView( getterMethod ) )
                {
                    log.info( "[ DEPENDENCY OBJECT ] : " + dependencyObject.getName() );

                    allDependencies.add( dependencyObject );
                }
            }
        }

        return allDependencies;
    }

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
            } else if ( ReflectionUtils.isType( field, Collection.class ) )
            {
                for ( Map.Entry<Class<? extends IdentifiableObject>, String> entry : ExchangeClasses.getExportMap().entrySet() )
                {
                    if ( ReflectionUtils.isGenericTypeOf( field, entry.getKey() ) )
                    {
                        dependencyFields.add( field );
                    }
                }
            }
        }

        return dependencyFields;
    }

    //--------------------------------------------------------------------------
    // Compute special cases
    //--------------------------------------------------------------------------

    private boolean isSpecialCase( IdentifiableObject identifiableObject )
    {
        return ( identifiableObject instanceof Indicator || identifiableObject instanceof ValidationRule );
    }

    private Map<Class<? extends IdentifiableObject>, Set<String>> computeSpecialDependencyCase( IdentifiableObject identifiableObject )
    {
        Map<Class<? extends IdentifiableObject>, Set<String>> resultMap = new HashMap<Class<? extends IdentifiableObject>, Set<String>>();

        if ( identifiableObject instanceof Indicator )
        {
            List<String> expressions = new ArrayList<String>();

            expressions.add( ( String ) ReflectionUtils.invokeGetterMethod( "numerator", identifiableObject ) );
            expressions.add( ( String ) ReflectionUtils.invokeGetterMethod( "denominator", identifiableObject ) );

            Set<String> uidsSet = exportUtils.getUidsByPattern( expressions, Pattern.compile( "\\w+" ) );
            resultMap.put( DataElement.class, uidsSet );

            return resultMap;
        } else if ( identifiableObject instanceof ValidationRule )
        {
            List<String> expressions = new ArrayList<String>();

            Expression leftSide = ReflectionUtils.invokeGetterMethod( "leftSide", identifiableObject );
            Expression rightSide = ReflectionUtils.invokeGetterMethod( "leftSide", identifiableObject );
            expressions.add( ( String ) ReflectionUtils.invokeGetterMethod( "expression", leftSide ) );
            expressions.add( ( String ) ReflectionUtils.invokeGetterMethod( "expression", rightSide ) );

            Set<String> uidsSet = exportUtils.getUidsByPattern( expressions, Pattern.compile( "\\w+" ) );
            resultMap.put( DataElement.class, uidsSet );

            return resultMap;
        }

        return resultMap;
    }
}
