/*
 * Copyright (c) 2004-2009, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.patient;

import java.util.Collection;

import org.hisp.dhis.activityplan.ActivityPlan;
import org.hisp.dhis.activityplan.ActivityPlanService;
import org.hisp.dhis.activityplan.ActivityPlanStore;
import org.hisp.dhis.encounter.Encounter;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.User;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class DefaultActivityPlanService 
	implements ActivityPlanService 
{
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ActivityPlanStore activityPlanStore;

    public void setActivityPlanStore( ActivityPlanStore activityPlanStore )
    {
        this.activityPlanStore = activityPlanStore;
    }

    // -------------------------------------------------------------------------
    // ActivityPlan
    // -------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.activityplan.ActivityPlanService#addActivityPlan(org.hisp.dhis.chis.activityplan.ActivityPlan)
	 */
	public int addActivityPlan( ActivityPlan activityPlan ) 
	{
		return activityPlanStore.addActivityPlan( activityPlan );
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.activityplan.ActivityPlanService#deleteActivityPlan(org.hisp.dhis.chis.activityplan.ActivityPlan)
	 */
	public void deleteActivityPlan( ActivityPlan activityPlan ) 
	{
		activityPlanStore.deleteActivityPlan( activityPlan );
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.activityplan.ActivityPlanService#getActivityPlan(int)
	 */
	public ActivityPlan getActivityPlan( int id ) 
	{
		return activityPlanStore.getActivityPlan( id );
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.activityplan.ActivityPlanService#getActivityPlans(org.hisp.dhis.source.Source, org.hisp.dhis.user.User, org.hisp.dhis.user.User, org.hisp.dhis.period.Period)
	 */
	public Collection<ActivityPlan> getActivityPlans( OrganisationUnit organisationUnit,
			User hew, Period period ) 
	{		
		return activityPlanStore.getActivityPlans( organisationUnit, hew, period );		
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.activityplan.ActivityPlanService#getActivityPlans(org.hisp.dhis.user.User, org.hisp.dhis.period.Period)
	 */
	public Collection<ActivityPlan> getActivityPlans( User user, Period period ) 
	{
		return activityPlanStore.getActivityPlans( user, period );
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.activityplan.ActivityPlanService#getActivityPlansByPeriod(org.hisp.dhis.period.Period)
	 */
	public Collection<ActivityPlan> getActivityPlansByPeriod( Period period ) 
	{
		return activityPlanStore.getActivityPlansByPeriod( period );
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.activityplan.ActivityPlanService#getActivityPlansBySource(org.hisp.dhis.source.Source)
	 */
	public Collection<ActivityPlan> getActivityPlansByOrgUnit( OrganisationUnit organsiationUnit ) 
	{
		return activityPlanStore.getActivityPlansByOrgUnit( organsiationUnit );
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.activityplan.ActivityPlanService#getActivityPlansBySourceAndPeriod(org.hisp.dhis.source.Source, org.hisp.dhis.period.Period)
	 */
	public Collection<ActivityPlan> getActivityPlansByOrgUnitAndPeriod( OrganisationUnit organisationUnit, Period period ) 
	{
		return activityPlanStore.getActivityPlansByOrgUnitAndPeriod( organisationUnit, period );
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.activityplan.ActivityPlanService#getActivityPlansByUserOrSupervisor(org.hisp.dhis.user.User)
	 */
	public Collection<ActivityPlan> getActivityPlans( User user ) 
	{
		return activityPlanStore.getActivityPlans( user );
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.activityplan.ActivityPlanService#getAllActivityPlans()
	 */
	public Collection<ActivityPlan> getAllActivityPlans() 
	{
		return activityPlanStore.getAllActivityPlans();
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.activityplan.ActivityPlanService#updateActivityPlan(org.hisp.dhis.chis.activityplan.ActivityPlan)
	 */
	public void updateActivityPlan( ActivityPlan activityPlan ) 
	{
		activityPlanStore.updateActivityPlan( activityPlan );
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.activityplan.ActivityPlanService#getActivityPlansByEncounters(java.util.Collection)
	 */
	public Collection<ActivityPlan> getActivityPlansByEncounters( Collection<Encounter> encounters ) 
	{
		return activityPlanStore.getActivityPlansByEncounters(encounters);
	}	
	
}