package org.hisp.dhis.program;

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

import java.util.regex.Pattern;

import org.hisp.dhis.analytics.AggregationType;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.MergeStrategy;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Chau Thu Tran
 */
@JacksonXmlRootElement( localName = "programIndicator", namespace = DxfNamespaces.DXF_2_0 )
public class ProgramIndicator
    extends BaseNameableObject
{
    public static final String SEPARATOR_ID = "\\.";
    public static final String SEP_OBJECT = ":";
    public static final String KEY_DATAELEMENT = "#";
    public static final String KEY_ATTRIBUTE = "A";
    public static final String KEY_PROGRAM_VARIABLE = "V";
    public static final String KEY_CONSTANT = "C";
    public static final String VAR_INCIDENT_DATE = "incident_date";
    public static final String VAR_ENROLLMENT_DATE = "enrollment_date";
    public static final String VAR_CURRENT_DATE = "current_date";
    public static final String VAR_VALUE_COUNT = "value_count";
    public static final String VAR_ZERO_POS_VALUE_COUNT = "zero_pos_value_count";
    public static final String VALUE_TYPE_DATE = "date";
    public static final String VALUE_TYPE_INT = "int";
    
    private static final String EXPRESSION_REGEXP = "(" + KEY_DATAELEMENT + "|" + KEY_ATTRIBUTE + "|" + KEY_PROGRAM_VARIABLE + "|" + KEY_CONSTANT + ")\\{(\\w+|" + 
        VAR_INCIDENT_DATE + "|" + VAR_ENROLLMENT_DATE + "|" + VAR_CURRENT_DATE + ")" + SEPARATOR_ID + "?(\\w*)\\}";
    private static final String SQL_FUNC_REGEXP = "d2:(.+?)\\((.+?)\\)";

    public static final Pattern EXPRESSION_PATTERN = Pattern.compile( EXPRESSION_REGEXP );
    public static final Pattern SQL_FUNC_PATTERN = Pattern.compile( SQL_FUNC_REGEXP );
    public static final Pattern DATAELEMENT_PATTERN = Pattern.compile( KEY_DATAELEMENT + "\\{(\\w{11})" + SEPARATOR_ID + "(\\w{11})\\}" );
    public static final Pattern ATTRIBUTE_PATTERN = Pattern.compile( KEY_ATTRIBUTE + "\\{(\\w{11})\\}" );
    public static final Pattern VALUECOUNT_PATTERN = Pattern.compile( "V\\{(" + VAR_VALUE_COUNT + "|" + VAR_ZERO_POS_VALUE_COUNT + ")\\}" );
    
    public static final String VALID = "valid";
    public static final String EXPRESSION_NOT_WELL_FORMED = "expression_not_well_formed";
    public static final String INVALID_IDENTIFIERS_IN_EXPRESSION = "invalid_identifiers_in_expression";
    public static final String FILTER_NOT_EVALUATING_TO_TRUE_OR_FALSE = "filter_not_evaluating_to_true_or_false";

    private Program program;
    
    private String valueType;

    private String expression;
    
    private String filter;
    
    private AggregationType aggregationType;

    /**
     * Number of decimals to use for indicator value, null implies default.
     */
    private Integer decimals;

    private Boolean displayInForm;
    
    private String rootDate;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ProgramIndicator()
    {
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public boolean hasFilter()
    {
        return filter != null;
    }

    public boolean hasDecimals()
    {
        return decimals != null && decimals >= 0;
    }
    
    /**
     * Returns aggregation type, if not exists returns AVERAGE.
     */
    public AggregationType getAggregationTypeFallback()
    {
        return aggregationType != null ? aggregationType : AggregationType.AVERAGE;
    }

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Program getProgram()
    {
        return program;
    }

    public void setProgram( Program program )
    {
        this.program = program;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getValueType()
    {
        return valueType;
    }

    public void setValueType( String valueType )
    {
        this.valueType = valueType;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getExpression()
    {
        return expression;
    }

    public void setExpression( String expression )
    {
        this.expression = expression;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getFilter()
    {
        return filter;
    }

    public void setFilter( String filter )
    {
        this.filter = filter;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public AggregationType getAggregationType()
    {
        return aggregationType;
    }

    public void setAggregationType( AggregationType aggregationType )
    {
        this.aggregationType = aggregationType;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Integer getDecimals()
    {
        return decimals;
    }

    public void setDecimals( Integer decimals )
    {
        this.decimals = decimals;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getDisplayInForm()
    {
        return displayInForm;
    }

    public void setDisplayInForm( Boolean displayInForm )
    {
        this.displayInForm = displayInForm;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getRootDate()
    {
        return rootDate;
    }

    public void setRootDate( String rootDate )
    {
        this.rootDate = rootDate;
    }

    @Override
    public void mergeWith( IdentifiableObject other, MergeStrategy strategy )
    {
        super.mergeWith( other, strategy );

        if ( other.getClass().isInstance( this ) )
        {
            ProgramIndicator programIndicator = (ProgramIndicator) other;
            
            if ( strategy.isReplace() )
            {
                program = programIndicator.getProgram();
                valueType = programIndicator.getValueType();
                expression = programIndicator.getExpression();
                filter = programIndicator.getFilter();
                decimals = programIndicator.getDecimals();
                displayInForm = programIndicator.getDisplayInForm();
                rootDate = programIndicator.getRootDate();
            }
            else if ( strategy.isMerge() )
            {
                program = programIndicator.getProgram() == null ? program : programIndicator.getProgram();
                valueType = programIndicator.getValueType() == null ? valueType : programIndicator.getValueType();
                expression = programIndicator.getExpression() == null ? expression : programIndicator.getExpression();
                filter = programIndicator.getFilter() == null ? filter : programIndicator.getFilter();
                decimals = programIndicator.getDecimals() == null ? decimals : programIndicator.getDecimals();
                displayInForm = programIndicator.getDisplayInForm() == null ? displayInForm : programIndicator.getDisplayInForm();
                rootDate = programIndicator.getRootDate() == null ? rootDate : programIndicator.getRootDate();
            }
        }
    }
}
