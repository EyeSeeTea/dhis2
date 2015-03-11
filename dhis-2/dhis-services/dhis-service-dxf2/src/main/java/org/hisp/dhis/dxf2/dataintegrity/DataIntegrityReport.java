package org.hisp.dhis.dxf2.dataintegrity;

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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.indicator.Indicator;

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
@JacksonXmlRootElement( namespace = DxfNamespaces.DXF_2_0, localName = "dataIntegrityReport" )
public class DataIntegrityReport
{
    @JsonProperty
    private List<String> dataElementsWithoutDataSet = new ArrayList<>();

    @JsonProperty
    private List<String> dataElementsWithoutGroups = new ArrayList<>();

    @JsonProperty
    private Map<String, Collection<String>> dataElementsAssignedToDataSetsWithDifferentPeriodTypes = new HashMap<>();

    @JsonProperty
    private SortedMap<String, Collection<String>> dataElementsViolatingExclusiveGroupSets = new TreeMap<>();

    @JsonProperty
    private SortedMap<String, Collection<String>> dataElementsInDataSetNotInForm = new TreeMap<>();

    @JsonProperty
    private Map<String, Set<String>> categoryOptionCombosNotInDataElementCategoryCombo = new HashMap<>();

    @JsonProperty
    private List<String> dataSetsNotAssignedToOrganisationUnits = new ArrayList<>();

    @JsonProperty
    private List<String> sectionsWithInvalidCategoryCombinations = new ArrayList<>();

    @JsonProperty
    private Collection<Collection<String>> indicatorsWithIdenticalFormulas = new HashSet<>();

    @JsonProperty
    private List<String> indicatorsWithoutGroups = new ArrayList<>();

    @JsonProperty
    private Map<String, String> invalidIndicatorNumerators = new HashMap<>();

    @JsonProperty
    private Map<String, String> invalidIndicatorDenominators = new HashMap<>();

    @JsonProperty
    private SortedMap<String, Collection<String>> indicatorsViolatingExclusiveGroupSets = new TreeMap<>();

    @JsonProperty
    private List<String> duplicatePeriods = new ArrayList<>();

    @JsonProperty
    private List<String> organisationUnitsWithCyclicReferences = new ArrayList<>( );

    @JsonProperty
    private List<String> orphanedOrganisationUnits = new ArrayList<>();

    @JsonProperty
    private List<String> organisationUnitsWithoutGroups = new ArrayList<>();

    @JsonProperty
    private SortedMap<String, Collection<String>> organisationUnitsViolatingExclusiveGroupSets = new TreeMap<>();

    @JsonProperty
    private List<String> organisationUnitGroupsWithoutGroupSets = new ArrayList<>();

    @JsonProperty
    private List<String> validationRulesWithoutGroups = new ArrayList<>();

    @JsonProperty
    private Map<String, String> invalidValidationRuleLeftSideExpressions = new HashMap<>();

    @JsonProperty
    private Map<String, String> invalidValidationRuleRightSideExpressions = new HashMap<>();

    public DataIntegrityReport()
    {

    }

