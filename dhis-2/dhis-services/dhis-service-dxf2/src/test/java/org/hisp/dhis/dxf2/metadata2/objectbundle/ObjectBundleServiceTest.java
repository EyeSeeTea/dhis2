package org.hisp.dhis.dxf2.metadata2.objectbundle;

/*
 * Copyright (c) 2004-2016, University of Oslo
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

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.common.IdScheme;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.common.MergeMode;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dxf2.metadata2.MetadataExportService;
import org.hisp.dhis.feedback.ErrorCode;
import org.hisp.dhis.feedback.ErrorReport;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.node.NodeService;
import org.hisp.dhis.option.Option;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.preheat.PreheatErrorReport;
import org.hisp.dhis.preheat.PreheatIdentifier;
import org.hisp.dhis.preheat.PreheatMode;
import org.hisp.dhis.render.RenderFormat;
import org.hisp.dhis.render.RenderService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserGroup;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class ObjectBundleServiceTest
    extends DhisSpringTest
{
    @Autowired
    private ObjectBundleService objectBundleService;

    @Autowired
    private IdentifiableObjectManager manager;

    @Autowired
    private MetadataExportService metadataExportService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private RenderService _renderService;

    @Override
    protected void setUpTest() throws Exception
    {
        renderService = _renderService;
    }

    @Test
    public void testCreateObjectBundle()
    {
        ObjectBundleParams params = new ObjectBundleParams();
        ObjectBundle bundle = objectBundleService.create( params );

        assertNotNull( bundle );
    }

    @Test
    public void testCreateDoesPreheating()
    {
        DataElementGroup dataElementGroup = fromJson( "dxf2/degAUidRef.json", DataElementGroup.class );
        defaultSetup();

        ObjectBundleParams params = new ObjectBundleParams();
        params.setPreheatMode( PreheatMode.REFERENCE );
        params.addObject( dataElementGroup );

        ObjectBundle bundle = objectBundleService.create( params );

        assertNotNull( bundle );
        assertFalse( bundle.getPreheat().isEmpty() );
        assertFalse( bundle.getPreheat().isEmpty( PreheatIdentifier.UID ) );
        assertFalse( bundle.getPreheat().isEmpty( PreheatIdentifier.UID, DataElement.class ) );
        assertTrue( bundle.getPreheat().containsKey( PreheatIdentifier.UID, DataElement.class, "deabcdefghA" ) );
        assertTrue( bundle.getPreheat().containsKey( PreheatIdentifier.UID, DataElement.class, "deabcdefghB" ) );
        assertTrue( bundle.getPreheat().containsKey( PreheatIdentifier.UID, DataElement.class, "deabcdefghC" ) );
        assertFalse( bundle.getPreheat().containsKey( PreheatIdentifier.UID, DataElement.class, "deabcdefghD" ) );
    }

    @Test
    public void testObjectBundleShouldAddToObjectAndPreheat()
    {
        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode( ObjectBundleMode.VALIDATE );

        ObjectBundle bundle = objectBundleService.create( params );

        DataElementGroup dataElementGroup = fromJson( "dxf2/degAUidRef.json", DataElementGroup.class );
        bundle.addObject( dataElementGroup );

        assertTrue( bundle.getObjects().get( DataElementGroup.class ).contains( dataElementGroup ) );
        assertTrue( bundle.getPreheat().containsKey( PreheatIdentifier.UID, DataElementGroup.class, dataElementGroup.getUid() ) );
    }

    @Test
    public void testPreheatValidations() throws IOException
    {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService.fromMetadata(
            new ClassPathResource( "dxf2/de_validate1.json" ).getInputStream(), RenderFormat.JSON );

        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode( ObjectBundleMode.VALIDATE );
        params.setObjects( metadata );

        ObjectBundle bundle = objectBundleService.create( params );
        ObjectBundleValidation validate = objectBundleService.validate( bundle );
        assertFalse( validate.getErrorReports().isEmpty() );

        Map<ErrorCode, List<ErrorReport>> dataElementErrorReport = validate.getErrorReports().get( DataElement.class );
        assertFalse( dataElementErrorReport.isEmpty() );

        for ( ErrorCode errorCode : dataElementErrorReport.keySet() )
        {
            List<ErrorReport> errorReports = dataElementErrorReport.get( errorCode );
            assertFalse( errorReports.isEmpty() );

            for ( ErrorReport errorReport : errorReports )
            {
                assertTrue( PreheatErrorReport.class.isInstance( errorReport ) );
                PreheatErrorReport preheatErrorReport = (PreheatErrorReport) errorReport;
                assertEquals( PreheatIdentifier.UID, preheatErrorReport.getPreheatIdentifier() );

                if ( DataElementCategoryCombo.class.isInstance( preheatErrorReport.getValue() ) )
                {
                    assertEquals( "p0KPaWEg3cf", preheatErrorReport.getObjectReference().getUid() );
                }
                else if ( User.class.isInstance( preheatErrorReport.getValue() ) )
                {
                    assertEquals( "GOLswS44mh8", preheatErrorReport.getObjectReference().getUid() );
                }
                else if ( OptionSet.class.isInstance( preheatErrorReport.getValue() ) )
                {
                    assertEquals( "pQYCiuosBnZ", preheatErrorReport.getObjectReference().getUid() );
                }
            }
        }
    }

    @Test
    public void testPreheatValidationsWithCatCombo() throws IOException
    {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService.fromMetadata(
            new ClassPathResource( "dxf2/de_validate1.json" ).getInputStream(), RenderFormat.JSON );

        DataElementCategoryCombo categoryCombo = manager.getByName( DataElementCategoryCombo.class, "default" );
        categoryCombo.setUid( "p0KPaWEg3cf" );
        manager.update( categoryCombo );

        OptionSet optionSet = new OptionSet( "OptionSet: pQYCiuosBnZ" );
        optionSet.setAutoFields();
        optionSet.setUid( "pQYCiuosBnZ" );
        manager.save( optionSet );

        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode( ObjectBundleMode.VALIDATE );
        params.setObjects( metadata );

        ObjectBundle bundle = objectBundleService.create( params );
        ObjectBundleValidation validate = objectBundleService.validate( bundle );
        assertFalse( validate.getErrorReports().isEmpty() );

        Map<ErrorCode, List<ErrorReport>> dataElementErrorReport = validate.getErrorReports().get( DataElement.class );
        assertFalse( dataElementErrorReport.isEmpty() );

        for ( ErrorCode errorCode : dataElementErrorReport.keySet() )
        {
            List<ErrorReport> errorReports = dataElementErrorReport.get( errorCode );
            assertFalse( errorReports.isEmpty() );

            for ( ErrorReport errorReport : errorReports )
            {
                assertTrue( PreheatErrorReport.class.isInstance( errorReport ) );
                PreheatErrorReport preheatErrorReport = (PreheatErrorReport) errorReport;
                assertEquals( PreheatIdentifier.UID, preheatErrorReport.getPreheatIdentifier() );

                if ( DataElementCategoryCombo.class.isInstance( preheatErrorReport.getValue() ) )
                {
                    assertFalse( true );
                }
                else if ( User.class.isInstance( preheatErrorReport.getValue() ) )
                {
                    assertEquals( "GOLswS44mh8", preheatErrorReport.getObjectReference().getUid() );
                }
                else if ( OptionSet.class.isInstance( preheatErrorReport.getValue() ) )
                {
                    assertFalse( true );
                }
            }
        }
    }

    @Test
    public void testPreheatValidationsInvalidObjects() throws IOException
    {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService.fromMetadata(
            new ClassPathResource( "dxf2/de_validate2.json" ).getInputStream(), RenderFormat.JSON );

        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode( ObjectBundleMode.VALIDATE );
        params.setObjects( metadata );

        ObjectBundle bundle = objectBundleService.create( params );
        ObjectBundleValidation validate = objectBundleService.validate( bundle );

        assertFalse( validate.getErrorReports().isEmpty() );
        assertEquals( 2, validate.getErrorReports().get( DataElement.class ).size() );
    }

    @Test
    public void testUpdateRequiresValidReferencesUID() throws IOException
    {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService.fromMetadata(
            new ClassPathResource( "dxf2/de_validate4.json" ).getInputStream(), RenderFormat.JSON );

        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode( ObjectBundleMode.VALIDATE );
        params.setPreheatIdentifier( PreheatIdentifier.UID );
        params.setImportMode( ImportStrategy.UPDATE );
        params.setObjects( metadata );

        ObjectBundle bundle = objectBundleService.create( params );
        ObjectBundleValidation validate = objectBundleService.validate( bundle );

        assertEquals( 3, validate.getErrorReports( DataElement.class, ErrorCode.E5001 ).size() );
    }

    @Test
    public void testUpdateWithPersistedObjectsRequiresValidReferencesUID() throws IOException
    {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService.fromMetadata(
            new ClassPathResource( "dxf2/de_validate7.json" ).getInputStream(), RenderFormat.JSON );
        defaultSetup();

        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode( ObjectBundleMode.VALIDATE );
        params.setPreheatIdentifier( PreheatIdentifier.UID );
        params.setImportMode( ImportStrategy.UPDATE );
        params.setObjects( metadata );

        ObjectBundle bundle = objectBundleService.create( params );
        ObjectBundleValidation validate = objectBundleService.validate( bundle );

        assertEquals( 1, validate.getErrorReports( DataElement.class ).get( ErrorCode.E5001 ).size() );
        assertFalse( validate.getErrorReports( DataElement.class ).get( ErrorCode.E4000 ).isEmpty() );
        assertEquals( 0, bundle.getObjects().get( DataElement.class ).size() );
    }

    @Test
    public void testUpdateRequiresValidReferencesCODE() throws IOException
    {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService.fromMetadata(
            new ClassPathResource( "dxf2/de_validate5.json" ).getInputStream(), RenderFormat.JSON );

        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode( ObjectBundleMode.VALIDATE );
        params.setPreheatIdentifier( PreheatIdentifier.CODE );
        params.setImportMode( ImportStrategy.UPDATE );
        params.setObjects( metadata );

        ObjectBundle bundle = objectBundleService.create( params );
        ObjectBundleValidation validate = objectBundleService.validate( bundle );

        assertFalse( validate.getErrorReports( DataElement.class ).isEmpty() );
        assertEquals( 3, validate.getErrorReports( DataElement.class, ErrorCode.E5001 ).size() );
    }

    @Test
    public void testUpdateRequiresValidReferencesAUTO() throws IOException
    {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService.fromMetadata(
            new ClassPathResource( "dxf2/de_validate6.json" ).getInputStream(), RenderFormat.JSON );

        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode( ObjectBundleMode.VALIDATE );
        params.setPreheatIdentifier( PreheatIdentifier.AUTO );
        params.setImportMode( ImportStrategy.UPDATE );
        params.setObjects( metadata );

        ObjectBundle bundle = objectBundleService.create( params );
        ObjectBundleValidation validate = objectBundleService.validate( bundle );

        assertFalse( validate.getErrorReports( DataElement.class ).isEmpty() );
        assertEquals( 3, validate.getErrorReports( DataElement.class, ErrorCode.E5001 ).size() );
    }

    @Test
    public void testDeleteRequiresValidReferencesUID() throws IOException
    {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService.fromMetadata(
            new ClassPathResource( "dxf2/de_validate4.json" ).getInputStream(), RenderFormat.JSON );

        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode( ObjectBundleMode.VALIDATE );
        params.setPreheatIdentifier( PreheatIdentifier.UID );
        params.setImportMode( ImportStrategy.DELETE );
        params.setObjects( metadata );

        ObjectBundle bundle = objectBundleService.create( params );
        ObjectBundleValidation validate = objectBundleService.validate( bundle );

        assertFalse( validate.getErrorReports( DataElement.class ).isEmpty() );
        assertEquals( 3, validate.getErrorReports( DataElement.class, ErrorCode.E5001 ).size() );
    }

    @Test
    public void testDeleteRequiresValidReferencesCODE() throws IOException
    {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService.fromMetadata(
            new ClassPathResource( "dxf2/de_validate5.json" ).getInputStream(), RenderFormat.JSON );

        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode( ObjectBundleMode.VALIDATE );
        params.setPreheatIdentifier( PreheatIdentifier.CODE );
        params.setImportMode( ImportStrategy.DELETE );
        params.setObjects( metadata );

        ObjectBundle bundle = objectBundleService.create( params );
        ObjectBundleValidation validate = objectBundleService.validate( bundle );

        assertFalse( validate.getErrorReports( DataElement.class ).isEmpty() );
        assertEquals( 3, validate.getErrorReports( DataElement.class, ErrorCode.E5001 ).size() );
    }

    @Test
    public void testDeleteRequiresValidReferencesAUTO() throws IOException
    {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService.fromMetadata(
            new ClassPathResource( "dxf2/de_validate6.json" ).getInputStream(), RenderFormat.JSON );

        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode( ObjectBundleMode.VALIDATE );
        params.setPreheatIdentifier( PreheatIdentifier.AUTO );
        params.setImportMode( ImportStrategy.DELETE );
        params.setObjects( metadata );

        ObjectBundle bundle = objectBundleService.create( params );
        ObjectBundleValidation validate = objectBundleService.validate( bundle );

        assertFalse( validate.getErrorReports( DataElement.class ).isEmpty() );
        assertEquals( 3, validate.getErrorReports( DataElement.class, ErrorCode.E5001 ).size() );
    }

    @Test
    public void testPreheatValidationsIncludingMerge() throws IOException
    {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService.fromMetadata(
            new ClassPathResource( "dxf2/de_validate3.json" ).getInputStream(), RenderFormat.JSON );
        defaultSetup();

        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode( ObjectBundleMode.VALIDATE );
        params.setMergeMode( MergeMode.REPLACE );
        params.setObjects( metadata );

        ObjectBundle bundle = objectBundleService.create( params );
        ObjectBundleValidation validate = objectBundleService.validate( bundle );

        assertNotNull( validate );
    }

    @Test
    public void testSimpleDataElementDeleteUID() throws IOException
    {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService.fromMetadata(
            new ClassPathResource( "dxf2/de_simple_delete_uid.json" ).getInputStream(), RenderFormat.JSON );
        defaultSetup();

        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode( ObjectBundleMode.COMMIT );
        params.setImportMode( ImportStrategy.DELETE );
        params.setObjects( metadata );

        ObjectBundle bundle = objectBundleService.create( params );
        objectBundleService.validate( bundle );
        objectBundleService.commit( bundle );

        List<DataElement> dataElements = manager.getAll( DataElement.class );
        assertEquals( 1, dataElements.size() );
        assertEquals( "deabcdefghB", dataElements.get( 0 ).getUid() );
    }

    @Test
    public void testSimpleDataElementDeleteCODE() throws IOException
    {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService.fromMetadata(
            new ClassPathResource( "dxf2/de_simple_delete_code.json" ).getInputStream(), RenderFormat.JSON );
        defaultSetup();

        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode( ObjectBundleMode.COMMIT );
        params.setPreheatIdentifier( PreheatIdentifier.CODE );
        params.setImportMode( ImportStrategy.DELETE );
        params.setObjects( metadata );

        ObjectBundle bundle = objectBundleService.create( params );
        objectBundleService.validate( bundle );
        objectBundleService.commit( bundle );

        List<DataElement> dataElements = manager.getAll( DataElement.class );
        assertEquals( 1, dataElements.size() );
        assertEquals( "DataElementCodeD", dataElements.get( 0 ).getCode() );
    }

    @Test
    public void testCreateSimpleMetadataUID() throws IOException
    {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService.fromMetadata(
            new ClassPathResource( "dxf2/simple_metadata.json" ).getInputStream(), RenderFormat.JSON );

        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode( ObjectBundleMode.COMMIT );
        params.setImportMode( ImportStrategy.CREATE );
        params.setObjects( metadata );

        ObjectBundle bundle = objectBundleService.create( params );
        objectBundleService.validate( bundle );
        objectBundleService.commit( bundle );

        List<OrganisationUnit> organisationUnits = manager.getAll( OrganisationUnit.class );
        List<DataElement> dataElements = manager.getAll( DataElement.class );
        List<DataSet> dataSets = manager.getAll( DataSet.class );
        List<UserAuthorityGroup> userRoles = manager.getAll( UserAuthorityGroup.class );
        List<User> users = manager.getAll( User.class );

        assertFalse( organisationUnits.isEmpty() );
        assertFalse( dataElements.isEmpty() );
        assertFalse( dataSets.isEmpty() );
        assertFalse( users.isEmpty() );
        assertFalse( userRoles.isEmpty() );

        Map<Class<? extends IdentifiableObject>, IdentifiableObject> defaults = manager.getDefaults();

        DataSet dataSet = dataSets.get( 0 );
        User user = users.get( 0 );

        for ( DataElement dataElement : dataElements )
        {
            assertNotNull( dataElement.getCategoryCombo() );
            assertEquals( defaults.get( DataElementCategoryCombo.class ), dataElement.getCategoryCombo() );
        }

        assertFalse( dataSet.getSources().isEmpty() );
        assertFalse( dataSet.getDataElements().isEmpty() );
        assertEquals( 1, dataSet.getSources().size() );
        assertEquals( 2, dataSet.getDataElements().size() );
        assertEquals( PeriodType.getPeriodTypeByName( "Monthly" ), dataSet.getPeriodType() );

        assertNotNull( user.getUserCredentials() );
        assertEquals( "admin", user.getUserCredentials().getUsername() );
        assertFalse( user.getUserCredentials().getUserAuthorityGroups().isEmpty() );
        assertFalse( user.getOrganisationUnits().isEmpty() );
        assertEquals( "PdWlltZnVZe", user.getOrganisationUnit().getUid() );
    }

    @Test
    public void testCreateSimpleMetadataAttributeValuesUID() throws IOException
    {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService.fromMetadata(
            new ClassPathResource( "dxf2/simple_metadata_with_av.json" ).getInputStream(), RenderFormat.JSON );

        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode( ObjectBundleMode.COMMIT );
        params.setImportMode( ImportStrategy.CREATE );
        params.setObjects( metadata );

        ObjectBundle bundle = objectBundleService.create( params );
        objectBundleService.validate( bundle );
        objectBundleService.commit( bundle );

        List<OrganisationUnit> organisationUnits = manager.getAll( OrganisationUnit.class );
        List<DataElement> dataElements = manager.getAll( DataElement.class );
        List<DataSet> dataSets = manager.getAll( DataSet.class );
        List<UserAuthorityGroup> userRoles = manager.getAll( UserAuthorityGroup.class );
        List<User> users = manager.getAll( User.class );
        List<Option> options = manager.getAll( Option.class );
        List<OptionSet> optionSets = manager.getAll( OptionSet.class );
        List<Attribute> attributes = manager.getAll( Attribute.class );

        assertFalse( organisationUnits.isEmpty() );
        assertFalse( dataElements.isEmpty() );
        assertFalse( dataSets.isEmpty() );
        assertFalse( users.isEmpty() );
        assertFalse( userRoles.isEmpty() );
        assertEquals( 2, attributes.size() );
        assertEquals( 2, options.size() );
        assertEquals( 1, optionSets.size() );

        Map<Class<? extends IdentifiableObject>, IdentifiableObject> defaults = manager.getDefaults();

        DataSet dataSet = dataSets.get( 0 );
        User user = users.get( 0 );
        OptionSet optionSet = optionSets.get( 0 );

        for ( DataElement dataElement : dataElements )
        {
            assertNotNull( dataElement.getCategoryCombo() );
            assertEquals( defaults.get( DataElementCategoryCombo.class ), dataElement.getCategoryCombo() );
        }

        assertFalse( dataSet.getSources().isEmpty() );
        assertFalse( dataSet.getDataElements().isEmpty() );
        assertEquals( 1, dataSet.getSources().size() );
        assertEquals( 2, dataSet.getDataElements().size() );
        assertEquals( PeriodType.getPeriodTypeByName( "Monthly" ), dataSet.getPeriodType() );

        assertNotNull( user.getUserCredentials() );
        assertEquals( "admin", user.getUserCredentials().getUsername() );
        assertFalse( user.getUserCredentials().getUserAuthorityGroups().isEmpty() );
        assertFalse( user.getOrganisationUnits().isEmpty() );
        assertEquals( "PdWlltZnVZe", user.getOrganisationUnit().getUid() );

        assertEquals( 2, optionSet.getOptions().size() );

        // attribute value check
        DataElement dataElementA = manager.get( DataElement.class, "SG4HuKlNEFH" );
        DataElement dataElementB = manager.get( DataElement.class, "CCwk5Yx440o" );
        DataElement dataElementC = manager.get( DataElement.class, "j5PneRdU7WT" );
        DataElement dataElementD = manager.get( DataElement.class, "k90AVpBahO4" );

        assertNotNull( dataElementA );
        assertNotNull( dataElementB );
        assertNotNull( dataElementC );
        assertNotNull( dataElementD );

        assertTrue( dataElementA.getAttributeValues().isEmpty() );
        assertTrue( dataElementB.getAttributeValues().isEmpty() );
        assertFalse( dataElementC.getAttributeValues().isEmpty() );
        assertFalse( dataElementD.getAttributeValues().isEmpty() );
    }

    @Test
    public void testValidateMetadataAttributeValuesUniqueAndMandatoryUID() throws IOException
    {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService.fromMetadata(
            new ClassPathResource( "dxf2/simple_metadata_uga.json" ).getInputStream(), RenderFormat.JSON );

        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode( ObjectBundleMode.VALIDATE );
        params.setObjects( metadata );

        ObjectBundle bundle = objectBundleService.create( params );
        ObjectBundleValidation validation = objectBundleService.validate( bundle );
    }

    @Test
    public void testCreateDataSetsWithUgaUID() throws IOException
    {
        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService.fromMetadata(
            new ClassPathResource( "dxf2/simple_metadata_uga.json" ).getInputStream(), RenderFormat.JSON );

        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode( ObjectBundleMode.COMMIT );
        params.setImportMode( ImportStrategy.CREATE );
        params.setObjects( metadata );

        ObjectBundle bundle = objectBundleService.create( params );
        objectBundleService.validate( bundle );
        objectBundleService.commit( bundle );

        List<OrganisationUnit> organisationUnits = manager.getAll( OrganisationUnit.class );
        List<DataElement> dataElements = manager.getAll( DataElement.class );
        List<UserAuthorityGroup> userRoles = manager.getAll( UserAuthorityGroup.class );
        List<User> users = manager.getAll( User.class );
        List<UserGroup> userGroups = manager.getAll( UserGroup.class );

        assertEquals( 1, organisationUnits.size() );
        assertEquals( 2, dataElements.size() );
        assertEquals( 1, userRoles.size() );
        assertEquals( 1, users.size() );
        assertEquals( 2, userGroups.size() );

        assertEquals( 1, dataElements.get( 0 ).getUserGroupAccesses().size() );
        assertEquals( 1, dataElements.get( 1 ).getUserGroupAccesses().size() );
    }

    @Test
    public void testUpdateDataElements() throws IOException
    {
        defaultSetup();

        Map<Class<? extends IdentifiableObject>, List<IdentifiableObject>> metadata = renderService.fromMetadata(
            new ClassPathResource( "dxf2/de_update1.json" ).getInputStream(), RenderFormat.JSON );

        ObjectBundleParams params = new ObjectBundleParams();
        params.setObjectBundleMode( ObjectBundleMode.COMMIT );
        params.setImportMode( ImportStrategy.UPDATE );
        params.setObjects( metadata );

        ObjectBundle bundle = objectBundleService.create( params );
        objectBundleService.validate( bundle );
        objectBundleService.commit( bundle );

        Map<String, DataElement> dataElementMap = manager.getIdMap( DataElement.class, IdScheme.UID );
        assertEquals( 4, dataElementMap.size() );

        DataElement dataElementA = dataElementMap.get( "deabcdefghA" );
        DataElement dataElementB = dataElementMap.get( "deabcdefghB" );
        DataElement dataElementC = dataElementMap.get( "deabcdefghC" );
        DataElement dataElementD = dataElementMap.get( "deabcdefghD" );

        assertNotNull( dataElementA );
        assertNotNull( dataElementB );
        assertNotNull( dataElementC );
        assertNotNull( dataElementD );

        assertEquals( "DEA", dataElementA.getName() );
        assertEquals( "DEB", dataElementB.getName() );
        assertEquals( "DEC", dataElementC.getName() );
        assertEquals( "DED", dataElementD.getName() );
    }

    private void defaultSetup()
    {
        DataElement de1 = createDataElement( 'A' );
        DataElement de2 = createDataElement( 'B' );
        DataElement de3 = createDataElement( 'C' );
        DataElement de4 = createDataElement( 'D' );

        manager.save( de1 );
        manager.save( de2 );
        manager.save( de3 );
        manager.save( de4 );

        User user = createUser( 'A' );
        manager.save( user );
    }
}