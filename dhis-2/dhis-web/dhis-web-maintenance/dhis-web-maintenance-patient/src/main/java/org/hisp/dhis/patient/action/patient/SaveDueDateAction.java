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
package org.hisp.dhis.patient.action.patient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.program.ProgramInstanceStage;
import org.hisp.dhis.program.ProgramInstanceStageService;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class SaveDueDateAction
    implements Action
{

    private static final Log LOG = LogFactory.getLog( SaveDueDateAction.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramInstanceStageService programInstanceStageService;

    public void setProgramInstanceStageService( ProgramInstanceStageService programInstanceStageService )
    {
        this.programInstanceStageService = programInstanceStageService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private String dueDate;

    public void setDueDate( String dueDate )
    {
        this.dueDate = dueDate;
    }

    private int programInstanceStageId;

    public void setProgramInstanceStageId( int programInstanceStageId )
    {
        this.programInstanceStageId = programInstanceStageId;
    }

    public int getProgramInstanceStageId()
    {
        return programInstanceStageId;
    }

    private int statusCode;

    public int getStatusCode()
    {
        return statusCode;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        ProgramInstanceStage programInstanceStage = programInstanceStageService
            .getProgramInstanceStage( programInstanceStageId );

        if ( programInstanceStage != null )
        {
            if ( dueDate != null && dueDate.trim().length() == 0 )
            {
                dueDate = null;
            }

            if ( dueDate != null )
            {
                dueDate = dueDate.trim();

                programInstanceStage.setDueDate( format.parseDate( dueDate ) );

                programInstanceStageService.updateProgramInstanceStage( programInstanceStage );

                LOG.debug( "Updating PatientDataValue, value added/changed" );
            }
        }

        return SUCCESS;
    }
}
