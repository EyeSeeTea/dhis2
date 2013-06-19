package org.hisp.dhis.i18n.locale;


//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;

//import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.common.BaseIdentifiableObject;
//import org.hisp.dhis.validation.ValidationCriteria;


public class I18nLocale extends BaseIdentifiableObject
{
    /**
     * 
     */
    private static final long serialVersionUID = -8425127015696485061L;

    private String description;

    private String language;

    private String country;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public I18nLocale()
    {
        
    }
    
    public I18nLocale(String name, String description, String language, String country)
    {
        this.name = name;
        this.description = description;
        this.language = language;
        this.country = country;
    }

    public I18nLocale(String name, String description)
    {
        this.name = name;
        this.description = description;
        this.language = "en";
        this.country = "US";
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof I18nLocale) )
        {
            return false;
        }

        final I18nLocale other = (I18nLocale) o;

        return name.equals( other.getName() );
    }
    
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getLanguage()
    {
        return language;
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

    // -------------------------------------------------------------------------
    // Convenience method
    // -------------------------------------------------------------------------

    
    // NEED TO IMPLEMENT SOMETHING TO LOAD THE ENTIRE LISTING?
    
}
