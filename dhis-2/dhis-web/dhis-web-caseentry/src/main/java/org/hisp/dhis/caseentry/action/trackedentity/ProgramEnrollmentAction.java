package org.hisp.dhis.caseentry.action.trackedentity;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.comparator.ProgramStageInstanceVisitDateComparator;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeGroup;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValue;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 */
public class ProgramEnrollmentAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramInstanceService programInstanceService;

    private OrganisationUnitSelectionManager selectionManager;

    private I18nFormat format;

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer programInstanceId;

    private Map<Integer, String> identiferMap;

    private List<ProgramStageInstance> programStageInstances = new ArrayList<ProgramStageInstance>();

    private Collection<TrackedEntityAttribute> noGroupAttributes = new HashSet<TrackedEntityAttribute>();

    private List<TrackedEntityAttributeGroup> attributeGroups;

    private Map<Integer, String> attributeValueMap = new HashMap<Integer, String>();

    private Boolean hasDataEntry;

    private List<TrackedEntityAttribute> attributes;

    private ProgramInstance programInstance;

    // -------------------------------------------------------------------------
    // Getters/Setters
    // -------------------------------------------------------------------------

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public Collection<TrackedEntityAttribute> getNoGroupAttributes()
    {
        return noGroupAttributes;
    }

    public List<TrackedEntityAttributeGroup> getAttributeGroups()
    {
        return attributeGroups;
    }

    public Map<Integer, String> getAttributeValueMap()
    {
        return attributeValueMap;
    }

    public Map<Integer, String> getIdentiferMap()
    {
        return identiferMap;
    }

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    public void setProgramInstanceId( Integer programInstanceId )
    {
        this.programInstanceId = programInstanceId;
    }

    public List<ProgramStageInstance> getProgramStageInstances()
    {
        return programStageInstances;
    }

    public Boolean getHasDataEntry()
    {
        return hasDataEntry;
    }

    public List<TrackedEntityAttribute> getAttributes()
    {
        return attributes;
    }

    public ProgramInstance getProgramInstance()
    {
        return programInstance;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        OrganisationUnit orgunit = selectionManager.getSelectedOrganisationUnit();

        // ---------------------------------------------------------------------
        // Load active ProgramInstance, completed = false
        // ---------------------------------------------------------------------

        programInstance = programInstanceService.getProgramInstance( programInstanceId );

        programStageInstances = new ArrayList<ProgramStageInstance>( programInstance.getProgramStageInstances() );

        Collections.sort( programStageInstances, new ProgramStageInstanceVisitDateComparator() );

        loadAttributes( programInstance );

        hasDataEntry = showDataEntry( orgunit, programInstance.getProgram(), programInstance );

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void loadAttributes( ProgramInstance programInstance )
    {
        // ---------------------------------------------------------------------
        // Load attributes of the selected program
        // ---------------------------------------------------------------------

        attributes = new ArrayList<TrackedEntityAttribute>( programInstance.getProgram().getTrackedEntityAttributes() );

        if ( attributes != null )
        {
            Collection<TrackedEntityAttributeValue> attributeValues = programInstance.getEntityInstance()
                .getAttributeValues();

            for ( TrackedEntityAttributeValue attributeValue : attributeValues )
            {
                if ( attributes.contains( attributeValue.getAttribute() ) )
                {
                    String value = attributeValue.getValue();
                    if ( attributeValue.getAttribute().getValueType().equals( TrackedEntityAttribute.TYPE_AGE ) )
                    {
                        Date date = format.parseDate( value );
                        value = TrackedEntityAttribute.getAgeFromDate( date ) + "";
                    }

                    attributeValueMap.put( attributeValue.getAttribute().getId(), value );
                }
            }
        }
    }

    private boolean showDataEntry( OrganisationUnit orgunit, Program program, ProgramInstance programInstance )
    {
        if ( !program.getOrganisationUnits().contains( orgunit ) )
        {
            return false;
        }
        else if ( !program.isSingleEvent() && programInstance == null )
        {
            return false;
        }

        return true;
    }

}
