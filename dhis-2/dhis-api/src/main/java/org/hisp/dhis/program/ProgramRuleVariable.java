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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.Objects;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.dataelement.DataElement;

/**
 *
 * @author markusbekken
 */
@JacksonXmlRootElement( localName = "programRuleVariable", namespace = DxfNamespaces.DXF_2_0 )
public class ProgramRuleVariable 
    extends BaseNameableObject {
    
    /* The program that the variable belongs to */
    private Program program;
    
    /* The type of the variable, used by the rules engine to know how to quote 
    * or convert the value. The allowed types are:
    * number
    * text
    * bool
    * date */
    private ProgramRuleVariableDataType dataType;
    
    /* The value that the variable should have initially.
    * Usually the default value is replaced by a value of the connected dataelement,
    * or by a calculation performed by rules. If not, the default value would be 
    * used in evaluating rules. */
    private String defaultValue;
    
    /* The source of the variables content. Allowed values are:
    * dataelement_newest_event_program_stage
    *   Get a specific dataelements value from the most recent event in the 
    *   current enrollment, but within one program stage. dataelement_uID and 
    *   programstage_uID needs to be specified.
    * dataelement_newest_event_program
    *   Get a specific dataelements value from the most recent event in the 
    *   current enrollment, regardless of program stage.datalement_uID needs to 
    *   be specified.
    * dataelement_current_event
    *   Get a specific dataelements value, but only within the current event.
    * dataelement_previous_event
    *    Get a specific dataelements value, specifically from the event 
    *    preceding the current event, if this exists.
    * calculated_value
    *   Do not assign the variable a hard-linked source, it will be populated by
    *   rules with “assignvariable” actions(i.e. calulation rules).
    * tei_attribute
    *   Get a specific attribute from the current tracked entity. 
    *   the linked attribute will be  used to lookup the attributes uID value.*/
    private ProgramRuleVariableSourceType sourceType;
    
    /* Used for sourceType tei_attribute to determine which attribute to 
    * fetch into the variable.*/
    private TrackedEntityAttribute attribute;
    
    /* The dataelement that is linked to the variable. 
    * Must de defined if the sourceType is one of the following:
    * dataelement_newest_event_program_stage
    * dataelement_newest_event_program
    * dataelement_current_event */
    private DataElement dataElement;
    
    /* Specification of the programstage that the variable should be fetched 
    * from. Only used for sourcetype dataelement_newest_event_program_stage*/
    private ProgramStage programStage;

    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public ProgramRuleVariable()
    {
        setAutoFields();
    }

    public ProgramRuleVariable( String name, Program program )
    {
        this();
        this.name = name;
        this.program = program;
    }
    
    // -------------------------------------------------------------------------
    //  equals and hashCode
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();

        result = prime * result + ((program == null) ? 0 : program.hashCode());
        result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((sourceType == null) ? 0 : sourceType.hashCode());
        result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
        result = prime * result + ((dataElement == null) ? 0 : dataElement.hashCode());
        result = prime * result + ((programStage == null) ? 0 : programStage.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) 
    {
        if (obj == null) 
        {
            return false;
        }
        if (getClass() != obj.getClass()) 
        {
            return false;
        }
        final ProgramRuleVariable other = (ProgramRuleVariable) obj;
        if (!Objects.equals(this.program, other.program)) 
        {
            return false;
        }
        if (this.dataType != other.dataType) 
        {
            return false;
        }
        if (!Objects.equals(this.defaultValue, other.defaultValue)) 
        {
            return false;
        }
        if (this.sourceType != other.sourceType) 
        {
            return false;
        }
        if (!Objects.equals(this.attribute, other.attribute)) 
        {
            return false;
        }
        if (!Objects.equals(this.dataElement, other.dataElement)) 
        {
            return false;
        }
        if (!Objects.equals(this.programStage, other.programStage)) 
        {
            return false;
        }
        return true;
    }
    
    
        
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Program getProgram()
    {
        return program;
    }
    
    public void setProgram( Program program )
    {
        this.program = program;
    }
    
    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public ProgramStage getProgramStage()
    {
        return programStage;
    }
    
    public void setProgramStage( ProgramStage programStage )
    {
        this.programStage = programStage;
    }
    
    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public DataElement getDataElement()
    {
        return dataElement;
    }
    
    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
    }
    
    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public TrackedEntityAttribute getAttribute()
    {
        return attribute;
    }
    
    public void setAttibute( TrackedEntityAttribute attribute )
    {
        this.attribute = attribute;
    }
       
    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getDefaultValue()
    {
        return defaultValue;
    }
    
    public void setDefaultValue( String defaultValue )
    {
        this.defaultValue = defaultValue;
    } 
    
    
    
    //Todo: create getters and setters for  dataType and sourcetypes. 
    //Make serializer like JacksonPeriodTypeSerializer
    
    
}
