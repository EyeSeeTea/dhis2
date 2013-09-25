package org.hisp.dhis.i18n.locale;

import org.hisp.dhis.common.BaseIdentifiableObject;

public class I18nLocale
    extends BaseIdentifiableObject
{
    /**
     * 
     */
    private static final long serialVersionUID = -8425127015696485061L;

    //public static final String DEFAULT_COUNTRY = "";
    
    //private String name;
    
    private String locale;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public I18nLocale()
    {
        this.name = "English";
        this.locale = "en";
    }

    public I18nLocale( String name, String locale )
    {
        this.name = name;
        this.locale = locale;
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

    public String getLocale()
    {
        return locale;
    }
    
    public void setLocale( String locale )
    {
        this.locale = locale;
    }

}
