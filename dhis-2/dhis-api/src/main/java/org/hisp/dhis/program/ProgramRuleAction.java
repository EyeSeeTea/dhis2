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

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.DimensionalView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.dataelement.DataElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * @author markusbekken
 */

public class ProgramRuleAction
    extends BaseIdentifiableObject
{
    private static final long serialVersionUID = -2807997671779497354L;

    /**
     * The programRule that the action belongs to
     */
    private ProgramRule programRule;
    
    /**
     * The type of action that is performed when the action is effectuated
     * 
     * The actual action the ruleaction row is performing. Allowed values are:
     *   displaytext
     *       Shows a text in the rulebound widget with the matching location string.
     *           location: the location code of the widget to display data in
     *           content: A hardcoded string to display
     *           data: a variable to be evaluated and displayed at the end of the string, can be null.
     *   displaykeyvaluepair
     *       Shows a key data box with a hardcoded name and a variable value.
     *           location: the location code of the widget to display data in
     *           content: A hardcoded string to display as title in the key data box
     *           data: The variable to be evaluated and display in the lower half of the key data box.
     *   hidefield
     *       Hides a dataelement from the page, as long as the dataelement is not containing a value.
     *           dataelement: the dataelement to hide.
     *   assignvariable
     *       Assigns/calculates a value that can be further used by other rules. Make sure the priorities is set so the rules that depend on the calculation is run after the assignvariable-rule.
     *           content: the variable name to be assigned. “$severeanemia” for example.
     *           data: the expression to be evaluated and assigned to the content field. Can contain a hardcoded value(f.ex. “true”) or an expression that is evaluated(for exampple “$hemoglobin < 7”). 
     *   showwarning
     *       Shows a validation warning connected to a designated dataelement
     *           dataelement: the dataelement to show validationerror for
     *           content: the validation error itself 
     *   showerror
     *       Shows a validation error connected to a designated dataelement
     *           dataelement: the dataelement to show validationerror for
     *           content: the validation error itself 
     */
    private ProgramRuleActionType programRuleActionType;
    
    /**
     * the dataelement that is affected by the ruleAction. 
     * Used for 
     *       hidefield
     *       showwarning
     *       showerror
     */
    private DataElement dataElement;
    
    /**
     * Used to determine which widget to display data for the two action types:
     *       displaytext
     *       displaykeydata
     */
    private String location;

    /**
     * Used by all the different actions. See “actions”
     */
    private String content;
    
    /**
     * Used by all the different actions. See “actions”. The data field will be evaluated, so it can contain a rich expression.
     */
    private String data;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    ProgramRuleAction()
    {
        setAutoFields();
    }
    
    ProgramRuleAction( String name, ProgramRule programRule, ProgramRuleActionType programRuleActionType, DataElement dataElement, String location, String content, String data )
    {
        this();
        this.name = name;
        this.programRule = programRule;
        this.programRuleActionType = programRuleActionType;
        this.dataElement = dataElement;
        this.location = location;
        this.content = content;
        this.data = data;
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public ProgramRule getProgramRule()
    {
        return programRule;
    }

    public void setProgramRule( ProgramRule programRule )
    {
        this.programRule = programRule;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public ProgramRuleActionType getProgramRuleActionType()
    {
        return programRuleActionType;
    }

    public void setProgramRuleActionType( ProgramRuleActionType programRuleActionType )
    {
        this.programRuleActionType = programRuleActionType;
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

    public String getLocation()
    {
        return location;
    }

    public void setLocation( String location )
    {
        this.location = location;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent( String content )
    {
        this.content = content;
    }

    public String getData()
    {
        return data;
    }

    public void setData( String data )
    {
        this.data = data;
    }

}
