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

import org.hisp.dhis.patient.Patient;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
@Transactional
public class DefaultProgramInstanceService
    implements ProgramInstanceService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramInstanceStore programInstanceStore;

    public void setProgramInstanceStore( ProgramInstanceStore programInstanceStore )
    {
        this.programInstanceStore = programInstanceStore;
    }

    // -------------------------------------------------------------------------
    // ProgramInstance implementation
    // -------------------------------------------------------------------------

    public int addProgramInstance( ProgramInstance programInstance )
    {
        return programInstanceStore.save( programInstance );
    }

    public void deleteProgramInstance( ProgramInstance programInstance )
    {
        programInstanceStore.delete( programInstance );
    }

    public Collection<ProgramInstance> getAllProgramInstances()
    {
        return programInstanceStore.getAll();
    }

    public ProgramInstance getProgramInstance( int id )
    {
        return programInstanceStore.get( id );
    }

    public Collection<ProgramInstance> getProgramInstances( boolean completed )
    {
        return programInstanceStore.getProgramInstances( completed );
    }

    public void updateProgramInstance( ProgramInstance programInstance )
    {
        programInstanceStore.update( programInstance );
    }

    public Collection<ProgramInstance> getProgramInstances( Program program )
    {
        return programInstanceStore.getProgramInstances( program );
    }

    public Collection<ProgramInstance> getProgramInstances( Program program, boolean completed )
    {
        return programInstanceStore.getProgramInstances( program, completed );
    }

    public Collection<ProgramInstance> getProgramInstances( Patient patient )
    {
        return programInstanceStore.getProgramInstances( patient );
    }

    public Collection<ProgramInstance> getProgramInstances( Patient patient, boolean completed )
    {
        return programInstanceStore.getProgramInstances( patient, completed );
    }

    public Collection<ProgramInstance> getProgramInstances( Patient patient, Program program )
    {
        return programInstanceStore.getProgramInstances( patient, program );
    }

    public Collection<ProgramInstance> getProgramInstances( Patient patient, Program program, boolean completed )
    {
        return programInstanceStore.getProgramInstances( patient, program, completed );
    }

}
