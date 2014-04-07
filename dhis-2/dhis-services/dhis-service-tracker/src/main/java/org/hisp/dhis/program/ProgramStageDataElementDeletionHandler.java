package org.hisp.dhis.program;

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

import java.util.Iterator;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.system.deletion.DeletionHandler;

/**
 * @author Chau Thu Tran
 * 
 * @version ProgramStageDataElementDeletionHandler.java Oct 5, 2010 11:06:03 PM
 */
public class ProgramStageDataElementDeletionHandler
    extends DeletionHandler
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStageDataElementService programStageDEService;

    public void setProgramStageDEService( ProgramStageDataElementService programStageDEService )
    {
        this.programStageDEService = programStageDEService;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    public String getClassName()
    {
        return ProgramStageDataElement.class.getSimpleName();
    }

    @Override
    public void deleteProgramStage( ProgramStage programStage )
    {
        Iterator<ProgramStageDataElement> iterator = programStage.getProgramStageDataElements().iterator();

        while ( iterator.hasNext() )
        {
            ProgramStageDataElement de = iterator.next();
            programStageDEService.deleteProgramStageDataElement( de );
        }
    }

    @Override
    public void deleteDataElement( DataElement dataElement )
    {
        if ( DataElement.DOMAIN_TYPE_PATIENT.equals( dataElement.getDomainType() ) )
        {
            Iterator<ProgramStageDataElement> iterator = programStageDEService.getAllProgramStageDataElements().iterator();

            while ( iterator.hasNext() )
            {
                ProgramStageDataElement element = iterator.next();

                if ( element.getDataElement() != null && element.getDataElement().equals( dataElement ) )
                {
                    programStageDEService.deleteProgramStageDataElement( element );
                }
            }
        }
    }
}
