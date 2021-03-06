package org.hisp.dhis.analytics.data;

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

import static org.hisp.dhis.analytics.AnalyticsTableManager.ANALYTICS_TABLE_NAME;
import static org.hisp.dhis.analytics.AnalyticsTableManager.COMPLETENESS_TABLE_NAME;
import static org.hisp.dhis.analytics.AnalyticsTableManager.COMPLETENESS_TARGET_TABLE_NAME;
import static org.hisp.dhis.analytics.AnalyticsTableManager.ORGUNIT_TARGET_TABLE_NAME;
import static org.hisp.dhis.analytics.DataQueryParams.COMPLETENESS_DIMENSION_TYPES;
import static org.hisp.dhis.analytics.DataQueryParams.DISPLAY_NAME_DATA_X;
import static org.hisp.dhis.analytics.DataQueryParams.DX_INDEX;
import static org.hisp.dhis.common.DimensionalObject.ATTRIBUTEOPTIONCOMBO_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.CATEGORYOPTIONCOMBO_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.DATA_X_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.DIMENSION_SEP;
import static org.hisp.dhis.common.DimensionalObject.ORGUNIT_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.PERIOD_DIM_ID;
import static org.hisp.dhis.common.IdentifiableObjectUtils.getLocalPeriodIdentifiers;
import static org.hisp.dhis.common.DimensionalObjectUtils.asTypedList;
import static org.hisp.dhis.organisationunit.OrganisationUnit.getParentGraphMap;
import static org.hisp.dhis.organisationunit.OrganisationUnit.getParentNameGraphMap;
import static org.hisp.dhis.period.PeriodType.getPeriodTypeFromIsoString;
import static org.hisp.dhis.reporttable.ReportTable.IRT2D;
import static org.hisp.dhis.reporttable.ReportTable.addIfEmpty;
import static org.hisp.dhis.common.DimensionalObjectUtils.getDimensionalItemIds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.analytics.AggregationType;
import org.hisp.dhis.analytics.AnalyticsManager;
import org.hisp.dhis.analytics.AnalyticsSecurityManager;
import org.hisp.dhis.analytics.AnalyticsService;
import org.hisp.dhis.analytics.AnalyticsUtils;
import org.hisp.dhis.analytics.DataQueryGroups;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.analytics.DataQueryService;
import org.hisp.dhis.analytics.DimensionItem;
import org.hisp.dhis.analytics.QueryPlanner;
import org.hisp.dhis.analytics.event.EventAnalyticsService;
import org.hisp.dhis.analytics.event.EventQueryParams;
import org.hisp.dhis.calendar.Calendar;
import org.hisp.dhis.calendar.DateTimeUnit;
import org.hisp.dhis.common.AnalyticalObject;
import org.hisp.dhis.common.BaseDimensionalObject;
import org.hisp.dhis.common.CombinationGenerator;
import org.hisp.dhis.common.DataDimensionItemType;
import org.hisp.dhis.common.DimensionType;
import org.hisp.dhis.common.DimensionalItemObject;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.DimensionalObjectUtils;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.common.IdentifiableObjectUtils;
import org.hisp.dhis.common.NameableObjectUtils;
import org.hisp.dhis.common.ReportingRateMetric;
import org.hisp.dhis.commons.collection.ListUtils;
import org.hisp.dhis.commons.util.DebugUtils;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.setting.SettingKey;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.system.util.SystemUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.util.Timer;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

/**
 * @author Lars Helge Overland
 */
