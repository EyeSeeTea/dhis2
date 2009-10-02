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

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
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
    
    private Set<ProgramStage> programStages = new HashSet<ProgramStage>();
    
    private OrganisationUnit organisationUnit;
    
    ProgramStage programStageA;
    
    ProgramStage programStageB;
    
    ProgramStage programStageC;
    
    
    @Override
    public void setUpTest()
    {
        programStageService = (ProgramStageService) getBean( ProgramStageService.ID );
        
        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );       
        
        programStore = (ProgramStore) getBean( ProgramStore.ID );
        
        organisationUnit = createOrganisationUnit( 'A' );
        
        organisationUnitService.addOrganisationUnit( organisationUnit );              
        
        programStageA = createProrgamStage( 'A', 1, 1, 10 );       
        programStageB = createProrgamStage( 'B', 2, 12, 40 );
        programStageC = createProrgamStage( 'C', 3, 45, 60 );        

        programStageService.addProgramStage( programStageA );
        programStageService.addProgramStage( programStageB );
        programStageService.addProgramStage( programStageC );
        
        programStages.add( programStageA );
        programStages.add( programStageB );
        programStages.add( programStageC );    
            
    }
    
    protected static Program createProgram( char uniqueCharacter, Set<ProgramStage> programStages, OrganisationUnit organisationUnit )
    {
        Program program = new Program();
        
        program.setName( "Program" + uniqueCharacter );
        program.setDescription( "Description" + uniqueCharacter );
        program.setNumberOfDays( 5 );
        program.setOrganisationUnit( organisationUnit );
        program.setProgramStages( programStages );
        
        return program;
    }
    
    @Test
    public void addGet()
    {
        Program programA = createProgram( 'A', programStages, organisationUnit );
        Program programB = createProgram( 'B', programStages, organisationUnit );
        
        int idA = programStore.addProgram( programA );
        int idB = programStore.addProgram( programB );
        
        assertEquals( programA, programStore.getProgram( idA ) );
        assertEquals( programB, programStore.getProgram( idB ) );

        assertEquals( programA.getOrganisationUnit(), programStore.getProgram( idA ).getOrganisationUnit() );
        assertEquals( programB.getOrganisationUnit(), programStore.getProgram( idB ).getOrganisationUnit() );
        
        assertEquals( programStages.size(), programStore.getProgram( idA ).getProgramStages().size() );
        assertEquals( programStages.size(), programStore.getProgram( idB ).getProgramStages().size() );
                
        assertTrue( programStore.getProgram( idA ).getProgramStages().containsAll( programStages ) );
        assertTrue( programStore.getProgram( idB ).getProgramStages().containsAll( programStages ) );
    }    
}

