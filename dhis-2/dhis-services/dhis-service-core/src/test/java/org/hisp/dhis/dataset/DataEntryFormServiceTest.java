package org.hisp.dhis.dataset;

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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.util.Collection;
import java.util.List;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormAssociation;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.period.PeriodType;
import org.junit.Test;

/**
 * @author Bharath
 * @version $Id$
 */
public class DataEntryFormServiceTest
    extends DhisSpringTest
{
    private PeriodStore periodStore;

    private DataSetService dataSetService;

    private DataEntryFormService dataEntryFormService;

    private PeriodType periodType;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws Exception
    {
        dataSetService = (DataSetService) getBean( DataSetService.ID );

        periodStore = (PeriodStore) getBean( PeriodStore.ID );

        dataEntryFormService = (DataEntryFormService) getBean( DataEntryFormService.ID );

        periodType = periodStore.getAllPeriodTypes().iterator().next();
    }

    // -------------------------------------------------------------------------
    // DataEntryForm
    // -------------------------------------------------------------------------

    @Test
    public void testAddDataEntryForm()
    {
        DataSet dataSetA = new DataSet( "DataSet-A", periodType );

        dataSetService.addDataSet( dataSetA );

        DataEntryForm dataEntryFormA = new DataEntryForm( "DataEntryForm-A");

        int dataEntryFormAid = dataEntryFormService.addDataEntryForm( dataEntryFormA, DataEntryFormAssociation.DATAENTRY_ASSOCIATE_DATASET, dataSetA.getId() );

        dataEntryFormA = dataEntryFormService.getDataEntryForm( dataEntryFormAid );

        assertEquals( dataEntryFormAid, dataEntryFormA.getId() );
        assertEquals( "DataEntryForm-A", dataEntryFormA.getName() );
    }

    @Test
    public void testUpdateDataEntryForm()
    {
        DataSet dataSetA = new DataSet( "DataSet-A", periodType );

        dataSetService.addDataSet( dataSetA );

        DataEntryForm dataEntryForm = new DataEntryForm( "DataEntryForm-A" );

        int id = dataEntryFormService.addDataEntryForm( dataEntryForm, DataEntryFormAssociation.DATAENTRY_ASSOCIATE_DATASET, dataSetA.getId() );

        dataEntryForm = dataEntryFormService.getDataEntryForm( id );

        assertEquals( "DataEntryForm-A", dataEntryForm.getName() );

        dataEntryForm.setName( "DataEntryForm-X" );

        dataEntryFormService.updateDataEntryForm( dataEntryForm );

        dataEntryForm = dataEntryFormService.getDataEntryForm( id );

        assertEquals( dataEntryForm.getName(), "DataEntryForm-X" );
    }

    @Test
    public void testDeleteAndGetDataEntryForm()
    {
        DataSet dataSetA = new DataSet( "DataSet-A", periodType );

        dataSetService.addDataSet( dataSetA );

        DataEntryForm dataEntryForm = new DataEntryForm( "DataEntryForm-A" );

        int id = dataEntryFormService.addDataEntryForm( dataEntryForm, DataEntryFormAssociation.DATAENTRY_ASSOCIATE_DATASET, dataSetA.getId() );

        dataEntryForm = dataEntryFormService.getDataEntryForm( id );

        assertNotNull( dataEntryFormService.getDataEntryForm( id ) );

        dataEntryFormService.deleteDataEntryForm( dataEntryFormService.getDataEntryForm( id ) );

        assertNull( dataEntryFormService.getDataEntryForm( id ) );
    }

    @Test
    public void testGetDataEntryFormByName()
    {
        DataSet dataSetA = new DataSet( "DataSet-A", periodType );

        dataSetService.addDataSet( dataSetA );

        DataEntryForm dataEntryForm = new DataEntryForm( "DataEntryForm-A" );

        int id = dataEntryFormService.addDataEntryForm( dataEntryForm, DataEntryFormAssociation.DATAENTRY_ASSOCIATE_DATASET, dataSetA.getId() );

        dataEntryForm = dataEntryFormService.getDataEntryForm( id );

        assertEquals( dataEntryFormService.getDataEntryFormByName( "DataEntryForm-A" ), dataEntryForm );
        assertNull( dataEntryFormService.getDataEntryFormByName( "DataEntryForm-X" ) );
    }

    @Test
    public void testGetDataEntryFormByDataSet()
    {
        DataSet dataSetA = new DataSet( "DataSet-A", periodType );

        dataSetService.addDataSet( dataSetA );

        DataEntryForm dataEntryForm = new DataEntryForm( "DataEntryForm-A" );

        int id = dataEntryFormService.addDataEntryForm( dataEntryForm, DataEntryFormAssociation.DATAENTRY_ASSOCIATE_DATASET, dataSetA.getId() );


        DataSet dataSetB = new DataSet( "DataSet-B", periodType );

        dataSetService.addDataSet( dataSetB );

        assertEquals( dataEntryFormService.getDataEntryFormByDataSet( dataSetA ).getId(), id );
        assertNull( dataEntryFormService.getDataEntryFormByDataSet( dataSetB ) );
    }

    @Test
    public void testGetAllDataEntryForms()
    {
        DataSet dataSetA = new DataSet( "DataSet-A", periodType );
        DataSet dataSetB = new DataSet( "DataSet-B", periodType );

        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );

        DataEntryForm dataEntryFormA = new DataEntryForm( "DataEntryForm-A" );
        DataEntryForm dataEntryFormB = new DataEntryForm( "DataEntryForm-B" );
        
        dataEntryFormService.addDataEntryForm( dataEntryFormA , DataEntryFormAssociation.DATAENTRY_ASSOCIATE_DATASET, dataSetA.getId());
        dataEntryFormService.addDataEntryForm( dataEntryFormB , DataEntryFormAssociation.DATAENTRY_ASSOCIATE_DATASET, dataSetB.getId());

        Collection<DataEntryForm> dataEntryForms = dataEntryFormService.getAllDataEntryForms();

        assertEquals( dataEntryForms.size(), 2 );
        assertTrue( dataEntryForms.contains( dataEntryFormA ) );
        assertTrue( dataEntryForms.contains( dataEntryFormB ) );
    }

    @Test
    public void testGetAvailableDataSets()
    {
        DataSet dataSetA = new DataSet( "DataSet-A", periodType );
        DataSet dataSetB = new DataSet( "DataSet-B", periodType );
        DataSet dataSetC = new DataSet( "DataSet-C", periodType );

        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );
        dataSetService.addDataSet( dataSetC );

        DataEntryForm dataEntryFormA = new DataEntryForm( "DataEntryForm-A" );
        DataEntryForm dataEntryFormB = new DataEntryForm( "DataEntryForm-B" );
        
        dataEntryFormService.addDataEntryForm( dataEntryFormA , DataEntryFormAssociation.DATAENTRY_ASSOCIATE_DATASET, dataSetA.getId());
        dataEntryFormService.addDataEntryForm( dataEntryFormB , DataEntryFormAssociation.DATAENTRY_ASSOCIATE_DATASET, dataSetB.getId());


        List<DataSet> dataSets = dataSetService.getAvailableDataSets();

        assertEquals( dataSets.size(), 1 );
    }

    @Test
    public void testGetAssignedDataSets()
    {
        DataSet dataSetA = new DataSet( "DataSet-A", periodType );
        DataSet dataSetB = new DataSet( "DataSet-B", periodType );
        DataSet dataSetC = new DataSet( "DataSet-C", periodType );

        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );
        dataSetService.addDataSet( dataSetC );

        DataEntryForm dataEntryFormA = new DataEntryForm( "DataEntryForm-A" );
        DataEntryForm dataEntryFormB = new DataEntryForm( "DataEntryForm-B" );
        
        dataEntryFormService.addDataEntryForm( dataEntryFormA , DataEntryFormAssociation.DATAENTRY_ASSOCIATE_DATASET, dataSetA.getId());
        dataEntryFormService.addDataEntryForm( dataEntryFormB , DataEntryFormAssociation.DATAENTRY_ASSOCIATE_DATASET, dataSetB.getId());


        List<DataSet> dataSets = dataSetService.getAssignedDataSets();

        assertEquals( dataSets.size(), 2 );
    }
}
