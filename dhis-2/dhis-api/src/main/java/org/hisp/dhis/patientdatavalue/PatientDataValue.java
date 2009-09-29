package org.hisp.dhis.patientdatavalue;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.patient.Patient;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class PatientDataValue
    implements Serializable
{
    private Patient patient;

    private DataElement dataElement;

    private DataElementCategoryOptionCombo optionCombo;

    private Date timestamp;
    
    private String value;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public PatientDataValue()
    {
    }

    public PatientDataValue( Patient patient, DataElement dataElement, DataElementCategoryOptionCombo optionCombo, Date timestamp )
    {
        this.patient = patient;
        this.dataElement = dataElement;
        this.optionCombo = optionCombo;
        this.timestamp = timestamp;
    }

    public PatientDataValue( Patient patient, DataElement dataElement, DataElementCategoryOptionCombo optionCombo, Date timestamp, String value )
    {
        this.patient = patient;
        this.dataElement = dataElement;
        this.optionCombo = optionCombo;
        this.timestamp = timestamp;
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

        result = result * prime + patient.hashCode();
        result = result * prime + dataElement.hashCode();
        result = result * prime + optionCombo.hashCode();
        result = result * prime + timestamp.hashCode();

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

        return patient.equals( other.patient ) && dataElement.equals( other.dataElement )
            && optionCombo.equals( other.optionCombo ) && timestamp.equals( other.timestamp );
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    public Patient getPatient()
    {
        return patient;
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
}
