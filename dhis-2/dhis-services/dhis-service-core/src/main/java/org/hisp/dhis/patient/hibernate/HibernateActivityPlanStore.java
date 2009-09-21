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
package org.hisp.dhis.patient.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.activityplan.ActivityPlan;
import org.hisp.dhis.activityplan.ActivityPlanStore;
import org.hisp.dhis.encounter.Encounter;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.user.User;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class HibernateActivityPlanStore
	implements ActivityPlanStore 
{
	
	 // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }
    
    // -------------------------------------------------------------------------
    // ActivityPlan
    // -------------------------------------------------------------------------
	
	public int addActivityPlan( ActivityPlan activityPlan ) 
	{
		Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( activityPlan );
	}
	
	public void deleteActivityPlan( ActivityPlan activityPlan ) 
	{
		Session session = sessionFactory.getCurrentSession();
		
		session.delete( activityPlan );
	}
	
	public ActivityPlan getActivityPlan( int id ) 
	{
		Session session = sessionFactory.getCurrentSession();

        return (ActivityPlan) session.get( ActivityPlan.class, id );
	}
	
	@SuppressWarnings( "unchecked" )
	public Collection<ActivityPlan> getActivityPlans( OrganisationUnit organisationUnit, User hew, Period period ) 
	{
		Session session = sessionFactory.getCurrentSession();      

        Criteria criteria = session.createCriteria( ActivityPlan.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );        
        criteria.add( Restrictions.eq( "hew", hew ) );
        criteria.add( Restrictions.eq( "period", period ) );

        return criteria.list();
	}

	@SuppressWarnings( "unchecked" )
	public Collection<ActivityPlan> getActivityPlans( User hew, Period period ) 
	{
		Session session = sessionFactory.getCurrentSession();      

        Criteria criteria = session.createCriteria( ActivityPlan.class );        
        criteria.add( Restrictions.eq( "hew", hew ) );
        criteria.add( Restrictions.eq( "period", period ) );

        return criteria.list();
	}
	
	@SuppressWarnings( "unchecked" )
	public Collection<ActivityPlan> getActivityPlansByPeriod( Period period ) 
	{
		Session session = sessionFactory.getCurrentSession();      

        Criteria criteria = session.createCriteria( ActivityPlan.class );        
        criteria.add( Restrictions.eq( "period", period ) );

        return criteria.list();
	}

	@SuppressWarnings( "unchecked" )
	public Collection<ActivityPlan> getActivityPlansByOrgUnit( OrganisationUnit organisationUnit ) 
	{
		Session session = sessionFactory.getCurrentSession();      

        Criteria criteria = session.createCriteria( ActivityPlan.class );        
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );

        return criteria.list();
	}

	@SuppressWarnings( "unchecked" )
	public Collection<ActivityPlan> getActivityPlansByOrgUnitAndPeriod( OrganisationUnit organisationUnit, Period period ) 
	{
		Session session = sessionFactory.getCurrentSession();      

        Criteria criteria = session.createCriteria( ActivityPlan.class );        
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "period", period ) );

        return criteria.list();
	}
	
	@SuppressWarnings( "unchecked" )
	public Collection<ActivityPlan> getActivityPlans( User hew ) 
	{
		Session session = sessionFactory.getCurrentSession();      

        Criteria criteria = session.createCriteria( ActivityPlan.class );        
        
        criteria.add( Restrictions.eq( "hew", hew ) );
        
        return criteria.list();
	}

	@SuppressWarnings( "unchecked" )
	public Collection<ActivityPlan> getAllActivityPlans() 
	{
		Session session = sessionFactory.getCurrentSession();      

        Criteria criteria = session.createCriteria( ActivityPlan.class );        
        return criteria.list();
	}

	public void updateActivityPlan(ActivityPlan activityPlan) 
	{
		Session session = sessionFactory.getCurrentSession();
		
		session.update( activityPlan );
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.activityplan.ActivityPlanStore#getActivityPlansByEncounters()
	 */
	public Collection<ActivityPlan> getActivityPlansByEncounters(Collection<Encounter> encounters ) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( ActivityPlan.class );
		criteria.add( Restrictions.in( "encounter",  encounters) );
		
		return null;
	}
}