    public DataIntegrityReport( org.hisp.dhis.dataintegrity.DataIntegrityReport report )
    {
        dataElementsWithoutDataSet.addAll( transformGenericList( report.getDataElementsWithoutDataSet() ) );

        dataElementsWithoutGroups.addAll( transformGenericList( report.getDataElementsWithoutGroups() ) );

        dataElementsAssignedToDataSetsWithDifferentPeriodTypes.putAll( transformDataElementMap( report.getDataElementsAssignedToDataSetsWithDifferentPeriodTypes() ) );

        dataElementsViolatingExclusiveGroupSets.putAll( transformSortedDataElementMap( report.getDataElementsViolatingExclusiveGroupSets() ) );

        dataElementsInDataSetNotInForm.putAll( transformSortedMap( report.getDataElementsInDataSetNotInForm() ) );

        categoryOptionCombosNotInDataElementCategoryCombo.putAll( transformDataSetMap( report.getCategoryOptionCombosNotInDataElementCategoryCombo() ) );

        dataSetsNotAssignedToOrganisationUnits.addAll( transformGenericList( report.getDataSetsNotAssignedToOrganisationUnits() ) );

        sectionsWithInvalidCategoryCombinations.addAll( transformGenericList( report.getSectionsWithInvalidCategoryCombinations() ) );

        indicatorsWithIdenticalFormulas.addAll( transformCollectionOfIndicatorCollections( report.getIndicatorsWithIdenticalFormulas() ) );

        indicatorsWithoutGroups.addAll( transformGenericList( report.getIndicatorsWithoutGroups() ) );

        invalidIndicatorNumerators.putAll( transformGenericMapOfStrings( report.getInvalidIndicatorNumerators() ) );

        invalidIndicatorDenominators.putAll( transformGenericMapOfStrings( report.getInvalidIndicatorDenominators() ) );

        
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    private Collection<Collection<String>> transformCollectionOfIndicatorCollections( Collection<Collection<Indicator>> collection )
    {
        return transformGenericCollectionOfCollections( (Collection<Collection<? extends IdentifiableObject>>)(Collection<?>) collection );
    }

    @SuppressWarnings( "unchecked" )
    private Map<String, Set<String>> transformDataSetMap( Map<DataSet, Set<DataElementOperand>> map )
    {
        return transformGenericMapOfSets( (Map<? extends IdentifiableObject, Set<? extends  IdentifiableObject>>)(Map<?, ?>) map );
    }

    @SuppressWarnings( "unchecked" )
    private Map<String, Collection<String>> transformDataElementMap( Map<DataElement, Collection<DataSet>> map )
    {
        return transformGenericMapOfCollections( (Map<? extends IdentifiableObject, Collection<? extends IdentifiableObject>>) (Map<?, ?>) map );
    }

    @SuppressWarnings( "unchecked" )
    private SortedMap<String, Collection<String>> transformSortedDataElementMap( Map<DataElement, Collection<DataElementGroup>> map )
    {
        return transformGenericSortedMap( (SortedMap<? extends IdentifiableObject, Collection<? extends IdentifiableObject>>)(SortedMap<?,?>) map );
    }

    @SuppressWarnings( "unchecked" )
    private SortedMap<String, Collection<String>> transformSortedMap( SortedMap<DataSet, Collection<DataElement>> sortedMap )
    {
        return transformGenericSortedMap( (SortedMap<? extends IdentifiableObject, Collection<? extends IdentifiableObject>>)(SortedMap<?,?>) sortedMap );
    }

    // -------------------------------------------------------------------------
    // Generic collection tranformer methods
    // -------------------------------------------------------------------------

    private Collection<Collection<String>> transformGenericCollectionOfCollections( Collection<Collection<? extends IdentifiableObject>> collection )
    {
        Collection<Collection<String>> newCollection = new HashSet<>();

        for ( Collection<? extends IdentifiableObject> c : collection )
        {
            newCollection.add( transformGenericCollection( c ) );
        }

        return newCollection;
    }

    @SuppressWarnings( "unchecked" )
    private Map<String, Set<String>> transformGenericMapOfSets( Map<? extends IdentifiableObject, Set<? extends IdentifiableObject>> map )
    {
        HashMap<String, Set<String>> newMap = new HashMap<>();

        for ( Map.Entry<? extends IdentifiableObject, Set<? extends IdentifiableObject>> entry : map.entrySet() )
        {
            Set<String> value = new HashSet<>();
            value.addAll( transformGenericCollection( entry.getValue() ));

            newMap.put( entry.getKey().getDisplayName(), value );
        }

        return newMap;
    }

    private Map<String, String> transformGenericMapOfStrings( Map<? extends IdentifiableObject, String> map )
    {
        HashMap<String, String> newMap = new HashMap<>( map.size() );

        for ( Map.Entry<? extends IdentifiableObject, String> entry : map.entrySet() )
        {
            newMap.put( entry.getKey().getDisplayName(), entry.getValue() );
        }

        return newMap;
    }

    @SuppressWarnings( "unchecked" )
    private Map<String, Collection<String>> transformGenericMapOfCollections( Map<? extends IdentifiableObject, Collection<? extends IdentifiableObject>> map )
    {
        HashMap<String, Collection<String>> newMap = new HashMap<>();

        for ( Map.Entry<? extends IdentifiableObject, Collection<? extends IdentifiableObject>> entry : map.entrySet() )
        {
            Collection<String> value = new HashSet<>();
            value.addAll( transformGenericCollection( entry.getValue() ) );

            newMap.put( entry.getKey().getDisplayName(), value );
        }

        return newMap;
    }

    private List<String> transformGenericList( List<? extends IdentifiableObject> list )
    {
        ArrayList<String> newList = new ArrayList<>( list.size() );
        for ( IdentifiableObject o : list )
        {
            newList.add( o.getDisplayName() );
        }
        return newList;
    }

    private Collection<String> transformGenericCollection( Collection<? extends IdentifiableObject> collection )
    {
        Collection<String> newCollection = new HashSet<>( collection.size() );

        for ( IdentifiableObject o : collection )
        {
            newCollection.add( o.getDisplayName() );
        }

        return newCollection;
    }

    private SortedMap<String, Collection<String>> transformGenericSortedMap( SortedMap<? extends IdentifiableObject, Collection<? extends IdentifiableObject>> map )
    {
        SortedMap<String, Collection<String>> newMap = new TreeMap<>();

        for ( SortedMap.Entry<? extends IdentifiableObject, Collection<? extends IdentifiableObject>> entry : map.entrySet() )
        {
            newMap.put( entry.getKey().getDisplayName(), transformGenericCollection( entry.getValue() ) );
        }

        return newMap;
    }
}
