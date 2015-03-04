package org.hisp.dhis.dataintegrity;

/*
 * Copyright (c) 2004-2015, University of Oslo
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

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hisp.dhis.common.IdentifiableObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Halvdan Hoem Grelland <halvdanhg@gmail.com>
 */
public class FlattenedDataIntegrityReport
{
    //-------------------------------------------------------------------------
    // Properties
    //-------------------------------------------------------------------------

    private List<String> dataElementsWithoutDataSet;

    private List<String> dataElementsWithoutGroups;

    private Map<String, Collection<String>> dataElementsAssignedToDataSetsWithDifferentPeriodTypes;

    private SortedMap<String, Collection<String>> dataElementsViolatingExclusiveGroupSets;

    private SortedMap<String, Collection<String>> dataElementsInDataSetNotInForm;

    private Map<String, Set<String>> categoryOptionCombosNotInDataElementCategoryCombo;

    private List<String> dataSetsNotAssignedToOrganisationUnits;

    private List<String> sectionsWithInvalidCategoryCombinations;

    private Collection<Collection<String>> indicatorsWithIdenticalFormulas;

    private List<String> indicatorsWithoutGroups;

    private Map<String, String> invalidIndicatorNumerators;

    private Map<String, String> invalidIndicatorDenominators;

    private SortedMap<String, Collection<String>> indicatorsViolatingExclusiveGroupSets;

    private List<String> duplicatePeriods;

    private List<String> organisationUnitsWithCyclicReferences;

    private List<String> orphanedOrganisationUnits;

    private List<String> organisationUnitsWithoutGroups;

    private SortedMap<String, Collection<String>> organisationUnitsViolatingExclusiveGroupSets;

    private List<String> organisationUnitGroupsWithoutGroupSets;

    private List<String> validationRulesWithoutGroups;

    private Map<String, String> invalidValidationRuleLeftSideExpressions;

    private Map<String, String> invalidValidationRuleRightSideExpressions;

    //-------------------------------------------------------------------------
    // Constructors
    //-------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    public FlattenedDataIntegrityReport( DataIntegrityReport report )
    {
        dataElementsWithoutDataSet
            = flattenList( ( List<IdentifiableObject> ) ( List<?> ) report.getDataElementsWithoutDataSet() );
        dataElementsWithoutGroups
            = flattenList( ( List<IdentifiableObject> ) ( List<?> ) report.getDataElementsWithoutGroups() );
        dataElementsAssignedToDataSetsWithDifferentPeriodTypes
            = flattenMapOfCollections( ( Map<IdentifiableObject, Collection<IdentifiableObject>> ) ( Map<?, ?> ) report.getDataElementsAssignedToDataSetsWithDifferentPeriodTypes() );
        dataElementsViolatingExclusiveGroupSets
            = flattenSortedMapOfCollections( ( SortedMap<IdentifiableObject, Collection<IdentifiableObject>> ) ( SortedMap<?, ?> ) report.getDataElementsViolatingExclusiveGroupSets() );
        dataElementsInDataSetNotInForm
            = flattenSortedMapOfCollections( ( SortedMap<IdentifiableObject, Collection<IdentifiableObject>> ) ( SortedMap<?, ?> ) report.getDataElementsInDataSetNotInForm() );
        categoryOptionCombosNotInDataElementCategoryCombo
            = flattenMapOfSets( (Map<IdentifiableObject, Set<IdentifiableObject>>) (Map<?, ?>) report.getCategoryOptionCombosNotInDataElementCategoryCombo() );
        dataSetsNotAssignedToOrganisationUnits
            = flattenList( ( List<IdentifiableObject> ) ( List<?> ) report.getDataSetsNotAssignedToOrganisationUnits() );
        sectionsWithInvalidCategoryCombinations
            = flattenList( ( List<IdentifiableObject> ) ( List<?> ) report.getSectionsWithInvalidCategoryCombinations() );
        indicatorsWithIdenticalFormulas
            = flattenCollectionOfCollections( ( Collection<Collection<IdentifiableObject>> ) ( Collection<?> ) report.getIndicatorsWithIdenticalFormulas() );
        invalidIndicatorNumerators
            = flattenMap( ( Map<IdentifiableObject, Object> ) ( Map<?, ?> ) report.getInvalidIndicatorNumerators() );
        invalidIndicatorDenominators
            = flattenMap( ( Map<IdentifiableObject, Object> ) ( Map<?, ?> ) report.getInvalidIndicatorDenominators() );
        indicatorsViolatingExclusiveGroupSets
            = flattenSortedMapOfCollections( ( SortedMap<IdentifiableObject, Collection<IdentifiableObject>> ) ( SortedMap<?, ?> ) report.getIndicatorsViolatingExclusiveGroupSets() );
        duplicatePeriods
            = flattenList( ( List<IdentifiableObject> ) ( List<?> ) report.getDuplicatePeriods() );
        organisationUnitsWithCyclicReferences
            = flattenList( ( List<IdentifiableObject> ) ( List<?> ) report.getOrganisationUnitsWithCyclicReferences() );
        orphanedOrganisationUnits
            = flattenList( ( List<IdentifiableObject> ) ( List<?> ) report.getOrphanedOrganisationUnits() );
        organisationUnitsWithoutGroups
            = flattenList( ( List<IdentifiableObject> ) ( List<?> ) report.getOrganisationUnitsWithoutGroups() );
        organisationUnitsViolatingExclusiveGroupSets
            = flattenSortedMapOfCollections( ( SortedMap<IdentifiableObject, Collection<IdentifiableObject>> ) ( SortedMap<?, ?> ) report.getOrganisationUnitsViolatingExclusiveGroupSets() );
        organisationUnitGroupsWithoutGroupSets
            = flattenList( ( List<IdentifiableObject> ) ( List<?> ) report.getOrganisationUnitGroupsWithoutGroupSets() );
        validationRulesWithoutGroups
            = flattenList( ( List<IdentifiableObject> ) ( List<?> ) report.getValidationRulesWithoutGroups() );
        invalidValidationRuleLeftSideExpressions
            = flattenMap( ( Map<IdentifiableObject, Object> ) ( Map<?, ?> ) report.getInvalidValidationRuleLeftSideExpressions() );
        invalidValidationRuleRightSideExpressions
            = flattenMap( ( Map<IdentifiableObject, Object> ) ( Map<?, ?> ) report.getInvalidValidationRuleRightSideExpressions() );
    }

