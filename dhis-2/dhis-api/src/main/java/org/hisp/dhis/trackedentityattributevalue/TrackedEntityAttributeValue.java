package org.hisp.dhis.trackedentityattributevalue;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityInstance;

import java.io.Serializable;

/**
 * TODO index on attribute and instance
 *
 * @author Abyot Asalefew
 */
@JacksonXmlRootElement( localName = "trackedEntityAttributeValue", namespace = DxfNamespaces.DXF_2_0 )
public class TrackedEntityAttributeValue
    implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -4469496681709547707L;

    public static final String UNKNOWN = " ";

    private TrackedEntityAttribute attribute;

    private TrackedEntityInstance entityInstance;

    private String encryptedValue;

    private String plainValue;

    /**
     * This value is only used to store values from setValue when we don't know
     * if attribute is set or not.
    */
    private String value;


    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public TrackedEntityAttributeValue()
    {
    }

    public TrackedEntityAttributeValue( TrackedEntityAttribute attribute, TrackedEntityInstance entityInstance )
    {
        setAttribute( attribute );
        setEntityInstance( entityInstance );
    }

    public TrackedEntityAttributeValue( TrackedEntityAttribute attribute, TrackedEntityInstance entityInstance,
        String value )
    {
        setAttribute( attribute );
        setEntityInstance( entityInstance );
        setValue( value );
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((entityInstance == null) ? 0 : entityInstance.hashCode());
        result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
        result = prime * result + ((getValue() == null) ? 0 : getValue().hashCode());
        return result;
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }

        if ( object == null )
        {
            return false;
        }

        if ( !getClass().isAssignableFrom( object.getClass() ) )
        {
            return false;
        }

        final TrackedEntityAttributeValue other = (TrackedEntityAttributeValue) object;

        if ( entityInstance == null )
        {
            if ( other.entityInstance != null )
            {
                return false;
            }
        }
        else if ( !entityInstance.equals( other.entityInstance ) )
        {
            return false;
        }

        if ( attribute == null )
        {
            if ( other.attribute != null )
            {
                return false;
            }
        }
        else if ( !attribute.equals( other.attribute ) )
        {
            return false;
        }

        if ( getValue() == null )
        {
            if ( other.getValue() != null )
            {
                return false;
            }
        }
        else if ( !getValue().equals( other.getValue() ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return "[Tracked attribute=" + attribute + ", entityInstance=" + entityInstance + ", value='" + getValue() + "'"
            + "]";
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    /**
     * Retrieves the encrypted value if the attribute is confidential.
     * If the value is not confidential, returns old value (Should be null unless it was
     * confidential at an earlier stage)
     * @return String with decrypted value or null
     */
    @JsonIgnore
    public String getEncryptedValue()
    {
        return (getAttribute().getConfidential() && this.value != null ? this.value : this.encryptedValue);
    }

    /**
     * Used by hibernate to set the object's value
     */
    public void setEncryptedValue( String encryptedValue )
    {
        this.encryptedValue = encryptedValue;
    }

    /**
     * Retrieves the plaintext value is the attribute isn't confidential.
     * If the value is confidential, this value should be null, unless it was non-confidential at
     * an earlier stage.
     * @return String with plaintext value or null
     */
    @JsonIgnore
    public String getPlainValue()
    {
        return (!getAttribute().getConfidential() && this.value != null ? this.value : this.plainValue);
    }

    /**
     * Used by hibernate to set the objects value
     */
    public void setPlainValue( String plainValue )
    {
        this.plainValue = plainValue;
    }

    /**
     * Returns the encrypted or the plaintext value, based on the attribute's confidential value.
     * @return String with value, either plaintext or decrypted
     */
    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getValue()
    {
        return (getAttribute().getConfidential() ? this.getEncryptedValue() : this.getPlainValue());
    }

    /**
     * Since we never can be 100% certain Attribute is not null, we store the value in a temporary
     * variable. The getEncrypted and getPlaintext methods will handle this value when someone requires it
     * (Either a user or hibernate)
     * @param value the value to be stored
     */
    public void setValue( String value )
    {
        this.value = value;
    }

    @JsonProperty( "trackedEntityAttribute" )
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( localName = "trackedEntityAttribute", namespace = DxfNamespaces.DXF_2_0 )
    public TrackedEntityAttribute getAttribute()
    {
        return attribute;
    }

    public void setAttribute( TrackedEntityAttribute attribute )
    {
        this.attribute = attribute;
    }

    @JsonProperty( "trackedEntityInstance" )
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( localName = "trackedEntityInstance", namespace = DxfNamespaces.DXF_2_0 )
    public TrackedEntityInstance getEntityInstance()
    {
        return entityInstance;
    }

    public void setEntityInstance( TrackedEntityInstance entityInstance )
    {
        this.entityInstance = entityInstance;
    }
}
