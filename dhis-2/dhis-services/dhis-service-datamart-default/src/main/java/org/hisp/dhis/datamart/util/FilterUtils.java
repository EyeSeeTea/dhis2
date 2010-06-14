package org.hisp.dhis.datamart.util;

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

import static org.hisp.dhis.dataelement.DataElement.AGGREGATION_OPERATOR_AVERAGE;
import static org.hisp.dhis.dataelement.DataElement.AGGREGATION_OPERATOR_SUM;
import static org.hisp.dhis.system.util.DateUtils.DAYS_IN_YEAR;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.DateUtils;

/**
 * @author Lars Helge Overland
 */
public class FilterUtils
{
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    
    public static Map<DataElementOperand, Integer> getSumOperands( Collection<DataElementOperand> operands, PeriodType periodType, Map<DataElementOperand, Integer> operandIndexMap )
    {
        Map<DataElementOperand, Integer> sumOperandIndexMap = new HashMap<DataElementOperand, Integer>();
        
        for ( final DataElementOperand operand : operands )
        {
            if ( operand.getAggregationOperator().equals( AGGREGATION_OPERATOR_SUM ) || 
                ( operand.getAggregationOperator().equals( AGGREGATION_OPERATOR_AVERAGE ) && operand.getFrequencyOrder() >= periodType.getFrequencyOrder() ) )
            {
                sumOperandIndexMap.put( operand, operandIndexMap.get( operand ) );
            }
        }
        
        return sumOperandIndexMap;
    }

    public static Map<DataElementOperand, Integer> getAvgOperands( Collection<DataElementOperand> operands, PeriodType periodType, Map<DataElementOperand, Integer> operandIndexMap )
    {
        Map<DataElementOperand, Integer> avgOperandIndexMap = new HashMap<DataElementOperand, Integer>();
        
        for ( final DataElementOperand operand : operands )
        {
            if ( operand.getAggregationOperator().equals( AGGREGATION_OPERATOR_AVERAGE ) &&
                operand.getFrequencyOrder() < periodType.getFrequencyOrder() )
            {
                avgOperandIndexMap.put( operand, operandIndexMap.get( operand ) );
            }
        }
        
        return avgOperandIndexMap;
    }
        
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
