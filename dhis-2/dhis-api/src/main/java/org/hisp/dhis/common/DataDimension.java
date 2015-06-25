package org.hisp.dhis.common;

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

import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementDomain;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.program.ProgramIndicator;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
* @author Lars Helge Overland
*/
public class DataDimension
{
    public enum DataDimensionType
    {
        INDICATOR, AGGREGATE_DATA_ELEMENT, DATA_ELEMENT_OPERAND, DATA_SET, 
        PROGRAM_INDICATOR, PROGRAM_DATA_ELEMENT, PROGRAM_ATTRIBUTE;
    }
   
    public static final Set<Class<? extends IdentifiableObject>> DATA_DIMENSION_CLASSES = ImmutableSet.<Class<? extends IdentifiableObject>>builder().
        add( Indicator.class ).add( DataElement.class ).add( DataElementOperand.class ).
        add( DataSet.class ).add( ProgramIndicator.class ).add( TrackedEntityAttribute.class ).build();
    
    public static final Map<DataDimensionType, Class<? extends NameableObject>> DATA_DIMENSION_TYPE_CLASS_MAP = ImmutableMap.<DataDimensionType, Class<? extends NameableObject>>builder().
        put( DataDimensionType.INDICATOR, Indicator.class ).put( DataDimensionType.AGGREGATE_DATA_ELEMENT, DataElement.class ).
        put( DataDimensionType.DATA_ELEMENT_OPERAND, DataElementOperand.class ).put( DataDimensionType.DATA_SET, DataSet.class ).
        put( DataDimensionType.PROGRAM_INDICATOR, ProgramIndicator.class ).put( DataDimensionType.PROGRAM_ATTRIBUTE, TrackedEntityAttribute.class ).
        put( DataDimensionType.PROGRAM_DATA_ELEMENT, DataElement.class ).build();
    
    public static final Map<DataDimensionType, DataElementDomain> DATA_DIMENSION_TYPE_DOMAIN_MAP = ImmutableMap.<DataDimension.DataDimensionType, DataElementDomain>builder().
        put( DataDimensionType.AGGREGATE_DATA_ELEMENT, DataElementDomain.AGGREGATE ).put( DataDimensionType.PROGRAM_DATA_ELEMENT, DataElementDomain.TRACKER ).build();
    
    private int id;
    
    // -------------------------------------------------------------------------
    // Data dimension objects
    // -------------------------------------------------------------------------

    private Indicator indicator;
    
    private DataElement dataElement;
    
    private DataElementOperand dataElementOperand;
    
    private DataSet dataSet;
    
    private ProgramIndicator programIndicator;
    
    private TrackedEntityAttribute trackedEntityAttribute;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public DataDimension()
    {
    }

    public static DataDimension create( NameableObject object )
    {
        DataDimension dimension = new DataDimension();
        
        if ( object.getClass().isAssignableFrom( Indicator.class ) )
        {
            dimension.setIndicator( (Indicator) object );
        }
        else if ( object.getClass().isAssignableFrom( DataElement.class ) )
        {
            dimension.setDataElement( (DataElement) object );
        }
        else if ( object.getClass().isAssignableFrom( DataElementOperand.class ) )
        {
            dimension.setDataElementOperand( (DataElementOperand) object );
        }
        else if ( object.getClass().isAssignableFrom( DataSet.class ) )
        {
            dimension.setDataSet( (DataSet) object );
        }
        else if ( object.getClass().isAssignableFrom( ProgramIndicator.class ) )
        {
            dimension.setProgramIndicator( (ProgramIndicator) object );
        }
        else if ( object.getClass().isAssignableFrom( TrackedEntityAttribute.class ) )
        {
            dimension.setTrackedEntityAttribute( (TrackedEntityAttribute) object );
        }
        
        return dimension;
    }
    
    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public NameableObject getNameableObject()
    {
        if ( indicator != null )
        {
            return indicator;
        }
        else if ( dataElement != null )
        {
            return dataElement;
        }
        else if ( dataElementOperand != null )
        {
            return dataElementOperand;
        }
        else if ( dataSet != null )
        {
            return dataSet;
        }
        else if ( programIndicator != null )
        {
            return programIndicator;
        }
        else if ( trackedEntityAttribute != null )
        {
            return trackedEntityAttribute;
        }
        
        throw new IllegalStateException( "Data dimension is null" );
    }
    
    // -------------------------------------------------------------------------
    // Get and set methods
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public Indicator getIndicator()
    {
        return indicator;
    }

    public void setIndicator( Indicator indicator )
    {
        this.indicator = indicator;
    }

    public DataElement getDataElement()
    {
        return dataElement;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
    }

    public DataElementOperand getDataElementOperand()
    {
        return dataElementOperand;
    }

    public void setDataElementOperand( DataElementOperand dataElementOperand )
    {
        this.dataElementOperand = dataElementOperand;
    }

    public DataSet getDataSet()
    {
        return dataSet;
    }

    public void setDataSet( DataSet dataSet )
    {
        this.dataSet = dataSet;
    }

    public ProgramIndicator getProgramIndicator()
    {
        return programIndicator;
    }

    public void setProgramIndicator( ProgramIndicator programIndicator )
    {
        this.programIndicator = programIndicator;
    }

    public TrackedEntityAttribute getTrackedEntityAttribute()
    {
        return trackedEntityAttribute;
    }

    public void setTrackedEntityAttribute( TrackedEntityAttribute trackedEntityAttribute )
    {
        this.trackedEntityAttribute = trackedEntityAttribute;
    }
}