    @SuppressWarnings( "unchecked" )
    private Map<String, String> flattenMap( Map<IdentifiableObject, Object> map )
    {
        HashMap newMap = Maps.newHashMap();

        for ( Map.Entry<IdentifiableObject, Object> entry : map.entrySet() )
        {
            newMap.put( entry.getKey().getDisplayName(), entry.getValue() );
        }

        return newMap;
    }

    private Collection<Collection<String>> flattenCollectionOfCollections( Collection<Collection<IdentifiableObject>> collectionOfCollections )
    {
        List<Collection<String>> newCollection = new ArrayList<>();

        for ( Collection<IdentifiableObject> collection : collectionOfCollections )
        {
            newCollection.add( flattenCollection( collection ) );
        }
        return newCollection;
    }

    private SortedMap<String, Collection<String>> flattenSortedMapOfCollections( SortedMap<IdentifiableObject, Collection<IdentifiableObject>> map )
    {
        SortedMap<String, Collection<String>> newMap = new TreeMap<String, Collection<String>>();

        for ( SortedMap.Entry<IdentifiableObject, Collection<IdentifiableObject>> entry : map.entrySet() )
        {
            newMap.put( entry.getKey().getDisplayName(), flattenCollection( entry.getValue() ) );
        }

        return newMap;
    }

    private Map<String, Set<String>> flattenMapOfSets( Map<IdentifiableObject, Set<IdentifiableObject>> map )
    {
        Map<String, Set<String>> newMap = Maps.newHashMap();

        for ( Map.Entry<IdentifiableObject, Set<IdentifiableObject>> entry : map.entrySet() )
        {
            newMap.put( entry.getKey().getDisplayName(), new HashSet<>( flattenCollection( entry.getValue() ) ) );
        }

        return  newMap;
    }

    private Map<String, Collection<String>> flattenMapOfCollections( Map<IdentifiableObject, Collection<IdentifiableObject>> map )
    {
        Map<String, Collection<String>> newMap = Maps.newHashMap();

        for ( Map.Entry<IdentifiableObject, Collection<IdentifiableObject>> entry : map.entrySet() )
        {
            newMap.put( entry.getKey().getDisplayName(), flattenCollection( entry.getValue() ) );
        }

        return newMap;
    }

