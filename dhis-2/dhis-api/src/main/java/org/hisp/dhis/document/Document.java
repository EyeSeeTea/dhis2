package org.hisp.dhis.document;

public class Document
{
    private int id;
    
    private String name;
    
    private String url;
    
    private boolean external;
    
    public Document()
    {   
    }
    
    public Document( String name, String url, boolean external )
    {
        this.name = name;
        this.url = url;
        this.external = external;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
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
        
        if ( getClass() != object.getClass() )
        {
            return false;
        }
        
        final Document other = (Document) object;
    
        return name.equals( other.name );
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    public boolean isExternal()
    {
        return external;
    }

    public void setExternal( boolean external )
    {
        this.external = external;
    }
}
