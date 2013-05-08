package org.hisp.dhis.common;

/*
 * Copyright (c) 2004-2005, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may
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

import static org.hisp.dhis.common.DimensionalObject.DIMENSION_OBJECT_TYPE_MAP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class DefaultDimensionService
    implements DimensionService
{
    @Autowired
    private IdentifiableObjectManager identifiableObjectManager;
    
    @Override
    public DimensionalObject getDimension( String uid )
    {        
        DataElementGroupSet degs = identifiableObjectManager.get( DataElementGroupSet.class, uid );
        
        if ( degs != null )
        {
            return degs;
        }
        
        OrganisationUnitGroupSet ougs = identifiableObjectManager.get( OrganisationUnitGroupSet.class, uid );
        
        if ( ougs != null )
        {
            return ougs;
        }
        
        DataElementCategory cat = identifiableObjectManager.get( DataElementCategory.class, uid );
        
        if ( cat != null )
        {
            return cat;
        }
        
        return null;
    }
    
    public DimensionType getDimensionType( String uid )
    {
        DataElementGroupSet degs = identifiableObjectManager.get( DataElementGroupSet.class, uid );
        
        if ( degs != null )
        {
            return DimensionType.DATAELEMENT_GROUPSET;
        }
        
        OrganisationUnitGroupSet ougs = identifiableObjectManager.get( OrganisationUnitGroupSet.class, uid );
        
        if ( ougs != null )
        {
            return DimensionType.ORGANISATIONUNIT_GROUPSET;
        }
        
        DataElementCategory cat = identifiableObjectManager.get( DataElementCategory.class, uid );
        
        if ( cat != null )
        {
            return DimensionType.CATEGORY;
        }
        
        return DIMENSION_OBJECT_TYPE_MAP.get( uid );
    }
    
    @Override
    public List<DimensionalObject> getAllDimensions()
    {
        Collection<DataElementGroupSet> degs = identifiableObjectManager.getAll( DataElementGroupSet.class );
        Collection<OrganisationUnitGroupSet> ougs = identifiableObjectManager.getAll( OrganisationUnitGroupSet.class );
        Collection<DataElementCategory> dcs = identifiableObjectManager.getAll( DataElementCategory.class );

        List<DimensionalObject> dimensions = new ArrayList<DimensionalObject>();
        
        dimensions.addAll( degs );
        dimensions.addAll( ougs );
        dimensions.addAll( dcs );
        
        return dimensions;
    }
}
