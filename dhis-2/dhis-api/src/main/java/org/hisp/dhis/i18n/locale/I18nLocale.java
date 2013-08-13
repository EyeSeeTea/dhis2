package org.hisp.dhis.i18n.locale;

//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;

//import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.common.BaseIdentifiableObject;

//import org.hisp.dhis.validation.ValidationCriteria;

public class I18nLocale
    extends BaseIdentifiableObject
{
    /**
     * 
     */
    private static final long serialVersionUID = -8425127015696485061L;

    public static final String DEFAULT_COUNTRY = "";
    
    private String name;
    
    private String language;

    private String country;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public I18nLocale()
    {
        this.name = "English, UNITED STATES";
        this.language = "en";
        this.country = "US";
    }

    public I18nLocale( String name, String language, String country )
    {
        this.name = name;
        this.language = language;
        this.country = country;
    }


    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( !super.equals( obj ) )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        I18nLocale other = (I18nLocale) obj;
        if ( name == null )
        {
            if ( other.name != null )
                return false;
        }
        else if ( !name.equals( other.name ) )
            return false;
        return true;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getLanguage()
    {
        return language;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setLanguage( String language )
    {
        this.language = language;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry( String country )
    {
        this.country = country;
    }


}
