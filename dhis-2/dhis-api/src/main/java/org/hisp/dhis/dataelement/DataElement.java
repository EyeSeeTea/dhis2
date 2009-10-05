package org.hisp.dhis.dataelement;

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

// import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.datadictionary.ExtendedDataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.period.PeriodType;

/**
 * A DataElement is a definition (meta-information about) of the entities that
 * are captured in the system. An example from public health care is a
 * DataElement representing the number BCG doses; A DataElement with "BCG dose"
 * as name, with type DataElement.TYPE_INT. DataElements can be structured
 * hierarchically, one DataElement can have a parent and a collection of
 * children. The sum of the children represent the same entity as the parent.
 * Hiearchies of DataElements are used to give more fine- or course-grained
 * representations of the entities.
 * 
 * @author Kristian Nordal
 * @version $Id: DataElement.java 5540 2008-08-19 10:47:07Z larshelg $
 */
public class DataElement
    extends IdentifiableObject
{
    public static final String TYPE_STRING = "string";

    public static final String TYPE_INT = "int";

    public static final String TYPE_BOOL = "bool";

    public static final String AGGREGATION_OPERATOR_SUM = "sum";

    public static final String AGGREGATION_OPERATOR_AVERAGE ="average";
    
    public static final String AGGREGATION_OPERATOR_COUNT = "count";

    /**
     * If this DataElement is active or not (enabled or disabled).
     */
    private boolean active;

    /**
     * The type of this DataElement; e.g. DataElement.TYPE_INT or
     * DataElement.TYPE_BOOL.
     */
    private String type;

    /**
     * The aggregation operator of this DataElement; e.g. DataElement.SUM og
     * DataElement.AVERAGE.
     */
    private String aggregationOperator;

    /**
     * A Collection of children DataElements.
     */
    private Set<DataElement> children = new HashSet<DataElement>();

    /**
     * The parent DataElement for this DataElement.
     */
    private DataElement parent;
    
    /**
     * Extended information about the DataElement.
     */
    private ExtendedDataElement extended;

    /**
     * A combination of categories to capture data.
     */    
    private DataElementCategoryCombo categoryCombo;
    
    /**
     * Defines a custom sort order.
     */
    private Integer sortOrder;
    
    /**
     * URL for lookup of additional information on the web.
     */
    private String url;
    
    /**
     * The data sets which this data element is a member of.
     */
    private Set<DataSet> dataSets = new HashSet<DataSet>();
    
    /**
     * The lower organisation unit levels for aggregation.
     */
    private List<Integer> aggregationLevels = new ArrayList<Integer>();
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataElement()
    {
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof DataElement) )
        {
            return false;
        }

        final DataElement other = (DataElement) o;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    /**
     * Returns the PeriodType of the DataElement, based on the PeriodType of the
     * DataSet which the DataElement is registered for.
     */
    public PeriodType getPeriodType()
    {
        return dataSets != null && dataSets.size() > 0 ? dataSets.iterator().next().getPeriodType() : null;
    }
    
    /**
     * Tests whether a PeriodType can be defined for the DataElement, which requires
     * that the DataElement is registered for DataSets with the same PeriodType.
     */
    public boolean periodTypeIsValid()
    {
        PeriodType periodType = null;
        
        for ( DataSet dataSet : dataSets )
        {
            if ( periodType != null && !periodType.equals( dataSet.getPeriodType() ) )
            {
                return false;
            }
            
            periodType = dataSet.getPeriodType();
        }
        
        return true;
    }
    
    public boolean hasAggregationLevels()
    {
        return aggregationLevels != null && aggregationLevels.size() > 0;
    }
    

    public boolean isActive()
    {
        return active;
    }

    public void setActive( boolean active )
    {
        this.active = active;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getAggregationOperator()
    {
        return aggregationOperator;
    }

    public void setAggregationOperator( String aggregationOperator )
    {
        this.aggregationOperator = aggregationOperator;
    }

    public Set<DataElement> getChildren()
    {
        return children;
    }

    public void setChildren( Set<DataElement> children )
    {
        this.children = children;
    }

    public DataElement getParent()
    {
        return parent;
    }

    public void setParent( DataElement parent )
    {
        this.parent = parent;
    }

    public ExtendedDataElement getExtended()
    {
        return extended;
    }

    public void setExtended( ExtendedDataElement extended )
    {
        this.extended = extended;
    }
    

    public DataElementCategoryCombo getCategoryCombo()
    {
        return categoryCombo;
    }

    public void setCategoryCombo( DataElementCategoryCombo categoryCombo )
    {
        this.categoryCombo = categoryCombo;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder( Integer sortOrder )
    {
        this.sortOrder = sortOrder;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    public Set<DataSet> getDataSets()
    {
        return dataSets;
    }

    public void setDataSets( Set<DataSet> dataSets )
    {
        this.dataSets = dataSets;
    }

    public List<Integer> getAggregationLevels()
    {
        return aggregationLevels;
    }

    public void setAggregationLevels( List<Integer> aggregationLevels )
    {
        this.aggregationLevels = aggregationLevels;
    }
}
