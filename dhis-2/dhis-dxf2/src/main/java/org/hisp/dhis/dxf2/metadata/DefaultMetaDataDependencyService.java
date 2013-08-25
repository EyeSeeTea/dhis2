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
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.system.util.ReflectionUtils;
import org.hisp.dhis.validation.ValidationRule;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ovidiu Rosu <rosu.ovi@gmail.com>
 */
public class DefaultMetaDataDependencyService
        implements MetaDataDependencyService
{
    private static final Log log = LogFactory.getLog( DefaultMetaDataDependencyService.class );

    //-------------------------------------------------------------------------------------------------------
    // Dependencies
    //-------------------------------------------------------------------------------------------------------

    @Autowired
    private IdentifiableObjectManager manager;

    //--------------------------------------------------------------------------
    // Get MetaData dependencies
    //--------------------------------------------------------------------------

    @Override
    public Set<IdentifiableObject> getDependencySet( IdentifiableObject identifiableObject )
    {
        Set<IdentifiableObject> dependencySet = new HashSet<IdentifiableObject>();
        List<IdentifiableObject> dependencies = computeAllDependencies( identifiableObject );

        for ( IdentifiableObject dependency : dependencies )
        {
            dependencySet.add( dependency );
        }

        if ( isSpecialCase( identifiableObject ) )
        {
            dependencySet.addAll( computeSpecialDependencyCase( identifiableObject ) );
        }

        return dependencySet;
    }

    @Override
    public Set<IdentifiableObject> getDependencySet( Collection<? extends IdentifiableObject> identifiableObjects )
    {
        Set<IdentifiableObject> dependencySet = new HashSet<IdentifiableObject>();

        for ( IdentifiableObject identifiableObject : identifiableObjects )
        {
            List<IdentifiableObject> dependencies = computeAllDependencies( identifiableObject );

            for ( IdentifiableObject dependency : dependencies )
            {
                dependencySet.add( dependency );
            }

            if ( isSpecialCase( identifiableObject ) )
            {
                dependencySet.addAll( computeSpecialDependencyCase( identifiableObject ) );
            }
        }

        return dependencySet;
    }

    //--------------------------------------------------------------------------
    // Compute dependencies
    //--------------------------------------------------------------------------

    private List<IdentifiableObject> computeAllDependencies( IdentifiableObject identifiableObject )
    {
        List<IdentifiableObject> finalDependencies = new ArrayList<IdentifiableObject>();
        List<IdentifiableObject> dependencies = getAllDependencies( identifiableObject );

        if ( dependencies.isEmpty() )
        {
            return finalDependencies;
        } else
        {
            for ( IdentifiableObject dependency : dependencies )
            {
                log.info( "[ COMPUTING DEPENDENCY ] : " + dependency.getName() );

                List<IdentifiableObject> computedDependencies = computeAllDependencies( dependency );

                finalDependencies.add( dependency );
                finalDependencies.addAll( computedDependencies );
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

                if ( dependencyCollection != null && isExportView( getterMethod ) )
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

                if ( dependencyObject != null && isExportView( getterMethod ) )
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

    private Set<IdentifiableObject> computeSpecialDependencyCase( IdentifiableObject identifiableObject )
    {
        Set<IdentifiableObject> resultSet = new HashSet<IdentifiableObject>();

        if ( identifiableObject instanceof Indicator )
        {
            List<String> expressions = new ArrayList<String>();

            expressions.add( ( String ) ReflectionUtils.invokeGetterMethod( "numerator", identifiableObject ) );
            expressions.add( ( String ) ReflectionUtils.invokeGetterMethod( "denominator", identifiableObject ) );

            Set<String> uidsSet = getUidsByPattern( expressions, Pattern.compile( "\\w+" ) );
            Collection<? extends IdentifiableObject> dataElements = manager.getByUid( DataElement.class, uidsSet );

            resultSet.addAll( dataElements );

            for ( IdentifiableObject dataElement : dataElements )
            {
                List<IdentifiableObject> dependencies = computeAllDependencies( dataElement );

                for ( IdentifiableObject dependency : dependencies )
                {
                    resultSet.add( dependency );
                }
            }

            return resultSet;
        } else if ( identifiableObject instanceof ValidationRule )
        {
            List<String> expressions = new ArrayList<String>();

            Expression leftSide = ReflectionUtils.invokeGetterMethod( "leftSide", identifiableObject );
            Expression rightSide = ReflectionUtils.invokeGetterMethod( "leftSide", identifiableObject );
            expressions.add( ( String ) ReflectionUtils.invokeGetterMethod( "expression", leftSide ) );
            expressions.add( ( String ) ReflectionUtils.invokeGetterMethod( "expression", rightSide ) );

            Set<String> uidsSet = getUidsByPattern( expressions, Pattern.compile( "\\w+" ) );
            Collection<? extends IdentifiableObject> dataElements = manager.getByUid( DataElement.class, uidsSet );

            resultSet.addAll( dataElements );

            for ( IdentifiableObject dataElement : dataElements )
            {
                List<IdentifiableObject> dependencies = computeAllDependencies( dataElement );

                for ( IdentifiableObject dependency : dependencies )
                {
                    resultSet.add( dependency );
                }
            }

            return resultSet;
        } else
        {
            return resultSet;
        }
    }

    //--------------------------------------------------------------------------
    // Utils
    //--------------------------------------------------------------------------

    public boolean isExportView( Method method )
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

    public Set<String> getUidsByPattern( List<String> expressions, Pattern pattern )
    {
        Set<String> uids = new HashSet<String>();

        for ( String expression : expressions )
        {
            Matcher matcher = pattern.matcher( expression );

            while ( matcher.find() )
            {
                uids.add( matcher.group() );
            }
        }

        return uids;
    }
}
