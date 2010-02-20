package org.hisp.dhis.importexport.dxf;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.Assert.assertEquals;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import org.hisp.dhis.DhisTest;
import org.hisp.dhis.datadictionary.DataDictionaryService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.dbms.DbmsManager;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.external.location.LocationManager;
import org.hisp.dhis.external.location.LocationManagerException;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportDataValueService;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportObjectStatus;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.ImportService;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.importexport.util.ImportExportUtils;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleService;
import org.junit.Ignore;
import org.junit.Test;

/**
 * <p>dxfA.zip contains 3 objects of each meta-data type.</p>
 * 
 * <p>dxfB.zip contains 5 objects of each meta-data type.</p>
 * 
 * <p>dxfC.zip contains 5 objects of each meta-data type with variations in properties in 3 of them, except for one-property objects.</p>
 * 
 * <p>dxfD.zip contains 2 objects of type DataElement, Period, and OrganisationUnit and 8 DataValues registered for all combinations.
 * 
 * <p>dxfE.zip contains 3 DataElements, 2 Periods, and 2 OrganisationUnits and 12 DataValues registered for all combinations.
 * 
 * <p>dxfF.zip contains 3 DataElements with variations in properties in 2 of them, 2 Periods, and 2 OrganisationUnits and 12 DataValues registered for all combinations.
 * 
 * <p>dxfG.zip contains 3 DataElements, 2 Periods, and 2 OrganisationUnits and 12 DataValues registered for all combinations with "20" as value instead of "10".
 * 
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DXFImportServiceTest
    extends DhisTest
{

    private final int dataASize = 3;

    private final int dataBSize = 5;

    private final int dataCSize = 5;

    private ImportService importService;

    private InputStream inputStreamA;

    private InputStream inputStreamAx;

    private InputStream inputStreamAz;

    private InputStream inputStreamB;

    private InputStream inputStreamC;

    private InputStream inputStreamD;

    private InputStream inputStreamE;

    private InputStream inputStreamF;

    private InputStream inputStreamG;

    private InputStream inputStreamH;

    private InputStream inputStreamSDMX;

    private InputStream inputStreamTransforms;

    private InputStream inputStreamSimpleXsl;

    private final static String TRANSFORMS = "transforms.xml";

    private final static String SIMPLEXSL = "changeroot.xsl";

    private ImportObjectService importObjectService;

    private ImportDataValueService importDataValueService;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------
    @Override
    public void setUpTest() throws LocationManagerException, IOException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        inputStreamA = classLoader.getResourceAsStream( "dxfA.zip" );
        inputStreamAx = classLoader.getResourceAsStream( "dxfA.xml" );
        inputStreamAz = classLoader.getResourceAsStream( "dxfA.xml.gz" );
        inputStreamB = classLoader.getResourceAsStream( "dxfB.zip" );
        inputStreamC = classLoader.getResourceAsStream( "dxfC.zip" );
        inputStreamD = classLoader.getResourceAsStream( "dxfD.zip" );
        inputStreamE = classLoader.getResourceAsStream( "dxfE.zip" );
        inputStreamF = classLoader.getResourceAsStream( "dxfF.zip" );
        inputStreamG = classLoader.getResourceAsStream( "dxfG.zip" );
        inputStreamH = classLoader.getResourceAsStream( "changeroot.xml" );
        inputStreamSDMX = classLoader.getResourceAsStream( "formattedCSDS2.xml" );

        inputStreamTransforms = classLoader.getResourceAsStream( TRANSFORMS );
        inputStreamSimpleXsl = classLoader.getResourceAsStream( SIMPLEXSL );

        importService = (ImportService) getBean( "org.hisp.dhis.importexport.XMLImportService" );

        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );

        dataElementService = (DataElementService) getBean( DataElementService.ID );

        dataDictionaryService = (DataDictionaryService) getBean( DataDictionaryService.ID );

        indicatorService = (IndicatorService) getBean( IndicatorService.ID );

        dataSetService = (DataSetService) getBean( DataSetService.ID );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );

        organisationUnitGroupService = (OrganisationUnitGroupService) getBean( OrganisationUnitGroupService.ID );

        dataValueService = (DataValueService) getBean( DataValueService.ID );

        validationRuleService = (ValidationRuleService) getBean( ValidationRuleService.ID );

        expressionService = (ExpressionService) getBean( ExpressionService.ID );

        importObjectService = (ImportObjectService) getBean( ImportObjectService.ID );

        importDataValueService = (ImportDataValueService) getBean( ImportDataValueService.ID );

        dbmsManager = (DbmsManager) getBean( DbmsManager.ID );

        locationManager = (LocationManager) getBean( LocationManager.ID );
        setExternalTestDir( locationManager );

        // horrible hack to copy some files into external test dir
        File extDir = null;
        extDir = locationManager.getFileForWriting( "." );
        if ( ( extDir != null ) && ( extDir.isDirectory() ) )
        {
            OutputStream transforms = null;
            OutputStream simplexsl = null;
            String transformPath = extDir.getPath() + "/transform";
            File transformDir = new File( transformPath );
            transformDir.mkdir();
            transforms = new FileOutputStream( transformPath + "/" + TRANSFORMS );
            simplexsl = new FileOutputStream( transformPath + "/" + SIMPLEXSL );
            copy( inputStreamTransforms, transforms );
            copy( inputStreamSimpleXsl, simplexsl );
            transforms.close();
            simplexsl.close();
        }

    }

    @Override
    public void tearDownTest()
        throws Exception
    {
        inputStreamA.close();
        inputStreamAx.close();
        inputStreamAz.close();
        inputStreamB.close();
        inputStreamC.close();
        inputStreamD.close();
        inputStreamE.close();
        inputStreamF.close();
        inputStreamH.close();
        inputStreamSDMX.close();

        inputStreamTransforms.close();
        inputStreamSimpleXsl.close();

        // clean up the mess ...
        removeExternalTestDir();
    }

    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------
    // TODO Improve test on duplicate GroupMemberAssociations
    @Test
    public void testSimpleImportWithTransform()
    {

        ImportParams importParams = ImportExportUtils.getImportParams( ImportStrategy.NEW_AND_UPDATES, false, false, false );

        importService.importData( importParams, inputStreamH );

        assertObjects( dataASize );
    }

    @Ignore
    @Test
    public void testSDMXImportWithTransform()
    {
        ImportParams importParams = ImportExportUtils.getImportParams( ImportStrategy.NEW_AND_UPDATES, false, false, false );

        importService.importData( importParams, inputStreamSDMX );

        assertEquals( dataElementService.getAllDataElements().size(), 1546 );

        assertEquals( organisationUnitService.getAllOrganisationUnits().size(), 1 );

        assertEquals( dataValueService.getAllDataValues().size(), 64 );
    }

    @Test
    public void testImportMetaData()
    {
        ImportParams importParams = ImportExportUtils.getImportParams( ImportStrategy.NEW_AND_UPDATES, false, false, false );

        importService.importData( importParams, inputStreamA );

        assertObjects( dataASize );
    }

    @Test
    public void testImportMetaDataFromXML()
    {
        ImportParams importParams = ImportExportUtils.getImportParams( ImportStrategy.NEW_AND_UPDATES, false, false, false );

        importService.importData( importParams, inputStreamAx );

        assertObjects( dataASize );
    }

    @Test
    public void testImportMetaDataFromGzip()
    {
        ImportParams importParams = ImportExportUtils.getImportParams( ImportStrategy.NEW_AND_UPDATES, false, false, false );

        importService.importData( importParams, inputStreamAz );

        assertObjects( dataASize );
    }

    @Test
    public void testImportMetaDataWithPreview()
    {
        ImportParams importParams = ImportExportUtils.getImportParams( ImportStrategy.NEW_AND_UPDATES, true, false, false );

        importService.importData( importParams, inputStreamA );

        assertImportObjects( ImportObjectStatus.NEW, dataASize );

        assertGroupMembers( dataASize );
    }

    @Test
    public void testImportMetaDataWithPreviewAndDuplicates()
    {
        ImportParams importParams = ImportExportUtils.getImportParams( ImportStrategy.NEW_AND_UPDATES, false, false, false );

        importService.importData( importParams, inputStreamA );

        assertObjects( dataASize );

        importParams = ImportExportUtils.getImportParams( ImportStrategy.NEW_AND_UPDATES, true, false, false );

        importService.importData( importParams, inputStreamB );

        assertImportObjects( ImportObjectStatus.NEW, dataBSize - dataASize );

        assertImportObjects( ImportObjectStatus.UPDATE, 0 );

        assertGroupMembers( dataBSize );
    }

    @Test
    public void testImportMetaDataWithPreviewAndUpdates()
    {
        ImportParams importParams = ImportExportUtils.getImportParams( ImportStrategy.NEW_AND_UPDATES, false, false, false );

        importService.importData( importParams, inputStreamA );

        assertObjects( dataASize );

        importParams = ImportExportUtils.getImportParams( ImportStrategy.NEW_AND_UPDATES, true, false, false );

        importService.importData( importParams, inputStreamC );

        assertUpdateableImportObjects( ImportObjectStatus.UPDATE, dataASize );

        assertImportObjects( ImportObjectStatus.NEW, dataCSize - dataASize );

        assertGroupMembers( dataCSize );
    }

    @Test
    public void testImportDataValues()
    {
        ImportParams importParams = ImportExportUtils.getImportParams( ImportStrategy.NEW_AND_UPDATES, false, true, false );

        importService.importData( importParams, inputStreamD );

        assertEquals( dataElementService.getAllDataElements().size(), 2 );

        assertEquals( organisationUnitService.getAllOrganisationUnits().size(), 2 );

        assertEquals( dataValueService.getAllDataValues().size(), 8 );
    }

    @Test
    public void testImportDataValuesWithUpdates()
    {
        ImportParams importParams = ImportExportUtils.getImportParams( ImportStrategy.NEW_AND_UPDATES, false, true, false );

        importService.importData( importParams, inputStreamE );

        assertEquals( 12, dataValueService.getAllDataValues().size() );

        for ( DataValue dataValue : dataValueService.getAllDataValues() )
        {
            assertEquals( "10", dataValue.getValue() );
        }

        dbmsManager.clearSession();

        importService.importData( importParams, inputStreamG );

        assertEquals( 12, dataValueService.getAllDataValues().size() );

        for ( DataValue dataValue : dataValueService.getAllDataValues() )
        {
            assertEquals( "20", dataValue.getValue() );
        }
    }

    @Test
    public void testImportDataValuesWithPreview()
    {
        ImportParams importParams = ImportExportUtils.getImportParams( ImportStrategy.NEW_AND_UPDATES, true, true, false );

        importService.importData( importParams, inputStreamD );

        assertEquals( importObjectService.getImportObjects( ImportObjectStatus.NEW, DataElement.class ).size(), 2 );

        assertEquals( importObjectService.getImportObjects( ImportObjectStatus.NEW, OrganisationUnit.class ).size(), 2 );

        assertEquals( importDataValueService.getImportDataValues( ImportObjectStatus.NEW ).size(), 8 );
    }

    @Test
    public void testImportDataValuesWithPreviewAndDuplicates()
    {
        ImportParams importParams = ImportExportUtils.getImportParams( ImportStrategy.NEW_AND_UPDATES, false, true, false );

        importService.importData( importParams, inputStreamD );

        assertEquals( dataElementService.getAllDataElements().size(), 2 );

        assertEquals( organisationUnitService.getAllOrganisationUnits().size(), 2 );

        assertEquals( dataValueService.getAllDataValues().size(), 8 );

        importParams = ImportExportUtils.getImportParams( ImportStrategy.NEW_AND_UPDATES, true, true, false );

        importService.importData( importParams, inputStreamE );

        assertEquals( importObjectService.getImportObjects( ImportObjectStatus.NEW, DataElement.class ).size(), 1 );

        assertEquals( importObjectService.getImportObjects( ImportObjectStatus.NEW, OrganisationUnit.class ).size(), 0 );

        assertEquals( importDataValueService.getImportDataValues( ImportObjectStatus.NEW ).size(), 12 );
    }

    @Test
    public void testImportDataValuesWithPreviewAndUpdates()
    {
        ImportParams importParams = ImportExportUtils.getImportParams( ImportStrategy.NEW_AND_UPDATES, false, true, false );

        importService.importData( importParams, inputStreamD );

        assertEquals( dataElementService.getAllDataElements().size(), 2 );

        assertEquals( organisationUnitService.getAllOrganisationUnits().size(), 2 );

        assertEquals( dataValueService.getAllDataValues().size(), 8 );

        importParams = ImportExportUtils.getImportParams( ImportStrategy.NEW_AND_UPDATES, true, true, false );

        importService.importData( importParams, inputStreamF );

        assertEquals( importObjectService.getImportObjects( ImportObjectStatus.NEW, DataElement.class ).size(), 1 );

        assertEquals( importObjectService.getImportObjects( ImportObjectStatus.NEW, OrganisationUnit.class ).size(), 0 );

        assertEquals( importObjectService.getImportObjects( ImportObjectStatus.UPDATE, DataElement.class ).size(), 2 );

        assertEquals( importDataValueService.getImportDataValues( ImportObjectStatus.NEW ).size(), 12 );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    private void assertObjects( int expectedSize )
    {
        assertEquals( categoryService.getAllDataElementCategoryOptions().size(), 5 ); // Including default

        assertEquals( categoryService.getAllDataElementCategories().size(), 3 ); // Including default

        assertEquals( categoryService.getAllDataElementCategoryCombos().size(), 3 ); // Including default

        assertEquals( categoryService.getAllDataElementCategoryOptionCombos().size(), 5 ); // Including default

        assertEquals( dataElementService.getAllDataElements().size(), expectedSize );

        assertEquals( dataElementService.getAllDataElementGroups().size(), expectedSize );

        for ( DataElementGroup group : dataElementService.getAllDataElementGroups() )
        {
            assertEquals( group.getMembers().size(), expectedSize );
        }

        assertEquals( indicatorService.getAllIndicatorTypes().size(), expectedSize );

        assertEquals( indicatorService.getAllIndicators().size(), expectedSize );

        assertEquals( indicatorService.getAllIndicatorGroups().size(), expectedSize );

        for ( IndicatorGroup group : indicatorService.getAllIndicatorGroups() )
        {
            assertEquals( group.getMembers().size(), expectedSize );
        }

        assertEquals( dataSetService.getAllDataSets().size(), expectedSize );

        for ( DataSet dataSet : dataSetService.getAllDataSets() )
        {
            assertEquals( dataSet.getDataElements().size(), expectedSize );
        }

        assertEquals( organisationUnitService.getAllOrganisationUnits().size(), expectedSize );

        assertEquals( organisationUnitGroupService.getAllOrganisationUnitGroups().size(), expectedSize );

        for ( OrganisationUnitGroup group : organisationUnitGroupService.getAllOrganisationUnitGroups() )
        {
            assertEquals( group.getMembers().size(), expectedSize );
        }

        assertEquals( organisationUnitGroupService.getAllOrganisationUnitGroupSets().size(), expectedSize );

        assertEquals( validationRuleService.getAllValidationRules().size(), expectedSize );
    }

    private void assertImportObjects( ImportObjectStatus status, int expectedSize )
    {
        assertUpdateableImportObjects( status, expectedSize );

        assertEquals( importObjectService.getImportObjects( status, DataElementGroup.class ).size(), expectedSize );

        assertEquals( importObjectService.getImportObjects( status, IndicatorType.class ).size(), expectedSize );

        assertEquals( importObjectService.getImportObjects( status, IndicatorGroup.class ).size(), expectedSize );

        assertEquals( importObjectService.getImportObjects( status, DataSet.class ).size(), expectedSize );

        assertEquals( importObjectService.getImportObjects( status, OrganisationUnitGroup.class ).size(), expectedSize );

        assertEquals( importObjectService.getImportObjects( status, OrganisationUnitGroupSet.class ).size(), expectedSize );

        assertEquals( importObjectService.getImportObjects( status, ValidationRule.class ).size(), expectedSize );
    }

    private void assertUpdateableImportObjects( ImportObjectStatus status, int expectedSize )
    {
        assertEquals( importObjectService.getImportObjects( status, DataElement.class ).size(), expectedSize );

        assertEquals( importObjectService.getImportObjects( status, Indicator.class ).size(), expectedSize );

        assertEquals( importObjectService.getImportObjects( status, OrganisationUnit.class ).size(), expectedSize );
    }

    private void assertGroupMembers( int expectedSize )
    {
        assertEquals( importObjectService.getImportObjects( GroupMemberType.DATAELEMENTGROUP ).size(), expectedSize * expectedSize );

        assertEquals( importObjectService.getImportObjects( GroupMemberType.INDICATORGROUP ).size(), expectedSize * expectedSize );

        assertEquals( importObjectService.getImportObjects( GroupMemberType.DATASET ).size(), expectedSize * expectedSize );

        assertEquals( importObjectService.getImportObjects( GroupMemberType.ORGANISATIONUNITGROUP ).size(), expectedSize * expectedSize );
    }

    // utility method to copy streams
    public void copy( InputStream in, OutputStream out ) throws IOException
    {
        byte buffer[] = new byte[1024 * 1024]; // 1MB
        while ( true )
        {
            int count = in.read( buffer );
            if ( count == -1 )
            {
                break;
            }
            out.write( buffer, 0, count );
        }
    }
}