    private Collection<String> flattenCollection( Collection<IdentifiableObject> collection )
    {
        return Collections2.transform( collection, new Function<IdentifiableObject, String>()
        {
            @Override
            public String apply( IdentifiableObject identifiableObject )
            {
                return identifiableObject.getDisplayName();
            }
        } );
    }

    private List<String> flattenList( List<IdentifiableObject> list )
    {
        return Lists.newArrayList( Lists.transform( list, new Function<IdentifiableObject, String>()
        {
            @Override
            public String apply( IdentifiableObject o )
            {
                return o.getDisplayName();
            }
        } ) );
    }

    //-------------------------------------------------------------------------
    // Getters and setters
    //-------------------------------------------------------------------------

    public List<String> getDataElementsWithoutDataSet()
    {
        return dataElementsWithoutDataSet;
    }

    public void setDataElementsWithoutDataSet( List<String> dataElementsWithoutDataSet )
    {
        this.dataElementsWithoutDataSet = dataElementsWithoutDataSet;
    }

    public List<String> getDataElementsWithoutGroups()
    {
        return dataElementsWithoutGroups;
    }

    public void setDataElementsWithoutGroups( List<String> dataElementsWithoutGroups )
    {
        this.dataElementsWithoutGroups = dataElementsWithoutGroups;
    }

    public Map<String, Collection<String>> getDataElementsAssignedToDataSetsWithDifferentPeriodTypes()
    {
        return dataElementsAssignedToDataSetsWithDifferentPeriodTypes;
    }

    public void setDataElementsAssignedToDataSetsWithDifferentPeriodTypes( Map<String, Collection<String>> dataElementsAssignedToDataSetsWithDifferentPeriodTypes )
    {
        this.dataElementsAssignedToDataSetsWithDifferentPeriodTypes = dataElementsAssignedToDataSetsWithDifferentPeriodTypes;
    }

    public SortedMap<String, Collection<String>> getDataElementsViolatingExclusiveGroupSets()
    {
        return dataElementsViolatingExclusiveGroupSets;
    }

    public void setDataElementsViolatingExclusiveGroupSets( SortedMap<String, Collection<String>> dataElementsViolatingExclusiveGroupSets )
    {
        this.dataElementsViolatingExclusiveGroupSets = dataElementsViolatingExclusiveGroupSets;
    }

    public SortedMap<String, Collection<String>> getDataElementsInDataSetNotInForm()
    {
        return dataElementsInDataSetNotInForm;
    }

    public void setDataElementsInDataSetNotInForm( SortedMap<String, Collection<String>> dataElementsInDataSetNotInForm )
    {
        this.dataElementsInDataSetNotInForm = dataElementsInDataSetNotInForm;
    }

    public Map<String, Set<String>> getCategoryOptionCombosNotInDataElementCategoryCombo()
    {
        return categoryOptionCombosNotInDataElementCategoryCombo;
    }

    public void setCategoryOptionCombosNotInDataElementCategoryCombo( Map<String, Set<String>> categoryOptionCombosNotInDataElementCategoryCombo )
    {
        this.categoryOptionCombosNotInDataElementCategoryCombo = categoryOptionCombosNotInDataElementCategoryCombo;
    }

    public List<String> getDataSetsNotAssignedToOrganisationUnits()
    {
        return dataSetsNotAssignedToOrganisationUnits;
    }

    public void setDataSetsNotAssignedToOrganisationUnits( List<String> dataSetsNotAssignedToOrganisationUnits )
    {
        this.dataSetsNotAssignedToOrganisationUnits = dataSetsNotAssignedToOrganisationUnits;
    }

    public List<String> getSectionsWithInvalidCategoryCombinations()
    {
        return sectionsWithInvalidCategoryCombinations;
    }

    public void setSectionsWithInvalidCategoryCombinations( List<String> sectionsWithInvalidCategoryCombinations )
    {
        this.sectionsWithInvalidCategoryCombinations = sectionsWithInvalidCategoryCombinations;
    }

    public Collection<Collection<String>> getIndicatorsWithIdenticalFormulas()
    {
        return indicatorsWithIdenticalFormulas;
    }

    public void setIndicatorsWithIdenticalFormulas( Collection<Collection<String>> indicatorsWithIdenticalFormulas )
    {
        this.indicatorsWithIdenticalFormulas = indicatorsWithIdenticalFormulas;
    }

    public List<String> getIndicatorsWithoutGroups()
    {
        return indicatorsWithoutGroups;
    }

