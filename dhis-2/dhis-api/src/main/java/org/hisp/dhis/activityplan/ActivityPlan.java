package org.hisp.dhis.activityplan;

import java.io.Serializable;
import java.util.Set;

import org.hisp.dhis.encounter.Encounter;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.User;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class ActivityPlan
	implements Serializable
{
	private int id;
	
	private User hew;
	
	private OrganisationUnit organisationUnit;
	
	private Period period;
	
	private Set<Encounter> encounters;
	
	// -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
	
	public ActivityPlan()
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

        result = result * prime + hew.hashCode();        
        result = result * prime + organisationUnit.hashCode();
        result = result * prime + period.hashCode();        
        
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

        if ( !(o instanceof ActivityPlan) )
        {
            return false;
        }

        final ActivityPlan other = (ActivityPlan) o;

        return hew.equals( other.getHew() ) &&        	
        	organisationUnit.equals( other.getOrganisationUnit() ) &&
        	period.equals( other.getPeriod() ) ;
    }   

	@Override
    public String toString()
    {
        return "[" + organisationUnit + ":"  + hew + ":" + period + "]";
    }
    
	// -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

	public User getHew() 
	{
		return hew;
	}

	public void setHew( User hew ) 
	{
		this.hew = hew;
	}
	
	public Period getPeriod() 
	{
		return period;
	}
	
	public void setPeriod( Period period ) 
	{
		this.period = period;
	}
	
	public Set<Encounter> getEncounters() 
	{
		return encounters;
	}
	
	public void setEncounters( Set<Encounter> encounters ) 
	{
		this.encounters = encounters;
	}
	
	public void setId( int id ) 
	{
		this.id = id;
	}
	
	public int getId() 
	{
		return id;
	}

	public OrganisationUnit getOrganisationUnit() 
	{
		return organisationUnit;
	}

	public void setOrganisationUnit(OrganisationUnit organisationUnit) 
	{
		this.organisationUnit = organisationUnit;
	}	

}
