package org.hisp.dhis.dataapproval;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.*;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dataelement.*;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jim Grace
 * @version $Id$
 */
public class DataApprovalServiceTest
    extends DhisSpringTest
{
    @Autowired
    private DataApprovalService dataApprovalService;

    @Autowired
    private DataApprovalLevelService dataApprovalLevelService;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private DataElementCategoryService categoryService;

    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    // -------------------------------------------------------------------------
    // Supporting data
    // -------------------------------------------------------------------------

    private DataSet dataSetA;

    private DataSet dataSetB;

    private Period periodA;

    private Period periodB;

    private Period periodC;

    private Period periodD;

    private OrganisationUnit organisationUnitA;

    private OrganisationUnit organisationUnitB;

    private OrganisationUnit organisationUnitC;

    private OrganisationUnit organisationUnitD;

    private OrganisationUnit organisationUnitE;

    private OrganisationUnit organisationUnitF;

    private OrganisationUnitLevel orgUnitLevel1;

    private OrganisationUnitLevel orgUnitLevel2;

    private OrganisationUnitLevel orgUnitLevel3;

    private OrganisationUnitLevel orgUnitLevel4;

    private DataApprovalLevel dataApprovalLevel1;

    private DataApprovalLevel dataApprovalLevel2;

    private DataApprovalLevel dataApprovalLevel3;

    private DataApprovalLevel dataApprovalLevel4;

    private DataApprovalLevel dataApprovalLevel1A;

    private DataApprovalLevel dataApprovalLevel2A;

    private DataApprovalLevel dataApprovalLevel3A;

    private DataApprovalLevel dataApprovalLevel4A;

    private DataApprovalLevel dataApprovalLevel1B;

    private DataApprovalLevel dataApprovalLevel2B;

    private DataApprovalLevel dataApprovalLevel3B;

    private DataApprovalLevel dataApprovalLevel4B;

    private User userA;

    private User userB;

    private DataElementCategoryOption categoryOptionA;

    private DataElementCategoryOption categoryOptionB;

    private DataElementCategoryOption categoryOptionC;

    private DataElementCategoryOption categoryOptionD;

    private DataElementCategoryOption categoryOptionE;

    private DataElementCategoryOption categoryOptionF;

    private DataElementCategoryOption categoryOptionG;

    private DataElementCategoryOption categoryOptionH;

    private DataElementCategoryOptionCombo categoryOptionComboA;

    private DataElementCategoryOptionCombo categoryOptionComboB;

    private DataElementCategoryOptionCombo categoryOptionComboC;

    private DataElementCategoryOptionCombo categoryOptionComboD;

    private DataElementCategoryOptionCombo categoryOptionComboE;

    private DataElementCategoryOptionCombo categoryOptionComboF;

    private DataElementCategoryOptionCombo categoryOptionComboG;

    private DataElementCategoryOptionCombo categoryOptionComboH;

    private DataElementCategoryOptionCombo categoryOptionComboI;

    private DataElementCategoryOptionCombo categoryOptionComboJ;

    private DataElementCategoryOptionCombo categoryOptionComboK;

    private DataElementCategoryOptionCombo categoryOptionComboL;

    private DataElementCategoryOptionCombo categoryOptionComboM;

    private DataElementCategoryOptionCombo categoryOptionComboN;

    private DataElementCategoryOptionCombo categoryOptionComboO;

    private DataElementCategoryOptionCombo categoryOptionComboP;

    private DataElementCategory categoryA;

    private DataElementCategory categoryB;

    private DataElementCategoryCombo categoryComboA;

    private CategoryOptionGroup noCategoryOptionGroup;

    private CategoryOptionGroup categoryOptionGroupA;

    private CategoryOptionGroup categoryOptionGroupB;

    private CategoryOptionGroup categoryOptionGroupC;

    private CategoryOptionGroup categoryOptionGroupD;

    private CategoryOptionGroupSet categoryOptionGroupSetA;

    private CategoryOptionGroupSet categoryOptionGroupSetB;

    // -------------------------------------------------------------------------
    // Set up/tear down
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest() throws Exception
    {
        identifiableObjectManager = (IdentifiableObjectManager) getBean( IdentifiableObjectManager.ID );
        userService = (UserService) getBean( UserService.ID );
        
        // ---------------------------------------------------------------------
        // Add supporting data
        // ---------------------------------------------------------------------

        PeriodType periodType = PeriodType.getPeriodTypeByName( "Monthly" );

        dataSetA = createDataSet( 'A', periodType );
        dataSetB = createDataSet( 'B', periodType );

        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );

        periodA = createPeriod( getDay( 5 ), getDay( 6 ) );
        periodB = createPeriod( getDay( 6 ), getDay( 7 ) );
        periodC = createPeriod( PeriodType.getPeriodTypeByName( "Yearly" ), getDay( 1 ), getDay( 365 ) );
        periodD = createPeriod( PeriodType.getPeriodTypeByName( "Weekly" ), getDay( 1 ), getDay( 365 ) );

        periodService.addPeriod( periodA );
        periodService.addPeriod( periodB );
        periodService.addPeriod( periodC );
        periodService.addPeriod( periodD );

        orgUnitLevel1 = new OrganisationUnitLevel( 1, "Level 1");
        orgUnitLevel2 = new OrganisationUnitLevel( 2, "Level 2");
        orgUnitLevel3 = new OrganisationUnitLevel( 3, "Level 3");
        orgUnitLevel4 = new OrganisationUnitLevel( 4, "Level 4");

        organisationUnitService.addOrganisationUnitLevel( orgUnitLevel1 );
        organisationUnitService.addOrganisationUnitLevel( orgUnitLevel2 );
        organisationUnitService.addOrganisationUnitLevel( orgUnitLevel3 );
        organisationUnitService.addOrganisationUnitLevel( orgUnitLevel4 );

        //
        // Organisation unit hierarchy:
        //
        //      A
        //      |
        //      B
        //     / \
        //    C   E
        //    |   |
        //    D   F
        //

        organisationUnitA = createOrganisationUnit( 'A' );
        organisationUnitB = createOrganisationUnit( 'B', organisationUnitA );
        organisationUnitC = createOrganisationUnit( 'C', organisationUnitB );
        organisationUnitD = createOrganisationUnit( 'D', organisationUnitC );
        organisationUnitE = createOrganisationUnit( 'E', organisationUnitB );
        organisationUnitF = createOrganisationUnit( 'F', organisationUnitE );

        organisationUnitA.setLevel( 1 );
        organisationUnitB.setLevel( 2 );
        organisationUnitC.setLevel( 3 );
        organisationUnitD.setLevel( 4 );
        organisationUnitE.setLevel( 3 );
        organisationUnitF.setLevel( 4 );

        organisationUnitService.addOrganisationUnit( organisationUnitA );
        organisationUnitService.addOrganisationUnit( organisationUnitB );
        organisationUnitService.addOrganisationUnit( organisationUnitC );
        organisationUnitService.addOrganisationUnit( organisationUnitD );
        organisationUnitService.addOrganisationUnit( organisationUnitE );
        organisationUnitService.addOrganisationUnit( organisationUnitF );

        dataApprovalLevel1 = new DataApprovalLevel( orgUnitLevel1, null );
        dataApprovalLevel2 = new DataApprovalLevel( orgUnitLevel2, null );
        dataApprovalLevel3 = new DataApprovalLevel( orgUnitLevel3, null );
        dataApprovalLevel4 = new DataApprovalLevel( orgUnitLevel4, null );

        userA = createUser( 'A' );
        userB = createUser( 'B' );

        userService.addUser( userA );
        userService.addUser( userB );

        noCategoryOptionGroup = null;
    }

    // ---------------------------------------------------------------------
    // Set Up Categories
    // ---------------------------------------------------------------------

    private void setUpCategories() throws Exception
    {
        categoryOptionA = new DataElementCategoryOption( "CategoryOptionA" );
        categoryOptionB = new DataElementCategoryOption( "CategoryOptionB" );
        categoryOptionC = new DataElementCategoryOption( "CategoryOptionC" );
        categoryOptionD = new DataElementCategoryOption( "CategoryOptionD" );
        categoryOptionE = new DataElementCategoryOption( "CategoryOptionE" );
        categoryOptionF = new DataElementCategoryOption( "CategoryOptionF" );
        categoryOptionG = new DataElementCategoryOption( "CategoryOptionG" );
        categoryOptionH = new DataElementCategoryOption( "CategoryOptionH" );

        categoryService.addDataElementCategoryOption( categoryOptionA );
        categoryService.addDataElementCategoryOption( categoryOptionB );
        categoryService.addDataElementCategoryOption( categoryOptionC );
        categoryService.addDataElementCategoryOption( categoryOptionD );
        categoryService.addDataElementCategoryOption( categoryOptionE );
        categoryService.addDataElementCategoryOption( categoryOptionF );
        categoryService.addDataElementCategoryOption( categoryOptionG );
        categoryService.addDataElementCategoryOption( categoryOptionH );

        categoryA = createDataElementCategory( 'A', categoryOptionA, categoryOptionB, categoryOptionC, categoryOptionD );
        categoryB = createDataElementCategory( 'B', categoryOptionE, categoryOptionF, categoryOptionG, categoryOptionH );

        categoryService.addDataElementCategory( categoryA );
        categoryService.addDataElementCategory( categoryB );

        categoryComboA = createCategoryCombo( 'A', categoryA, categoryB );

        categoryService.addDataElementCategoryCombo( categoryComboA );

        categoryOptionComboA = createCategoryOptionCombo( 'A', categoryComboA, categoryOptionA, categoryOptionE );
        categoryOptionComboB = createCategoryOptionCombo( 'B', categoryComboA, categoryOptionA, categoryOptionF );
        categoryOptionComboC = createCategoryOptionCombo( 'C', categoryComboA, categoryOptionA, categoryOptionG );
        categoryOptionComboD = createCategoryOptionCombo( 'D', categoryComboA, categoryOptionA, categoryOptionH );
        categoryOptionComboE = createCategoryOptionCombo( 'E', categoryComboA, categoryOptionB, categoryOptionE );
        categoryOptionComboF = createCategoryOptionCombo( 'F', categoryComboA, categoryOptionB, categoryOptionF );
        categoryOptionComboG = createCategoryOptionCombo( 'G', categoryComboA, categoryOptionB, categoryOptionG );
        categoryOptionComboH = createCategoryOptionCombo( 'H', categoryComboA, categoryOptionB, categoryOptionH );
        categoryOptionComboI = createCategoryOptionCombo( 'I', categoryComboA, categoryOptionC, categoryOptionE );
        categoryOptionComboJ = createCategoryOptionCombo( 'J', categoryComboA, categoryOptionC, categoryOptionF );
        categoryOptionComboK = createCategoryOptionCombo( 'K', categoryComboA, categoryOptionC, categoryOptionG );
        categoryOptionComboL = createCategoryOptionCombo( 'L', categoryComboA, categoryOptionC, categoryOptionH );
        categoryOptionComboM = createCategoryOptionCombo( 'M', categoryComboA, categoryOptionD, categoryOptionE );
        categoryOptionComboN = createCategoryOptionCombo( 'N', categoryComboA, categoryOptionD, categoryOptionF );
        categoryOptionComboO = createCategoryOptionCombo( 'O', categoryComboA, categoryOptionD, categoryOptionG );
        categoryOptionComboP = createCategoryOptionCombo( 'P', categoryComboA, categoryOptionD, categoryOptionH );

        categoryService.addDataElementCategoryOptionCombo( categoryOptionComboA );
        categoryService.addDataElementCategoryOptionCombo( categoryOptionComboB );
        categoryService.addDataElementCategoryOptionCombo( categoryOptionComboC );
        categoryService.addDataElementCategoryOptionCombo( categoryOptionComboD );
        categoryService.addDataElementCategoryOptionCombo( categoryOptionComboE );
        categoryService.addDataElementCategoryOptionCombo( categoryOptionComboF );
        categoryService.addDataElementCategoryOptionCombo( categoryOptionComboG );
        categoryService.addDataElementCategoryOptionCombo( categoryOptionComboH );
        categoryService.addDataElementCategoryOptionCombo( categoryOptionComboI );
        categoryService.addDataElementCategoryOptionCombo( categoryOptionComboJ );
        categoryService.addDataElementCategoryOptionCombo( categoryOptionComboK );
        categoryService.addDataElementCategoryOptionCombo( categoryOptionComboL );
        categoryService.addDataElementCategoryOptionCombo( categoryOptionComboM );
        categoryService.addDataElementCategoryOptionCombo( categoryOptionComboN );
        categoryService.addDataElementCategoryOptionCombo( categoryOptionComboO );
        categoryService.addDataElementCategoryOptionCombo( categoryOptionComboP );

        categoryOptionGroupA = createCategoryOptionGroup( 'A', categoryOptionA, categoryOptionB );
        categoryOptionGroupB = createCategoryOptionGroup( 'B', categoryOptionC, categoryOptionD );
        categoryOptionGroupC = createCategoryOptionGroup( 'C', categoryOptionE, categoryOptionF );
        categoryOptionGroupD = createCategoryOptionGroup( 'D', categoryOptionG, categoryOptionH );

        categoryService.saveCategoryOptionGroup( categoryOptionGroupA );
        categoryService.saveCategoryOptionGroup( categoryOptionGroupB );
        categoryService.saveCategoryOptionGroup( categoryOptionGroupC );
        categoryService.saveCategoryOptionGroup( categoryOptionGroupD );

        categoryOptionGroupSetA = createCategoryOptionGroupSet( 'A', categoryOptionGroupA, categoryOptionGroupB );
        categoryOptionGroupSetB = createCategoryOptionGroupSet( 'B', categoryOptionGroupC, categoryOptionGroupD );

        categoryService.saveCategoryOptionGroupSet( categoryOptionGroupSetA );
        categoryService.saveCategoryOptionGroupSet( categoryOptionGroupSetB );

        dataApprovalLevel1A = new DataApprovalLevel( orgUnitLevel1, categoryOptionGroupSetA );
        dataApprovalLevel1B = new DataApprovalLevel( orgUnitLevel1, categoryOptionGroupSetB );
        dataApprovalLevel2A = new DataApprovalLevel( orgUnitLevel2, categoryOptionGroupSetA );
        dataApprovalLevel2B = new DataApprovalLevel( orgUnitLevel2, categoryOptionGroupSetB );
        dataApprovalLevel3A = new DataApprovalLevel( orgUnitLevel3, categoryOptionGroupSetA );
        dataApprovalLevel3B = new DataApprovalLevel( orgUnitLevel3, categoryOptionGroupSetB );
        dataApprovalLevel4A = new DataApprovalLevel( orgUnitLevel4, categoryOptionGroupSetA );
        dataApprovalLevel4B = new DataApprovalLevel( orgUnitLevel4, categoryOptionGroupSetB );
    }

    // -------------------------------------------------------------------------
    // Basic DataApproval
    // -------------------------------------------------------------------------

    @Test
    public void testAddAndGetDataApproval() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, organisationUnitA, noCategoryOptionGroup, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup, date, userA );
        DataApproval dataApprovalC = new DataApproval( dataSetA, periodB, organisationUnitA, noCategoryOptionGroup, date, userA );
        DataApproval dataApprovalD = new DataApproval( dataSetB, periodA, organisationUnitA, noCategoryOptionGroup, date, userA );
        DataApproval dataApprovalE;

        dataApprovalService.addDataApproval( dataApprovalA );
        dataApprovalService.addDataApproval( dataApprovalB );
        dataApprovalService.addDataApproval( dataApprovalC );
        dataApprovalService.addDataApproval( dataApprovalD );

        dataSetA.setApproveData( true );
        dataSetB.setApproveData( true );

        DataApprovalStatus status;

        status = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noCategoryOptionGroup );
        dataApprovalA = status.getDataApproval();
        assertEquals( DataApprovalState.APPROVED_HERE, status.getDataApprovalState() );
        assertNotNull( dataApprovalA );
        assertEquals( dataSetA.getId(), dataApprovalA.getDataSet().getId() );
        assertEquals( periodA, dataApprovalA.getPeriod() );
        assertEquals( organisationUnitA.getId(), dataApprovalA.getOrganisationUnit().getId() );
        assertEquals( date, dataApprovalA.getCreated() );
        assertEquals( userA.getId(), dataApprovalA.getCreator().getId() );

        status = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup );
        dataApprovalB = status.getDataApproval();
        assertEquals( DataApprovalState.APPROVED_HERE, status.getDataApprovalState() );
        assertNotNull( dataApprovalB );
        assertEquals( dataSetA.getId(), dataApprovalB.getDataSet().getId() );
        assertEquals( periodA, dataApprovalB.getPeriod() );
        assertEquals( organisationUnitB.getId(), dataApprovalB.getOrganisationUnit().getId() );
        assertEquals( date, dataApprovalB.getCreated() );
        assertEquals( userA.getId(), dataApprovalB.getCreator().getId() );

        status = dataApprovalService.getDataApprovalStatus( dataSetA, periodB, organisationUnitA, noCategoryOptionGroup );
        dataApprovalC = status.getDataApproval();
        assertEquals( DataApprovalState.APPROVED_HERE, status.getDataApprovalState() );
        assertNotNull( dataApprovalC );
        assertEquals( dataSetA.getId(), dataApprovalC.getDataSet().getId() );
        assertEquals( periodB, dataApprovalC.getPeriod() );
        assertEquals( organisationUnitA.getId(), dataApprovalC.getOrganisationUnit().getId() );
        assertEquals( date, dataApprovalC.getCreated() );
        assertEquals( userA.getId(), dataApprovalC.getCreator().getId() );

        status = dataApprovalService.getDataApprovalStatus( dataSetB, periodA, organisationUnitA, noCategoryOptionGroup );
        dataApprovalD = status.getDataApproval();
        assertEquals( DataApprovalState.APPROVED_HERE, status.getDataApprovalState() );
        assertNotNull( dataApprovalD );
        assertEquals( dataSetB.getId(), dataApprovalD.getDataSet().getId() );
        assertEquals( periodA, dataApprovalD.getPeriod() );
        assertEquals( organisationUnitA.getId(), dataApprovalD.getOrganisationUnit().getId() );
        assertEquals( date, dataApprovalD.getCreated() );
        assertEquals( userA.getId(), dataApprovalD.getCreator().getId() );

        status = dataApprovalService.getDataApprovalStatus( dataSetB, periodB, organisationUnitB, noCategoryOptionGroup );
        dataApprovalE = status.getDataApproval();
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, status.getDataApprovalState() );
        assertNull( dataApprovalE );
    }

    @Test
    public void testAddDuplicateDataApproval() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, organisationUnitA, noCategoryOptionGroup, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, organisationUnitA, noCategoryOptionGroup, date, userA );

        dataApprovalService.addDataApproval( dataApprovalA );

        try
        {
            dataApprovalService.addDataApproval( dataApprovalB );
            fail("Should give unique constraint violation");
        }
        catch ( Exception e )
        {
            // Expected
        }
    }

    @Test
    public void testDeleteDataApproval() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, organisationUnitA, noCategoryOptionGroup, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup, date, userB );
        DataApproval testA;
        DataApproval testB;

        dataApprovalService.addDataApproval( dataApprovalA );
        dataApprovalService.addDataApproval( dataApprovalB );

        dataSetA.setApproveData( true );

        testA = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noCategoryOptionGroup ).getDataApproval();
        assertNotNull( testA );

        testB = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup ).getDataApproval();
        assertNotNull( testB );

        dataApprovalService.deleteDataApproval( dataApprovalA ); // Only A should be deleted.

        testA = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noCategoryOptionGroup ).getDataApproval();
        assertNull( testA );

        testB = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup ).getDataApproval();
        assertNotNull( testB );

        dataApprovalService.addDataApproval( dataApprovalA );
        dataApprovalService.deleteDataApproval( dataApprovalB ); // A and B should both be deleted.

        testA = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noCategoryOptionGroup ).getDataApproval();
        assertNull( testA );

        testB = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup ).getDataApproval();
        assertNull( testB );
    }

    @Test
    public void testGetDataApprovalState() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        // Not enabled.
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noCategoryOptionGroup ).getDataApprovalState() );

        // Enabled for data set, but data set not associated with organisation unit.

        dataSetA.setApproveData( true );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noCategoryOptionGroup ).getDataApprovalState() );

        // Enabled for data set, and associated with organisation unit C.
        organisationUnitC.addDataSet( dataSetA );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noCategoryOptionGroup ).getDataApprovalState() );

        Date date = new Date();

        // Approved for sourceD
        DataApproval dataApprovalD = new DataApproval( dataSetA, periodA, organisationUnitD, noCategoryOptionGroup, date, userA );
        dataApprovalService.addDataApproval( dataApprovalD );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noCategoryOptionGroup ).getDataApprovalState() );

        // Also approved for sourceC
        DataApproval dataApprovalC = new DataApproval( dataSetA, periodA, organisationUnitC, noCategoryOptionGroup, date, userA );
        dataApprovalService.addDataApproval( dataApprovalC );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noCategoryOptionGroup ).getDataApprovalState() );

        // Also approved for sourceF
        DataApproval dataApprovalF = new DataApproval( dataSetA, periodA, organisationUnitF, noCategoryOptionGroup, date, userA );
        dataApprovalService.addDataApproval( dataApprovalF );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noCategoryOptionGroup ).getDataApprovalState() );

        // Also approved also for sourceE
        DataApproval dataApprovalE = new DataApproval( dataSetA, periodA, organisationUnitE, noCategoryOptionGroup, date, userA );
        dataApprovalService.addDataApproval( dataApprovalE );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noCategoryOptionGroup ).getDataApprovalState() );

        // Disable approval for dataset.
        dataSetA.setApproveData( false );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noCategoryOptionGroup ).getDataApprovalState() );
    }

    @Test
    public void testGetDataApprovalStateWithMultipleChildren() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        dataSetA.setApproveData( true );

        organisationUnitD.addDataSet( dataSetA );
        organisationUnitF.addDataSet( dataSetA );

        Date date = new Date();

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noCategoryOptionGroup ).getDataApprovalState() );

        dataApprovalService.addDataApproval( new DataApproval( dataSetA, periodA, organisationUnitF, noCategoryOptionGroup, date, userA ) );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noCategoryOptionGroup ).getDataApprovalState() );

        dataApprovalService.addDataApproval( new DataApproval( dataSetA, periodA, organisationUnitD, noCategoryOptionGroup, date, userA ) );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noCategoryOptionGroup ).getDataApprovalState() );

        dataApprovalService.addDataApproval( new DataApproval( dataSetA, periodA, organisationUnitE, noCategoryOptionGroup, date, userA ) );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noCategoryOptionGroup ).getDataApprovalState() );

        dataApprovalService.addDataApproval( new DataApproval( dataSetA, periodA, organisationUnitC, noCategoryOptionGroup, date, userA ) );

        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, noCategoryOptionGroup ).getDataApprovalState() );
    }

    @Test
    public void testGetDataApprovalStateOtherPeriodTypes() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        dataSetA.setApproveData( true );

        organisationUnitA.addDataSet( dataSetA );
        organisationUnitD.addDataSet( dataSetA );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodB, organisationUnitA, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodC, organisationUnitA, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodC, organisationUnitD, noCategoryOptionGroup ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodD, organisationUnitD, noCategoryOptionGroup ).getDataApprovalState() );
    }

    @Test
    public void testMayApprove() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitC );
        createUserAndInjectSecurityContext( units, false, DataApproval.AUTH_APPROVE );

        assertEquals( false, dataApprovalService.mayApprove( organisationUnitA ) );
        assertEquals( false, dataApprovalService.mayApprove( organisationUnitB ) );
        assertEquals( true, dataApprovalService.mayApprove( organisationUnitC ) );
        assertEquals( false, dataApprovalService.mayApprove( organisationUnitD ) );
    }

    @Test
    public void testMayApproveLowerLevels() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        createUserAndInjectSecurityContext( units, false, DataApproval.AUTH_APPROVE_LOWER_LEVELS );

        assertEquals( false, dataApprovalService.mayApprove( organisationUnitA ) );
        assertEquals( false, dataApprovalService.mayApprove( organisationUnitB ) );
        assertEquals( true, dataApprovalService.mayApprove( organisationUnitC ) );
        assertEquals( true, dataApprovalService.mayApprove( organisationUnitD ) );
    }

    @Test
    public void testMayApproveSameAndLowerLevels() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        createUserAndInjectSecurityContext( units, false, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS );

        assertEquals( false, dataApprovalService.mayApprove( organisationUnitA ) );
        assertEquals( true, dataApprovalService.mayApprove( organisationUnitB ) );
        assertEquals( true, dataApprovalService.mayApprove( organisationUnitC ) );
        assertEquals( true, dataApprovalService.mayApprove( organisationUnitD ) );
    }

    @Test
    public void testMayApproveNoAuthority() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitC );
        createUserAndInjectSecurityContext( units, false );

        assertEquals( false, dataApprovalService.mayApprove( organisationUnitA ) );
        assertEquals( false, dataApprovalService.mayApprove( organisationUnitB ) );
        assertEquals( false, dataApprovalService.mayApprove( organisationUnitC ) );
        assertEquals( false, dataApprovalService.mayApprove( organisationUnitD ) );
    }

    @Test
    public void testMayUnapprove() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        createUserAndInjectSecurityContext( units, false, DataApproval.AUTH_APPROVE );

        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, organisationUnitA, noCategoryOptionGroup, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup, date, userA );
        DataApproval dataApprovalC = new DataApproval( dataSetA, periodA, organisationUnitC, noCategoryOptionGroup, date, userA );
        DataApproval dataApprovalD = new DataApproval( dataSetA, periodA, organisationUnitD, noCategoryOptionGroup, date, userA );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalB ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalC ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalD ) );

        // These approvals will not block un-approving
        dataApprovalService.addDataApproval( dataApprovalB );
        dataApprovalService.addDataApproval( dataApprovalC );
        dataApprovalService.addDataApproval( dataApprovalD );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalB ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalC ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalD ) );

        // Approval at A will block them all
        dataApprovalService.addDataApproval( dataApprovalA );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalB ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalC ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalD ) );
    }

    @Test
    public void testMayUnapproveLowerLevels() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        createUserAndInjectSecurityContext( units, false, DataApproval.AUTH_APPROVE_LOWER_LEVELS );

        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, organisationUnitA, noCategoryOptionGroup, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup, date, userA );
        DataApproval dataApprovalC = new DataApproval( dataSetA, periodA, organisationUnitC, noCategoryOptionGroup, date, userA );
        DataApproval dataApprovalD = new DataApproval( dataSetA, periodA, organisationUnitD, noCategoryOptionGroup, date, userA );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalB ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalC ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalD ) );

        // These approvals will not block un-approving
        dataApprovalService.addDataApproval( dataApprovalC );
        dataApprovalService.addDataApproval( dataApprovalD );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalB ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalC ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalD ) );

        // Approval at B will block them all
        dataApprovalService.addDataApproval( dataApprovalB );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalB ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalC ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalD ) );
    }

    @Test
    public void testMayUnapproveSameAndLowerLevels() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        createUserAndInjectSecurityContext( units, false, DataApproval.AUTH_APPROVE );

        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, organisationUnitA, noCategoryOptionGroup, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup, date, userA );
        DataApproval dataApprovalC = new DataApproval( dataSetA, periodA, organisationUnitC, noCategoryOptionGroup, date, userA );
        DataApproval dataApprovalD = new DataApproval( dataSetA, periodA, organisationUnitD, noCategoryOptionGroup, date, userA );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalB ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalC ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalD ) );

        // These approvals will not block un-approving
        dataApprovalService.addDataApproval( dataApprovalB );
        dataApprovalService.addDataApproval( dataApprovalC );
        dataApprovalService.addDataApproval( dataApprovalD );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalB ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalC ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalD ) );

        // Approval at A will block them all
        dataApprovalService.addDataApproval( dataApprovalA );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalB ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalC ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalD ) );
    }

    @Test
    public void testMayUnapproveNoAuthority() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel1 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel3 );
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel4 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        createUserAndInjectSecurityContext( units, false );

        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, organisationUnitA, noCategoryOptionGroup, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, organisationUnitB, noCategoryOptionGroup, date, userA );
        DataApproval dataApprovalC = new DataApproval( dataSetA, periodA, organisationUnitC, noCategoryOptionGroup, date, userA );
        DataApproval dataApprovalD = new DataApproval( dataSetA, periodA, organisationUnitD, noCategoryOptionGroup, date, userA );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalB ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalC ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalD ) );
    }

    // ---------------------------------------------------------------------
    // Test with Categories
    // ---------------------------------------------------------------------

    @Test
    public void testAddAndGetDataApprovalWithCategories() throws Exception
    {
        setUpCategories();

        Date date = new Date();

        //
        // Group set A -> Groups A,B -> Options A,B,C,D
        // Group set B -> Groups C,D -> Options E,F,G,H
        //
        // Category A -> Options A,B,C,D
        // Category B -> Options E,F,G,H
        //

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, categoryOptionGroupA ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, categoryOptionGroupA ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, categoryOptionGroupA ).getDataApprovalState() );
/* In progress:
        dataApprovalLevelService.addDataApprovalLevel( dataApprovalLevel2A );

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, categoryOptionGroupA ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, categoryOptionGroupA ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, categoryOptionGroupA ).getDataApprovalState() );

        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, organisationUnitA, categoryOptionGroupA, date, userA );

        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, categoryOptionGroupA ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, categoryOptionGroupA ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, categoryOptionGroupA ).getDataApprovalState() );
*/
    }
}
