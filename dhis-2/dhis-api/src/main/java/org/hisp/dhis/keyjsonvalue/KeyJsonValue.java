package org.hisp.dhis.keyjsonvalue;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.user.User;

/**
 * Created by Stian Sandvold on 27.09.2015.
 */
public class KeyJsonValue
    extends BaseIdentifiableObject
{
    private String namespace;

    private String key;

    private String value;

    @JsonProperty
    public String getKey()
    {
        return key;
    }

    @JsonProperty
    public String getNamespace()
    {
        return namespace;
    }

    @JsonProperty
    public String getValue()
    {
        return value;
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    public void setNamespace( String namespace )
    {
        this.namespace = namespace;
    }

    public void setValue( String value )
    {
        this.value = value;
    }
}
