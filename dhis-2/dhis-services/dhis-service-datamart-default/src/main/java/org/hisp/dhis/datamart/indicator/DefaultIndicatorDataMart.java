package org.hisp.dhis.datamart.indicator;

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

import static org.hisp.dhis.datamart.util.ParserUtil.generateExpression;
import static org.hisp.dhis.options.SystemSettingManager.KEY_OMIT_INDICATORS_ZERO_NUMERATOR_DATAMART;
import static org.hisp.dhis.system.util.DateUtils.DAYS_IN_YEAR;
import static org.hisp.dhis.system.util.MathUtils.calculateExpression;
import static org.hisp.dhis.system.util.MathUtils.getRounded;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.hisp.dhis.aggregation.AggregatedIndicatorValue;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datamart.aggregation.cache.AggregationCache;
import org.hisp.dhis.datamart.aggregation.dataelement.DataElementAggregator;
import org.hisp.dhis.datamart.crosstab.CrossTabService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.jdbc.batchhandler.AggregatedIndicatorValueBatchHandler;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitHierarchy;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 * @version $Id: DefaultIndicatorDataMart.java 6069 2008-10-28 17:31:02Z larshelg $
 */
public class DefaultIndicatorDataMart
    implements IndicatorDataMart
{
    private static final int DECIMALS = 1;

    private static final String TRUE = "true";
    private static final String FALSE = "false";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataElementAggregator sumIntAggregator;

    public void setSumIntAggregator( DataElementAggregator sumIntDataElementAggregator )
    {
        this.sumIntAggregator = sumIntDataElementAggregator;
    }

    private DataElementAggregator averageIntAggregator;

    public void setAverageIntAggregator( DataElementAggregator averageIntDataElementAggregator )
    {
        this.averageIntAggregator = averageIntDataElementAggregator;
    }
    
    private DataElementAggregator averageIntSingleValueAggregator;

    public void setAverageIntSingleValueAggregator( DataElementAggregator averageIntSingleValueAggregator )
    {
        this.averageIntSingleValueAggregator = averageIntSingleValueAggregator;
    }

    private CrossTabService crossTabService;

    public void setCrossTabService( CrossTabService crossTabService )
    {
        this.crossTabService = crossTabService;
    }    
    
    private AggregationCache aggregationCache;

    public void setAggregationCache( AggregationCache aggregationCache )
    {
        this.aggregationCache = aggregationCache;
    }

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    @Autowired
    private BatchHandlerFactory batchHandlerFactory;

    // -------------------------------------------------------------------------
    // IndicatorDataMart implementation
    // -------------------------------------------------------------------------
    
    public int exportIndicatorValues( final Collection<Integer> indicatorIds, final Collection<Integer> periodIds, 
        final Collection<Integer> organisationUnitIds, final Collection<DataElementOperand> operands )
    {
        final Map<DataElementOperand, Integer> operandIndexMap = crossTabService.getOperandIndexMap( operands );
        
        final Collection<Indicator> indicators = indicatorService.getIndicators( indicatorIds );        
        final Collection<Period> periods = periodService.getPeriods( periodIds );
        final Collection<OrganisationUnit> organisationUnits = organisationUnitService.getOrganisationUnits( organisationUnitIds );

        final BatchHandler<AggregatedIndicatorValue> batchHandler = batchHandlerFactory.createBatchHandler( AggregatedIndicatorValueBatchHandler.class );

        batchHandler.init();

        final OrganisationUnitHierarchy hierarchy = organisationUnitService.getOrganisationUnitHierarchy().prepareChildren( organisationUnitIds );
        
        int count = 0;
        
        double annualizationFactor = 0.0;
        double factor = 0.0;
        double aggregatedValue = 0.0;
        double annualizedFactor = 0.0;
        
        final boolean omitZeroNumerator = (Boolean) systemSettingManager.getSystemSetting( KEY_OMIT_INDICATORS_ZERO_NUMERATOR_DATAMART, false );
        
        final AggregatedIndicatorValue indicatorValue = new AggregatedIndicatorValue();
        
        for ( final Period period : periods )
        {
            final PeriodType periodType = period.getPeriodType();
            
            final Map<DataElementOperand, Integer> sumOperandIndexMap = sumIntAggregator.getOperandIndexMap( operands, periodType, operandIndexMap );
            final Map<DataElementOperand, Integer> averageOperandIndexMap = averageIntAggregator.getOperandIndexMap( operands, periodType, operandIndexMap );
            final Map<DataElementOperand, Integer> averageSingleValueOperandIndexMap = averageIntSingleValueAggregator.getOperandIndexMap( operands, periodType, operandIndexMap );

            for ( final OrganisationUnit unit : organisationUnits )
            {
                final int level = aggregationCache.getLevelOfOrganisationUnit( unit.getId() );
                
                final Map<DataElementOperand, Double> sumIntValueMap = sumIntAggregator.getAggregatedValues( sumOperandIndexMap, period, unit, level, hierarchy );                
                final Map<DataElementOperand, Double> averageIntValueMap = averageIntAggregator.getAggregatedValues( averageOperandIndexMap, period, unit, level, hierarchy);
                final Map<DataElementOperand, Double> averageIntSingleValueMap = averageIntSingleValueAggregator.getAggregatedValues( averageSingleValueOperandIndexMap, period, unit, level, hierarchy );
                
                final Map<DataElementOperand, Double> valueMap = new HashMap<DataElementOperand, Double>( sumIntValueMap );
                valueMap.putAll( averageIntValueMap );
                valueMap.putAll( averageIntSingleValueMap );
                
                for ( final Indicator indicator : indicators )
                {
                    final double numeratorValue = calculateExpression( generateExpression( indicator.getNumerator(), valueMap ) );                    
                    final double denominatorValue = calculateExpression( generateExpression( indicator.getDenominator(), valueMap ) );

                    // ---------------------------------------------------------
                    // AggregatedIndicatorValue
                    // ---------------------------------------------------------

                    if ( denominatorValue != 0 && !( omitZeroNumerator && numeratorValue == 0 ) )
                    {
                        annualizationFactor = getAnnualizationFactor( indicator, period );
                        
                        factor = indicator.getIndicatorType().getFactor();
                        
                        aggregatedValue = ( numeratorValue / denominatorValue ) * factor * annualizationFactor;
                        
                        annualizedFactor = factor * annualizationFactor;

                        indicatorValue.clear();
                        
                        indicatorValue.setIndicatorId( indicator.getId() );
                        indicatorValue.setPeriodId( period.getId() );
                        indicatorValue.setPeriodTypeId( periodType.getId() );
                        indicatorValue.setOrganisationUnitId( unit.getId() );
                        indicatorValue.setLevel( level );
                        indicatorValue.setAnnualized( getAnnualizationString( indicator.getAnnualized() ) );
                        indicatorValue.setFactor( annualizedFactor );
                        indicatorValue.setValue( getRounded( aggregatedValue, DECIMALS ) );
                        indicatorValue.setNumeratorValue( getRounded( numeratorValue, DECIMALS ) );
                        indicatorValue.setDenominatorValue( getRounded( denominatorValue, DECIMALS ) );
                        
                        batchHandler.addObject( indicatorValue );
                        
                        count++;
                    }
                }
            }
        }
        
        batchHandler.flush();
        
        return count;
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    
    public static double getAnnualizationFactor( final Indicator indicator, final Period period )
    {
        double factor = 1.0;
        
        if ( indicator.getAnnualized() != null && indicator.getAnnualized() )
        {
            final int daysInPeriod = DateUtils.daysBetween( period.getStartDate(), period.getEndDate() ) + 1;
            
            factor = DAYS_IN_YEAR / daysInPeriod;
        }
        
        return factor;
    }
    
    public static String getAnnualizationString( final Boolean annualized )
    {
        return ( annualized == null || !annualized ) ? FALSE : TRUE;
    }
}
