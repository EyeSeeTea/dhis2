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

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
@Transactional
public class DefaultProgramInstanceStageService
    implements ProgramInstanceStageService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramInstanceStageStore programInstanceStageStore;

    public void setProgramInstanceStageStore( ProgramInstanceStageStore programInstanceStageStore )
    {
        this.programInstanceStageStore = programInstanceStageStore;
    }

    // -------------------------------------------------------------------------
    // ProgramInstanceStage implementation
    // -------------------------------------------------------------------------

    public int addProgramInstanceStage( ProgramInstanceStage programInstanceStage )
    {
        return programInstanceStageStore.save( programInstanceStage );
    }

    public void deleteProgramInstanceStage( ProgramInstanceStage programInstanceStage )
    {
        programInstanceStageStore.delete( programInstanceStage );
    }

    public Collection<ProgramInstanceStage> getAllProgramInstanceStages()
    {
        return programInstanceStageStore.getAll();
    }

    public ProgramInstanceStage getProgramInstanceStage( int id )
    {
        return programInstanceStageStore.get( id );
    }

    public ProgramInstanceStage getProgramInstanceStage( ProgramInstance programInstance, ProgramStage programStage )
    {
        return programInstanceStageStore.getProgramInstanceStage( programInstance, programStage );
    }

    public Collection<ProgramInstanceStage> getProgramInstanceStages( ProgramInstance programInstance )
    {
        return programInstanceStageStore.get( programInstance );
    }

    public Collection<ProgramInstanceStage> getProgramInstanceStages( ProgramStage programStage )
    {
        return programInstanceStageStore.get( programStage );
    }

    public void updateProgramInstanceStage( ProgramInstanceStage programInstanceStage )
    {
        programInstanceStageStore.update( programInstanceStage );
    }    
}
