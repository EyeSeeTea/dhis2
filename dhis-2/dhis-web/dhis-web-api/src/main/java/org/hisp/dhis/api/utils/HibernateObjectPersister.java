package org.hisp.dhis.api.utils;

/*
 * Copyright (c) 2004-2011, University of Oslo
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

import org.hisp.dhis.dataelement.*;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Component
public class HibernateObjectPersister implements ObjectPersister
{
    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private DataElementCategoryService dataElementCategoryService;

    @Autowired
    private DataSetService dataSetService;

    public void persistDataElement( DataElement dataElement )
    {
        if ( dataElement.getCategoryCombo() != null )
        {
            DataElementCategoryCombo dataElementCategoryCombo = dataElementCategoryService.getDataElementCategoryCombo( dataElement.getCategoryCombo().getUid() );
            dataElement.setCategoryCombo( dataElementCategoryCombo );
        }

        Collection<DataElementGroup> dataElementGroups = new ArrayList<DataElementGroup>( dataElement.getGroups() );
        Collection<DataSet> dataSets = new ArrayList<DataSet>( dataElement.getDataSets() );
        dataElement.getGroups().clear();
        dataElement.getDataSets().clear();

        dataElementService.addDataElement( dataElement );

        System.err.println( "UID: " + dataElement.getUid() );

        for ( DataElementGroup dataElementGroup : dataElementGroups )
        {
            dataElementGroup = dataElementService.getDataElementGroup( dataElementGroup.getUid() );
            dataElement.addDataElementGroup( dataElementGroup );
        }

        for ( DataSet dataSet : dataSets )
        {
            dataSet = dataSetService.getDataSet( dataSet.getUid() );
            dataSet.addDataElement( dataElement );
        }

        dataElementService.updateDataElement( dataElement );
    }
}
