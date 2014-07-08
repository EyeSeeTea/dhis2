package org.hisp.dhis.datavalue;

/*
 * Copyright (c) 2004-2014, University of Oslo
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

import org.hisp.dhis.DhisTest;
import org.hisp.dhis.common.AuditModificationType;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.junit.Test;

import java.util.Collection;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by Halvdan Hoem Grelland
 */
public class DataValueAuditServiceTest
        extends DhisTest
{
    // -------------------------------------------------------------------------
    // Supporting data
    // -------------------------------------------------------------------------

    private DataElement dataElementA;

    private DataElement dataElementB;

    private DataElement dataElementC;

    private DataElement dataElementD;

    private DataElementCategoryOptionCombo optionCombo;

    private Period periodA;

    private Period periodB;

    private Period periodC;

    private Period periodD;

    private OrganisationUnit orgUnitA;

    private OrganisationUnit orgUnitB;

    private OrganisationUnit orgUnitC;

    private OrganisationUnit orgUnitD;

    private DataValue dataValueA;

    private DataValue dataValueB;

    private DataValue dataValueC;

    private DataValue dataValueD;

    // -------------------------------------------------------------------------
    // Set up/tear down
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws Exception
    {
        dataValueAuditService = (DataValueAuditService) getBean( DataValueAuditService.ID );

        dataValueService = (DataValueService) getBean( DataValueService.ID );

        dataElementService = (DataElementService) getBean( DataElementService.ID );

        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );

        periodService = (PeriodService) getBean( PeriodService.ID );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );

        // ---------------------------------------------------------------------
        // Add supporting data
        // ---------------------------------------------------------------------

        dataElementA = createDataElement( 'A' );
        dataElementB = createDataElement( 'B' );
        dataElementC = createDataElement( 'C' );
        dataElementD = createDataElement( 'D' );

        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );
        dataElementService.addDataElement( dataElementC );
        dataElementService.addDataElement( dataElementD );

        periodA = createPeriod( getDay( 5 ), getDay( 6 ) );
        periodB = createPeriod( getDay( 6 ), getDay( 7 ) );
        periodC = createPeriod( getDay( 7 ), getDay( 8 ) );
        periodD = createPeriod( getDay( 8 ), getDay( 9 ) );

        orgUnitA = createOrganisationUnit( 'A' );
        orgUnitB = createOrganisationUnit( 'B' );
        orgUnitC = createOrganisationUnit( 'C' );
        orgUnitD = createOrganisationUnit( 'D' );

        organisationUnitService.addOrganisationUnit( orgUnitA );
        organisationUnitService.addOrganisationUnit( orgUnitB );
        organisationUnitService.addOrganisationUnit( orgUnitC );
        organisationUnitService.addOrganisationUnit( orgUnitD );

        optionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();

        dataValueA = createDataValue( dataElementA, periodA, orgUnitA,  "1", optionCombo );
        dataValueB = createDataValue( dataElementB, periodB, orgUnitB,  "2", optionCombo );
        dataValueC = createDataValue( dataElementC, periodC, orgUnitC,  "3", optionCombo );
        dataValueD = createDataValue( dataElementD, periodD, orgUnitD,  "4", optionCombo );

        dataValueService.addDataValue( dataValueA );
        dataValueService.addDataValue( dataValueB );
        dataValueService.addDataValue( dataValueC );
        dataValueService.addDataValue( dataValueD );
    }

    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }

    // -------------------------------------------------------------------------
    // Basic DataValueAudit
    // -------------------------------------------------------------------------

    @Test
    public void testAddDataValueAudit()
    {
        Date now = new Date();

        DataValueAudit dataValueAuditA = new DataValueAudit( dataValueA, dataValueA.getValue(), dataValueA.getStoredBy(),
            now, AuditModificationType.UPDATE );
        DataValueAudit dataValueAuditB = new DataValueAudit( dataValueB, dataValueB.getValue(), dataValueB.getStoredBy(),
            now, AuditModificationType.UPDATE );

        dataValueAuditService.addDataValueAudit( dataValueAuditA );
        dataValueAuditService.addDataValueAudit( dataValueAuditB );

        Collection<DataValueAudit> audits = dataValueAuditService.getDataValueAudits( dataValueA );
        assertNotNull( audits );
        assertTrue( audits.contains( dataValueAuditA ) );
        assertFalse( audits.contains( dataValueAuditB ) );
    }

    @Test
    public void testDeleteDataValueAudit()
    {
        Date now = new Date();

        DataValueAudit dataValueAuditA = new DataValueAudit( dataValueA, dataValueA.getValue(), dataValueA.getStoredBy(),
            now, AuditModificationType.UPDATE );
        DataValueAudit dataValueAuditB = new DataValueAudit( dataValueA, dataValueA.getValue(), dataValueA.getStoredBy(),
            now, AuditModificationType.DELETE );

        dataValueAuditService.addDataValueAudit( dataValueAuditA );
        dataValueAuditService.addDataValueAudit( dataValueAuditB );

        dataValueAuditService.deleteDataValueAudit( dataValueAuditA );

        Collection<DataValueAudit> audits = dataValueAuditService.getDataValueAudits( dataValueA );

        assertNotNull( audits );
        assertEquals( audits.size(), 1 );
        assertTrue( audits.contains( dataValueAuditB ) );
    }

    // -------------------------------------------------------------------------
    // DeletionHandler methods
    // -------------------------------------------------------------------------

    @Test
    public void testDeleteDataValueAuditByDataElement()
    {
        Date now = new Date();

        DataValueAudit dataValueAuditA = new DataValueAudit( dataValueA, dataValueA.getValue(), dataValueA.getStoredBy(),
            now, AuditModificationType.UPDATE );
        DataValueAudit dataValueAuditB = new DataValueAudit( dataValueB, dataValueB.getValue(), dataValueB.getStoredBy(),
            now, AuditModificationType.UPDATE );

        DataValueAudit dataValueAuditC1 = new DataValueAudit( dataValueC, dataValueC.getValue(), dataValueC.getStoredBy(),
            now, AuditModificationType.UPDATE );
        DataValueAudit dataValueAuditC2 = new DataValueAudit( dataValueC, dataValueC.getValue(), dataValueC.getStoredBy(),
            now, AuditModificationType.UPDATE );

        dataValueAuditService.addDataValueAudit( dataValueAuditA );
        dataValueAuditService.addDataValueAudit( dataValueAuditB );

        dataValueAuditService.addDataValueAudit( dataValueAuditC1 );
        dataValueAuditService.addDataValueAudit( dataValueAuditC2 );

        dataValueAuditService.deleteDataValueAuditByDataElement( dataValueAuditA.getDataElement() );

        Collection<DataValueAudit> audits = dataValueAuditService.getDataValueAudits( dataValueA );

        assertTrue( audits.isEmpty() );

        audits = dataValueAuditService.getDataValueAudits( dataValueB );
        assertTrue( audits.contains( dataValueAuditB ) );

        dataValueAuditService.deleteDataValueAuditByDataElement( dataValueAuditC1.getDataElement() );

        audits = dataValueAuditService.getDataValueAudits( dataValueD );

        assertFalse( audits.contains( dataValueAuditC1 ) );
        assertFalse( audits.contains( dataValueAuditC2 ) );
    }

    @Test
    public void testDeleteDataValueAuditByPeriod()
    {
        Date now = new Date();

        DataValueAudit dataValueAuditA = new DataValueAudit( dataValueA, dataValueA.getValue(), dataValueA.getStoredBy(),
            now, AuditModificationType.UPDATE );
        DataValueAudit dataValueAuditB = new DataValueAudit( dataValueB, dataValueB.getValue(), dataValueB.getStoredBy(),
            now, AuditModificationType.UPDATE );

        DataValueAudit dataValueAuditC1 = new DataValueAudit( dataValueC, dataValueC.getValue(), dataValueC.getStoredBy(),
            now, AuditModificationType.UPDATE );
        DataValueAudit dataValueAuditC2 = new DataValueAudit( dataValueC, dataValueC.getValue(), dataValueC.getStoredBy(),
            now, AuditModificationType.UPDATE );

        dataValueAuditService.addDataValueAudit( dataValueAuditA );
        dataValueAuditService.addDataValueAudit( dataValueAuditB );

        dataValueAuditService.addDataValueAudit( dataValueAuditC1 );
        dataValueAuditService.addDataValueAudit( dataValueAuditC2 );

        dataValueAuditService.deleteDataValueAuditByPeriod( dataValueAuditA.getPeriod() );

        Collection<DataValueAudit> audits = dataValueAuditService.getDataValueAudits( dataValueA );

        assertTrue( audits.isEmpty() );

        dataValueAuditService.deleteDataValueAuditByPeriod( dataValueAuditC1.getPeriod() );

        audits = dataValueAuditService.getDataValueAudits( dataValueB );

        assertTrue( audits.contains( dataValueAuditB ) );

        audits = dataValueAuditService.getDataValueAudits( dataValueC );

        assertTrue( audits.isEmpty() );
    }

    @Test
    public void testDeleteDataValueAuditByOrganisationUnit()
    {
        Date now = new Date();

        DataValueAudit dataValueAuditA = new DataValueAudit( dataValueA, dataValueA.getValue(), dataValueA.getStoredBy(),
            now, AuditModificationType.UPDATE );
        DataValueAudit dataValueAuditB = new DataValueAudit( dataValueB, dataValueB.getValue(), dataValueB.getStoredBy(),
            now, AuditModificationType.UPDATE );

        DataValueAudit dataValueAuditC1 = new DataValueAudit( dataValueC, dataValueC.getValue(), dataValueC.getStoredBy(),
            now, AuditModificationType.UPDATE );
        DataValueAudit dataValueAuditC2 = new DataValueAudit( dataValueC, dataValueC.getValue(), dataValueC.getStoredBy(),
            now, AuditModificationType.UPDATE );

        dataValueAuditService.addDataValueAudit( dataValueAuditA );
        dataValueAuditService.addDataValueAudit( dataValueAuditB );

        dataValueAuditService.addDataValueAudit( dataValueAuditC1 );
        dataValueAuditService.addDataValueAudit( dataValueAuditC2 );

        dataValueAuditService.deleteDataValueAuditByOrganisationUnit( dataValueAuditA.getOrganisationUnit() );

        Collection<DataValueAudit> audits = dataValueAuditService.getDataValueAudits( dataValueA );

        assertTrue( audits.isEmpty() );

        dataValueAuditService.deleteDataValueAuditByOrganisationUnit( dataValueAuditC1.getOrganisationUnit() );

        audits = dataValueAuditService.getDataValueAudits( dataValueC );

        assertTrue( audits.isEmpty() );

        audits = dataValueAuditService.getDataValueAudits( dataValueB );

        assertTrue( audits.contains( dataValueAuditB ) );
    }

    @Test
    public void testDeleteDataValueAuditByCategoryOptionCombo()
    {
        Date now = new Date();

        DataValueAudit dataValueAuditA = new DataValueAudit( dataValueA, dataValueA.getValue(), dataValueA.getStoredBy(),
            now, AuditModificationType.UPDATE );
        DataValueAudit dataValueAuditB = new DataValueAudit( dataValueB, dataValueB.getValue(), dataValueB.getStoredBy(),
            now, AuditModificationType.UPDATE );
        DataValueAudit dataValueAuditC = new DataValueAudit( dataValueC, dataValueC.getValue(), dataValueC.getStoredBy(),
            now, AuditModificationType.UPDATE );

        dataValueAuditService.addDataValueAudit( dataValueAuditA );
        dataValueAuditService.addDataValueAudit( dataValueAuditB );
        dataValueAuditService.addDataValueAudit( dataValueAuditC );

        dataValueAuditService.deleteDataValueAuditByCategoryOptionCombo( optionCombo );

        Collection<DataValueAudit> audits = dataValueAuditService.getDataValueAudits( dataValueA );
        assertTrue( audits.isEmpty() );

        audits = dataValueAuditService.getDataValueAudits( dataValueB );
        assertTrue( audits.isEmpty() );

        audits = dataValueAuditService.getDataValueAudits( dataValueC );
        assertTrue( audits.isEmpty() );
    }
}
