package org.hisp.dhis.program;

/*
 * Copyright (c) 2004-2015, University of Oslo
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
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class ProgramRuleServiceTest
    extends DhisSpringTest
{
    private Program programA;
    private ProgramStage programStageA;
    
    @Autowired
    private ProgramService programService;
    
    @Autowired
    private ProgramStageService programStageService;
    
    @Autowired
    private ProgramRuleService ruleService;
    
    @Override
    public void setUpTest()
    {
        programA = createProgram( 'A', null, null );
        programStageA = createProgramStage( 'A', 1 );
       
        
        programService.addProgram( programA );
        programStageService.saveProgramStage( programStageA );
    }
    
    @Test
    public void testAddGet()
    {
        ProgramRule ruleA = new ProgramRule( "RuleA", "descriptionA", programA, programStageA, null, "true", null);
        ProgramRule ruleB = new ProgramRule( "RuleA", "descriptionA", programA, programStageA, null, "true", 1);
        ProgramRule ruleC = new ProgramRule( "RuleA", "descriptionA", programA, programStageA, null, "true", 0);
        
        int idA = ruleService.addProgramRule( ruleA );
        int idB = ruleService.addProgramRule( ruleB );
        int idC = ruleService.addProgramRule( ruleC );
        
        assertEquals( ruleA, ruleService.getProgramRule( idA ) );
        assertEquals( ruleB, ruleService.getProgramRule( idB ) );
        assertEquals( ruleC, ruleService.getProgramRule( idC ) );
    }
}
