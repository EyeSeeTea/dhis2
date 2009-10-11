package org.hisp.dhis.patientdatavalue;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStage;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class PatientDataValue
    implements Serializable
{

    private DataElement dataElement;

    private DataElementCategoryOptionCombo optionCombo;

    private ProgramInstance programInstance;

    private ProgramStage programStage;

    private OrganisationUnit organisationUnit;

    private Date timestamp;

    private String value;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public PatientDataValue()
    {
    }

    public PatientDataValue( ProgramInstance programInstance, ProgramStage programStage, DataElement dataElement,
        DataElementCategoryOptionCombo optionCombo, OrganisationUnit organisationUnit )
    {
        this.programInstance = programInstance;
        this.programStage = programStage;
        this.dataElement = dataElement;
        this.optionCombo = optionCombo;
        this.organisationUnit = organisationUnit;
    }

    public PatientDataValue( ProgramInstance programInstance, ProgramStage programStage, DataElement dataElement,
        DataElementCategoryOptionCombo optionCombo, OrganisationUnit organisationUnit, Date timeStamp )
    {
        this.programInstance = programInstance;
        this.programStage = programStage;
        this.dataElement = dataElement;
        this.optionCombo = optionCombo;
        this.organisationUnit = organisationUnit;
        this.timestamp = timeStamp;
    }

    public PatientDataValue( ProgramInstance programInstance, ProgramStage programStage, DataElement dataElement,
        DataElementCategoryOptionCombo optionCombo, OrganisationUnit organisationUnit, Date timeStamp, String value )
    {
        this.programInstance = programInstance;
        this.programStage = programStage;
        this.dataElement = dataElement;
        this.optionCombo = optionCombo;
        this.organisationUnit = organisationUnit;
        this.timestamp = timeStamp;
        this.value = value;
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + programInstance.hashCode();
        result = result * prime + programStage.hashCode();
        result = result * prime + dataElement.hashCode();
        result = result * prime + optionCombo.hashCode();
        result = result * prime + organisationUnit.hashCode();

        return result;
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

        if ( !(o instanceof PatientDataValue) )
        {
            return false;
        }

        final PatientDataValue other = (PatientDataValue) o;

        return programInstance.equals( other.programInstance ) && programStage.equals( other.programStage )
            && dataElement.equals( other.dataElement ) && optionCombo.equals( other.optionCombo )
            && organisationUnit.equals( other.organisationUnit );
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public void setProgramInstance( ProgramInstance programInstance )
    {
        this.programInstance = programInstance;
    }

    public ProgramInstance getProgramInstance()
    {
        return programInstance;
    }

    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    public void setProgramStage( ProgramStage programStage )
    {
        this.programStage = programStage;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
    }

    public DataElement getDataElement()
    {
        return dataElement;
    }

    public void setOptionCombo( DataElementCategoryOptionCombo optionCombo )
    {
        this.optionCombo = optionCombo;
    }

    public DataElementCategoryOptionCombo getOptionCombo()
    {
        return optionCombo;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

}
