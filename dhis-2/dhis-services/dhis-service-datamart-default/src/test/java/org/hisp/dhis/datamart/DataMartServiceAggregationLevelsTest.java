package org.hisp.dhis.datamart;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.hisp.dhis.DhisConvenienceTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryComboService;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataMartServiceAggregationLevelsTest
    extends DhisConvenienceTest
{
    private DataMartInternalProcess dataMartInternalProcess;
    
    private DataMartStore dataMartStore;

    private DataElementCategoryCombo categoryCombo;
    
    private DataElementCategoryOptionCombo categoryOptionCombo;

    private Collection<Integer> dataElementIds;
    private Collection<Integer> periodIds;
    private Collection<Integer> organisationUnitIds;
    
    private DataElement dataElement;
    
    private Period period;
    
    private OrganisationUnit unitA;
    private OrganisationUnit unitB;
    private OrganisationUnit unitC;
    private OrganisationUnit unitD;
    private OrganisationUnit unitE;
    private OrganisationUnit unitF;
    private OrganisationUnit unitG;
    private OrganisationUnit unitH;
    private OrganisationUnit unitI;
    private OrganisationUnit unitJ; 
    private OrganisationUnit unitK;
    private OrganisationUnit unitL;
    private OrganisationUnit unitM;     

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        dataMartInternalProcess = (DataMartInternalProcess) getBean( DataMartInternalProcess.ID );

        dataMartStore = (DataMartStore) getBean( DataMartStore.ID );
        
        categoryOptionComboService = (DataElementCategoryOptionComboService) getBean( DataElementCategoryOptionComboService.ID );
        
        categoryComboService = (DataElementCategoryComboService) getBean( DataElementCategoryComboService.ID );
        
        dataElementService = (DataElementService) getBean( DataElementService.ID );
        
        periodService = (PeriodService) getBean( PeriodService.ID );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );

        dataValueService = (DataValueService) getBean( DataValueService.ID );

        categoryCombo = categoryComboService.getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );
        
        categoryOptionCombo = categoryOptionComboService.getDefaultDataElementCategoryOptionCombo();
        
        // ---------------------------------------------------------------------
        // Setup identifier Collections
        // ---------------------------------------------------------------------

        dataElementIds = new HashSet<Integer>();
        periodIds = new HashSet<Integer>();
        organisationUnitIds = new HashSet<Integer>();
        
        // ---------------------------------------------------------------------
        // Setup DataElements
        // ---------------------------------------------------------------------

        dataElement = createDataElement( 'A', DataElement.TYPE_INT, DataElement.AGGREGATION_OPERATOR_SUM, categoryCombo );
        
        dataElementIds.add( dataElementService.addDataElement( dataElement ) );
        
        // ---------------------------------------------------------------------
        // Setup Periods
        // ---------------------------------------------------------------------

        period = createPeriod( new MonthlyPeriodType(), getDate( 2005, 3, 1 ), getDate( 2005, 3, 31 ) );
        
        periodIds.add( periodService.addPeriod( period ) );
        
        // ---------------------------------------------------------------------
        // Setup OrganisationUnits
        // ---------------------------------------------------------------------

        unitA = createOrganisationUnit( 'A' );
        unitB = createOrganisationUnit( 'B', unitA );
        unitC = createOrganisationUnit( 'C', unitA );
        unitD = createOrganisationUnit( 'D', unitB );
        unitE = createOrganisationUnit( 'E', unitB );
        unitF = createOrganisationUnit( 'F', unitB );
        unitG = createOrganisationUnit( 'G', unitF);
        unitH = createOrganisationUnit( 'H', unitF );
        unitI = createOrganisationUnit( 'I', unitG );
        unitJ = createOrganisationUnit( 'J', unitG );
        unitK = createOrganisationUnit( 'K', unitI );
        unitL = createOrganisationUnit( 'L', unitI );
        unitM = createOrganisationUnit( 'M', unitI );

        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitA ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitB ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitC ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitD ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitE ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitF ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitG ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitH ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitI ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitJ ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitK ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitL ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitM ) );
        
        organisationUnitService.addOrganisationUnitHierarchy( new Date() ); //TODO

        // ---------------------------------------------------------------------
        // Setup DataValues
        // ---------------------------------------------------------------------

        dataValueService.addDataValue( createDataValue( dataElement, period, unitB, "20", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElement, period, unitC, "40", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElement, period, unitD, "60", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElement, period, unitE, "70", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElement, period, unitF, "90", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElement, period, unitG, "15", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElement, period, unitH, "75", categoryOptionCombo ) );       
        dataValueService.addDataValue( createDataValue( dataElement, period, unitI, "100", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElement, period, unitJ, "200", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElement, period, unitK, "55", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElement, period, unitL, "75", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElement, period, unitM, "60", categoryOptionCombo ) );        
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    public void testSumIntDataElementDataMart()
    {
        dataElement.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );
        dataElement.setType( DataElement.TYPE_INT );
        dataElement.setAggregationLevels( Arrays.asList( 3, 5 ) );        
        
        dataElementService.updateDataElement( dataElement );
        
        dataMartInternalProcess.export( dataElementIds, new ArrayList<Integer>(), periodIds, organisationUnitIds );
        
        assertEquals( 280.0, dataMartStore.getAggregatedValue( dataElement, period, unitA ) );
        assertEquals( 240.0, dataMartStore.getAggregatedValue( dataElement, period, unitB ) );
        assertEquals( 40.0, dataMartStore.getAggregatedValue( dataElement, period, unitC ) );
        assertEquals( 60.0, dataMartStore.getAggregatedValue( dataElement, period, unitD ) );
        assertEquals( 70.0, dataMartStore.getAggregatedValue( dataElement, period, unitE ) );
        assertEquals( 90.0, dataMartStore.getAggregatedValue( dataElement, period, unitF ) );
        assertEquals( 315.0, dataMartStore.getAggregatedValue( dataElement, period, unitG ) );
        assertEquals( 75.0, dataMartStore.getAggregatedValue( dataElement, period, unitH ) );
        assertEquals( 100.0, dataMartStore.getAggregatedValue( dataElement, period, unitI ) );
        assertEquals( 200.0, dataMartStore.getAggregatedValue( dataElement, period, unitJ ) );
        assertEquals( 55.0, dataMartStore.getAggregatedValue( dataElement, period, unitK ) );
        assertEquals( 75.0, dataMartStore.getAggregatedValue( dataElement, period, unitL ) );
        assertEquals( 60.0, dataMartStore.getAggregatedValue( dataElement, period, unitM ) );
    }    
}
