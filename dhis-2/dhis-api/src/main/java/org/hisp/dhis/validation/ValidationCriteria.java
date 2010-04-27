package org.hisp.dhis.validation;

public class ValidationCriteria
{
    public static final int OPERATOR_LESS_THAN = -1;
    public static final int OPERATOR_EQUAL_TO = 0;
    public static final int OPERATOR_GREATER_THAN = 1;
    
    private int id;
    
    private String description;
    
    private String property;
    
    private int operator;
    
    private Object value;
    
    public ValidationCriteria()
    {
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getProperty()
    {
        return property;
    }

    public void setProperty( String property )
    {
        this.property = property;
    }

    public int getOperator()
    {
        return operator;
    }

    public void setOperator( int operator )
    {
        this.operator = operator;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue( Object value )
    {
        this.value = value;
    }
}
