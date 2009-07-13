package org.hisp.dhis.vn.chr.statement;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.ArrayList;

import org.amplecode.quick.StatementBuilder;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.Form;

public abstract class FormStatement
{
    /** SQL commands */
    public static final String DROP_STATUS = "DROP COLUMN";
    public static final String ADD_STATUS = "ADD";
    public static final String ALTER_STATUS = "ALTER COLUMN";
    protected static final String NUMERIC_COLUMN_TYPE = "INTEGER NOT NULL";
    protected static final String SHORT_TEXT_COLUMN_TYPE = "VARCHAR (15)";
    protected static final String MEDIUM_TEXT_COLUMN_TYPE = "VARCHAR (40)";
    protected static final String LONG_TEXT_COLUMN_TYPE = "VARCHAR (255)";
    
    /** Data type */
    protected static final String NUMBER = "INTEGER";
    protected static final String STRING = "VARCHAR";
    protected static final String DATE = "DATE";
    protected static final String DOUBLE = "DOUBLE";

    /** Symbol in SQL */
    protected static final String QUERY_PARAM_ID = ":";
    protected static final String SEPARATOR_COMMAND = ";";
    public static final String SPACE = " ";
    public static final String SEPARATOR = ", ";

    /** Variables needs to create statement */
    protected String status;
    protected Element element;
    protected String column;
    protected int value;
    protected ArrayList<String> data;
    protected String keyword;

    /** Statement Builder */
    protected StatementBuilder statementBuilder;

    /** Statement String */
    protected String statement;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Default constructor
     * 
     * */
    @SuppressWarnings( "unused" )
    private FormStatement()
    {
    }

    /**
     * Constructor method
     * 
     * @param form Form needs to create a table
     * @param dialect the dialect in configuration file
     * 
     */
    public FormStatement( Form form, StatementBuilder statementBuilder )
    {
        this.statementBuilder = statementBuilder;
        
        init( form );
    }

    /**
     * Constructor method
     * 
     * @param form Form needs to create a table
     * @param dialect The dialect in configuration file
     * @param element Column needs to add or alter into the table
     * 
     */
    public FormStatement( Form form, StatementBuilder statementBuilder, String status, Element element )
    {
        this.statementBuilder = statementBuilder;
        this.status = status;
        this.element = element;
        
        init( form );
    }

    /**
     * Constructor method
     * 
     * @param form Form needs to create a table
     * @param dialect The dialect in configuration file
     * @param column Column's name needs to delete
     * 
     */
    public FormStatement( Form form, StatementBuilder statementBuilder, String status, String column )
    {
        this.statementBuilder = statementBuilder;
        this.status = status;
        this.column = column;
        
        init( form );
    }

    /**
     * Constructor method used to select list of object
     * 
     * @param form Form needs to create a table
     * @param dialect The dialect in configuration file
     * @param value Index of page, ID of object, ...
     * 
     */
    public FormStatement( Form form, StatementBuilder statementBuilder, int value )
    {
        this.statementBuilder = statementBuilder;
        this.value = value;

        init( form );
    }

    /**
     * Constructor method used to add a object
     * 
     * @param form Form needs to create a table
     * @param dialect The dialect in configuration file
     * @param keyword Keyword to search
     * 
     */
    public FormStatement( Form form, StatementBuilder statementBuilder, String keyword )
    {
        this.statementBuilder = statementBuilder;
        this.keyword = keyword;

        init( form );
    }

    /**
     * Constructor method used to add a object
     * 
     * @param form Form needs to create a table
     * @param dialect The dialect in configuration file
     * @param data Data of Object
     * 
     */
    public FormStatement( Form form, StatementBuilder statementBuilder, ArrayList<String> data )
    {
        this.statementBuilder = statementBuilder;
        this.data = data;

        init( form );
    }

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public String getStatement()
    {
        return statement;
    }

    public void setString( String param, String value )
    {
        statement = statement.replace( QUERY_PARAM_ID + param, value );
    }

    public void setInt( String param, Integer value )
    {
        statement = statement.replace( QUERY_PARAM_ID + param, String.valueOf( value ) );
    }

    // -------------------------------------------------------------------------
    // Abstract methods
    // -------------------------------------------------------------------------

    protected abstract void init( Form form );
}
