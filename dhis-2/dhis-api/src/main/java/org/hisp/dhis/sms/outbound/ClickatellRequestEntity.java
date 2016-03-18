package org.hisp.dhis.sms.outbound;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement( localName = "requestEntity" )
public class ClickatellRequestEntity
{
    private String text;
    
    private List<String> to;
    
    public ClickatellRequestEntity()
    {
        super();
    }

    public ClickatellRequestEntity( String text, List<String> to )
    {
        super();
        this.text = text;
        this.to = to;
    }

    @JsonProperty( value = "text" )
    @JacksonXmlProperty( localName = "text" )
    public String getText()
    {
        return text;
    }

    public void setText( String text )
    {
        this.text = text;
    }

    @JsonProperty( value = "to" )
    @JacksonXmlProperty( localName = "to" )
    public List<String> getTo()
    {
        return to;
    }

    public void setTo( List<String> to )
    {
        this.to = to;
    }

}
