package org.hisp.dhis.dataelement;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * This object can act both as a hydrated persisted object and as a wrapper
 * object (but not both at the same time).
 * 
 * @author Abyot Asalefew
 * @version $Id$
 */
public class DataElementOperand
    implements Serializable, Comparable<DataElementOperand>
{
    public static final String SEPARATOR = ".";

    private static final String SPACE = "";

    private static final String COLUMN_PREFIX = "de";

    private static final String COLUMN_SEPARATOR = "_";

    // -------------------------------------------------------------------------
    // Persisted properties
    // -------------------------------------------------------------------------

    private int id;

    private DataElement dataElement;

    private DataElementCategoryOptionCombo categoryOptionCombo;

    // -------------------------------------------------------------------------
    // Transient properties
    // -------------------------------------------------------------------------

    private int dataElementId;

    private int optionComboId;

    private String operandId;

    private String operandName;

    private String valueType;

    private String aggregationOperator;

    private List<Integer> aggregationLevels = new ArrayList<Integer>();

    private int frequencyOrder;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataElementOperand()
    {
    }

    public DataElementOperand( DataElement dataElement, DataElementCategoryOptionCombo categoryOptionCombo )
    {
        this.dataElement = dataElement;
        this.categoryOptionCombo = categoryOptionCombo;
    }

    public DataElementOperand( int dataElementId, int optionComboId )
    {
        this.dataElementId = dataElementId;
        this.optionComboId = optionComboId;
        this.operandId = dataElementId + SEPARATOR + optionComboId;
    }

    public DataElementOperand( int dataElementId, int optionComboId, String operandName )
    {
        this.dataElementId = dataElementId;
        this.optionComboId = optionComboId;
        this.operandId = dataElementId + SEPARATOR + optionComboId;
        this.operandName = operandName;
    }

    public DataElementOperand( int dataElementId, int optionComboId, String operandName, String valueType,
        String aggregationOperator, List<Integer> aggregationLevels, int frequencyOrder )
    {
        this.dataElementId = dataElementId;
        this.optionComboId = optionComboId;
        this.operandId = dataElementId + SEPARATOR + optionComboId;
        this.operandName = operandName;
        this.valueType = valueType;
        this.aggregationOperator = aggregationOperator;
        this.aggregationLevels = aggregationLevels;
        this.frequencyOrder = frequencyOrder;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    /**
     * Tests whether the hierarchy level of the OrganisationUnit associated with
     * the relevant DataValue is equal to or higher than the relevant
     * aggregation level. Returns true if no aggregation levels exist.
     * 
     * @param organisationUnitLevel the hierarchy level of the aggregation
     *        OrganisationUnit.
     * @param dataValueLevel the hierarchy level of the OrganisationUnit
     *        associated with the relevant DataValue.
     */
    public boolean aggregationLevelIsValid( int organisationUnitLevel, int dataValueLevel )
    {
        if ( aggregationLevels == null || aggregationLevels.size() == 0 )
        {
            return true;
        }

        final Integer aggregationLevel = getRelevantAggregationLevel( organisationUnitLevel );

        return aggregationLevel == null || dataValueLevel <= aggregationLevel;
    }

    /**
     * Returns the relevant aggregation level for the DataElement. The relevant
     * aggregation level will be the next in ascending order after the
     * organisation unit level. If no aggregation levels lower than the
     * organisation unit level exist, null is returned.
     * 
     * @param organisationUnitLevel the hiearchy level of the relevant
     *        OrganisationUnit.
     */
    public Integer getRelevantAggregationLevel( int organisationUnitLevel )
    {
        Collections.sort( aggregationLevels );

        for ( final Integer aggregationLevel : aggregationLevels )
        {
            if ( aggregationLevel >= organisationUnitLevel )
            {
                return aggregationLevel;
            }
        }

        return null;
    }

    /**
     * Generates a DataElementOperand based on the given formula. The formula
     * needs to be on the form "[<dataelementid>,<categoryoptioncomboid>]".
     * 
     * @param formula the formula.
     * @return a DataElementOperand.
     */
    public static DataElementOperand getOperand( String formula )
    {
        formula = formula.replaceAll( "[\\[\\]]", "" ); //TODO fix
        
        final int dataElementId = Integer.parseInt( formula.substring( 0, formula.indexOf( SEPARATOR ) ) );
        final int categoryOptionComboId = Integer.parseInt( formula.substring( formula.indexOf( SEPARATOR ) + 1,
            formula.length() ) );

        return new DataElementOperand( dataElementId, categoryOptionComboId );
    }

    /**
     * Returns a name based on the DataElement and the
     * DataElementCategoryOptionCombo.
     * 
     * @return the name.
     */
    public String getPersistedName()
    {
        return dataElement.getName() + SPACE + categoryOptionCombo.getName();
    }

    /**
     * Returns an id based on the DataElement and the
     * DataElementCategoryOptionCombo.
     * 
     * @return the id.
     */
    public String getPersistedId()
    {
        return dataElement.getId() + SEPARATOR + categoryOptionCombo.getId();
    }

    public String getSimpleName()
    {
        return COLUMN_PREFIX + dataElementId + COLUMN_SEPARATOR + optionComboId;
    }
    
    public String getPrettyName( DataElement dataElement, DataElementCategoryOptionCombo categoryOptionCombo )
    {
        if ( dataElement == null || categoryOptionCombo == null )
        {
            return null;
        }
        
        return categoryOptionCombo.isDefault() ? dataElement.getName() : dataElement.getName() + SPACE + categoryOptionCombo.getName();
    }

    public void updateProperties( DataElement dataElement, DataElementCategoryOptionCombo categoryOptionCombo )
    {        
        this.dataElementId = dataElement.getId();
        this.optionComboId = categoryOptionCombo.getId();
        this.operandId = dataElement.getId() + SEPARATOR + categoryOptionCombo.getId();
        this.operandName = getPrettyName( dataElement, categoryOptionCombo );
        this.aggregationOperator = dataElement.getAggregationOperator();
        this.frequencyOrder = dataElement.getFrequencyOrder();
        this.aggregationLevels = new ArrayList<Integer>( dataElement.getAggregationLevels() );
        this.valueType = dataElement.getType();
    }

    // -------------------------------------------------------------------------
    // Getters & setters
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public DataElement getDataElement()
    {
        return dataElement;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
    }

    public DataElementCategoryOptionCombo getCategoryOptionCombo()
    {
        return categoryOptionCombo;
    }

    public void setCategoryOptionCombo( DataElementCategoryOptionCombo categoryOptionCombo )
    {
        this.categoryOptionCombo = categoryOptionCombo;
    }

    public int getDataElementId()
    {
        return dataElementId;
    }

    public void setDataElementId( int dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    public int getOptionComboId()
    {
        return optionComboId;
    }

    public void setOptionComboId( int optionComboId )
    {
        this.optionComboId = optionComboId;
    }

    public String getOperandId()
    {
        return operandId;
    }

    public void setOperandId( String operandId )
    {
        this.operandId = operandId;
    }

    public String getOperandName()
    {
        return operandName;
    }

    public void setOperandName( String operandName )
    {
        this.operandName = operandName;
    }

    public String getValueType()
    {
        return valueType;
    }

    public void setValueType( String valueType )
    {
        this.valueType = valueType;
    }

    public String getAggregationOperator()
    {
        return aggregationOperator;
    }

    public void setAggregationOperator( String aggregationOperator )
    {
        this.aggregationOperator = aggregationOperator;
    }

    public List<Integer> getAggregationLevels()
    {
        return aggregationLevels;
    }

    public void setAggregationLevels( List<Integer> aggregationLevels )
    {
        this.aggregationLevels = aggregationLevels;
    }

    public int getFrequencyOrder()
    {
        return frequencyOrder;
    }

    public void setFrequencyOrder( int frequencyOrder )
    {
        this.frequencyOrder = frequencyOrder;
    }

    // -------------------------------------------------------------------------
    // hashCode, equals, toString, compareTo
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((categoryOptionCombo == null) ? 0 : categoryOptionCombo.hashCode());
        result = prime * result + ((dataElement == null) ? 0 : dataElement.hashCode());
        result = prime * result + dataElementId;
        result = prime * result + optionComboId;

        return result;
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }

        if ( object == null )
        {
            return false;
        }

        if ( getClass() != object.getClass() )
        {
            return false;
        }

        final DataElementOperand other = (DataElementOperand) object;

        if ( categoryOptionCombo == null )
        {
            if ( other.categoryOptionCombo != null )
            {
                return false;
            }
        }
        else if ( !categoryOptionCombo.equals( other.categoryOptionCombo ) )
        {
            return false;
        }

        if ( dataElement == null )
        {
            if ( other.dataElement != null )
            {
                return false;
            }
        }
        else if ( !dataElement.equals( other.dataElement ) )
        {
            return false;
        }

        if ( dataElementId != other.dataElementId )
        {
            return false;
        }

        if ( optionComboId != other.optionComboId )
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return "[DataElementId: " + dataElementId + ", CategoryOptionComboId: " + optionComboId + "]";
    }

    public int compareTo( DataElementOperand other )
    {
        if ( this.getDataElementId() != other.getDataElementId() )
        {
            return this.getDataElementId() - other.getDataElementId();
        }

        return this.getOptionComboId() - other.getOptionComboId();
    }

    public String toJSON()
    {
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append( "{" );
        stringBuffer.append( "\"dataElement\":{\"id\":\"" + this.dataElementId + "\", \"name\":\""
            + StringEscapeUtils.escapeJavaScript( this.dataElement.getName() ) + "\",\"shortName\":\""
            + StringEscapeUtils.escapeJavaScript( this.dataElement.getShortName() ) + "\",\"type\":\""
            + this.dataElement.getType() + "\"}" );

        stringBuffer.append( ",\"optionCombo\":{\"id\":\"" + this.optionComboId + "\",\"name\":\""
            + StringEscapeUtils.escapeJavaScript( this.categoryOptionCombo.getName() ) + "\"}" );

        stringBuffer.append( "}" );

        return stringBuffer.toString();
    }
}
