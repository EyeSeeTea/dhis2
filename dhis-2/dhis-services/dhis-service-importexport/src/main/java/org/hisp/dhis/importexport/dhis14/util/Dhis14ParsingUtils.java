package org.hisp.dhis.importexport.dhis14.util;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import static org.hisp.dhis.expression.Expression.SEPARATOR;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElementOperand;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class Dhis14ParsingUtils
{
    /**
     * Replaces occurences of {chr13} and {chr10} which represents carriage return
     * and new line with a space.
     * 
     * @param string the input string.
     * @return a replaced string.
     */
    public static String removeNewLine( String string )
    {
        if ( string != null )
        {
            string = string.replaceAll( "\\{chr1[03]\\}", " " );
        }
        
        return string;
    }    

    /**
     * Gets the operands and corresponding factor from the calculated data element
     * expression.
     * 
     * @param calculatedDataElement the calculated data element.
     * @return a map with operands and factors.
     */
    public static Map<DataElementOperand, Double> getOperandFactors( CalculatedDataElement calculatedDataElement )
    {
        Map<DataElementOperand, Double> factorMap = new HashMap<DataElementOperand, Double>();

        Pattern pattern = Pattern.compile( "\\[(\\d+\\.\\d+)\\]\\s*\\*\\s*(-?\\d+)" ); // "[id] * factor"

        Matcher matcher = pattern.matcher( calculatedDataElement.getExpression().getExpression() );

        while ( matcher.find() )
        {
            final DataElementOperand operand = new DataElementOperand();
            
            String operandString = matcher.group( 1 );
            
            operand.setDataElementId( Integer.valueOf( operandString.substring( 0, operandString.indexOf( SEPARATOR ) ) ) );
            operand.setOptionComboId( Integer.valueOf( operandString.substring( operandString.indexOf( SEPARATOR ) + 1, operandString.length() ) ) );
            
            factorMap.put( operand, Double.parseDouble( matcher.group( 2 ) ) );
        }

        return factorMap;
    }
}
