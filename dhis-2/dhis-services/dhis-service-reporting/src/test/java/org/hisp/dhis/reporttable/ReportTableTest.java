package org.hisp.dhis.reporttable;

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
import static junit.framework.Assert.assertTrue;
import static org.hisp.dhis.reporttable.ReportTable.getColumnIdentifier;
import static org.hisp.dhis.reporttable.ReportTable.getColumnName;
import static org.hisp.dhis.reporttable.ReportTable.getIdentifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.DhisTest;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.mock.MockI18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.RelativePeriods;
import org.hisp.dhis.period.YearlyPeriodType;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ReportTableTest
    extends DhisTest
{
    private List<DataElement> dataElements;
    private List<DataElementCategoryOptionCombo> categoryOptionCombos;
    private List<Indicator> indicators;
    private List<DataSet> dataSets;
    private List<Period> periods;
    private List<Period> relativePeriods;
    private List<OrganisationUnit> units;

    private PeriodType montlyPeriodType;
    private PeriodType yearPeriodType;

    private DataElement dataElementA;
    private DataElement dataElementB;
    
    private DataElementCategoryOptionCombo categoryOptionComboA;
    private DataElementCategoryOptionCombo categoryOptionComboB;

    private DataElementCategoryCombo categoryCombo;
    
    private IndicatorType indicatorType;
    
    private Indicator indicatorA;
    private Indicator indicatorB;

    private DataSet dataSetA;
    private DataSet dataSetB;
    
    private Period periodA;
    private Period periodB;
    private Period periodC;
    private Period periodD;
    
    private OrganisationUnit unitA;
    private OrganisationUnit unitB;
    
    private RelativePeriods relatives;

    private I18nFormat i18nFormat;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws Exception
    {
        dataElements = new ArrayList<DataElement>();
        categoryOptionCombos = new ArrayList<DataElementCategoryOptionCombo>();
        indicators = new ArrayList<Indicator>();
        dataSets = new ArrayList<DataSet>();
        periods = new ArrayList<Period>();
        relativePeriods = new ArrayList<Period>();
        units = new ArrayList<OrganisationUnit>();
        
        montlyPeriodType = PeriodType.getPeriodTypeByName( MonthlyPeriodType.NAME );
        yearPeriodType = PeriodType.getPeriodTypeByName( YearlyPeriodType.NAME );

        dataElementA = createDataElement( 'A' );
        dataElementB = createDataElement( 'B' );
        
        dataElementA.setId( 'A' );
        dataElementB.setId( 'B' );
        
        dataElements.add( dataElementA );
        dataElements.add( dataElementB );
        
        categoryOptionComboA = createCategoryOptionCombo( 'A', 'A', 'B' );
        categoryOptionComboB = createCategoryOptionCombo( 'B', 'C', 'D' );
        
        categoryOptionComboA.setId( 'A' );
        categoryOptionComboB.setId( 'B' );
        
        categoryOptionCombos.add( categoryOptionComboA );
        categoryOptionCombos.add( categoryOptionComboB );

        categoryCombo = new DataElementCategoryCombo( "CategoryComboA" );
        categoryCombo.setId( 'A' );
        categoryCombo.setOptionCombos( new HashSet<DataElementCategoryOptionCombo>( categoryOptionCombos ) );
        
        indicatorType = createIndicatorType( 'A' );

        indicatorA = createIndicator( 'A', indicatorType );
        indicatorB = createIndicator( 'B', indicatorType );
        
        indicatorA.setId( 'A' );
        indicatorB.setId( 'B' );
                
        indicators.add( indicatorA );
        indicators.add( indicatorB );
        
        dataSetA = createDataSet( 'A', montlyPeriodType );
        dataSetB = createDataSet( 'B', montlyPeriodType );
        
        dataSetA.setId( 'A' );
        dataSetB.setId( 'B' );
        
        dataSets.add( dataSetA );
        dataSets.add( dataSetB );        
        
        periodA = createPeriod( montlyPeriodType, getDate( 2008, 1, 1 ), getDate( 2008, 1, 31 ) );
        periodB = createPeriod( montlyPeriodType, getDate( 2008, 2, 1 ), getDate( 2008, 2, 28 ) );
        
        periodA.setId( 'A' );
        periodB.setId( 'B' );
        
        periods.add( periodA );
        periods.add( periodB );
        
        periodC = createPeriod( montlyPeriodType, getDate( 2008, 3, 1 ), getDate( 2008, 3, 31 ) );
        periodD = createPeriod( yearPeriodType, getDate( 2008, 1, 1 ), getDate( 2008, 12, 31 ) );
        
        periodC.setId( 'C' );
        periodD.setId( 'D' );
        
        periodC.setName( RelativePeriods.REPORTING_MONTH );
        periodD.setName( RelativePeriods.THIS_YEAR );
        
        relativePeriods.add( periodC );
        relativePeriods.add( periodD );
        
        unitA = createOrganisationUnit( 'A' );
        unitB = createOrganisationUnit( 'B' );
        
        unitA.setId( 'A' );
        unitB.setId( 'B' );
        
        units.add( unitA );
        units.add( unitB );
        
        relatives = new RelativePeriods();
        
        relatives.setReportingMonth( true );
        relatives.setThisYear( true );

        i18nFormat = new MockI18nFormat();
    }
    
    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testGetColumnIdentifier()
    {
        IdentifiableObject[] a1 = { unitA, periodA };
        IdentifiableObject[] a2 = { indicatorA };
        
        IdentifiableObject[] b1 = { periodA };
        IdentifiableObject[] b2 = { indicatorA, unitA };
        
        assertNotNull( getColumnIdentifier( a1, a2 ) );
        assertNotNull( getColumnIdentifier( b1, b2 ) );
        assertEquals( getColumnIdentifier( a1, a2 ), getColumnIdentifier( b1, b2 ) );
        
        Integer identifier = getColumnIdentifier( getIdentifier( unitA.getClass(), unitA.getId() ), 
            getIdentifier( periodA.getClass(), periodA.getId() ), getIdentifier( indicatorA.getClass(), indicatorA.getId() ) );
        
        assertEquals( getColumnIdentifier( a1, a2 ), identifier );

        identifier = getColumnIdentifier( getIdentifier( periodA.getClass(), periodA.getId() ), 
            getIdentifier( indicatorA.getClass(), indicatorA.getId() ), getIdentifier( unitA.getClass(), unitA.getId() ) );
        
        assertEquals( getColumnIdentifier( b1, b2 ), identifier );
    }
    
    @Test
    public void testGetColumnName()
    {
        IdentifiableObject[] a1 = { unitA, periodC };
        
        assertNotNull( getColumnName( a1 ) );
        assertEquals( "shortnamea_reporting_month", getColumnName( a1 ) );
        
        IdentifiableObject[] a2 = { unitB, periodD };

        assertNotNull( getColumnName( a2 ) );
        assertEquals( "shortnameb_year", getColumnName( a2 ) );        
    }
    
    @Test
    public void testIndicatorReportTableA()
    {
        ReportTable reportTable = new ReportTable( "Embezzlement", ReportTable.MODE_INDICATORS, false,
            new ArrayList<DataElement>(), indicators, new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(), 
            null, true, true, false, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        List<String> indexColumns = reportTable.getIndexColumns();
        
        assertNotNull( indexColumns );
        assertEquals( 1, indexColumns.size() );
        assertTrue( indexColumns.contains( ReportTable.ORGANISATIONUNIT_ID ) );
        
        List<String> indexNameColumns = reportTable.getIndexNameColumns();

        assertNotNull( indexNameColumns );
        assertEquals( 1, indexNameColumns.size() );
        assertTrue( indexNameColumns.contains( ReportTable.ORGANISATIONUNIT_NAME ) );
        
        List<IdentifiableObject[]> columns = reportTable.getColumns();

        assertNotNull( columns ); 
        assertEquals( 8, columns.size() );
        
        Iterator<IdentifiableObject[]> iterator = columns.iterator();
        
        IdentifiableObject[] a1 = { indicatorA, periodA };
        Arrays.equals( a1, iterator.next() );
        IdentifiableObject[] a2 = { indicatorA, periodB };
        Arrays.equals( a2, iterator.next() );
        IdentifiableObject[] a3 = { indicatorA, periodC };
        Arrays.equals( a3, iterator.next() );
        IdentifiableObject[] a4 = { indicatorA, periodD };
        Arrays.equals( a4, iterator.next() );
        IdentifiableObject[] a5 = { indicatorB, periodA };
        Arrays.equals( a5, iterator.next() );
        IdentifiableObject[] a6 = { indicatorB, periodB };
        Arrays.equals( a6, iterator.next() );
        IdentifiableObject[] a7 = { indicatorB, periodC };
        Arrays.equals( a7, iterator.next() );
        IdentifiableObject[] a8 = { indicatorB, periodD };
        Arrays.equals( a8, iterator.next() );
            
        List<String> columnNames = reportTable.getColumnNames();
        
        assertNotNull( columnNames );        
        assertEquals( 8, columnNames.size() );
        
        assertTrue( columnNames.contains( "shortnamea_reporting_month" ) );
        assertTrue( columnNames.contains( "shortnamea_year" ) );
        assertTrue( columnNames.contains( "shortnameb_reporting_month" ) );
        assertTrue( columnNames.contains( "shortnameb_year" ) );
        
        List<IdentifiableObject[]> rows = reportTable.getRows();
        
        assertNotNull( rows );
        assertEquals( 2, rows.size() );
        
        iterator = rows.iterator();
        
        IdentifiableObject[] b1 = { unitA };
        Arrays.equals( b1, iterator.next() );
        IdentifiableObject[] b2 = { unitB };
        Arrays.equals( b2, iterator.next() );
    }

    @Test
    public void testIndicatorReportTableB()
    {
        ReportTable reportTable = new ReportTable( "Embezzlement", ReportTable.MODE_INDICATORS, false,
            new ArrayList<DataElement>(), indicators, new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(), 
            null, false, false, true, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        List<String> indexColumns = reportTable.getIndexColumns();

        assertNotNull( indexColumns );
        assertEquals( 2, indexColumns.size() );
        assertTrue( indexColumns.contains( ReportTable.INDICATOR_ID ) );
        assertTrue( indexColumns.contains( ReportTable.PERIOD_ID ) );        

        List<String> indexNameColumns = reportTable.getIndexNameColumns();

        assertNotNull( indexNameColumns );
        assertEquals( 2, indexNameColumns.size() );
        assertTrue( indexNameColumns.contains( ReportTable.INDICATOR_NAME ) );
        assertTrue( indexNameColumns.contains( ReportTable.PERIOD_NAME ) );        

        List<IdentifiableObject[]> columns = reportTable.getColumns();
        
        assertNotNull( columns );
        assertEquals( 2, columns.size() );

        Iterator<IdentifiableObject[]> iterator = columns.iterator();
        
        IdentifiableObject[] a1 = { unitA };
        Arrays.equals( a1, iterator.next() );
        IdentifiableObject[] a2 = { unitB };
        Arrays.equals( a2, iterator.next() );
        
        List<String> columnNames = reportTable.getColumnNames();
        
        assertNotNull( columnNames );
        assertEquals( 2, columnNames.size() );
        
        assertTrue( columnNames.contains( "shortnamea" ) );
        assertTrue( columnNames.contains( "shortnameb" ) );
        
        List<IdentifiableObject[]> rows = reportTable.getRows();
        
        assertNotNull( rows );
        assertEquals( 8, rows.size() );
        
        iterator = rows.iterator();
        
        IdentifiableObject[] b1 = { indicatorA, periodA };
        Arrays.equals( b1, iterator.next() );
        IdentifiableObject[] b2 = { indicatorA, periodB };
        Arrays.equals( b2, iterator.next() );
        IdentifiableObject[] b3 = { indicatorA, periodC };
        Arrays.equals( b3, iterator.next() );
        IdentifiableObject[] b4 = { indicatorA, periodD };
        Arrays.equals( b4, iterator.next() );
        IdentifiableObject[] b5 = { indicatorB, periodA };
        Arrays.equals( b5, iterator.next() );
        IdentifiableObject[] b6 = { indicatorB, periodB };
        Arrays.equals( b6, iterator.next() );
        IdentifiableObject[] b7 = { indicatorB, periodC };
        Arrays.equals( b7, iterator.next() );
        IdentifiableObject[] b8 = { indicatorB, periodD };
        Arrays.equals( b8, iterator.next() );            
    }

    @Test
    public void testIndicatorReportTableC()
    {        
        ReportTable reportTable = new ReportTable( "Embezzlement", ReportTable.MODE_INDICATORS, false, 
            new ArrayList<DataElement>(), indicators, new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(), 
            null, true, false, true, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        List<String> indexColumns = reportTable.getIndexColumns();

        assertNotNull( indexColumns );
        assertEquals( 1, indexColumns.size() );
        assertTrue( indexColumns.contains( ReportTable.PERIOD_ID ) );

        List<String> indexNameColumns = reportTable.getIndexNameColumns();

        assertNotNull( indexNameColumns );
        assertEquals( 1, indexNameColumns.size() );
        assertTrue( indexNameColumns.contains( ReportTable.PERIOD_NAME ) );

        List<IdentifiableObject[]> columns = reportTable.getColumns();
        
        assertNotNull( columns );
        assertEquals( 4, columns.size() );

        Iterator<IdentifiableObject[]> iterator = columns.iterator();
        
        IdentifiableObject[] a1 = { indicatorA, unitA };
        Arrays.equals( a1, iterator.next() );
        IdentifiableObject[] a2 = { indicatorA, unitB };
        Arrays.equals( a2, iterator.next() );
        IdentifiableObject[] a3 = { indicatorB, unitA };
        Arrays.equals( a3, iterator.next() );
        IdentifiableObject[] a4 = { indicatorB, unitB };
        Arrays.equals( a4, iterator.next() );
        
        List<String> columnNames = reportTable.getColumnNames();
        
        assertNotNull( columnNames );
        assertEquals( 4, columnNames.size() );
        
        assertTrue( columnNames.contains( "shortnamea_shortnamea" ) );
        assertTrue( columnNames.contains( "shortnamea_shortnameb" ) );
        assertTrue( columnNames.contains( "shortnameb_shortnamea" ) );
        assertTrue( columnNames.contains( "shortnameb_shortnameb" ) );
        
        List<IdentifiableObject[]> rows = reportTable.getRows();
        
        assertNotNull( rows );
        assertEquals( 4, rows.size() );

        iterator = rows.iterator();
        
        IdentifiableObject[] b1 = { periodA };
        Arrays.equals( b1, iterator.next() );
        IdentifiableObject[] b2 = { periodB };
        Arrays.equals( b2, iterator.next() );
        IdentifiableObject[] b3 = { periodC };
        Arrays.equals( b3, iterator.next() );
        IdentifiableObject[] b4 = { periodD };
        Arrays.equals( b4, iterator.next() );
    }

    @Test
    public void testDataElementReportTableA()
    {
        ReportTable reportTable = new ReportTable( "Embezzlement", ReportTable.MODE_DATAELEMENTS, false,
            dataElements, new ArrayList<Indicator>(), new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(), 
            null, true, true, false, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        List<String> indexColumns = reportTable.getIndexColumns();
        
        assertNotNull( indexColumns );
        assertEquals( 1, indexColumns.size() );
        assertTrue( indexColumns.contains( ReportTable.ORGANISATIONUNIT_ID ) );
        
        List<String> indexNameColumns = reportTable.getIndexNameColumns();

        assertNotNull( indexNameColumns );
        assertEquals( 1, indexNameColumns.size() );
        assertTrue( indexNameColumns.contains( ReportTable.ORGANISATIONUNIT_NAME ) );

        List<IdentifiableObject[]> columns = reportTable.getColumns();
        
        assertNotNull( columns );
        assertEquals( 8, columns.size() );
        
        List<String> columnNames = reportTable.getColumnNames();
        
        assertNotNull( columnNames );
        assertEquals( 8, columnNames.size() );
        
        assertTrue( columnNames.contains( "shortnamea_year" ) );
        assertTrue( columnNames.contains( "shortnamea_reporting_month" ) );
        assertTrue( columnNames.contains( "shortnameb_year" ) );
        assertTrue( columnNames.contains( "shortnameb_reporting_month" ) );
        
        List<IdentifiableObject[]> rows = reportTable.getRows();
        
        assertNotNull( rows );
        assertEquals( 2, rows.size() );
    }

    @Test
    public void testDataElementReportTableB()
    {
        ReportTable reportTable = new ReportTable( "Embezzlement", ReportTable.MODE_DATAELEMENTS, false,
            dataElements, new ArrayList<Indicator>(), new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(), 
            null, false, false, true, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        List<String> indexColumns = reportTable.getIndexColumns();

        assertNotNull( indexColumns );
        assertEquals( 2, indexColumns.size() );
        assertTrue( indexColumns.contains( ReportTable.INDICATOR_ID ) );
        assertTrue( indexColumns.contains( ReportTable.PERIOD_ID ) );

        List<String> indexNameColumns = reportTable.getIndexNameColumns();

        assertNotNull( indexNameColumns );
        assertEquals( 2, indexNameColumns.size() );
        assertTrue( indexNameColumns.contains( ReportTable.INDICATOR_NAME ) );
        assertTrue( indexNameColumns.contains( ReportTable.PERIOD_NAME ) );

        List<IdentifiableObject[]> columns = reportTable.getColumns();
        
        assertNotNull( columns );
        assertEquals( 2, columns.size() );
        
        List<String> columnNames = reportTable.getColumnNames();
        
        assertNotNull( columnNames );
        assertEquals( 2, columnNames.size() );
        
        assertTrue( columnNames.contains( "shortnamea" ) );
        assertTrue( columnNames.contains( "shortnameb" ) );
        
        List<IdentifiableObject[]> rows = reportTable.getRows();
        
        assertNotNull( rows );
        assertEquals( 8, rows.size() );        
    }

    @Test
    public void testDataElementReportTableC()
    {
        ReportTable reportTable = new ReportTable( "Embezzlement", ReportTable.MODE_DATAELEMENTS, false,
            dataElements, new ArrayList<Indicator>(), new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(), 
            null, true, false, true, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        List<String> indexColumns = reportTable.getIndexColumns();

        assertNotNull( indexColumns );
        assertEquals( 1, indexColumns.size() );
        assertTrue( indexColumns.contains( ReportTable.PERIOD_ID ) );

        List<String> indexNameColumns = reportTable.getIndexNameColumns();

        assertNotNull( indexNameColumns );
        assertEquals( 1, indexNameColumns.size() );
        assertTrue( indexNameColumns.contains( ReportTable.PERIOD_NAME ) );

        List<IdentifiableObject[]> columns = reportTable.getColumns();
        
        assertNotNull( columns );
        assertEquals( 4, columns.size() );
        
        List<String> columnNames = reportTable.getColumnNames();
        
        assertNotNull( columnNames );
        assertEquals( 4, columnNames.size() );
        
        assertTrue( columnNames.contains( "shortnamea_shortnamea" ) );
        assertTrue( columnNames.contains( "shortnamea_shortnameb" ) );
        assertTrue( columnNames.contains( "shortnameb_shortnamea" ) );
        assertTrue( columnNames.contains( "shortnameb_shortnameb" ) );
        
        List<IdentifiableObject[]> rows = reportTable.getRows();
        
        assertNotNull( rows );
        assertEquals( 4, rows.size() );
    }
/*
    @Test
    public void testDataElementWithCategoryOptionReportTableA()
    {
        ReportTable reportTable = new ReportTable( "Embezzlement", ReportTable.MODE_DATAELEMENTS, false,
            dataElements, new ArrayList<Indicator>(), new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(), 
            categoryCombo, true, true, false, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        List<String> indexColumns = reportTable.getIndexColumns();
        
        assertNotNull( indexColumns );
        assertEquals( 1, indexColumns.size() );
        assertTrue( indexColumns.contains( ReportTable.ORGANISATIONUNIT_ID ) );
        
        List<String> indexNameColumns = reportTable.getIndexNameColumns();

        assertNotNull( indexNameColumns );
        assertEquals( 1, indexNameColumns.size() );
        assertTrue( indexNameColumns.contains( ReportTable.ORGANISATIONUNIT_NAME ) );
        
        List<String> selectColumns = reportTable.getSelectColumns();
        
        assertNotNull( selectColumns );
        assertEquals( 3, selectColumns.size() );
        assertTrue( selectColumns.contains( ReportTable.DATAELEMENT_ID ) );
        assertTrue( selectColumns.contains( ReportTable.CATEGORYCOMBO_ID ) );        
        assertTrue( selectColumns.contains( ReportTable.PERIOD_ID ) );
        
        List<String> crossTabColumns = reportTable.getCrossTabColumns();
        
        assertNotNull( crossTabColumns );        
        assertEquals( 16, crossTabColumns.size() );
        
        assertTrue( crossTabColumns.contains( "shortnamea_categoryoptiona_categoryoptionb_reporting_month" ) );
        assertTrue( crossTabColumns.contains( "shortnamea_categoryoptiona_categoryoptionb_year" ) );
        assertTrue( crossTabColumns.contains( "shortnamea_categoryoptionc_categoryoptiond_reporting_month" ) );
        assertTrue( crossTabColumns.contains( "shortnamea_categoryoptionc_categoryoptiond_year" ) );
        assertTrue( crossTabColumns.contains( "shortnameb_categoryoptiona_categoryoptionb_reporting_month" ) );
        assertTrue( crossTabColumns.contains( "shortnameb_categoryoptiona_categoryoptionb_year" ) );
        assertTrue( crossTabColumns.contains( "shortnameb_categoryoptionc_categoryoptiond_reporting_month" ) );
        assertTrue( crossTabColumns.contains( "shortnameb_categoryoptionc_categoryoptiond_year" ) );
        
        List<String> crossTabIdentifiers = reportTable.getCrossTabIdentifiers();
        
        assertNotNull( crossTabIdentifiers );
        assertEquals( 16, crossTabIdentifiers.size() );

        assertTrue( crossTabIdentifiers.contains( DATAELEMENT_ID + Integer.valueOf( 'A' ) + SEPARATOR + CATEGORYCOMBO_ID + Integer.valueOf( 'A' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'A' ) ) );
        assertTrue( crossTabIdentifiers.contains( DATAELEMENT_ID + Integer.valueOf( 'A' ) + SEPARATOR + CATEGORYCOMBO_ID + Integer.valueOf( 'A' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'B' ) ) );
        assertTrue( crossTabIdentifiers.contains( DATAELEMENT_ID + Integer.valueOf( 'A' ) + SEPARATOR + CATEGORYCOMBO_ID + Integer.valueOf( 'A' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'C' ) ) );
        assertTrue( crossTabIdentifiers.contains( DATAELEMENT_ID + Integer.valueOf( 'A' ) + SEPARATOR + CATEGORYCOMBO_ID + Integer.valueOf( 'A' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'D' ) ) );
        assertTrue( crossTabIdentifiers.contains( DATAELEMENT_ID + Integer.valueOf( 'B' ) + SEPARATOR + CATEGORYCOMBO_ID + Integer.valueOf( 'A' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'A' ) ) );
        assertTrue( crossTabIdentifiers.contains( DATAELEMENT_ID + Integer.valueOf( 'B' ) + SEPARATOR + CATEGORYCOMBO_ID + Integer.valueOf( 'A' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'B' ) ) );
        assertTrue( crossTabIdentifiers.contains( DATAELEMENT_ID + Integer.valueOf( 'B' ) + SEPARATOR + CATEGORYCOMBO_ID + Integer.valueOf( 'A' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'C' ) ) );
        assertTrue( crossTabIdentifiers.contains( DATAELEMENT_ID + Integer.valueOf( 'B' ) + SEPARATOR + CATEGORYCOMBO_ID + Integer.valueOf( 'A' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'D' ) ) );

        assertTrue( crossTabIdentifiers.contains( DATAELEMENT_ID + Integer.valueOf( 'A' ) + SEPARATOR + CATEGORYCOMBO_ID + Integer.valueOf( 'B' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'A' ) ) );
        assertTrue( crossTabIdentifiers.contains( DATAELEMENT_ID + Integer.valueOf( 'A' ) + SEPARATOR + CATEGORYCOMBO_ID + Integer.valueOf( 'B' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'B' ) ) );
        assertTrue( crossTabIdentifiers.contains( DATAELEMENT_ID + Integer.valueOf( 'A' ) + SEPARATOR + CATEGORYCOMBO_ID + Integer.valueOf( 'B' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'C' ) ) );
        assertTrue( crossTabIdentifiers.contains( DATAELEMENT_ID + Integer.valueOf( 'A' ) + SEPARATOR + CATEGORYCOMBO_ID + Integer.valueOf( 'B' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'D' ) ) );
        assertTrue( crossTabIdentifiers.contains( DATAELEMENT_ID + Integer.valueOf( 'B' ) + SEPARATOR + CATEGORYCOMBO_ID + Integer.valueOf( 'B' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'A' ) ) );
        assertTrue( crossTabIdentifiers.contains( DATAELEMENT_ID + Integer.valueOf( 'B' ) + SEPARATOR + CATEGORYCOMBO_ID + Integer.valueOf( 'B' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'B' ) ) );
        assertTrue( crossTabIdentifiers.contains( DATAELEMENT_ID + Integer.valueOf( 'B' ) + SEPARATOR + CATEGORYCOMBO_ID + Integer.valueOf( 'B' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'C' ) ) );
        assertTrue( crossTabIdentifiers.contains( DATAELEMENT_ID + Integer.valueOf( 'B' ) + SEPARATOR + CATEGORYCOMBO_ID + Integer.valueOf( 'B' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'D' ) ) );
        
        assertTrue( reportTable.getReportIndicators().contains( null ) );
        assertTrue( reportTable.getReportPeriods().contains( null ) );
        assertTrue( reportTable.getReportUnits().size() == 2 );
        
        Map<String, String> prettyCrossTabColumns = reportTable.getPrettyCrossTabColumns();
        
        assertNotNull( prettyCrossTabColumns );
        assertEquals( 16, prettyCrossTabColumns.size() );
    }

    @Test
    public void testDataElementWithCategoryOptionReportTableB()
    {
        ReportTable reportTable = new ReportTable( "Embezzlement", ReportTable.MODE_DATAELEMENTS, false,
            dataElements, new ArrayList<Indicator>(), new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(), 
            categoryCombo, false, false, true, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        List<String> indexColumns = reportTable.getIndexColumns();

        assertNotNull( indexColumns );
        assertEquals( 2, indexColumns.size() );
        assertTrue( indexColumns.contains( ReportTable.DATAELEMENT_ID ) );
        assertTrue( indexColumns.contains( ReportTable.PERIOD_ID ) );        

        List<String> indexNameColumns = reportTable.getIndexNameColumns();

        assertNotNull( indexNameColumns );
        assertEquals( 2, indexNameColumns.size() );
        assertTrue( indexNameColumns.contains( ReportTable.DATAELEMENT_NAME ) );
        assertTrue( indexNameColumns.contains( ReportTable.PERIOD_NAME ) );        

        List<String> selectColumns = reportTable.getSelectColumns();
        
        assertNotNull( selectColumns );
        assertEquals( 2, selectColumns.size() );
        assertTrue( selectColumns.contains( ReportTable.CATEGORYCOMBO_ID ) );
        assertTrue( selectColumns.contains( ReportTable.ORGANISATIONUNIT_ID ) );
        
        List<String> crossTabColumns = reportTable.getCrossTabColumns();
        
        assertNotNull( crossTabColumns );
        assertEquals( 4, crossTabColumns.size() );

        assertTrue( crossTabColumns.contains( "categoryoptiona_categoryoptionb_shortnamea" ) );
        assertTrue( crossTabColumns.contains( "categoryoptiona_categoryoptionb_shortnameb" ) );
        assertTrue( crossTabColumns.contains( "categoryoptionc_categoryoptiond_shortnamea" ) );
        assertTrue( crossTabColumns.contains( "categoryoptionc_categoryoptiond_shortnameb" ) );

        List<String> crossTabIdentifiers = reportTable.getCrossTabIdentifiers();
        
        assertNotNull( crossTabIdentifiers );
        assertEquals( 4, crossTabIdentifiers.size() );
        
        assertTrue( crossTabIdentifiers.contains( CATEGORYCOMBO_ID + Integer.valueOf( 'A' ) + SEPARATOR + ORGANISATIONUNIT_ID + Integer.valueOf( 'A' ) ) );
        assertTrue( crossTabIdentifiers.contains( CATEGORYCOMBO_ID + Integer.valueOf( 'A' ) + SEPARATOR + ORGANISATIONUNIT_ID + Integer.valueOf( 'B' ) ) );
        assertTrue( crossTabIdentifiers.contains( CATEGORYCOMBO_ID + Integer.valueOf( 'B' ) + SEPARATOR + ORGANISATIONUNIT_ID + Integer.valueOf( 'A' ) ) );
        assertTrue( crossTabIdentifiers.contains( CATEGORYCOMBO_ID + Integer.valueOf( 'B' ) + SEPARATOR + ORGANISATIONUNIT_ID + Integer.valueOf( 'B' ) ) );

        assertTrue( reportTable.getReportIndicators().size() == 2 );
        assertTrue( reportTable.getReportPeriods().size() == 4 );
        assertTrue( reportTable.getReportUnits().contains( null ) );
        
        Map<String, String> prettyCrossTabColumns = reportTable.getPrettyCrossTabColumns();
        
        assertNotNull( prettyCrossTabColumns );
        assertEquals( 4, prettyCrossTabColumns.size() );
    }

    @Test
    public void testDataElementWithCategoryOptionReportTableC()
    {
        //TODO
    }
*/
    @Test
    public void testDataSetReportTableA()
    {
        ReportTable reportTable = new ReportTable( "Embezzlement", ReportTable.MODE_DATASETS, false,
            new ArrayList<DataElement>(), new ArrayList<Indicator>(), dataSets, periods, relativePeriods, units, new ArrayList<OrganisationUnit>(), 
            null, true, true, false, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        List<String> indexColumns = reportTable.getIndexColumns();
        
        assertNotNull( indexColumns );
        assertEquals( 1, indexColumns.size() );
        assertTrue( indexColumns.contains( ReportTable.ORGANISATIONUNIT_ID ) );
        
        List<String> indexNameColumns = reportTable.getIndexNameColumns();

        assertNotNull( indexNameColumns );
        assertEquals( 1, indexNameColumns.size() );
        assertTrue( indexNameColumns.contains( ReportTable.ORGANISATIONUNIT_NAME ) );

        List<IdentifiableObject[]> columns = reportTable.getColumns();
        
        assertNotNull( columns );
        assertEquals( 8, columns.size() );
        
        List<String> columnNames = reportTable.getColumnNames();
        
        assertNotNull( columnNames );
        assertEquals( 8, columnNames.size() );
        
        assertTrue( columnNames.contains( "shortnamea_year" ) );
        assertTrue( columnNames.contains( "shortnamea_reporting_month" ) );
        assertTrue( columnNames.contains( "shortnameb_year" ) );
        assertTrue( columnNames.contains( "shortnameb_reporting_month" ) );
        
        List<IdentifiableObject[]> rows = reportTable.getRows();
        
        assertNotNull( rows );
        assertEquals( 2, rows.size() );
    }

    @Test
    public void testDataSetReportTableB()
    {
        ReportTable reportTable = new ReportTable( "Embezzlement", ReportTable.MODE_DATASETS, false,
            new ArrayList<DataElement>(), new ArrayList<Indicator>(), dataSets, periods, relativePeriods, units, new ArrayList<OrganisationUnit>(), 
            null, false, false, true, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        List<String> indexColumns = reportTable.getIndexColumns();

        assertNotNull( indexColumns );
        assertEquals( 2, indexColumns.size() );
        assertTrue( indexColumns.contains( ReportTable.INDICATOR_ID ) );
        assertTrue( indexColumns.contains( ReportTable.PERIOD_ID ) );        

        List<String> indexNameColumns = reportTable.getIndexNameColumns();

        assertNotNull( indexNameColumns );
        assertEquals( 2, indexNameColumns.size() );
        assertTrue( indexNameColumns.contains( ReportTable.INDICATOR_NAME ) );
        assertTrue( indexNameColumns.contains( ReportTable.PERIOD_NAME ) );        

        List<IdentifiableObject[]> columns = reportTable.getColumns();
        
        assertNotNull( columns );
        assertEquals( 2, columns.size() );
        
        List<String> columnNames = reportTable.getColumnNames();
        
        assertNotNull( columnNames );
        assertEquals( 2, columnNames.size() );
        
        assertTrue( columnNames.contains( "shortnamea" ) );
        assertTrue( columnNames.contains( "shortnameb" ) );
        
        List<IdentifiableObject[]> rows = reportTable.getRows();
        
        assertNotNull( rows );
        assertEquals( 8, rows.size() );
    }

    @Test
    public void testDataSetReportTableC()
    {        
        ReportTable reportTable = new ReportTable( "Embezzlement", ReportTable.MODE_DATASETS, false, 
            new ArrayList<DataElement>(), new ArrayList<Indicator>(), dataSets, periods, relativePeriods, units, new ArrayList<OrganisationUnit>(), 
            null, true, false, true, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        List<String> indexColumns = reportTable.getIndexColumns();

        assertNotNull( indexColumns );
        assertEquals( 1, indexColumns.size() );
        assertTrue( indexColumns.contains( ReportTable.PERIOD_ID ) );

        List<String> indexNameColumns = reportTable.getIndexNameColumns();

        assertNotNull( indexNameColumns );
        assertEquals( 1, indexNameColumns.size() );
        assertTrue( indexNameColumns.contains( ReportTable.PERIOD_NAME ) );

        List<IdentifiableObject[]> columns = reportTable.getColumns();
        
        assertNotNull( columns );
        assertEquals( 4, columns.size() );
        
        List<String> columnNames = reportTable.getColumnNames();
        
        assertNotNull( columnNames );
        assertEquals( 4, columnNames.size() );
        
        assertTrue( columnNames.contains( "shortnamea_shortnamea" ) );
        assertTrue( columnNames.contains( "shortnamea_shortnameb" ) );
        assertTrue( columnNames.contains( "shortnameb_shortnamea" ) );
        assertTrue( columnNames.contains( "shortnameb_shortnameb" ) );
        
        List<IdentifiableObject[]> rows = reportTable.getRows();
        
        assertNotNull( rows );
        assertEquals( 4, rows.size() );
    }
}
