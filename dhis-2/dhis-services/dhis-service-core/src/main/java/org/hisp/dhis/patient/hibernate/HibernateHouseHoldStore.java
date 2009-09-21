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
import org.hisp.dhis.household.HouseHold;
import org.hisp.dhis.household.HouseHoldStore;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class HibernateHouseHoldStore 
	implements HouseHoldStore 
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
    // HouseHold
    // -------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.household.HouseHoldStore#addHouseHold(org.hisp.dhis.chis.household.HouseHold)
	 */
	public int addHouseHold(HouseHold houseHold) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();
		
		return (Integer) session.save( houseHold );
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.household.HouseHoldStore#deleteHouseHold(org.hisp.dhis.chis.household.HouseHold)
	 */
	public void deleteHouseHold(HouseHold houseHold) {
		// TODO Auto-generated method stub

		Session session = sessionFactory.getCurrentSession();
		
		session.delete( houseHold );
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.household.HouseHoldStore#getAllHouseHolds()
	 */
	@SuppressWarnings( "unchecked" )
	public Collection<HouseHold> getAllHouseHolds() {
		// TODO Auto-generated method stub
	
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( HouseHold.class );
		
        return criteria.list();
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.household.HouseHoldStore#getHouseHold(int)
	 */
	public HouseHold getHouseHold(int id) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();
		
		return (HouseHold) session.get( HouseHold.class, id );
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.household.HouseHoldStore#getHouseHoldForAPatient(org.hisp.dhis.chis.patient.Patient)
	 */
	public HouseHold getHouseHoldForAPatient(Patient patient) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( HouseHold.class );
		criteria.add( Restrictions.eq( "patient", patient ) );
		
		return (HouseHold) criteria.uniqueResult();
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.household.HouseHoldStore#getHouseHoldsForAReportingUnit(org.hisp.dhis.organisationunit.OrganisationUnit)
	 */
	@SuppressWarnings( "unchecked" )
	public Collection<HouseHold> getHouseHoldsForAReportingUnit(
			OrganisationUnit organisationUnit) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria( HouseHold.class );
		criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
		
        return criteria.list();
	}

	/* (non-Javadoc)
	 * @see org.hisp.dhis.chis.household.HouseHoldStore#updateHouseHold(org.hisp.dhis.chis.household.HouseHold)
	 */
	public void updateHouseHold(HouseHold houseHold) {
		// TODO Auto-generated method stub

		Session session = sessionFactory.getCurrentSession();
		
		session.update( houseHold );
	}
}
