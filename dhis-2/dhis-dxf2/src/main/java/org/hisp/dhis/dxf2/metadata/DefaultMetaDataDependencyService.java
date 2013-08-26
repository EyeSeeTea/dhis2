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
import org.hibernate.proxy.HibernateProxy;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.system.util.ReflectionUtils;
import org.hisp.dhis.validation.ValidationRule;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

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

    @Autowired
    private ExpressionService expressionService;

    //--------------------------------------------------------------------------
    // Get MetaData dependency Map
    //--------------------------------------------------------------------------

    public Map<String, List<IdentifiableObject>> getIdentifiableObjectMap( Map<String, List<String>> identifiableObjectUidMap )
    {
        Map<String, List<IdentifiableObject>> identifiableObjectMap = new HashMap<String, List<IdentifiableObject>>();

        for ( Map.Entry<String, List<String>> identifiableObjectUidEntry : identifiableObjectUidMap.entrySet() )
        {
            String className = identifiableObjectUidEntry.getKey();
            for ( Map.Entry<Class<? extends IdentifiableObject>, String> entry : ExchangeClasses.getExportMap().entrySet() )
            {
                if ( entry.getValue().equals( className ) )
                {
                    Class<? extends IdentifiableObject> identifiableObjectClass = entry.getKey();
                    Collection<? extends IdentifiableObject> identifiableObjects = manager.getByUid( identifiableObjectClass, identifiableObjectUidEntry.getValue() );

                    identifiableObjectMap.put( entry.getValue(), new ArrayList<IdentifiableObject>( identifiableObjects ) );
                }
            }
        }

        return identifiableObjectMap;
    }

    public Map<String, List<IdentifiableObject>> getAllIdentifiableObjectMap( Map<String, List<String>> identifiableObjectUidMap )
    {
        Map<String, List<IdentifiableObject>> allIdentifiableObjectMap = getIdentifiableObjectMap( identifiableObjectUidMap );
        Collection<IdentifiableObject> identifiableObjects = new HashSet<IdentifiableObject>();

        for ( Map.Entry<String, List<IdentifiableObject>> identifiableObjectEntry : allIdentifiableObjectMap.entrySet() )
        {
            identifiableObjects.addAll( identifiableObjectEntry.getValue() );
        }

        Set<IdentifiableObject> dependencySet = getDependencySet( identifiableObjects );

        for ( IdentifiableObject dependency : dependencySet )
        {
            for ( Map.Entry<Class<? extends IdentifiableObject>, String> entry : ExchangeClasses.getExportMap().entrySet() )
            {
                if ( entry.getKey().equals( dependency.getClass() ) )
                {
                    if ( allIdentifiableObjectMap.get( entry.getValue() ) != null )
                    {
                        allIdentifiableObjectMap.get( entry.getValue() ).add( dependency );
                    } else
                    {
                        List<IdentifiableObject> idObjects = new ArrayList<IdentifiableObject>();
                        idObjects.add( dependency );

                        allIdentifiableObjectMap.put( entry.getValue(), idObjects );
                    }
                }
            }
        }

        return allIdentifiableObjectMap;
    }

    //--------------------------------------------------------------------------
    // Get MetaData dependency Set
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
            dependencySet.addAll( getDependencySet( identifiableObject ) );
        }

        return dependencySet;
    }

    //--------------------------------------------------------------------------
    // Compute dependencies
    //--------------------------------------------------------------------------

    private List<IdentifiableObject> computeAllDependencies( IdentifiableObject identifiableObject )
    {
        List<IdentifiableObject> finalDependencies = new ArrayList<IdentifiableObject>();
        List<IdentifiableObject> allDependencies = getAllDependencies( identifiableObject );

        if ( allDependencies.isEmpty() )
        {
            return finalDependencies;
        } else
        {
            for ( IdentifiableObject dependency : allDependencies )
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
        List<Field> fields = ReflectionUtils.getAllFields( identifiableObject.getClass() );

        for ( Field field : fields )
        {
            for ( Map.Entry<Class<? extends IdentifiableObject>, String> entry : ExchangeClasses.getExportMap().entrySet() )
            {
                if ( ReflectionUtils.isType( field, entry.getKey() ) )
                {
                    Method getterMethod = ReflectionUtils.findGetterMethod( field.getName(), identifiableObject );
                    IdentifiableObject dependencyObject = ReflectionUtils.invokeGetterMethod( field.getName(), identifiableObject );

                    if ( dependencyObject != null && isExportView( getterMethod ) )
                    {
                        log.info( "[ DEPENDENCY OBJECT ] : " + dependencyObject.getName() );

                        if ( dependencyObject instanceof HibernateProxy )
                        {
                            Object hibernateProxy = ( ( HibernateProxy ) dependencyObject ).getHibernateLazyInitializer().getImplementation();
                            IdentifiableObject deProxyDependencyObject = ( IdentifiableObject ) hibernateProxy;

                            allDependencies.add( deProxyDependencyObject );
                        } else
                        {
                            allDependencies.add( dependencyObject );
                        }
                    }
                } else if ( ReflectionUtils.isCollection( field.getName(), identifiableObject, entry.getKey() ) )
                {
                    Method getterMethod = ReflectionUtils.findGetterMethod( field.getName(), identifiableObject );
                    Collection<IdentifiableObject> dependencyCollection = ReflectionUtils.invokeGetterMethod( field.getName(), identifiableObject );

                    if ( dependencyCollection != null && isExportView( getterMethod ) )
                    {
                        for ( IdentifiableObject dependencyElement : dependencyCollection )
                        {
                            log.info( "[ DEPENDENCY COLLECTION ELEMENT ] : " + dependencyElement.getName() );

                            if ( dependencyElement instanceof HibernateProxy )
                            {
                                Object hibernateProxy = ( ( HibernateProxy ) dependencyElement ).getHibernateLazyInitializer().getImplementation();
                                IdentifiableObject deProxyDependencyObject = ( IdentifiableObject ) hibernateProxy;

                                allDependencies.add( deProxyDependencyObject );
                            } else
                            {
                                allDependencies.add( dependencyElement );
                            }
                        }
                    }
                }
            }
        }

        return allDependencies;
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
            List<Indicator> indicators = new ArrayList<Indicator>();
            indicators.add( ( Indicator ) identifiableObject );
            Set<DataElement> dataElementSet = expressionService.getDataElementsInIndicators( indicators );

            resultSet.addAll( dataElementSet );
            resultSet.addAll( getDependencySet( resultSet ) );

            return resultSet;
        } else if ( identifiableObject instanceof ValidationRule )
        {
            Set<DataElement> dataElementSet = new HashSet<DataElement>();

            Expression leftSide = ReflectionUtils.invokeGetterMethod( "leftSide", identifiableObject );
            Expression rightSide = ReflectionUtils.invokeGetterMethod( "leftSide", identifiableObject );

            dataElementSet.addAll( expressionService.getDataElementsInExpression( leftSide.getExpression() ) );
            dataElementSet.addAll( expressionService.getDataElementsInExpression( rightSide.getExpression() ) );

            resultSet.addAll( dataElementSet );
            resultSet.addAll( getDependencySet( resultSet ) );

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
}
