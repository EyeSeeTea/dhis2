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

package org.hisp.dhis.caseentry.action.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.caseentry.state.SelectedStateManager;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class GenerateReportAction
    implements Action
{

    private static final String RED = "#ff0000";

    private static final String YELLOW = "#ffff00";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private Program program;

    public Program getProgram()
    {
        return program;
    }

    private Collection<Program> programs = new ArrayList<Program>();

    public Collection<Program> getPrograms()
    {
        return programs;
    }

    private Collection<ProgramStage> programStages;

    public Collection<ProgramStage> getProgramStages()
    {
        return programStages;
    }

    private Integer programId;

    public Integer getProgramId()
    {
        return programId;
    }

    private String startDate;

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    public String getStartDate()
    {
        return startDate;
    }

    private String endDate;

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    Collection<ProgramInstance> programInstances = new ArrayList<ProgramInstance>();

    public Collection<ProgramInstance> getProgramInstances()
    {
        return programInstances;
    }

    private Map<Integer, String> colorMap = new HashMap<Integer, String>();

    public Map<Integer, String> getColorMap()
    {
        return colorMap;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        program = selectedStateManager.getSelectedProgram();

        programId = program.getId();

        programs = programService.getPrograms( organisationUnit );

        // ---------------------------------------------------------------------
        // Program instances for the selected program
        // ---------------------------------------------------------------------

        Collection<ProgramInstance> selectedProgramInstances = programInstanceService.getProgramInstances( program );

        for ( ProgramInstance programInstance : selectedProgramInstances )
        {
            if ( !programInstance.isCompleted() )
            {
                programInstances.add( programInstance );
            }
            else
            {
                if ( programInstance.getEnrollmentDate().before( format.parseDate( endDate ) )
                    && programInstance.getEnrollmentDate().after( format.parseDate( startDate ) ) )
                {
                    programInstances.add( programInstance );
                }
            }

            for ( ProgramStageInstance programStageInstance : programInstance.getProgramStageInstances() )
            {
                // -------------------------------------------------------------
                // If a program stage is not provided even a day after its due
                // date, then that service is alerted red - because we are
                // getting late
                // -------------------------------------------------------------

                Calendar dueDateCalendar = Calendar.getInstance();
                dueDateCalendar.setTime( programStageInstance.getDueDate() );
                dueDateCalendar.add( Calendar.DATE, 1 );

                if ( dueDateCalendar.getTime().before( new Date() ) )
                {
                    colorMap.put( programStageInstance.getId(), RED );
                }
                else
                {
                    colorMap.put( programStageInstance.getId(), YELLOW );
                }
            }

        }

        return SUCCESS;
    }
}
