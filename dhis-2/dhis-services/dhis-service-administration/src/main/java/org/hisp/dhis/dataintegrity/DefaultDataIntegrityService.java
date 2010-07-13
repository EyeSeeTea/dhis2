package org.hisp.dhis.dataintegrity;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import static org.hisp.dhis.system.util.ListUtils.getDuplicates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataelement.comparator.DataElementNameComparator;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.comparator.IndicatorNameComparator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.filter.OrganisationUnitGroupWithoutGroupSetFilter;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleService;
import org.hisp.dhis.validation.comparator.ValidationRuleNameComparator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * @author Lars Helge Overland
 * @version $Id: DefaultDataIntegrityService.java 2010-03-18 11:52:20Z Chau Thu
 *          Tran $
 */
@Transactional
public class DefaultDataIntegrityService
    implements DataIntegrityService
{
    private static final String FORMULA_SEPARATOR = "#";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private ValidationRuleService validationRuleService;

    public void setValidationRuleService( ValidationRuleService validationRuleService )
    {
        this.validationRuleService = validationRuleService;
    }

    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    // -------------------------------------------------------------------------
    // DataIntegrityService implementation
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // DataElement
    // -------------------------------------------------------------------------

    public Collection<DataElement> getDataElementsWithoutDataSet()
    {
        return dataElementService.getDataElementsWithoutDataSets();
    }

    public Collection<DataElement> getDataElementsWithoutGroups()
    {
        return dataElementService.getDataElementsWithoutGroups();
    }

    public SortedMap<DataElement, Collection<DataSet>> getDataElementsAssignedToDataSetsWithDifferentPeriodTypes()
    {
        Collection<DataElement> dataElements = dataElementService.getAllDataElements();

        Collection<DataSet> dataSets = dataSetService.getAllDataSets();

        SortedMap<DataElement, Collection<DataSet>> targets = new TreeMap<DataElement, Collection<DataSet>>(
            new DataElementNameComparator() );

        for ( DataElement element : dataElements )
        {
            final Set<PeriodType> targetPeriodTypes = new HashSet<PeriodType>();
            final Collection<DataSet> targetDataSets = new HashSet<DataSet>();

            for ( DataSet dataSet : dataSets )
            {
                if ( dataSet.getDataElements().contains( element ) )
                {
                    targetPeriodTypes.add( dataSet.getPeriodType() );
                    targetDataSets.add( dataSet );
                }
            }

            if ( targetPeriodTypes.size() > 1 )
            {
                targets.put( element, targetDataSets );
            }
        }

        return targets;
    }

    @Override
    public Collection<DataElement> getDataElementsViolatingCompulsoryGroupSets()
    {

        Collection<DataElementGroupSet> groupSets = dataElementService.getAllDataElementGroupSets();

        Collection<DataElement> dataElements = dataElementService.getAllDataElements();

        Set<DataElement> targets = new HashSet<DataElement>();

        for ( DataElement de : dataElements )
        {
            for ( DataElementGroupSet groupSet : groupSets )
            {
                if ( !CollectionUtils.containsAny( groupSet.getMembers(), de.getGroups() ) )
                {
                    targets.add( de );
                }
            }
        }

        return targets;

    }

    @Override
    public Collection<DataElement> getDataElementsViolatingExclusiveGroupSets()
    {
        Collection<DataElementGroupSet> groupSets = dataElementService.getAllDataElementGroupSets();

        Set<DataElement> targets = new HashSet<DataElement>();

        for ( DataElementGroupSet groupSet : groupSets )
        {
            targets.addAll( getDuplicates( new ArrayList<DataElement>( groupSet.getDataElements() ),
                new DataElementNameComparator() ) );
        }

        return targets;
    }

    // -------------------------------------------------------------------------
    // DataSet
    // -------------------------------------------------------------------------

    public Collection<DataSet> getDataSetsNotAssignedToOrganisationUnits()
    {
        Collection<DataSet> dataSets = dataSetService.getAllDataSets();

        return FilterUtils.filter( dataSets, new Filter<DataSet>()
        {
            public boolean retain( DataSet object )
            {
                return object.getSources() == null || object.getSources().size() == 0;
            }
        } );
    }

    // -------------------------------------------------------------------------
    // Indicator
    // -------------------------------------------------------------------------

    public Collection<Collection<Indicator>> getIndicatorsWithIdenticalFormulas()
    {
        Hashtable<String, Indicator> formulas = new Hashtable<String, Indicator>();

        Hashtable<String, Collection<Indicator>> targets = new Hashtable<String, Collection<Indicator>>();

        Collection<Indicator> indicators = indicatorService.getAllIndicators();

        for ( Indicator indicator : indicators )
        {
            final String formula = indicator.getNumerator() + FORMULA_SEPARATOR + indicator.getDenominator();

            if ( formulas.containsKey( formula ) )
            {
                if ( targets.containsKey( formula ) )
                {
                    targets.get( formula ).add( indicator );
                }
                else
                {
                    Set<Indicator> elements = new HashSet<Indicator>();

                    elements.add( indicator );
                    elements.add( formulas.get( formula ) );

                    targets.put( formula, elements );
                    targets.get( formula ).add( indicator );
                }
            }
            else
            {
                formulas.put( formula, indicator );
            }
        }

        return targets.values();
    }

    public Collection<Indicator> getIndicatorsWithoutGroups()
    {
        return indicatorService.getIndicatorsWithoutGroups();
    }

    public SortedMap<Indicator, String> getInvalidIndicatorNumerators()
    {
        SortedMap<Indicator, String> invalids = new TreeMap<Indicator, String>( new IndicatorNameComparator() );

        for ( Indicator indicator : indicatorService.getAllIndicators() )
        {
            String result = expressionService.expressionIsValid( indicator.getNumerator() );

            if ( !result.equals( ExpressionService.VALID ) )
            {
                invalids.put( indicator, result );
            }
        }

        return invalids;
    }

    public SortedMap<Indicator, String> getInvalidIndicatorDenominators()
    {
        SortedMap<Indicator, String> invalids = new TreeMap<Indicator, String>( new IndicatorNameComparator() );

        for ( Indicator indicator : indicatorService.getAllIndicators() )
        {
            String result = expressionService.expressionIsValid( indicator.getDenominator() );

            if ( !result.equals( ExpressionService.VALID ) )
            {
                invalids.put( indicator, result );
            }
        }

        return invalids;
    }

    @Override
    public Collection<Indicator> getIndicatorsViolatingCompulsoryGroupSets()
    {
        Collection<IndicatorGroupSet> groupSets = indicatorService.getAllIndicatorGroupSets();

        Collection<Indicator> indicators = indicatorService.getAllIndicators();

        Set<Indicator> targets = new HashSet<Indicator>();

        for ( Indicator in :  indicators)
        {
            for ( IndicatorGroupSet groupSet : groupSets )
            {
                if ( !CollectionUtils.containsAny( groupSet.getMembers(), in.getGroups() ) )
                {
                    targets.add( in );
                }
            }
        }

        return targets;
    }

    @Override
    public Collection<Indicator> getIndicatorsViolatingExclusiveGroupSets()
    {
        Collection<IndicatorGroupSet> groupSets = indicatorService.getAllIndicatorGroupSets();

        Set<Indicator> targets = new HashSet<Indicator>();

        for ( IndicatorGroupSet groupSet : groupSets )
        {
            targets.addAll( getDuplicates( new ArrayList<Indicator>( groupSet.getIndicators() ),
                new IndicatorNameComparator() ) );
        }

        return targets;
    }

    // -------------------------------------------------------------------------
    // OrganisationUnit
    // -------------------------------------------------------------------------

    public Collection<OrganisationUnit> getOrganisationUnitsWithCyclicReferences()
    {
        Collection<OrganisationUnit> organisationUnits = organisationUnitService.getAllOrganisationUnits();

        Set<OrganisationUnit> cyclic = new HashSet<OrganisationUnit>();

        Set<OrganisationUnit> visited = new HashSet<OrganisationUnit>();

        OrganisationUnit parent = null;

        for ( OrganisationUnit unit : organisationUnits )
        {
            parent = unit;

            while ( (parent = parent.getParent()) != null )
            {
                if ( parent.equals( unit ) ) // Cyclic reference
                {
                    cyclic.add( unit );

                    break;
                }
                else if ( visited.contains( parent ) ) // Ends in cyclic
                // reference but not part
                // of it
                {
                    break;
                }
                else
                // Remember visited
                {
                    visited.add( parent );
                }
            }

            visited.clear();
        }

        return cyclic;
    }

    public Collection<OrganisationUnit> getOrphanedOrganisationUnits()
    {
        Collection<OrganisationUnit> organisationUnits = organisationUnitService.getAllOrganisationUnits();

        return FilterUtils.filter( organisationUnits, new Filter<OrganisationUnit>()
        {
            public boolean retain( OrganisationUnit object )
            {
                return object.getParent() == null && (object.getChildren() == null || object.getChildren().size() == 0);
            }
        } );
    }

    public Collection<OrganisationUnit> getOrganisationUnitsWithoutGroups()
    {
        return organisationUnitService.getOrganisationUnitsWithoutGroups();
    }

    public Collection<OrganisationUnit> getOrganisationUnitsViolatingCompulsoryGroupSets()
    {
        Collection<OrganisationUnitGroupSet> groupSets = organisationUnitGroupService
            .getCompulsoryOrganisationUnitGroupSets();

        Collection<OrganisationUnit> organisationUnits = organisationUnitService.getAllOrganisationUnits();

        Set<OrganisationUnit> targets = new HashSet<OrganisationUnit>();

        for ( OrganisationUnit unit : organisationUnits )
        {
            for ( OrganisationUnitGroupSet groupSet : groupSets )
            {
                if ( !CollectionUtils.containsAny( groupSet.getOrganisationUnitGroups(), unit.getGroups() ) )
                {
                    targets.add( unit );
                }
            }
        }

        return targets;
    }

    public Collection<OrganisationUnit> getOrganisationUnitsViolatingExclusiveGroupSets()
    {
        Collection<OrganisationUnitGroupSet> groupSets = organisationUnitGroupService.getAllOrganisationUnitGroupSets();

        Set<OrganisationUnit> targets = new HashSet<OrganisationUnit>();

        for ( OrganisationUnitGroupSet groupSet : groupSets )
        {
            targets.addAll( getDuplicates( new ArrayList<OrganisationUnit>( groupSet.getOrganisationUnits() ),
                new OrganisationUnitNameComparator() ) );
        }

        return targets;
    }

    public Collection<OrganisationUnitGroup> getOrganisationUnitGroupsWithoutGroupSets()
    {
        Collection<OrganisationUnitGroup> groups = organisationUnitGroupService.getAllOrganisationUnitGroups();

        return FilterUtils.filter( groups, new OrganisationUnitGroupWithoutGroupSetFilter() );
    }

    // -------------------------------------------------------------------------
    // ValidationRule
    // -------------------------------------------------------------------------

    public Collection<ValidationRule> getValidationRulesWithoutGroups()
    {
        Collection<ValidationRule> validationRules = validationRuleService.getAllValidationRules();

        return FilterUtils.filter( validationRules, new Filter<ValidationRule>()
        {
            public boolean retain( ValidationRule object )
            {
                return object.getGroups() == null || object.getGroups().size() == 0;
            }
        } );
    }

    public SortedMap<ValidationRule, String> getInvalidValidationRuleLeftSideExpressions()
    {
        SortedMap<ValidationRule, String> invalids = new TreeMap<ValidationRule, String>(
            new ValidationRuleNameComparator() );

        for ( ValidationRule rule : validationRuleService.getAllValidationRules() )
        {
            String result = expressionService.expressionIsValid( rule.getLeftSide().getExpression() );

            if ( !result.equals( ExpressionService.VALID ) )
            {
                invalids.put( rule, result );
            }
        }

        return invalids;
    }

    public SortedMap<ValidationRule, String> getInvalidValidationRuleRightSideExpressions()
    {
        SortedMap<ValidationRule, String> invalids = new TreeMap<ValidationRule, String>(
            new ValidationRuleNameComparator() );

        for ( ValidationRule rule : validationRuleService.getAllValidationRules() )
        {
            String result = expressionService.expressionIsValid( rule.getRightSide().getExpression() );

            if ( !result.equals( ExpressionService.VALID ) )
            {
                invalids.put( rule, result );
            }
        }

        return invalids;
    }

}