    public void setIndicatorsWithoutGroups( List<String> indicatorsWithoutGroups )
    {
        this.indicatorsWithoutGroups = indicatorsWithoutGroups;
    }

    public Map<String, String> getInvalidIndicatorNumerators()
    {
        return invalidIndicatorNumerators;
    }

    public void setInvalidIndicatorNumerators( Map<String, String> invalidIndicatorNumerators )
    {
        this.invalidIndicatorNumerators = invalidIndicatorNumerators;
    }

    public Map<String, String> getInvalidIndicatorDenominators()
    {
        return invalidIndicatorDenominators;
    }

    public void setInvalidIndicatorDenominators( Map<String, String> invalidIndicatorDenominators )
    {
        this.invalidIndicatorDenominators = invalidIndicatorDenominators;
    }

    public SortedMap<String, Collection<String>> getIndicatorsViolatingExclusiveGroupSets()
    {
        return indicatorsViolatingExclusiveGroupSets;
    }

    public void setIndicatorsViolatingExclusiveGroupSets( SortedMap<String, Collection<String>> indicatorsViolatingExclusiveGroupSets )
    {
        this.indicatorsViolatingExclusiveGroupSets = indicatorsViolatingExclusiveGroupSets;
    }

    public List<String> getDuplicatePeriods()
    {
        return duplicatePeriods;
    }

    public void setDuplicatePeriods( List<String> duplicatePeriods )
    {
        this.duplicatePeriods = duplicatePeriods;
    }

    public List<String> getOrganisationUnitsWithCyclicReferences()
    {
        return organisationUnitsWithCyclicReferences;
    }

    public void setOrganisationUnitsWithCyclicReferences( List<String> organisationUnitsWithCyclicReferences )
    {
        this.organisationUnitsWithCyclicReferences = organisationUnitsWithCyclicReferences;
    }

    public List<String> getOrphanedOrganisationUnits()
    {
        return orphanedOrganisationUnits;
    }

    public void setOrphanedOrganisationUnits( List<String> orphanedOrganisationUnits )
    {
        this.orphanedOrganisationUnits = orphanedOrganisationUnits;
    }

    public List<String> getOrganisationUnitsWithoutGroups()
    {
        return organisationUnitsWithoutGroups;
    }

    public void setOrganisationUnitsWithoutGroups( List<String> organisationUnitsWithoutGroups )
    {
        this.organisationUnitsWithoutGroups = organisationUnitsWithoutGroups;
    }

    public SortedMap<String, Collection<String>> getOrganisationUnitsViolatingExclusiveGroupSets()
    {
        return organisationUnitsViolatingExclusiveGroupSets;
    }

    public void setOrganisationUnitsViolatingExclusiveGroupSets( SortedMap<String, Collection<String>> organisationUnitsViolatingExclusiveGroupSets )
    {
        this.organisationUnitsViolatingExclusiveGroupSets = organisationUnitsViolatingExclusiveGroupSets;
    }

    public List<String> getOrganisationUnitGroupsWithoutGroupSets()
    {
        return organisationUnitGroupsWithoutGroupSets;
    }

    public void setOrganisationUnitGroupsWithoutGroupSets( List<String> organisationUnitGroupsWithoutGroupSets )
    {
        this.organisationUnitGroupsWithoutGroupSets = organisationUnitGroupsWithoutGroupSets;
    }

    public List<String> getValidationRulesWithoutGroups()
    {
        return validationRulesWithoutGroups;
    }

    public void setValidationRulesWithoutGroups( List<String> validationRulesWithoutGroups )
    {
        this.validationRulesWithoutGroups = validationRulesWithoutGroups;
    }

    public Map<String, String> getInvalidValidationRuleLeftSideExpressions()
    {
        return invalidValidationRuleLeftSideExpressions;
    }

    public void setInvalidValidationRuleLeftSideExpressions( Map<String, String> invalidValidationRuleLeftSideExpressions )
    {
        this.invalidValidationRuleLeftSideExpressions = invalidValidationRuleLeftSideExpressions;
    }

    public Map<String, String> getInvalidValidationRuleRightSideExpressions()
    {
        return invalidValidationRuleRightSideExpressions;
    }

    public void setInvalidValidationRuleRightSideExpressions( Map<String, String> invalidValidationRuleRightSideExpressions )
    {
        this.invalidValidationRuleRightSideExpressions = invalidValidationRuleRightSideExpressions;
    }
}
