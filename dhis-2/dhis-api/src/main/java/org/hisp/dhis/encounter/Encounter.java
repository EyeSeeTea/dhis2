package org.hisp.dhis.encounter;

import java.io.Serializable;
import java.util.Date;

import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;


/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class Encounter
	implements Serializable
{
	
	private int id;
	
	private Patient patient;
	
	private OrganisationUnit organisationUnit;
	
	private DataSet dataSet;
	
	private Date encounterDateTime;
	
	private Boolean isExecuted = false;	
	
	// -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

	public Encounter()
	{		
	}
	
	// -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
    	int prime = 31;
        int result = 1;

        result = result * prime + patient.hashCode();
        result = result * prime + organisationUnit.hashCode();
        result = result * prime + dataSet.hashCode();
        result = result * prime + encounterDateTime.hashCode();        
        
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

        if ( !(o instanceof Encounter) )
        {
            return false;
        }

        final Encounter other = (Encounter) o;

        return  patient.equals( other.getPatient() ) &&
        	organisationUnit.equals( other.getOrganisationUnit() ) &&
        	dataSet.equals( other.getDataSet() ) &&         	
        	encounterDateTime.equals( other.getEncounterDateTime() ) ;
    }

	@Override
    public String toString()
    {
        return "[" + dataSet.getName() + ":"  + patient + ":" + encounterDateTime + "]";
    }
    
	// -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------    

	public void setId( int id ) 
	{
		this.id = id;
	}
	
	public int getId() 
	{
		return id;
	}	
	
	public void setPatient( Patient patient ) 
	{
		this.patient = patient;
	}
	
	public Patient getPatient() 
	{
		return patient;
	}
	
	public OrganisationUnit getOrganisationUnit() 
	{
		return organisationUnit;
	}

	public void setOrganisationUnit(OrganisationUnit organisationUnit) 
	{
		this.organisationUnit = organisationUnit;
	}
		
	public void setDataSet( DataSet dataSet ) 
	{
		this.dataSet = dataSet;
	}
	
	public DataSet getDataSet() 
	{
		return dataSet;
	}	
	
	public Date getEncounterDateTime() 
	{
		return encounterDateTime;
	}
	
	public void setEncounterDateTime( Date encounterDateTime ) 
	{
		this.encounterDateTime = encounterDateTime;
	}
	
	public Boolean getIsExecuted() 
	{
		return isExecuted;
	}

	public void setIsExecuted(Boolean isExecuted) 
	{
		this.isExecuted = isExecuted;
	}
	
}
