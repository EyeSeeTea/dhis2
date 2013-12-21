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

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author Jim Grace
 * @version $Id$
 */
public class DataApprovalServiceTest
        extends DhisSpringTest
{
    private DataApprovalService dataApprovalService;

    private PeriodStore periodStore;

    private SystemSettingManager systemSettingManager;

    // -------------------------------------------------------------------------
    // Supporting data
    // -------------------------------------------------------------------------

    private DataSet dataSetA;

    private DataSet dataSetB;

    private Period periodA;

    private Period periodB;

    private OrganisationUnit sourceA;

    private OrganisationUnit sourceB;

    private OrganisationUnit sourceC;

    private OrganisationUnit sourceD;

    private User userA;

    private User userB;

    // -------------------------------------------------------------------------
    // Set up/tear down
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest() throws Exception
    {
        dataApprovalService = (DataApprovalService) getBean( DataApprovalService.ID );

        dataSetService = (DataSetService) getBean( DataSetService.ID );

        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );

        periodStore = (PeriodStore) getBean( PeriodStore.ID );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );

        userService = (UserService) getBean( UserService.ID );

        systemSettingManager = (SystemSettingManager) getBean( SystemSettingManager.ID );

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

        periodStore.addPeriod( periodA );
        periodStore.addPeriod( periodB );

        sourceA = createOrganisationUnit( 'A' );
        sourceB = createOrganisationUnit( 'B', sourceA );
        sourceC = createOrganisationUnit( 'C', sourceB );
        sourceD = createOrganisationUnit( 'D', sourceC );

        organisationUnitService.addOrganisationUnit( sourceA );
        organisationUnitService.addOrganisationUnit( sourceB );
        organisationUnitService.addOrganisationUnit( sourceC );
        organisationUnitService.addOrganisationUnit( sourceD );

        userA = createUser( 'A' );
        userB = createUser( 'B' );

        userService.addUser( userA );
        userService.addUser( userB );

    }

    // -------------------------------------------------------------------------
    // Basic DataApproval
    // -------------------------------------------------------------------------

    @Test
    public void testAddAndGetDataApproval() throws Exception
    {
        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, sourceA, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, sourceB, date, userA );
        DataApproval dataApprovalC = new DataApproval( dataSetA, periodB, sourceA, date, userA );
        DataApproval dataApprovalD = new DataApproval( dataSetB, periodA, sourceA, date, userA );
        DataApproval dataApprovalE;

        dataApprovalService.addDataApproval( dataApprovalA );
        dataApprovalService.addDataApproval( dataApprovalB );
        dataApprovalService.addDataApproval( dataApprovalC );
        dataApprovalService.addDataApproval( dataApprovalD );

        dataApprovalA = dataApprovalService.getDataApproval( dataSetA, periodA, sourceA );
        assertNotNull( dataApprovalA );
        assertEquals( dataSetA.getId(), dataApprovalA.getDataSet().getId() );
        assertEquals( periodA, dataApprovalA.getPeriod() );
        assertEquals( sourceA.getId(), dataApprovalA.getSource().getId() );
        assertEquals( date, dataApprovalA.getCreated() );
        assertEquals( userA.getId(), dataApprovalA.getCreator().getId() );

        dataApprovalB = dataApprovalService.getDataApproval( dataSetA, periodA, sourceB );
        assertNotNull( dataApprovalB );
        assertEquals( dataSetA.getId(), dataApprovalB.getDataSet().getId() );
        assertEquals( periodA, dataApprovalB.getPeriod() );
        assertEquals( sourceB.getId(), dataApprovalB.getSource().getId() );
        assertEquals( date, dataApprovalB.getCreated() );
        assertEquals( userA.getId(), dataApprovalB.getCreator().getId() );

        dataApprovalC = dataApprovalService.getDataApproval( dataSetA, periodB, sourceA );
        assertNotNull( dataApprovalC );
        assertEquals( dataSetA.getId(), dataApprovalC.getDataSet().getId() );
        assertEquals( periodB, dataApprovalC.getPeriod() );
        assertEquals( sourceA.getId(), dataApprovalC.getSource().getId() );
        assertEquals( date, dataApprovalC.getCreated() );
        assertEquals( userA.getId(), dataApprovalC.getCreator().getId() );

        dataApprovalD = dataApprovalService.getDataApproval( dataSetB, periodA, sourceA );
        assertNotNull( dataApprovalD );
        assertEquals( dataSetB.getId(), dataApprovalD.getDataSet().getId() );
        assertEquals( periodA, dataApprovalD.getPeriod() );
        assertEquals( sourceA.getId(), dataApprovalD.getSource().getId() );
        assertEquals( date, dataApprovalD.getCreated() );
        assertEquals( userA.getId(), dataApprovalD.getCreator().getId() );

        dataApprovalE = dataApprovalService.getDataApproval( dataSetB, periodB, sourceB );
        assertNull( dataApprovalE );
    }

    @Test
    public void testAddDuplicateDataApproval() throws Exception
    {
        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, sourceA, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, sourceA, date, userA );

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
        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, sourceA, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, sourceB, date, userB );
        DataApproval testA;
        DataApproval testB;

        dataApprovalService.addDataApproval( dataApprovalA );
        dataApprovalService.addDataApproval( dataApprovalB );

        testA = dataApprovalService.getDataApproval( dataSetA, periodA, sourceA );
        assertNotNull( testA );

        testB = dataApprovalService.getDataApproval( dataSetA, periodA, sourceB );
        assertNotNull( testB );

        dataApprovalService.deleteDataApproval( dataApprovalA ); // Only A should be deleted.

        testA = dataApprovalService.getDataApproval( dataSetA, periodA, sourceA );
        assertNull( testA );

        testB = dataApprovalService.getDataApproval( dataSetA, periodA, sourceB );
        assertNotNull( testB );

        dataApprovalService.addDataApproval( dataApprovalA );
        dataApprovalService.deleteDataApproval( dataApprovalB ); // A and B should both be deleted.

        testA = dataApprovalService.getDataApproval( dataSetA, periodA, sourceA );
        assertNull( testA );

        testB = dataApprovalService.getDataApproval( dataSetA, periodA, sourceB );
        assertNull( testB );
    }

    @Test
    public void testGetDataApprovalState() throws Exception
    {
        // Not enabled.
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceA ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceB ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceC ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceD ) );

        // Enabled globally, but not for data set.
        systemSettingManager.saveSystemSetting( SystemSettingManager.KEY_ENABLE_DATA_APPROVAL, true );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceA ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceB ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceC ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceD ) );

        // Enabled for data set, but data set not associated with organisation unit.
        dataSetA.setApproveData( true );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceA ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceB ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceC ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceD ) );

        // Enabled for data set, and associated with organisation unit C.
        sourceC.addDataSet( dataSetA );
        assertEquals( DataApprovalState.WAITING_FOR_LOWER_LEVEL_APPROVAL, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceA ) );
        assertEquals( DataApprovalState.WAITING_FOR_LOWER_LEVEL_APPROVAL, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceB ) );
        assertEquals( DataApprovalState.READY_FOR_APPROVAL, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceC ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceD ) );

        // Approved for sourceC
        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, sourceC, date, userA );
        dataApprovalService.addDataApproval( dataApprovalA );
        assertEquals( DataApprovalState.WAITING_FOR_LOWER_LEVEL_APPROVAL, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceA ) );
        assertEquals( DataApprovalState.READY_FOR_APPROVAL, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceB ) );
        assertEquals( DataApprovalState.APPROVED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceC ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceD ) );

        // Disable approval for dataset.
        dataSetA.setApproveData( false );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceA ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceB ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceC ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceD ) );

        // Enable for dataset but disable for system.
        dataSetA.setApproveData( true );
        systemSettingManager.saveSystemSetting( SystemSettingManager.KEY_ENABLE_DATA_APPROVAL, false );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceA ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceB ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceC ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, sourceD ) );
    }

    @Test
    public void testMayApprove() throws Exception
    {
        userB.addOrganisationUnit( sourceB );

        assertEquals( false, dataApprovalService.mayApprove( sourceA, userB, false, false ) );
        assertEquals( false, dataApprovalService.mayApprove( sourceB, userB, false, false ) );
        assertEquals( false, dataApprovalService.mayApprove( sourceC, userB, false, false ) );
        assertEquals( false, dataApprovalService.mayApprove( sourceD, userB, false, false ) );

        assertEquals( false, dataApprovalService.mayApprove( sourceA, userB, false, true ) );
        assertEquals( false, dataApprovalService.mayApprove( sourceB, userB, false, true ) );
        assertEquals( true, dataApprovalService.mayApprove( sourceC, userB, false, true ) );
        assertEquals( true, dataApprovalService.mayApprove( sourceD, userB, false, true ) );

        assertEquals( false, dataApprovalService.mayApprove( sourceA, userB, true, false ) );
        assertEquals( true, dataApprovalService.mayApprove( sourceB, userB, true, false ) );
        assertEquals( false, dataApprovalService.mayApprove( sourceC, userB, true, false ) );
        assertEquals( false, dataApprovalService.mayApprove( sourceD, userB, true, false ) );

        assertEquals( false, dataApprovalService.mayApprove( sourceA, userB, true, true ) );
        assertEquals( true, dataApprovalService.mayApprove( sourceB, userB, true, true ) );
        assertEquals( true, dataApprovalService.mayApprove( sourceC, userB, true, true ) );
        assertEquals( true, dataApprovalService.mayApprove( sourceD, userB, true, true ) );
    }

    @Test
    public void testMayUnapprove() throws Exception
    {
        userA.addOrganisationUnit( sourceA );
        userB.addOrganisationUnit( sourceB );

        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, sourceA, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, sourceB, date, userA );
        DataApproval dataApprovalC = new DataApproval( dataSetA, periodA, sourceC, date, userA );
        DataApproval dataApprovalD = new DataApproval( dataSetA, periodA, sourceD, date, userA );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA, userB, false, false ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalB, userB, false, false ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalC, userB, false, false ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalD, userB, false, false ) );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA, userB, false, true ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalB, userB, false, true ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalC, userB, false, true ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalD, userB, false, true ) );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA, userB, true, false ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalB, userB, true, false ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalC, userB, true, false ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalD, userB, true, false ) );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA, userB, true, true ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalB, userB, true, true ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalC, userB, true, true ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalD, userB, true, true ) );

        // If the organisation unit has no parent:
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA, userA, false, false ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA, userA, false, true ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalA, userA, true, false ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalA, userA, true, true ) );

        dataApprovalService.addDataApproval( dataApprovalB );
        dataApprovalService.addDataApproval( dataApprovalC );
        dataApprovalService.addDataApproval( dataApprovalD );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA, userB, true, true ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalB, userB, true, true ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalC, userB, true, true ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalD, userB, true, true ) );

        dataApprovalService.addDataApproval( dataApprovalA );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA, userB, true, true ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalB, userB, true, true ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalC, userB, true, true ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalD, userB, true, true ) );
    }
}