public class DefaultAnalyticsService
    implements AnalyticsService
{
    private static final Log log = LogFactory.getLog( DefaultAnalyticsService.class );

    private static final String VALUE_HEADER_NAME = "Value";
    private static final int PERCENT = 100;
    private static final int MAX_QUERIES = 8;

    //TODO completeness on time

    @Autowired
    private AnalyticsManager analyticsManager;

    @Autowired
    private AnalyticsSecurityManager securityManager;

    @Autowired
    private QueryPlanner queryPlanner;

    @Autowired
    private ExpressionService expressionService;

    @Autowired
    private ConstantService constantService;

    @Autowired
    private SystemSettingManager systemSettingManager;

    @Autowired
    private EventAnalyticsService eventAnalyticsService;    
    
    @Autowired
    private DataQueryService dataQueryService;
    
    @Autowired
    private CurrentUserService currentUserService;
    
    @Autowired
    private I18nService i18nService;

    // -------------------------------------------------------------------------
    // Methods for retrieving aggregated data
    // -------------------------------------------------------------------------

    @Override
    public Grid getAggregatedDataValues( DataQueryParams params )
    {
        // ---------------------------------------------------------------------
        // Security and validation
        // ---------------------------------------------------------------------

        securityManager.decideAccess( params );

        securityManager.applyDataApprovalConstraints( params );

        securityManager.applyDimensionConstraints( params );

        queryPlanner.validate( params );

        params.conform();
        
        return getAggregatedDataValueGridInternal( params );
    }
    
    /**
     * Returns a grid with aggregated data.
     * 
     * @param params the data query parameters.
     * @return a grid with aggregated data.
     */
    private Grid getAggregatedDataValueGridInternal( DataQueryParams params )
    {
        // ---------------------------------------------------------------------
        // Headers
        // ---------------------------------------------------------------------

        Grid grid = new ListGrid();

        addHeaders( params, grid );

        // ---------------------------------------------------------------------
        // Data
        // ---------------------------------------------------------------------

        addIndicatorValues( params, grid );

        addDataElementValues( params, grid );
        
        addDataElementOperandValues( params, grid );

        addReportingRates( params, grid );
        
        addProgramDataElementAttributeIndicatorValues( params, grid );

        addDynamicDimensionValues( params, grid );

        // ---------------------------------------------------------------------
        // Meta-data
        // ---------------------------------------------------------------------

        addMetaData( params, grid );
        
        applyIdScheme( params, grid  );

        return grid;
    }

    /**
     * Adds headers to the given grid based on the given data query parameters.
     */
    private void addHeaders( DataQueryParams params, Grid grid )
    {
        if ( !params.isSkipData() && !params.isSkipHeaders() )
        {
            for ( DimensionalObject col : params.getDimensions() )
            {
                grid.addHeader( new GridHeader( col.getDimension(), col.getDisplayName(), String.class.getName(), false, true ) );
            }
    
            grid.addHeader( new GridHeader( DataQueryParams.VALUE_ID, VALUE_HEADER_NAME, Double.class.getName(), false, false ) );
        }
    }

    /**
     * Adds indicator values to the given grid based on the given data query
     * parameters.
     *
     * @param params the data query parameters.
     * @param grid the grid.
     */
    private void addIndicatorValues( DataQueryParams params, Grid grid )
    {
        if ( !params.getIndicators().isEmpty() && !params.isSkipData() )
        {
            DataQueryParams dataSourceParams = params.instance();
            dataSourceParams.retainDataDimension( DataDimensionItemType.INDICATOR );

            List<Indicator> indicators = asTypedList( dataSourceParams.getIndicators() );

            Period filterPeriod = dataSourceParams.getFilterPeriod();

            Map<String, Double> constantMap = constantService.getConstantMap();

            // -----------------------------------------------------------------
            // Get indicator values
            // -----------------------------------------------------------------

            Map<String, Map<String, Integer>> permutationOrgUnitTargetMap = getOrgUnitTargetMap( dataSourceParams, indicators );

            List<List<DimensionItem>> dimensionItemPermutations = dataSourceParams.getDimensionItemPermutations();

            Map<String, Map<DimensionalItemObject, Double>> permutationDimensionalItemValueMap = getPermutationDimensionalItemValueMap( dataSourceParams );

            for ( Indicator indicator : indicators )
            {
                for ( List<DimensionItem> dimensionItems : dimensionItemPermutations )
                {
                    String permKey = DimensionItem.asItemKey( dimensionItems );

                    Map<DimensionalItemObject, Double> valueMap = permutationDimensionalItemValueMap.get( permKey );

                    if ( valueMap == null )
                    {
                        continue;
                    }

                    Period period = filterPeriod != null ? filterPeriod : (Period) DimensionItem.getPeriodItem( dimensionItems );

                    OrganisationUnit unit = (OrganisationUnit) DimensionItem.getOrganisationUnitItem( dimensionItems );

                    String ou = unit != null ? unit.getUid() : null;

                    Map<String, Integer> orgUnitCountMap = permutationOrgUnitTargetMap != null ? permutationOrgUnitTargetMap.get( ou ) : null;

                    Double value = expressionService.getIndicatorValue( indicator, period, valueMap, constantMap, orgUnitCountMap );

                    if ( value != null )
                    {
                        List<DimensionItem> row = new ArrayList<>( dimensionItems );

                        row.add( DX_INDEX, new DimensionItem( DATA_X_DIM_ID, indicator ) );

                        grid.addRow();
                        grid.addValues( DimensionItem.getItemIdentifiers( row ) );
                        grid.addValue( AnalyticsUtils.getRoundedValue( dataSourceParams, indicator.getDecimals(), value ) );
                    }
                }
            }
        }
    }

    /**
     * Adds data element values to the given grid based on the given data query
     * parameters.
     *
     * @param params the data query parameters.
     * @param grid the grid.
     */
    private void addDataElementValues( DataQueryParams params, Grid grid )
    {
        if ( !params.getAllDataElements().isEmpty() && !params.isSkipData() )
        {
            DataQueryParams dataSourceParams = params.instance();
            dataSourceParams.retainDataDimension( DataDimensionItemType.DATA_ELEMENT );

            Map<String, Object> aggregatedDataMap = getAggregatedDataValueMapObjectTyped( dataSourceParams );

            for ( Map.Entry<String, Object> entry : aggregatedDataMap.entrySet() )
            {
                grid.addRow();
                grid.addValues( entry.getKey().split( DIMENSION_SEP ) );
                grid.addValue( AnalyticsUtils.getRoundedValueObject( params, entry.getValue() ) );
            }
        }
    }
    
    /**
     * Adds data element operand values to the given grid based on the given data
     * query parameters.
     * 
     * @param params the data query parameters.
     * @param grid the grid.
     */
    private void addDataElementOperandValues( DataQueryParams params, Grid grid )
    {
        if ( !params.getDataElementOperands().isEmpty() && !params.isSkipData() )
        {
            // -----------------------------------------------------------------
            // Replace operands with data element, option combo and attribute combo dimensions
            // -----------------------------------------------------------------
            
            List<DataElementOperand> allOperands =
                asTypedList( params.instance().getDataElementOperands() );

            Collection<List<DataElementOperand>> operandsGroupedByAttributePresence = allOperands
                .stream()
                    .collect(Collectors.groupingBy(operand ->
                        DimensionalObjectUtils.getAttributeOptionCombos(
                            Collections.singletonList(operand)).isEmpty()))
                    .values();

            for ( List<DataElementOperand> operands : operandsGroupedByAttributePresence )
            {
                DataQueryParams dataSourceParams = params.instance();
                dataSourceParams.retainDataDimension( DataDimensionItemType.DATA_ELEMENT_OPERAND );

                List<DimensionalItemObject> dataElements =
                    Lists.newArrayList(DimensionalObjectUtils.getDataElements(operands));
                List<DimensionalItemObject> categoryOptionCombos =
                    Lists.newArrayList(DimensionalObjectUtils.getCategoryOptionCombos(operands));
                List<DimensionalItemObject> attributeOptionCombos =
                    Lists.newArrayList(DimensionalObjectUtils.getAttributeOptionCombos(operands));

            //TODO check if data was dim or filter
            
            dataSourceParams.removeDimension( DATA_X_DIM_ID );
            dataSourceParams.addDimension( new BaseDimensionalObject( DATA_X_DIM_ID, DimensionType.DATA_X, dataElements ) );

                dataSourceParams.addDimension(new BaseDimensionalObject(CATEGORYOPTIONCOMBO_DIM_ID,
                    DimensionType.CATEGORY_OPTION_COMBO, categoryOptionCombos));

                if ( !attributeOptionCombos.isEmpty() )
                {
                    dataSourceParams.addDimension(
                        new BaseDimensionalObject(ATTRIBUTEOPTIONCOMBO_DIM_ID,
                        DimensionType.ATTRIBUTE_OPTION_COMBO, attributeOptionCombos));
                }
            
                Map<String, Object> aggregatedDataMap =
                    getAggregatedDataValueMapObjectTyped(dataSourceParams);

                Integer replacementsCount = attributeOptionCombos.isEmpty() ? 1 : 2;
                aggregatedDataMap =
                    AnalyticsUtils.convertDxToOperand(aggregatedDataMap, replacementsCount);

                for ( Map.Entry<String, Object> entry : aggregatedDataMap.entrySet() ) {
                    grid.addRow();
                    grid.addValues(entry.getKey().split(DIMENSION_SEP));
                    grid.addValue(AnalyticsUtils.getRoundedValueObject(dataSourceParams, entry.getValue()));
                }
            }
        }
    }

    /**
     * Adds reporting rates to the given grid based on the given data query
     * parameters.
     *
     * @param params the data query parameters.
     * @param grid the grid.
     */
    private void addReportingRates( DataQueryParams params, Grid grid )
    {
        if ( !params.getReportingRates().isEmpty() && !params.isSkipData() )
        {
            for ( ReportingRateMetric metric : ReportingRateMetric.values() )
            {
                DataQueryParams dataSourceParams = params.instance();
                dataSourceParams.retainDataDimensionReportingRates( metric );
                dataSourceParams.ignoreDataApproval(); // No approval for reporting rates
                dataSourceParams.setAggregationType( AggregationType.COUNT );
                
                addReportingRates( dataSourceParams, grid, metric );
            }
        }
    }

    /**
     * Adds reporting rates to the given grid based on the given data query
     * parameters and reporting rate metric.
     *
     * @param params the data query parameters.
     * @param grid the grid.
     * @param metic the reporting rate metric.
     */
    private void addReportingRates( DataQueryParams dataSourceParams, Grid grid, ReportingRateMetric metric )
    {
        if ( !dataSourceParams.getReportingRates().isEmpty() && !dataSourceParams.isSkipData() )
        {
            if ( !COMPLETENESS_DIMENSION_TYPES.containsAll( dataSourceParams.getDimensionTypes() ) )
            {
                return;
            }

            // -----------------------------------------------------------------
            // Get complete data set registrations
            // -----------------------------------------------------------------

            Map<String, Double> aggregatedDataMap = getAggregatedCompletenessValueMap( dataSourceParams );

            // -----------------------------------------------------------------
            // Get completeness targets
            // -----------------------------------------------------------------

            List<Integer> completenessDimIndexes = dataSourceParams.getCompletenessDimensionIndexes();
            List<Integer> completenessFilterIndexes = dataSourceParams.getCompletenessFilterIndexes();

            DataQueryParams targetParams = dataSourceParams.instance();

            targetParams.setDimensions( ListUtils.getAtIndexes( targetParams.getDimensions(), completenessDimIndexes ) );
            targetParams.setFilters( ListUtils.getAtIndexes( targetParams.getFilters(), completenessFilterIndexes ) );
            targetParams.setSkipPartitioning( true );
            targetParams.setAggregationType( AggregationType.SUM );

            Map<String, Double> targetMap = getAggregatedCompletenessTargetMap( targetParams );

            Integer periodIndex = dataSourceParams.getPeriodDimensionIndex();
            Integer dataSetIndex = DataQueryParams.DX_INDEX;

            Map<String, PeriodType> dsPtMap = dataSourceParams.getDataSetPeriodTypeMap();

            PeriodType filterPeriodType = dataSourceParams.getFilterPeriodType();

            // -----------------------------------------------------------------
            // Join data maps, calculate completeness and add to grid
            // -----------------------------------------------------------------

            for ( Map.Entry<String, Double> entry : aggregatedDataMap.entrySet() )
            {
                List<String> dataRow = Lists.newArrayList( entry.getKey().split( DIMENSION_SEP ) );

                // -------------------------------------------------------------
                // Get target value
                // -------------------------------------------------------------

                List<String> targetRow = ListUtils.getAtIndexes( dataRow, completenessDimIndexes );
                String targetKey = StringUtils.join( targetRow, DIMENSION_SEP );
                Double target = targetMap.get( targetKey );
                
                Double actual = entry.getValue();

                if ( target != null && actual != null )
                {
                    // ---------------------------------------------------------
                    // Multiply target value by number of periods in time span
                    // ---------------------------------------------------------

                    PeriodType queryPt = filterPeriodType != null ? filterPeriodType : getPeriodTypeFromIsoString( dataRow.get( periodIndex ) );
                    PeriodType dataSetPt = dsPtMap.get( dataRow.get( dataSetIndex ) );
                    target = target * queryPt.getPeriodSpan( dataSetPt );

                    // ---------------------------------------------------------
                    // Calculate reporting rate and replace data set with rate
                    // ---------------------------------------------------------

                    double rate = actual * PERCENT / target;
                    
                    Double value = rate;
                    
                    if ( ReportingRateMetric.ACTUAL_REPORTS == metric )
                    {
                        value = actual;
                    }
                    else if ( ReportingRateMetric.EXPECTED_REPORTS == metric )
                    {
                        value = target;
                    }
                    
                    String reportingRate = DimensionalObjectUtils.getDimensionItem( dataRow.get( DX_INDEX ), metric );
                    dataRow.set( DX_INDEX, reportingRate );

                    grid.addRow();
                    grid.addValues( dataRow.toArray() );
                    grid.addValue( dataSourceParams.isSkipRounding() ? value : MathUtils.getRounded( value ) );
                }
            }
        }
    }

    /**
     * Adds program data element values to the given grid based on the given data
     * query parameters.
     * 
     * @param params the data query parameters.
     * @param grid the grid.
     */
    private void addProgramDataElementAttributeIndicatorValues( DataQueryParams params, Grid grid )
    {
        if ( ( !params.getAllProgramDataElementsAndAttributes().isEmpty() || !params.getProgramIndicators().isEmpty() ) && !params.isSkipData() )
        {
            DataQueryParams dataSourceParams = params.instance();
            dataSourceParams.retainDataDimensions( DataDimensionItemType.PROGRAM_DATA_ELEMENT, 
                DataDimensionItemType.PROGRAM_ATTRIBUTE, DataDimensionItemType.PROGRAM_INDICATOR );
            
            EventQueryParams eventQueryParams = EventQueryParams.fromDataQueryParams( dataSourceParams );
            eventQueryParams.setSkipMeta( true );
            
            Grid eventGrid = eventAnalyticsService.getAggregatedEventData( eventQueryParams );
            
            grid.addRows( eventGrid );
        }
    }

    /**
     * Adds values to the given grid based on dynamic dimensions from the given
     * data query parameters. This assumes that no fixed dimensions are part of
     * the query.
     *
     * @param params the data query parameters.
     * @param grid the grid.
     */
    private void addDynamicDimensionValues( DataQueryParams params, Grid grid )
    {
        if ( params.getDataDimensionAndFilterOptions().isEmpty() && !params.isSkipData() )
        {
            Map<String, Double> aggregatedDataMap = getAggregatedDataValueMap( params.instance() );

            for ( Map.Entry<String, Double> entry : aggregatedDataMap.entrySet() )
            {
                grid.addRow();
                grid.addValues( entry.getKey().split( DIMENSION_SEP ) );
                grid.addValue( params.isSkipRounding() ? entry.getValue() : MathUtils.getRounded( entry.getValue() ) );
            }
        }
    }

    /**
     * Adds meta data values to the given grid based on the given data query
     * parameters.
     *
     * @param params the data query parameters.
     * @param grid the grid.
     */
    private void addMetaData( DataQueryParams params, Grid grid )
    {
        if ( !params.isSkipMeta() )
        {
            Map<Object, Object> metaData = new HashMap<>();

            // -----------------------------------------------------------------
            // Names element
            // -----------------------------------------------------------------

            Map<String, String> uidNameMap = getUidNameMap( params );
            Map<String, String> cocNameMap = getCocNameMap( params );
            uidNameMap.putAll( cocNameMap );
            uidNameMap.put( DATA_X_DIM_ID, DISPLAY_NAME_DATA_X );

            metaData.put( NAMES_META_KEY, uidNameMap );

            // -----------------------------------------------------------------
            // Item order elements
            // -----------------------------------------------------------------

            Calendar calendar = PeriodType.getCalendar();

            List<String> periodUids = calendar.isIso8601() ? 
                getDimensionalItemIds( params.getDimensionOrFilterItems( PERIOD_DIM_ID ) ) :
                    getLocalPeriodIdentifiers( params.getDimensionOrFilterItems( PERIOD_DIM_ID ), calendar );

            metaData.put( PERIOD_DIM_ID, periodUids );
            metaData.put( CATEGORYOPTIONCOMBO_DIM_ID, cocNameMap.keySet() );

            for ( DimensionalObject dim : params.getDimensionsAndFilters() )
            {
                if ( !metaData.keySet().contains( dim.getDimension() ) )
                {
                    metaData.put( dim.getDimension(), getDimensionalItemIds( dim.getItems() ) );
                }
            }

            // -----------------------------------------------------------------
            // Organisation unit hierarchy
            // -----------------------------------------------------------------

            User user = currentUserService.getCurrentUser();
            
            List<OrganisationUnit> organisationUnits = asTypedList( params.getDimensionOrFilterItems( ORGUNIT_DIM_ID ) );
            Collection<OrganisationUnit> roots = user != null ? user.getOrganisationUnits() : null;
            
            if ( params.isHierarchyMeta() )
            {
                metaData.put( OU_HIERARCHY_KEY, getParentGraphMap( organisationUnits, roots ) );
            }

            if ( params.isShowHierarchy() )
            {
                metaData.put( OU_NAME_HIERARCHY_KEY, getParentNameGraphMap( organisationUnits, roots, true, params.getDisplayProperty() ) );
            }
            
            grid.setMetaData( metaData );
        }
    }

    /**
     * Substitutes the meta data of the grid with the identifier scheme meta data
     * property indicated in the query.
     * 
     * @param params the data query parameters.
     * @param grid the grid.
     */
    private void applyIdScheme( DataQueryParams params, Grid grid )
    {
        if ( !params.isSkipMeta() && params.hasNonUidOutputIdScheme() )
        {
            List<DimensionalItemObject> items = params.getAllDimensionItems();
            
            Map<String, String> map = NameableObjectUtils.getUidPropertyMap( items, params.getOutputIdScheme() );
            
            grid.substituteMetaData( map );
        }
    }
    
    @Override
    public Grid getAggregatedDataValues( DataQueryParams params, boolean tableLayout, List<String> columns, List<String> rows )
    {
        if ( !tableLayout )
        {
            return getAggregatedDataValues( params );
        }
        
        Locale locale = i18nService.getCurrentLocale();
        
        params.setOutputIdScheme( null );
        
        Grid grid = getAggregatedDataValues( params );

        ListUtils.removeEmptys( columns );
        ListUtils.removeEmptys( rows );

        queryPlanner.validateTableLayout( params, columns, rows );

        ReportTable reportTable = new ReportTable();

        List<DimensionalItemObject[]> tableColumns = new ArrayList<>();
        List<DimensionalItemObject[]> tableRows = new ArrayList<>();

        if ( columns != null )
        {
            for ( String dimension : columns )
            {
                reportTable.getColumnDimensions().add( dimension );
                
                List<DimensionalItemObject> items = params.getDimensionArrayExplodeCoc( dimension );
                
                i18nService.internationalise( items, locale );

                tableColumns.add( items.toArray( new DimensionalItemObject[0] ) );
            }
        }

        if ( rows != null )
        {
            for ( String dimension : rows )
            {
                reportTable.getRowDimensions().add( dimension );
                
                List<DimensionalItemObject> items = params.getDimensionArrayExplodeCoc( dimension );
                
                i18nService.internationalise( items, locale );

                tableRows.add( items.toArray( new DimensionalItemObject[0] ) );
            }
        }
        
        reportTable.setGridColumns( new CombinationGenerator<>( tableColumns.toArray( IRT2D ) ).getCombinations() );
        reportTable.setGridRows( new CombinationGenerator<>( tableRows.toArray( IRT2D ) ).getCombinations() );

        addIfEmpty( reportTable.getGridColumns() );
        addIfEmpty( reportTable.getGridRows() );

        reportTable.setTitle( IdentifiableObjectUtils.join( params.getFilterItems() ) );
        reportTable.setHideEmptyRows( params.isHideEmptyRows() );
        reportTable.setShowHierarchy( params.isShowHierarchy() );

        Map<String, Object> valueMap = getAggregatedDataValueMapping( grid );

        return reportTable.getGrid( new ListGrid( grid.getMetaData() ), valueMap, params.getDisplayProperty(), false );
    }

    @Override
    public Grid getAggregatedDataValues( AnalyticalObject object, I18nFormat format )
    {
        DataQueryParams params = dataQueryService.getFromAnalyticalObject( object, format );
        
        return getAggregatedDataValues( params );
    }
    
    @Override
    public Map<String, Object> getAggregatedDataValueMapping( DataQueryParams params )
    {
        Grid grid = getAggregatedDataValues( params );

        return getAggregatedDataValueMapping( grid );
    }

    @Override
    public Map<String, Object> getAggregatedDataValueMapping( AnalyticalObject object, I18nFormat format )
    {
        DataQueryParams params = dataQueryService.getFromAnalyticalObject( object, format );

        return getAggregatedDataValueMapping( params );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Generates a mapping of permutations keys (organisation unit id or null)
     * and mappings of organisation unit group and counts.
     *
     * @param params the data query parameters.
     * @param indicators the indicators for which formulas to scan for organisation
     *        unit groups.
     * @return a map of maps.
     */
    private Map<String, Map<String, Integer>> getOrgUnitTargetMap( DataQueryParams params, Collection<Indicator> indicators )
    {
        Set<OrganisationUnitGroup> orgUnitGroups = expressionService.getOrganisationUnitGroupsInIndicators( indicators );

        if ( orgUnitGroups.isEmpty() )
        {
            return null;
        }

        DataQueryParams orgUnitTargetParams = params.instance().pruneToDimensionType( DimensionType.ORGANISATION_UNIT );
        orgUnitTargetParams.getDimensions().add( new BaseDimensionalObject( DimensionalObject.ORGUNIT_GROUP_DIM_ID, null, new ArrayList<DimensionalItemObject>( orgUnitGroups ) ) );
        orgUnitTargetParams.setSkipPartitioning( true );

        Map<String, Double> orgUnitCountMap = getAggregatedOrganisationUnitTargetMap( orgUnitTargetParams );

        return DataQueryParams.getPermutationOrgUnitGroupCountMap( orgUnitCountMap );
    }

    /**
     * Generates a mapping where the key represents the dimensional item identifiers
     * concatenated by "-" and the value is the corresponding aggregated data value
     * based on the given grid.
     *
     * @param grid the grid.
     * @return a mapping between item identifiers and aggregated values.
     */
    private Map<String, Object> getAggregatedDataValueMapping( Grid grid )
    {
        Map<String, Object> map = new HashMap<>();

        int metaCols = grid.getWidth() - 1;
        int valueIndex = grid.getWidth() - 1;

        for ( List<Object> row : grid.getRows() )
        {
            StringBuilder key = new StringBuilder();

            for ( int index = 0; index < metaCols; index++ )
            {
                key.append( row.get( index ) ).append( DIMENSION_SEP );
            }

            key.deleteCharAt( key.length() - 1 );

            Object value = row.get( valueIndex );

            map.put( key.toString(), value );
        }

        return map;
    }

    /**
     * Generates aggregated values for the given query. Creates a mapping between
     * a dimension key and the aggregated value. The dimension key is a
     * concatenation of the identifiers of the dimension items separated by "-".
     *
     * @param params the data query parameters.
     * @return a mapping between a dimension key and the aggregated value.
     */
    private Map<String, Double> getAggregatedDataValueMap( DataQueryParams params )
    {
        return AnalyticsUtils.getDoubleMap( getAggregatedValueMap( params, ANALYTICS_TABLE_NAME ) );
    }

    /**
     * Generates aggregated values for the given query. Creates a mapping between
     * a dimension key and the aggregated value. The dimension key is a
     * concatenation of the identifiers of the dimension items separated by "-".
     *
     * @param params the data query parameters.
     * @return a mapping between a dimension key and the aggregated value.
     */
    private Map<String, Object> getAggregatedDataValueMapObjectTyped( DataQueryParams params )
    {
        return getAggregatedValueMap( params, ANALYTICS_TABLE_NAME );
    }

    /**
     * Generates aggregated values for the given query. Creates a mapping between
     * a dimension key and the aggregated value. The dimension key is a
     * concatenation of the identifiers of the dimension items separated by "-".
     *
     * @param params the data query parameters.
     * @return a mapping between a dimension key and the aggregated value.
     */
    private Map<String, Double> getAggregatedCompletenessValueMap( DataQueryParams params )
    {
        return AnalyticsUtils.getDoubleMap( getAggregatedValueMap( params, COMPLETENESS_TABLE_NAME ) );
    }

    /**
     * Generates a mapping between the the data set dimension key and the count
     * of expected data sets to report.
     *
     * @param params the data query parameters.
     * @return a mapping between the the data set dimension key and the count of
     *         expected data sets to report.
     */
    private Map<String, Double> getAggregatedCompletenessTargetMap( DataQueryParams params )
    {
        return AnalyticsUtils.getDoubleMap( getAggregatedValueMap( params, COMPLETENESS_TARGET_TABLE_NAME ) );
    }

    /**
     * Generates a mapping between the the org unit dimension key and the count
     * of org units inside the subtree of the given organisation units and
     * members of the given organisation unit groups.
     *
     * @param params the data query parameters.
     * @return a mapping between the the data set dimension key and the count of
     *         expected data sets to report.
     */
    private Map<String, Double> getAggregatedOrganisationUnitTargetMap( DataQueryParams params )
    {
        return AnalyticsUtils.getDoubleMap( getAggregatedValueMap( params, ORGUNIT_TARGET_TABLE_NAME ) );
    }

    /**
     * Generates a mapping between a dimension key and the aggregated value. The
     * dimension key is a concatenation of the identifiers of the dimension items
     * separated by "-".
     *
     * @param params the data query parameters.
     * @return a mapping between a dimension key and aggregated values.
     */
    private Map<String, Object> getAggregatedValueMap( DataQueryParams params, String tableName )
    {
        queryPlanner.validateMaintenanceMode();

        int optimalQueries = MathUtils.getWithin( getProcessNo(), 1, MAX_QUERIES );

        int maxLimit = (Integer) systemSettingManager.getSystemSetting( SettingKey.ANALYTICS_MAX_LIMIT );
        
        Timer timer = new Timer().start().disablePrint();

        DataQueryGroups queryGroups = queryPlanner.planQuery( params, optimalQueries, tableName );

        timer.getSplitTime( "Planned analytics query, got: " + queryGroups.getLargestGroupSize() + " for optimal: " + optimalQueries );

        Map<String, Object> map = new HashMap<>();

        for ( List<DataQueryParams> queries : queryGroups.getSequentialQueries() )
        {
            List<Future<Map<String, Object>>> futures = new ArrayList<>();

            for ( DataQueryParams query : queries )
            {
                futures.add( analyticsManager.getAggregatedDataValues( query, maxLimit ) );
            }

            for ( Future<Map<String, Object>> future : futures )
            {
                try
                {
                    Map<String, Object> taskValues = future.get();

                    if ( taskValues != null )
                    {
                        map.putAll( taskValues );
                    }
                }
                catch ( Exception ex )
                {
                    log.error( DebugUtils.getStackTrace( ex ) );
                    log.error( DebugUtils.getStackTrace( ex.getCause() ) );

                    throw new RuntimeException( "Error during execution of aggregation query task", ex );
                }
            }
        }

        timer.getTime( "Got analytics values" );

        return map;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Returns a mapping of permutation keys and mappings of data element operands
     * and values based on the given query.
     * 
     * @param params the data query parameters.
     */
    private Map<String, Map<DimensionalItemObject, Double>> getPermutationDimensionalItemValueMap( DataQueryParams params )
    {
        List<Indicator> indicators = asTypedList( params.getIndicators() );
        
        Map<String, Double> valueMap = getAggregatedDataValueMap( params, indicators );
        
        return DataQueryParams.getPermutationDimensionalItemValueMap( valueMap );
    }

    /**
     * Returns a mapping between dimension items and values for the given data 
     * query and list of indicators. The dimensional items part of the indicator 
     * numerators and denominators are used as dimensional item for the aggregated
     * values being retrieved.
     * 
     * @param params the query.
     * @param indicators the list of indicators.
     * @return a dimensional items to aggregate values map.
     */
    private Map<String, Double> getAggregatedDataValueMap( DataQueryParams params, List<Indicator> indicators )
    {
        List<DimensionalItemObject> items = Lists.newArrayList( expressionService.getDimensionalItemObjectsInIndicators( indicators ) );

        DimensionalObject dimension = new BaseDimensionalObject( DimensionalObject.DATA_X_DIM_ID, DimensionType.DATA_X, null, DISPLAY_NAME_DATA_X, items );
        
        DataQueryParams dataSourceParams = params.instance();
        dataSourceParams.removeDimension( DimensionalObject.DATA_X_DIM_ID );
        dataSourceParams.addDimension( dimension );
        dataSourceParams.setSkipHeaders( true );
        dataSourceParams.setSkipMeta( true );
        
        Grid grid = getAggregatedDataValueGridInternal( dataSourceParams );
        
        return grid.getAsMap( grid.getWidth() - 1, DimensionalObject.DIMENSION_SEP );
    }
    
    /**
     * Returns a mapping between identifiers and names for the given dimensional
     * objects.
     *
     * @param params the data query parameters.
     * @return a mapping between identifiers and names.
     */
    private Map<String, String> getUidNameMap( DataQueryParams params )
    {
        List<DimensionalObject> dimensions = params.getDimensionsAndFilters();
        
        Map<String, String> map = new HashMap<>();
        
        Calendar calendar = PeriodType.getCalendar();
        
        Locale locale = i18nService.getCurrentLocale();

        for ( DimensionalObject dimension : dimensions )
        {
            List<DimensionalItemObject> items = new ArrayList<>( dimension.getItems() );
            
            i18nService.internationalise( items, locale );

            for ( DimensionalItemObject object : items )
            {
                if ( DimensionType.PERIOD.equals( dimension.getDimensionType() ) && !calendar.isIso8601() )
                {
                    Period period = (Period) object;
                    DateTimeUnit dateTimeUnit = calendar.fromIso( period.getStartDate() );
                    map.put( period.getPeriodType().getIsoDate( dateTimeUnit ), period.getDisplayName() );
                }
                else
                {
                    map.put( object.getDimensionItem(), object.getDisplayProperty( params.getDisplayProperty() ) );
                }

                if ( DimensionType.ORGANISATION_UNIT.equals( dimension.getDimensionType() ) && params.isHierarchyMeta() )
                {
                    OrganisationUnit unit = (OrganisationUnit) object;
                    
                    map.putAll( NameableObjectUtils.getUidDisplayPropertyMap( unit.getAncestors(), params.getDisplayProperty() ) );
                }
            }

            map.put( dimension.getDimension(), dimension.getDisplayProperty( params.getDisplayProperty() ) );
        }

        return map;
    }
    
    /**
     * Returns a mapping between the category option combo identifiers and names
     * in the given grid.
     *
     * @param params the data query parameters.
     * @param a mapping between identifiers and names.
     */
    private Map<String, String> getCocNameMap( DataQueryParams params )
    {
        Map<String, String> metaData = new HashMap<>();

        List<DimensionalItemObject> des = params.getAllDataElements();

        if ( des != null && !des.isEmpty() )
        {
            Set<DataElementCategoryCombo> categoryCombos = new HashSet<>();

            for ( DimensionalItemObject de : des )
            {
                DataElement dataElement = (DataElement) de;

                if ( dataElement.hasCategoryCombo() )
                {
                    categoryCombos.add( dataElement.getCategoryCombo() );
                }
            }

            for ( DataElementCategoryCombo cc : categoryCombos )
            {
                for ( DataElementCategoryOptionCombo coc : cc.getOptionCombos() )
                {
                    metaData.put( coc.getUid(), coc.getName() );
                }
            }
        }

        return metaData;
    }
    
    /**
     * Gets the number of available cores. Uses explicit number from system
     * setting if available. Detects number of cores from current server runtime
     * if not.
     * 
     * @return the number of available cores.
     */
    private int getProcessNo()
    {
        Integer cores = (Integer) systemSettingManager.getSystemSetting( SettingKey.DATABASE_SERVER_CPUS );

        return ( cores == null || cores == 0 ) ? SystemUtils.getCpuCores() : cores;
    }
}
