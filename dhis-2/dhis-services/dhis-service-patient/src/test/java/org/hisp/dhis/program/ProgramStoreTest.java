/*
 * Copyright (c) 2004-2009, University of Oslo
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

package org.hisp.dhis.program;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */     
public class ProgramStoreTest
    extends DhisSpringTest
{    
    private ProgramStore programStore;
    
    private List<DataSet> dataSets = new ArrayList<DataSet>();
    
    private OrganisationUnit organisationUnit;
    
    @Override
    public void setUpTest()
    {
        dataSetService = (DataSetService) getBean( DataSetService.ID );
        
        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );
        
        periodService = (PeriodService) getBean( PeriodService.ID );
        
        programStore = (ProgramStore) getBean( ProgramStore.ID );
        
        organisationUnit = createOrganisationUnit( 'A' );
        
        organisationUnitService.addOrganisationUnit( organisationUnit );
        
        PeriodType periodType = periodService.getPeriodTypeByName( MonthlyPeriodType.NAME );
        
        DataSet dataSetA = createDataSet( 'A', periodType );
        DataSet dataSetB = createDataSet( 'B', periodType );
        DataSet dataSetC = createDataSet( 'C', periodType );
        
        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );
        dataSetService.addDataSet( dataSetC );
        
        dataSets.add( dataSetA );
        dataSets.add( dataSetB );
        dataSets.add( dataSetC );        
    }
    
    protected static Program createProgram( char uniqueCharacter, List<DataSet> dataSets, OrganisationUnit organisationUnit )
    {
        Program program = new Program();
        
        program.setName( "Program" + uniqueCharacter );
        program.setDescription( "Description" + uniqueCharacter );
        program.setNumberOfDays( 5 );
        program.setOrganisationUnit( organisationUnit );
        program.setDataSets( dataSets );
        
        return program;
    }
    
    @Test
    public void addGet()
    {
        Program programA = createProgram( 'A', dataSets, organisationUnit );
        Program programB = createProgram( 'B', dataSets, organisationUnit );
        
        int idA = programStore.addProgram( programA );
        int idB = programStore.addProgram( programB );
        
        assertEquals( programA, programStore.getProgram( idA ) );
        assertEquals( programB, programStore.getProgram( idB ) );

        assertEquals( programA.getOrganisationUnit(), programStore.getProgram( idA ).getOrganisationUnit() );
        assertEquals( programB.getOrganisationUnit(), programStore.getProgram( idB ).getOrganisationUnit() );
        
        assertEquals( dataSets.size(), programStore.getProgram( idA ).getDataSets().size() );
        assertEquals( dataSets.size(), programStore.getProgram( idB ).getDataSets().size() );
                
        assertTrue( programStore.getProgram( idA ).getDataSets().containsAll( dataSets ) );
        assertTrue( programStore.getProgram( idB ).getDataSets().containsAll( dataSets ) );
    }    
}
