package org.hisp.dhis.common;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.DimensionalView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
* @author Lars Helge Overland
*/
@JacksonXmlRootElement( localName = "dimension", namespace = DxfNamespaces.DXF_2_0)
public interface DimensionalObject
{
    final String DATA_X_DIM_ID = "dx"; // in, de, ds, do
    final String INDICATOR_DIM_ID = "in";
    final String DATAELEMENT_DIM_ID = "de";
    final String DATASET_DIM_ID = "ds";
    final String DATAELEMENT_OPERAND_ID = "do";
    final String CATEGORYOPTIONCOMBO_DIM_ID = "co";
    final String PERIOD_DIM_ID = "pe";
    final String ORGUNIT_DIM_ID = "ou";

    final Map<String, DimensionType> DIMENSION_OBJECT_TYPE_MAP = new HashMap<String, DimensionType>() { {
        DIMENSION_OBJECT_TYPE_MAP.put( DimensionalObject.DATA_X_DIM_ID, DimensionType.DATA_X );
        DIMENSION_OBJECT_TYPE_MAP.put( DimensionalObject.INDICATOR_DIM_ID, DimensionType.INDICATOR );
        DIMENSION_OBJECT_TYPE_MAP.put( DimensionalObject.DATAELEMENT_DIM_ID, DimensionType.DATAELEMENT );
        DIMENSION_OBJECT_TYPE_MAP.put( DimensionalObject.DATASET_DIM_ID, DimensionType.DATASET );
        DIMENSION_OBJECT_TYPE_MAP.put( DimensionalObject.DATAELEMENT_OPERAND_ID, DimensionType.DATAELEMENT_OPERAND );
        DIMENSION_OBJECT_TYPE_MAP.put( DimensionalObject.PERIOD_DIM_ID, DimensionType.PERIOD );
        DIMENSION_OBJECT_TYPE_MAP.put( DimensionalObject.ORGUNIT_DIM_ID, DimensionType.ORGANISATIONUNIT );
    } };
            
    @JsonProperty
    @JsonView({ DetailedView.class, DimensionalView.class })
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    String getDimension();
    
    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonView({ DetailedView.class, DimensionalView.class })
    @JacksonXmlElementWrapper( localName = "items", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "item", namespace = DxfNamespaces.DXF_2_0 )
    List<IdentifiableObject> getItems();
}
