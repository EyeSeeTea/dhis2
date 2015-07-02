package org.hisp.dhis.trackedentity.action.program;

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

import com.opensymphony.xwork2.Action;

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.program.ProgramType;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.relationship.RelationshipTypeService;
import org.hisp.dhis.system.util.AttributeUtils;
import org.hisp.dhis.trackedentity.TrackedEntity;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.trackedentity.TrackedEntityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class UpdateProgramAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    @Autowired
    private ProgramService programService;

    @Autowired
    private TrackedEntityAttributeService trackedEntityAttributeService;

    @Autowired
    private RelationshipTypeService relationshipTypeService;

    @Autowired
    private TrackedEntityService trackedEntityService;

    @Autowired
    private AttributeService attributeService;

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private String dateOfEnrollmentDescription;

    public void setDateOfEnrollmentDescription( String dateOfEnrollmentDescription )
    {
        this.dateOfEnrollmentDescription = dateOfEnrollmentDescription;
    }

    private String dateOfIncidentDescription;

    public void setDateOfIncidentDescription( String dateOfIncidentDescription )
    {
        this.dateOfIncidentDescription = dateOfIncidentDescription;
    }

    private String type;

    public void setType( String type )
    {
        this.type = type;
    }

    private Boolean displayProvidedOtherFacility;

    public void setDisplayProvidedOtherFacility( Boolean displayProvidedOtherFacility )
    {
        this.displayProvidedOtherFacility = displayProvidedOtherFacility;
    }

    private Boolean displayIncidentDate;

    public void setDisplayIncidentDate( Boolean displayIncidentDate )
    {
        this.displayIncidentDate = displayIncidentDate;
    }

    private List<String> selectedPropertyIds = new ArrayList<>();

    public void setSelectedPropertyIds( List<String> selectedPropertyIds )
    {
        this.selectedPropertyIds = selectedPropertyIds;
    }

    private List<Boolean> personDisplayNames = new ArrayList<>();

    public void setPersonDisplayNames( List<Boolean> personDisplayNames )
    {
        this.personDisplayNames = personDisplayNames;
    }

    private List<Boolean> mandatory = new ArrayList<>();

    public void setMandatory( List<Boolean> mandatory )
    {
        this.mandatory = mandatory;
    }

    private Boolean generateBydEnrollmentDate;

    public void setGeneratedByEnrollmentDate( Boolean generateBydEnrollmentDate )
    {
        this.generateBydEnrollmentDate = generateBydEnrollmentDate;
    }

    private Boolean ignoreOverdueEvents;

    public void setIgnoreOverdueEvents( Boolean ignoreOverdueEvents )
    {
        this.ignoreOverdueEvents = ignoreOverdueEvents;
    }

    private Boolean blockEntryForm;

    public void setBlockEntryForm( Boolean blockEntryForm )
    {
        this.blockEntryForm = blockEntryForm;
    }

    private Boolean onlyEnrollOnce = false;

    public void setOnlyEnrollOnce( Boolean onlyEnrollOnce )
    {
        this.onlyEnrollOnce = onlyEnrollOnce;
    }

    private Boolean remindCompleted = false;

    public void setRemindCompleted( Boolean remindCompleted )
    {
        this.remindCompleted = remindCompleted;
    }

    private Boolean selectEnrollmentDatesInFuture;

    public void setSelectEnrollmentDatesInFuture( Boolean selectEnrollmentDatesInFuture )
    {
        this.selectEnrollmentDatesInFuture = selectEnrollmentDatesInFuture;
    }

    private Boolean selectIncidentDatesInFuture;

    public void setSelectIncidentDatesInFuture( Boolean selectIncidentDatesInFuture )
    {
        this.selectIncidentDatesInFuture = selectIncidentDatesInFuture;
    }

    private String relationshipText;

    public void setRelationshipText( String relationshipText )
    {
        this.relationshipText = relationshipText;
    }

    private Integer relationshipTypeId;

    public void setRelationshipTypeId( Integer relationshipTypeId )
    {
        this.relationshipTypeId = relationshipTypeId;
    }

    private Boolean relationshipFromA;

    public void setRelationshipFromA( Boolean relationshipFromA )
    {
        this.relationshipFromA = relationshipFromA;
    }

    private Integer relatedProgramId;

    public void setRelatedProgramId( Integer relatedProgramId )
    {
        this.relatedProgramId = relatedProgramId;
    }

    private Boolean dataEntryMethod;

    public void setDataEntryMethod( Boolean dataEntryMethod )
    {
        this.dataEntryMethod = dataEntryMethod;
    }

    private Integer trackedEntityId;

    public void setTrackedEntityId( Integer trackedEntityId )
    {
        this.trackedEntityId = trackedEntityId;
    }

    private List<Boolean> allowFutureDate = new ArrayList<>();

    public void setAllowFutureDate( List<Boolean> allowFutureDate )
    {
        this.allowFutureDate = allowFutureDate;
    }

    private List<String> jsonAttributeValues = new ArrayList<>();

    public void setJsonAttributeValues( List<String> jsonAttributeValues )
    {
        this.jsonAttributeValues = jsonAttributeValues;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        displayProvidedOtherFacility = (displayProvidedOtherFacility == null) ? false : displayProvidedOtherFacility;
        displayIncidentDate = (displayIncidentDate == null) ? false : displayIncidentDate;
        generateBydEnrollmentDate = (generateBydEnrollmentDate == null) ? false : generateBydEnrollmentDate;
        ignoreOverdueEvents = (ignoreOverdueEvents == null) ? false : ignoreOverdueEvents;
        blockEntryForm = (blockEntryForm == null) ? false : blockEntryForm;
        remindCompleted = (remindCompleted == null) ? false : remindCompleted;
        selectEnrollmentDatesInFuture = (selectEnrollmentDatesInFuture == null) ? false : selectEnrollmentDatesInFuture;
        selectIncidentDatesInFuture = (selectIncidentDatesInFuture == null) ? false : selectIncidentDatesInFuture;
        dataEntryMethod = (dataEntryMethod == null) ? false : dataEntryMethod;
        ProgramType programType = ProgramType.fromValue( type );
        
        Program program = programService.getProgram( id );
        program.setName( StringUtils.trimToNull( name ) );
        program.setDescription( StringUtils.trimToNull( description ) );
        program.setDateOfEnrollmentDescription( StringUtils.trimToNull( dateOfEnrollmentDescription ) );
        program.setDateOfIncidentDescription( StringUtils.trimToNull( dateOfIncidentDescription ) );
        program.setProgramType( programType );
        program.setDisplayIncidentDate( displayIncidentDate );
        program.setOnlyEnrollOnce( onlyEnrollOnce );
        program.setSelectEnrollmentDatesInFuture( selectEnrollmentDatesInFuture );
        program.setSelectIncidentDatesInFuture( selectIncidentDatesInFuture );
        program.setDataEntryMethod( dataEntryMethod );

        if ( program.isRegistration() )
        {
            program.setIgnoreOverdueEvents( ignoreOverdueEvents );
        }
        else
        {
            program.setIgnoreOverdueEvents( false );
        }

        if ( relationshipTypeId != null )
        {
            RelationshipType relationshipType = relationshipTypeService.getRelationshipType( relationshipTypeId );
            program.setRelationshipType( relationshipType );
            program.setRelationshipFromA( relationshipFromA );
            program.setRelationshipText( relationshipText ); 
            
            Program relatedProgram = programService.getProgram( relatedProgramId );
            program.setRelatedProgram( relatedProgram );
        }
        else
        {
            program.setRelationshipType( null );
            program.setRelationshipFromA( null );
            program.setRelationshipText( null );
            program.setRelatedProgram( null );
        }

        if ( trackedEntityId != null )
        {
            TrackedEntity trackedEntity = trackedEntityService.getTrackedEntity( trackedEntityId );
            program.setTrackedEntity( trackedEntity );
        }
        else if ( program.getTrackedEntity() != null )
        {
            program.setTrackedEntity( null );
        }

        if ( program.getProgramAttributes() != null )
        {
            program.getProgramAttributes().clear();
        }

        int index = 0;

        for ( String selectedPropertyId : selectedPropertyIds )
        {
            String[] ids = selectedPropertyId.split( "_" );

            if ( ids[0].equals( TrackedEntityInstance.PREFIX_TRACKED_ENTITY_ATTRIBUTE ) )
            {
                TrackedEntityAttribute attribute = trackedEntityAttributeService.getTrackedEntityAttribute( Integer
                    .parseInt( ids[1] ) );
                ProgramTrackedEntityAttribute programAttribute = new ProgramTrackedEntityAttribute( attribute,
                    personDisplayNames.get( index ), mandatory.get( index ), allowFutureDate.get( index ) );
                program.getProgramAttributes().add( programAttribute );
            }

            index++;
        }


        program.increaseVersion(); //TODO make more fine-grained

        if ( jsonAttributeValues != null )
        {
            AttributeUtils.updateAttributeValuesFromJson( program.getAttributeValues(), jsonAttributeValues, attributeService );
        }

        programService.updateProgram( program );

        return SUCCESS;
    }
}
