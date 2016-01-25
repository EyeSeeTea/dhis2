package org.hisp.dhis.preheat;

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

import com.google.common.collect.Sets;
import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.legend.LegendSet;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.user.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class PreheatServiceTest
    extends DhisSpringTest
{
    @Autowired
    private PreheatService preheatService;

    @Autowired
    private IdentifiableObjectManager manager;

    @Test( expected = PreheatException.class )
    public void testValidateAllFail()
    {
        PreheatParams params = new PreheatParams().setPreheatMode( PreheatMode.ALL );
        preheatService.validate( params );
    }

    @Test
    public void testValidateAll()
    {
        PreheatParams params = new PreheatParams().setPreheatMode( PreheatMode.ALL );
        params.getClasses().add( DataElement.class );

        preheatService.validate( params );
    }

    @Test( expected = PreheatException.class )
    public void testValidateRefFail()
    {
        PreheatParams params = new PreheatParams().setPreheatMode( PreheatMode.REFERENCE );
        preheatService.validate( params );
    }

    @Test
    public void testValidateRef()
    {
        PreheatParams params = new PreheatParams().setPreheatMode( PreheatMode.REFERENCE );
        params.getReferences().put( DataElement.class, Sets.newHashSet( "ID1", "ID2" ) );

        preheatService.validate( params );
    }

    @Test
    public void testScanNoObjectsDE()
    {
        DataElement dataElement = createDataElement( 'A' );
        Map<Class<? extends IdentifiableObject>, Set<String>> references = preheatService.scanObjectForReferences( dataElement, PreheatIdentifier.UID );

        assertTrue( references.containsKey( OptionSet.class ) );
        assertTrue( references.containsKey( LegendSet.class ) );
        assertTrue( references.containsKey( DataElementCategoryCombo.class ) );
        assertTrue( references.containsKey( User.class ) );
    }

    @Test
    public void testScanNoObjectsDEG()
    {
        DataElementGroup dataElementGroup = createDataElementGroup( 'A' );
        Map<Class<? extends IdentifiableObject>, Set<String>> references = preheatService.scanObjectForReferences( dataElementGroup, PreheatIdentifier.UID );

        assertTrue( references.containsKey( DataElement.class ) );
        assertTrue( references.containsKey( User.class ) );
    }

    @Test
    public void testScanObjectReferenceUidDEG()
    {
        DataElementGroup deg1 = createDataElementGroup( 'A' );

        DataElement de1 = createDataElement( 'A' );
        DataElement de2 = createDataElement( 'B' );
        DataElement de3 = createDataElement( 'C' );

        User user = createUser( 'A' );

        deg1.addDataElement( de1 );
        deg1.addDataElement( de2 );
        deg1.addDataElement( de3 );

        deg1.setUser( user );

        Map<Class<? extends IdentifiableObject>, Set<String>> references = preheatService.scanObjectForReferences( deg1, PreheatIdentifier.UID );

        assertTrue( references.containsKey( DataElement.class ) );
        assertTrue( references.containsKey( User.class ) );

        assertEquals( 3, references.get( DataElement.class ).size() );
        assertEquals( 1, references.get( User.class ).size() );

        assertTrue( references.get( DataElement.class ).contains( de1.getUid() ) );
        assertTrue( references.get( DataElement.class ).contains( de2.getUid() ) );
        assertTrue( references.get( DataElement.class ).contains( de3.getUid() ) );

        assertTrue( references.get( User.class ).contains( user.getUid() ) );
    }

    @Test
    public void testScanObjectsReferenceUidDEG()
    {
        DataElementGroup deg1 = createDataElementGroup( 'A' );
        DataElementGroup deg2 = createDataElementGroup( 'B' );

        DataElement de1 = createDataElement( 'A' );
        DataElement de2 = createDataElement( 'B' );
        DataElement de3 = createDataElement( 'C' );

        deg1.addDataElement( de1 );
        deg1.addDataElement( de2 );
        deg2.addDataElement( de3 );

        Map<Class<? extends IdentifiableObject>, Set<String>> references = preheatService.scanObjectsForReferences(
            Sets.newHashSet( deg1, deg2 ), PreheatIdentifier.UID );

        assertTrue( references.containsKey( DataElement.class ) );
        assertEquals( 3, references.get( DataElement.class ).size() );

        assertTrue( references.get( DataElement.class ).contains( de1.getUid() ) );
        assertTrue( references.get( DataElement.class ).contains( de2.getUid() ) );
        assertTrue( references.get( DataElement.class ).contains( de3.getUid() ) );
    }

    @Test
    public void testScanObjectReferenceCodeDEG()
    {
        DataElementGroup dataElementGroup = createDataElementGroup( 'A' );

        DataElement de1 = createDataElement( 'A' );
        DataElement de2 = createDataElement( 'B' );
        DataElement de3 = createDataElement( 'C' );

        User user = createUser( 'A' );

        dataElementGroup.addDataElement( de1 );
        dataElementGroup.addDataElement( de2 );
        dataElementGroup.addDataElement( de3 );

        dataElementGroup.setUser( user );

        Map<Class<? extends IdentifiableObject>, Set<String>> references = preheatService.scanObjectForReferences( dataElementGroup, PreheatIdentifier.CODE );

        assertTrue( references.containsKey( DataElement.class ) );
        assertTrue( references.containsKey( User.class ) );

        assertEquals( 3, references.get( DataElement.class ).size() );
        assertEquals( 1, references.get( User.class ).size() );

        assertTrue( references.get( DataElement.class ).contains( de1.getCode() ) );
        assertTrue( references.get( DataElement.class ).contains( de2.getCode() ) );
        assertTrue( references.get( DataElement.class ).contains( de3.getCode() ) );

        assertTrue( references.get( User.class ).contains( user.getCode() ) );
    }

    @Test
    public void testScanObjectsReferenceCodeDEG()
    {
        DataElementGroup deg1 = createDataElementGroup( 'A' );
        DataElementGroup deg2 = createDataElementGroup( 'B' );

        DataElement de1 = createDataElement( 'A' );
        DataElement de2 = createDataElement( 'B' );
        DataElement de3 = createDataElement( 'C' );

        deg1.addDataElement( de1 );
        deg1.addDataElement( de2 );
        deg2.addDataElement( de3 );

        Map<Class<? extends IdentifiableObject>, Set<String>> references = preheatService.scanObjectsForReferences(
            Sets.newHashSet( deg1, deg2 ), PreheatIdentifier.CODE );

        assertTrue( references.containsKey( DataElement.class ) );
        assertEquals( 3, references.get( DataElement.class ).size() );

        assertTrue( references.get( DataElement.class ).contains( de1.getCode() ) );
        assertTrue( references.get( DataElement.class ).contains( de2.getCode() ) );
        assertTrue( references.get( DataElement.class ).contains( de3.getCode() ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testPreheatAllUID()
    {
        DataElementGroup dataElementGroup = new DataElementGroup( "DataElementGroupA" );
        dataElementGroup.setAutoFields();

        DataElement de1 = createDataElement( 'A' );
        DataElement de2 = createDataElement( 'B' );
        DataElement de3 = createDataElement( 'C' );

        manager.save( de1 );
        manager.save( de2 );
        manager.save( de3 );

        User user = createUser( 'A' );
        manager.save( user );

        dataElementGroup.addDataElement( de1 );
        dataElementGroup.addDataElement( de2 );
        dataElementGroup.addDataElement( de3 );

        dataElementGroup.setUser( user );
        manager.save( dataElementGroup );

        PreheatParams params = new PreheatParams();
        params.setPreheatMode( PreheatMode.ALL );
        params.setClasses( Sets.newHashSet( DataElement.class, DataElementGroup.class, User.class ) );

        preheatService.validate( params );
        Preheat preheat = preheatService.preheat( params );

        assertFalse( preheat.isEmpty() );
        assertFalse( preheat.isEmpty( PreheatIdentifier.UID ) );
        assertFalse( preheat.isEmpty( PreheatIdentifier.UID, DataElement.class ) );
        assertFalse( preheat.isEmpty( PreheatIdentifier.UID, DataElementGroup.class ) );
        assertFalse( preheat.isEmpty( PreheatIdentifier.UID, User.class ) );

        assertTrue( preheat.containsKey( PreheatIdentifier.UID, DataElement.class, de1.getUid() ) );
        assertTrue( preheat.containsKey( PreheatIdentifier.UID, DataElement.class, de2.getUid() ) );
        assertTrue( preheat.containsKey( PreheatIdentifier.UID, DataElement.class, de3.getUid() ) );
        assertTrue( preheat.containsKey( PreheatIdentifier.UID, DataElementGroup.class, dataElementGroup.getUid() ) );
        assertTrue( preheat.containsKey( PreheatIdentifier.UID, User.class, user.getUid() ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testPreheatReferenceUID()
    {
        DataElementGroup dataElementGroup = new DataElementGroup( "DataElementGroupA" );
        dataElementGroup.setAutoFields();

        DataElement de1 = createDataElement( 'A' );
        DataElement de2 = createDataElement( 'B' );
        DataElement de3 = createDataElement( 'C' );

        manager.save( de1 );
        manager.save( de2 );
        manager.save( de3 );

        User user = createUser( 'A' );
        manager.save( user );

        dataElementGroup.addDataElement( de1 );
        dataElementGroup.addDataElement( de2 );
        dataElementGroup.addDataElement( de3 );

        dataElementGroup.setUser( user );
        manager.save( dataElementGroup );

        PreheatParams params = new PreheatParams();
        params.setPreheatMode( PreheatMode.REFERENCE );

        Map<Class<? extends IdentifiableObject>, Set<String>> referenceMap = new HashMap<>();
        referenceMap.put( DataElement.class, Sets.newHashSet( de1.getUid(), de2.getUid() ) );
        referenceMap.put( User.class, Sets.newHashSet( user.getUid() ) );
        params.setReferences( referenceMap );

        preheatService.validate( params );
        Preheat preheat = preheatService.preheat( params );

        assertFalse( preheat.isEmpty() );
        assertFalse( preheat.isEmpty( PreheatIdentifier.UID ) );
        assertFalse( preheat.isEmpty( PreheatIdentifier.UID, DataElement.class ) );
        assertTrue( preheat.isEmpty( PreheatIdentifier.UID, DataElementGroup.class ) );
        assertFalse( preheat.isEmpty( PreheatIdentifier.UID, User.class ) );

        assertTrue( preheat.containsKey( PreheatIdentifier.UID, DataElement.class, de1.getUid() ) );
        assertTrue( preheat.containsKey( PreheatIdentifier.UID, DataElement.class, de2.getUid() ) );
        assertFalse( preheat.containsKey( PreheatIdentifier.UID, DataElement.class, de3.getUid() ) );
        assertFalse( preheat.containsKey( PreheatIdentifier.UID, DataElementGroup.class, dataElementGroup.getUid() ) );
        assertTrue( preheat.containsKey( PreheatIdentifier.UID, User.class, user.getUid() ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testPreheatReferenceCODE()
    {
        DataElementGroup dataElementGroup = new DataElementGroup( "DataElementGroupA" );
        dataElementGroup.setAutoFields();

        DataElement de1 = createDataElement( 'A' );
        DataElement de2 = createDataElement( 'B' );
        DataElement de3 = createDataElement( 'C' );

        manager.save( de1 );
        manager.save( de2 );
        manager.save( de3 );

        User user = createUser( 'A' );
        manager.save( user );

        dataElementGroup.addDataElement( de1 );
        dataElementGroup.addDataElement( de2 );
        dataElementGroup.addDataElement( de3 );

        dataElementGroup.setUser( user );
        manager.save( dataElementGroup );

        PreheatParams params = new PreheatParams();
        params.setPreheatIdentifier( PreheatIdentifier.CODE );
        params.setPreheatMode( PreheatMode.REFERENCE );

        Map<Class<? extends IdentifiableObject>, Set<String>> references = new HashMap<>();
        references.put( DataElement.class, Sets.newHashSet( de1.getCode(), de2.getCode() ) );
        references.put( User.class, Sets.newHashSet( user.getCode() ) );
        params.setReferences( references );

        preheatService.validate( params );
        Preheat preheat = preheatService.preheat( params );

        assertFalse( preheat.isEmpty() );
        assertFalse( preheat.isEmpty( PreheatIdentifier.CODE ) );
        assertFalse( preheat.isEmpty( PreheatIdentifier.CODE, DataElement.class ) );
        assertTrue( preheat.isEmpty( PreheatIdentifier.CODE, DataElementGroup.class ) );
        assertFalse( preheat.isEmpty( PreheatIdentifier.CODE, User.class ) );

        assertTrue( preheat.containsKey( PreheatIdentifier.CODE, DataElement.class, de1.getCode() ) );
        assertTrue( preheat.containsKey( PreheatIdentifier.CODE, DataElement.class, de2.getCode() ) );
        assertFalse( preheat.containsKey( PreheatIdentifier.CODE, DataElement.class, de3.getCode() ) );
        assertFalse( preheat.containsKey( PreheatIdentifier.CODE, DataElementGroup.class, dataElementGroup.getCode() ) );
        assertTrue( preheat.containsKey( PreheatIdentifier.CODE, User.class, user.getCode() ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testPreheatReferenceWithScanUID()
    {
        DataElementGroup dataElementGroup = new DataElementGroup( "DataElementGroupA" );
        dataElementGroup.setAutoFields();

        DataElement de1 = createDataElement( 'A' );
        DataElement de2 = createDataElement( 'B' );
        DataElement de3 = createDataElement( 'C' );

        manager.save( de1 );
        manager.save( de2 );
        manager.save( de3 );

        User user = createUser( 'A' );
        manager.save( user );

        dataElementGroup.addDataElement( de1 );
        dataElementGroup.addDataElement( de2 );
        dataElementGroup.addDataElement( de3 );

        dataElementGroup.setUser( user );
        manager.save( dataElementGroup );

        Map<Class<? extends IdentifiableObject>, Set<String>> references = preheatService.scanObjectForReferences( dataElementGroup, PreheatIdentifier.UID );

        PreheatParams params = new PreheatParams();
        params.setPreheatMode( PreheatMode.REFERENCE );
        params.setReferences( references );

        preheatService.validate( params );
        Preheat preheat = preheatService.preheat( params );

        assertFalse( preheat.isEmpty() );
        assertFalse( preheat.isEmpty( PreheatIdentifier.UID ) );
        assertFalse( preheat.isEmpty( PreheatIdentifier.UID, DataElement.class ) );
        assertTrue( preheat.isEmpty( PreheatIdentifier.UID, DataElementGroup.class ) );
        assertFalse( preheat.isEmpty( PreheatIdentifier.UID, User.class ) );

        assertTrue( preheat.containsKey( PreheatIdentifier.UID, DataElement.class, de1.getUid() ) );
        assertTrue( preheat.containsKey( PreheatIdentifier.UID, DataElement.class, de2.getUid() ) );
        assertTrue( preheat.containsKey( PreheatIdentifier.UID, DataElement.class, de3.getUid() ) );
        assertFalse( preheat.containsKey( PreheatIdentifier.UID, DataElementGroup.class, dataElementGroup.getUid() ) );
        assertTrue( preheat.containsKey( PreheatIdentifier.UID, User.class, user.getUid() ) );
    }
}
