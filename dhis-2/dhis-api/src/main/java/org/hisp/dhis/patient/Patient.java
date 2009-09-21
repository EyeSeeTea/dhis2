package org.hisp.dhis.patient;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class Patient
	implements Serializable
{
	
	public static final String MALE = "M";
	
	public static final String FEMALE = "F";	    
    
	private Integer id;	

	private String firstName;
    
    private String middleName;
    
    private String lastName;
    
    private String gender;   

	private Date birthDate;
    
    private Date deathDate;
    
    private Boolean isDead = false;    
    
	private Set<PatientIdentifier> identifiers;
    
    private Set<PatientAddress> addresses;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------   

	public Patient()
    {
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------   

	@Override
    public int hashCode()
    {
        return id.hashCode();
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

        if ( !(o instanceof Patient) )
        {
            return false;
        }

        final Patient other = (Patient) o;

        return id.equals( other.getId() );
    }    
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
	public Integer getId() 
	{
		return id;
	}

	public void setId( Integer id ) 
	{
		this.id = id;
	}	

	public String getFirstName() 
	{
		return firstName;
	}

	public void setFirstName( String firstName ) 
	{
		this.firstName = firstName;
	}

	public String getMiddleName() 
	{
		return middleName;
	}

	public void setMiddleName( String middleName ) 
	{
		this.middleName = middleName;
	}

	public String getLastName() 
	{
		return lastName;
	}

	public void setLastName( String lastName ) 
	{
		this.lastName = lastName;
	}

	public String getGender() 
	{
		return gender;
	}
	
	public void setGender( String gender )
	{
		this.gender = gender;
	}

	public Date getBirthDate() 
	{
		return birthDate;
	}

	public void setBirthDate( Date birthDate ) 
	{
		this.birthDate = birthDate;
	}

	public Date getDeathDate() 
	{
		return deathDate;
	}

	public void setDeathDate( Date deathDate ) 
	{
		this.deathDate = deathDate;
	}	
	
	public Boolean getIsDead() 
	{
		return isDead;
	}
	
	public void setIsDead(Boolean isDead) 
	{
		this.isDead = isDead;
	}
	
	public Set<PatientIdentifier> getIdentifiers() 
	{
		return identifiers;
	}

	public void setIdentifiers( Set<PatientIdentifier> identifiers ) 
	{
		this.identifiers = identifiers;
	}
	
	public Set<PatientAddress> getAddresses() 
	{
		return addresses;
	}

	public void setAddresses( Set<PatientAddress> addresses ) 
	{
		this.addresses = addresses;
	}	
	
	public PatientIdentifier getPreferredPatientIdentifier()
	{
		if( getIdentifiers() != null && getIdentifiers().size()  > 0 )
		{
			for( PatientIdentifier patientIdentifier : getIdentifiers() )
			{
				if( patientIdentifier.getPreferred() )
				{
					return patientIdentifier;
				}
			}
			
			return null;
		}
		
		return null;
	}
}