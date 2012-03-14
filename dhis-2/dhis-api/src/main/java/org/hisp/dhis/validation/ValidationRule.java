package org.hisp.dhis.validation;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.expression.Operator;
import org.hisp.dhis.period.PeriodType;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kristian Nordal
 */
@JacksonXmlRootElement( localName = "validationRule", namespace = Dxf2Namespace.NAMESPACE )
public class ValidationRule
    extends BaseIdentifiableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -9058559806538024350L;

    public static final String TYPE_STATISTICAL = "statistical";
    public static final String TYPE_ABSOLUTE = "absolute";

    private String description;

    private String type;

    private Operator operator;

    private Expression leftSide;

    private Expression rightSide;

    private Set<ValidationRuleGroup> groups = new HashSet<ValidationRuleGroup>();

    private PeriodType periodType;

    // -------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------- 

    public ValidationRule()
    {
    }

    public ValidationRule( String name, String description, String type,
                           Operator operator, Expression leftSide, Expression rightSide )
    {
        this.name = name;
        this.description = description;
        this.type = type;
        this.operator = operator;
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // ------------------------------------------------------------------------- 

    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }
        else if ( object == null )
        {
            return false;
        }
        else if ( !(object instanceof ValidationRule) )
        {
            return false;
        }

        final ValidationRule validationRule = (ValidationRule) object;

        return name.equals( validationRule.getName() );
    }

    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }

    public void clearExpressions()
    {
        this.leftSide = null;
        this.rightSide = null;
    }

    // -------------------------------------------------------------------------
    // Set and get methods
    // -------------------------------------------------------------------------  

    @JsonProperty
    @JsonView( {DetailedView.class} )
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public PeriodType getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType( PeriodType periodType )
    {
        this.periodType = periodType;
    }

    @JsonProperty
    @JsonView( {DetailedView.class} )
    public Operator getOperator()
    {
        return operator;
    }

    public void setOperator( Operator operator )
    {
        this.operator = operator;
    }

    @JsonProperty
    @JsonView( {DetailedView.class} )
    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    // TODO fix serialization of this..
    @JsonProperty
    @JsonView( {DetailedView.class} )
    public Expression getLeftSide()
    {
        return leftSide;
    }

    public void setLeftSide( Expression leftSide )
    {
        this.leftSide = leftSide;
    }

    // TODO fix serialization of this..
    @JsonProperty
    @JsonView( {DetailedView.class} )
    public Expression getRightSide()
    {
        return rightSide;
    }

    public void setRightSide( Expression rightSide )
    {
        this.rightSide = rightSide;
    }

    @JsonProperty( value = "validationRuleGroups" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonView( {DetailedView.class} )
    @JacksonXmlElementWrapper( localName = "validationRuleGroups" )
    @JacksonXmlProperty( localName = "validationRuleGroup" )
    public Set<ValidationRuleGroup> getGroups()
    {
        return groups;
    }

    public void setGroups( Set<ValidationRuleGroup> groups )
    {
        this.groups = groups;
    }

}
