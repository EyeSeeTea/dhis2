package org.hisp.dhis.web.api.model;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Task implements DataStreamSerializable
{
    private int id;   
    
    private int programStageId;
    
    private int programId;
    
    private boolean completed;

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }    

    public int getProgramStageId()
    {
        return programStageId;
    }

    public void setProgramStageId( int programStageId )
    {
        this.programStageId = programStageId;
    }
    
    public int getProgramId()
    {
        return programId;
    }

    public void setProgramId( int programId )
    {
        this.programId = programId;
    }

    public boolean isCompleted()
    {
        return completed;
    }

    public void setCompleted( boolean completed )
    {
        this.completed = completed;
    }

    @Override
    public void serialize( DataOutputStream dout )
        throws IOException
    {
        dout.writeInt(this.getId());
        dout.writeInt(this.getProgramStageId()); 
        dout.writeInt( this.getProgramId() );
        dout.writeBoolean(this.isCompleted());
    }

    @Override
    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        // Fixme
        
    }
}
